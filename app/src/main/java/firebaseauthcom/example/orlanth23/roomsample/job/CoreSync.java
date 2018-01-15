package firebaseauthcom.example.orlanth23.roomsample.job;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisWithStepsRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.EtapeRepository;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.TrackingData;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.ColisDto;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.HtmlTransformer;
import firebaseauthcom.example.orlanth23.roomsample.mapper.ColisMapper;
import firebaseauthcom.example.orlanth23.roomsample.network.aftership.RetrofitAfterShipClient;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static firebaseauthcom.example.orlanth23.roomsample.mapper.ColisMapper.convertTrackingDataToEntity;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by orlanth23 on 18/12/2017.
 */

class CoreSync {
    private static final String TAG = CoreSync.class.getName();

    private CoreSync() {
    }

    // This consumer only catch the Throwables and log them.
    private static Consumer<Throwable> consThrowable = throwable -> Log.e(TAG, "Erreur sur l'API AfterShip : " + throwable.getMessage() + " Localized message : " + throwable.getLocalizedMessage(), throwable);

    /**
     * Création d'un Observable interval qui va envoyer un élément toutes les 10 secondes.
     * Cet élément sera synchronisé avec un élément de la liste de colis présents dans la DB.
     * Pour ce colis qui sera présent toutes les 10 secondes, on va appeler le service CoreSync.callOptTracking()
     * L'interval de temps nous permet de ne pas saturer le réseau avec des requêtes quand on a trop de colis dans la DB.
     *
     * @param context
     */
    static void callGetAllTracking(Context context) {
        List<ColisEntity> listColis = ColisRepository.getInstance(context).getAllColis(true);

        Observable.zip(
                Observable.interval(10, TimeUnit.SECONDS),
                Observable.just(listColis).flatMapIterable(colisEntities -> colisEntities),
                (aLong, colisEntity) -> colisEntity)
                .subscribe(colisEntity -> CoreSync.callOptTracking(context, colisEntity.getIdColis()),
                        consThrowable);
    }

    /**
     * Call the OPT tracking API
     * For the moment we just call a URL and parse a HTML page.
     * With the new IPS API soon, we will be able to call a REST API
     *
     * @param context
     * @param trackingNumber
     */
    static void callOptTracking(Context context, String trackingNumber) {
        // Get the Colis from the content provider, if it exists, or create a new one.
        // We will send it at the end.
        ColisWithSteps colisWithSteps = ColisWithStepsRepository.getInstance(context).findActiveColisWithStepsByIdColis(trackingNumber);
        if (colisWithSteps == null) {
            colisWithSteps = new ColisWithSteps();
            colisWithSteps.colisEntity.setIdColis(trackingNumber);
        }
        final ColisWithSteps resultColis = colisWithSteps;

        // Call OPT Service
        RetrofitAfterShipClient.getTrackingOpt(trackingNumber)
                .subscribe(htmlString -> {
                            ColisRepository repoColis = ColisRepository.getInstance(context);
                            EtapeRepository repoEtape = EtapeRepository.getInstance(context);
                            ColisDto colisDto = new ColisDto();
                            colisDto.setIdColis(trackingNumber);
                            if (transformHtmlToColisDto(colisDto, htmlString)) {
                                Log.d(TAG, "Transformation de la réponse OPT OK");
                                ColisWithSteps anotherColisWithSteps = ColisMapper.convertToEntity(colisDto);
                                repoColis.insert(anotherColisWithSteps.colisEntity);
                                repoColis.updateLastUpdateSuccessful(anotherColisWithSteps.colisEntity);
                                repoEtape.insert(anotherColisWithSteps.etapeEntityList);
                            } else {
                                Log.e(TAG, "Fail to receive response from OPT service");
                            }
                        },
                        consThrowable,
                        () -> callDetectCourierAfterShip(context, trackingNumber, resultColis)
                );
    }

