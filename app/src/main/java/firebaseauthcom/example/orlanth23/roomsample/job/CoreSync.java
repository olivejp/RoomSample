package firebaseauthcom.example.orlanth23.roomsample.job;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import firebaseauthcom.example.orlanth23.roomsample.NotificationSender;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisWithStepsRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.StepRepository;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.ColisDto;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.HtmlTransformer;
import firebaseauthcom.example.orlanth23.roomsample.mapper.ColisMapper;
import firebaseauthcom.example.orlanth23.roomsample.network.aftership.RetrofitAfterShipClient;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static firebaseauthcom.example.orlanth23.roomsample.mapper.ColisMapper.convertTrackingDataToEntity;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by orlanth23 on 18/12/2017.
 * <p>
 * This class contains the series of network calls to make to retreive the tracking informations.
 */
class CoreSync {
    private static final String TAG = CoreSync.class.getName();

    private static CoreSync INSTANCE;
    private WeakReference<Context> contextWeakReference;
    private Consumer<Throwable> consThrowable;
    private boolean sendNotification;

    private CoreSync(Context context, boolean sendNotification) {
        if (context != null) {
            this.contextWeakReference = new WeakReference<>(context);
        }
        this.sendNotification = sendNotification;
        this.consThrowable = throwable -> Log.e(TAG, "Erreur sur l'API AfterShip : " + throwable.getMessage() + " Localized message : " + throwable.getLocalizedMessage(), throwable);
    }

    public static CoreSync getInstance(Context context, boolean sendNotification) {
        if (INSTANCE == null) {
            INSTANCE = new CoreSync(context, sendNotification);
        }
        return INSTANCE;
    }

    /**
     * Création d'un Observable interval qui va envoyer un élément toutes les 10 secondes.
     * Cet élément sera synchronisé avec un élément de la liste de colis présents dans la DB.
     * Pour ce colis qui sera présent toutes les 10 secondes, on va appeler le service CoreSync.callOptTracking()
     * L'interval de temps nous permet de ne pas saturer le réseau avec des requêtes quand on a trop de colis dans la DB.
     */
    void callGetAllTracking() {
        ColisRepository.getInstance(contextWeakReference.get()).getAllActiveAndNonDeliveredColis()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(listColis ->
                        Observable.zip(
                                // ToDo remettre à 10 secondes
                                Observable.interval(1, TimeUnit.MILLISECONDS),
                                Observable.just(listColis).flatMapIterable(colisEntities -> colisEntities),
                                (aLong, colisEntity) -> colisEntity)
                                .subscribe(this::callOptTracking, consThrowable)
                );

    }

    /**
     * Call the OPT tracking API
     * For the moment we just call a URL and parse a HTML page.
     * With the new IPS API soon, we will be able to call a REST API
     *
     * @param colisEntity
     */
    void callOptTracking(ColisEntity colisEntity) {
        if (contextWeakReference.get() != null) {
            String trackingNumber = colisEntity.getIdColis();
            RetrofitAfterShipClient.getTrackingOpt(trackingNumber)
                    .doOnError(throwable -> {
                        ColisWithSteps colisWithSteps = new ColisWithSteps();
                        colisWithSteps.colisEntity = colisEntity;
                        colisWithSteps.colisEntity.setDeleted(0);
                        callDetectCourierAfterShip(colisWithSteps, trackingNumber);
                    })
                    .subscribe(htmlString -> {
                                ColisDto colisDto = new ColisDto();
                                colisDto.setIdColis(trackingNumber);
                                colisDto.setDescription(colisEntity.getDescription());
                                ColisWithSteps resultColis;
                                if (transformHtmlToColisDto(colisDto, htmlString)) {
                                    Log.d(TAG, "Transformation de la réponse OPT OK");
                                    resultColis = ColisMapper.convertToActiveEntity(colisDto);
                                } else {
                                    Log.e(TAG, "Fail to receive response from OPT service");
                                    resultColis = new ColisWithSteps();
                                    resultColis.colisEntity.setIdColis(trackingNumber);
                                }
                                callDetectCourierAfterShip(resultColis, trackingNumber);
                            },
                            consThrowable
                    );
        }
    }