    /**
     * @param colisDto
     * @param htmlToTransform
     * @return
     */
    private static boolean transformHtmlToColisDto(ColisDto colisDto, String htmlToTransform) {
        try {
            switch (HtmlTransformer.getColisFromHtml(htmlToTransform, colisDto)) {
                case HtmlTransformer.RESULT_SUCCESS:
                    Log.d(TAG, "HtmlTransformer return SUCCESS");
                    return true;
                case HtmlTransformer.RESULT_NO_ITEM_FOUND:
                    Log.d(TAG, "HtmlTransformer return NO ITEM FOUND");
                    return false;
                default:
                    return false;
            }
        } catch (HtmlTransformer.HtmlTransformerException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    /**
     * @param context
     * @param trackingNumber
     * @param resultColis
     */
    private static void callDetectCourierAfterShip(Context context, String trackingNumber, ColisWithSteps resultColis) {
        Log.d(TAG, "Détection du bon slug.");
        RetrofitAfterShipClient.detectCourier(trackingNumber).subscribe(responseDataDetectCourier -> {
            if (!responseDataDetectCourier.getCouriers().isEmpty()) {
                final String slug = responseDataDetectCourier.getCouriers().get(0).getSlug();
                Log.d(TAG, "Slug found for the tracking : " + trackingNumber + ", it's : " + slug);
                resultColis.colisEntity.setSlug(slug);

                // Post tracking
                postAfterShipTracking(context, resultColis, trackingNumber, slug);
            } else {
                Log.d(TAG, "No courier was found for this tracking number : " + trackingNumber);
            }
        }, consThrowable);
    }

    /**
     * Post the Tracking number to the AfterShip API
     * Response from the AfterShip API could take some time, so we wait 10 seconds
     *
     * @param context
     * @param trackingNumber
     * @param slug
     * @param resultColis
     */
    private static void postAfterShipTracking(Context context, ColisWithSteps resultColis, String trackingNumber, String slug) {
        RetrofitAfterShipClient.postTracking(trackingNumber)
                .doOnError(throwable0 -> {
                    Log.d(TAG, "Post tracking fail, try to get it by get trackings/:slug/:trackingNumber for the tracking : " + trackingNumber);
                    callGetTrackingBySlugAndTrackingNumber(context, resultColis, slug, trackingNumber);
                })
                .delay(10, SECONDS)
                .subscribe(trackingData -> {
                    Log.d(TAG, "Post Tracking Successful, try to get the tracking by get trackings/:id");
                    callGetTrackingByTrackingId(context, resultColis, trackingData.getId());
                }, consThrowable);
    }

    /**
     * @param slug
     * @param trackingNumber
     * @param resultColis
     * @param context
     */
    private static void callGetTrackingBySlugAndTrackingNumber(Context context, ColisWithSteps resultColis, String slug, String trackingNumber) {
        RetrofitAfterShipClient.getTrackingBySlugAndTrackingNumber(slug, trackingNumber).subscribe(trackingData -> {
            Log.d(TAG, "TrackingData récupéré : " + trackingData.toString());
            saveSuccessfulyColis(context, resultColis, trackingData);
        }, consThrowable);
    }


    /**
     * Call the AfterShip API to get tracking informations
     *
     * @param trackingId
     * @param resultColis
     * @param context
     */
    private static void callGetTrackingByTrackingId(Context context, ColisWithSteps resultColis, String trackingId) {
        RetrofitAfterShipClient.getTrackingByTrackingId(trackingId).subscribe(trackingData -> {
            Log.d(TAG, "TrackingData récupéré : " + trackingData.toString());
            saveSuccessfulyColis(context, resultColis, trackingData);
        }, consThrowable);
    }

    /**
     * Delete tracking from the AfterShip account.
     *
     * @param context
     * @param colis
     */
    static void deleteAfterShipTracking(Context context, ColisEntity colis) {
        if (colis.getSlug() != null && colis.getSlug().length() != 0) {
            if (colis.getIdColis() != null && colis.getIdColis().length() != 0) {
                RetrofitAfterShipClient.deleteTrackingBySlugAndTrackingNumber(colis.getSlug(), colis.getIdColis())
                        .subscribe(trackingDelete -> {
                            Log.d(TAG, "Suppression effective du tracking " + trackingDelete.getId() + " sur l'API AfterShip");
                            // ToDo Réactiver interaction Firebase
                            // FirebaseService.deleteRemoteColis(colis.getIdColis());
                            ColisRepository.getInstance(context).delete(colis.getIdColis());
                        }, consThrowable);
            } else {
                Log.e(TAG, "Can't delete without tracking number");
            }
        } else {
            Log.e(TAG, "Can't delete without slug");
        }
    }

    /**
     * @param context
     * @param resultColis
     * @param trackingData
     */
    private static void saveSuccessfulyColis(Context context, ColisWithSteps resultColis, TrackingData trackingData) {
        ColisWithSteps colisWithSteps = convertTrackingDataToEntity(resultColis, trackingData);
        ColisRepository.getInstance(context).insert(colisWithSteps.colisEntity);
        ColisRepository.getInstance(context).updateLastUpdateSuccessful(colisWithSteps.colisEntity);
        EtapeRepository.getInstance(context).insert(colisWithSteps.etapeEntityList);
    }
}