    /**
     * @param colisDto
     * @param htmlToTransform
     * @return
     */
    private boolean transformHtmlToColisDto(ColisDto colisDto, String htmlToTransform) {
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
     * @param resultColis
     * @param trackingNumber
     */
    private void callDetectCourierAfterShip(final ColisWithSteps resultColis, String trackingNumber) {
        Log.d(TAG, "Détection du bon slug.");
        RetrofitAfterShipClient.detectCourier(trackingNumber)
                .subscribe(responseDataDetectCourier -> {
                    if (!responseDataDetectCourier.getCouriers().isEmpty()) {
                        final String slug = responseDataDetectCourier.getCouriers().get(0).getSlug();
                        Log.d(TAG, "Slug found for the tracking : " + trackingNumber + ", it's : " + slug);
                        resultColis.colisEntity.setSlug(slug);

                        // Post tracking
                        postAfterShipTracking(resultColis, trackingNumber, slug);
                    } else {
                        Log.d(TAG, "No courier was found for this tracking number : " + trackingNumber);
                    }
                }, consThrowable);
    }

    /**
     * Post the Tracking number to the AfterShip API
     * Response from the AfterShip API could take some time, so we wait 10 seconds
     *
     * @param trackingNumber
     * @param slug
     * @param resultColis
     */
    private void postAfterShipTracking(final ColisWithSteps resultColis, String trackingNumber, String slug) {
        RetrofitAfterShipClient.postTracking(trackingNumber)
                .doOnError(throwable0 -> {
                    Log.d(TAG, "Post tracking fail, try to get it by get trackings/:slug/:trackingNumber for the tracking : " + trackingNumber);
                    callGetTrackingBySlugAndTrackingNumber(resultColis, slug, trackingNumber);
                })
                // ToDo remettre à 10 secondes
                .delay(1, MILLISECONDS)
                .subscribe(trackingData -> {
                    Log.d(TAG, "Post Tracking Successful, try to get the tracking by get trackings/:id");
                    callGetTrackingByTrackingId(resultColis, trackingData.getId());
                }, consThrowable);
    }

    /**
     * @param slug
     * @param trackingNumber
     * @param resultColis
     */
    private void callGetTrackingBySlugAndTrackingNumber(final ColisWithSteps resultColis, String slug, String trackingNumber) {
        RetrofitAfterShipClient.getTrackingBySlugAndTrackingNumber(slug, trackingNumber)
                .subscribe(trackingData -> {
                    Log.d(TAG, "TrackingData récupéré : " + trackingData.toString());
                    convertTrackingDataToEntity(resultColis, trackingData);
                    saveSuccessfulColis(resultColis);
                }, consThrowable);
    }


    /**
     * Call the AfterShip API to get tracking informations
     *
     * @param trackingId
     * @param resultColis
     */
    private void callGetTrackingByTrackingId(final ColisWithSteps resultColis, String trackingId) {
        RetrofitAfterShipClient.getTrackingByTrackingId(trackingId)
                .subscribe(trackingData -> {
                    Log.d(TAG, "TrackingData récupéré : " + trackingData.toString());
                    convertTrackingDataToEntity(resultColis, trackingData);
                    saveSuccessfulColis(resultColis);
                }, consThrowable);
    }

    /**
     * Delete tracking from the AfterShip account.
     *
     * @param colis
     */
    void deleteAfterShipTracking(ColisEntity colis) {
        if (colis.getSlug() != null && colis.getSlug().length() != 0) {
            if (colis.getIdColis() != null && colis.getIdColis().length() != 0) {
                RetrofitAfterShipClient.deleteTrackingBySlugAndTrackingNumber(colis.getSlug(), colis.getIdColis())
                        .subscribe(trackingDelete ->
                                        Log.d(TAG, "Suppression effective du tracking " + trackingDelete.getId() + " sur l'API AfterShip")
                                , consThrowable, () -> {
                                    ColisRepository.getInstance(contextWeakReference.get()).delete(colis.getIdColis());
                                    // ToDo Réactiver interaction Firebase
                                    // FirebaseService.deleteRemoteColis(colis.getIdColis());
                                });
            } else {
                Log.e(TAG, "Can't markAsDeleted without tracking number");
            }
        } else {
            Log.e(TAG, "Can't markAsDeleted without slug");
        }
    }

    /**
     * Find the record in DB.
     * If count :
     * - Update only the steps.
     * If not :
     * - Insert the new colis
     * - Insert the new steps
     * Anyway update the last successful update
     *
     * @param resultColis
     */
    private void saveSuccessfulColis(ColisWithSteps resultColis) {
        Log.d(TAG, "ColisWithSteps qui va être enregistré : " + resultColis.toString());
        Context context = contextWeakReference.get();
        ColisWithStepsRepository.getInstance(context).findActiveColisWithStepsByIdColis(resultColis.colisEntity.getIdColis())
                .subscribe(colisWithSteps -> {
                    if (colisWithSteps != null) {
                        if (resultColis.stepEntityList.size() > colisWithSteps.stepEntityList.size()) {
                            if (sendNotification) {
                                NotificationSender.sendNotification(context, context.getString(R.string.app_name), resultColis.colisEntity.getIdColis() + " a été mis à jour.", R.drawable.ic_archive_white_48dp);
                            }
                        }
                    } else {
                        ColisRepository.getInstance(context).save(resultColis.colisEntity);
                    }
                    StepRepository.getInstance(context).save(resultColis.stepEntityList);
                    ColisRepository.getInstance(context).updateLastSuccessfulUpdate(resultColis.colisEntity);
                });
    }
}
