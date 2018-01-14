package firebaseauthcom.example.orlanth23.roomsample.job;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import firebaseauthcom.example.orlanth23.roomsample.database.firebase.FirebaseService;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.SendTrackingData;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.Tracking;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.TrackingData;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.ColisDto;
import firebaseauthcom.example.orlanth23.roomsample.network.retrofit.RetrofitClient;
import firebaseauthcom.example.orlanth23.roomsample.utils.ColisService;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static firebaseauthcom.example.orlanth23.roomsample.utils.ColisService.convertTrackingDataToEntity;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by orlanth23 on 18/12/2017.
 */

public class CoreSync {
    private static final String TAG = CoreSync.class.getName();

    private CoreSync() {
    }

    // This consumer only catch the Throwables and log them.
    private static Consumer<Throwable> consThrowable = throwable -> Log.e(TAG, "Erreur sur l'API AfterShip : " + throwable.getMessage() + " Localized message : " + throwable.getLocalizedMessage(), throwable);

    /**
     * @param observable
     * @param resultColis
     * @param context
     */
    private static void callGetTracking(Observable<TrackingData> observable, ColisEntity resultColis, Context context) {
        observable.subscribe(trackingData -> {
            Log.d(TAG, "TrackingData récupéré : " + trackingData.toString());
            Observable.just(convertTrackingDataToEntity(resultColis, trackingData))
                    .subscribe(colisEntity -> {
                        if (ColisService.save(context, colisEntity)) {
                            Log.d(TAG, "Insertion en base réussie");
                        } else {
                            Log.d(TAG, "Insertion en base échouée");
                        }
                    }, consThrowable);
        }, consThrowable);
    }

    /**
     * Création d'un Observable interval qui va envoyer un élément toutes les 10 secondes.
     * Cet élément sera synchronisé avec un élément de la liste de colis présents dans la DB.
     * Pour ce colis qui sera présent toutes les 10 secondes, on va appeler le service CoreSync.getTracking()
     * L'interval de temps nous permet de ne pas saturer le réseau avec des requêtes quand on a trop de colis dans la DB.
     *
     * @param context
     * @param sendNotification
     */
    public static void getAllTracking(Context context, boolean sendNotification) {
        Observable.zip(
                Observable.interval(10, TimeUnit.SECONDS),
                ColisService.observableListColisFromProvider(context).flatMapIterable(colisEntities -> colisEntities),
                (aLong, colisEntity) -> colisEntity)
                .subscribe(colisEntity -> CoreSync.getTracking(context, colisEntity.getIdColis(), sendNotification),
                        consThrowable);
    }

    /**
     * Tracking Core
     *
     * @param context
     * @param trackingNumber
     * @param sendNotification
     */
    public static void getTracking(Context context, String trackingNumber, boolean sendNotification) {
        // Get the Colis from the content provider, if it exists, or create a new one.
        // We will send it at the end.
        ColisEntity colisFromDb = ColisService.get(context, trackingNumber);
        if (colisFromDb == null) {
            colisFromDb = new ColisEntity();
            colisFromDb.setIdColis(trackingNumber);
        }
        ColisEntity resultColis = colisFromDb;

        // Call OPT Service
        RetrofitClient.getTrackingOpt(trackingNumber)
                .subscribe(htmlString -> {
                            Log.d(TAG, "Réponse reçue lors de l'appel service OPT : " + htmlString);
                            if (transformHtmlToColisDto(context, trackingNumber, htmlString, sendNotification)) {
                                Log.d(TAG, "Transformation de la réponse OPT OK");
                            } else {
                                Log.e(TAG, "Fail to receive response from OPT service");
                            }
                        },
                        consThrowable,
                        // Detect courier
                        () -> RetrofitClient.detectCourier(trackingNumber).retry(2).subscribe(responseDataDetectCourier -> {
                            Log.d(TAG, "Détection du bon slug.");
                            if (!responseDataDetectCourier.getCouriers().isEmpty()) {
                                String slug = responseDataDetectCourier.getCouriers().get(0).getSlug();
                                resultColis.setSlug(slug);

                                Log.d(TAG, "Slug found for the tracking : " + trackingNumber + ", it's : " + slug);

                                // Post tracking
                                RetrofitClient.postTracking(trackingNumber)
                                        .doOnError(throwable0 -> {
                                            Log.d(TAG, "Post tracking fail, try to get it by get trackings/:slug/:trackingNumber for the tracking : " + trackingNumber);
                                            callGetTracking(RetrofitClient.getTracking(slug, trackingNumber), resultColis, context);
                                        })
                                        .delay(10, SECONDS)
                                        .subscribe(trackingData -> {
                                            Log.d(TAG, "Post Tracking Successful, try to get the tracking by get trackings/:id");
                                            callGetTracking(RetrofitClient.getTracking(trackingData.getId()), resultColis, context);
                                        }, consThrowable);
                            } else {
                                Log.d(TAG, "No courier was found for this tracking number : " + trackingNumber);
                            }
                        }, consThrowable));
    }

    /**
     * @param context
     * @param idColis
     * @param htmlToTransform
     * @param sendNotification
     * @return
     */
    private static boolean transformHtmlToColisDto(Context context, String idColis, String htmlToTransform, boolean sendNotification) {
        ColisDto colisDto = new ColisDto();
        colisDto.setIdColis(idColis);
        try {
            switch (HtmlTransformer.getColisFromHtml(htmlToTransform, colisDto)) {
                case HtmlTransformer.RESULT_SUCCESS:
                    Log.d(TAG, "HtmlTransformer return SUCCESS");
                    ColisService.saveColisDto(context, colisDto, sendNotification);
                    return true;
                case HtmlTransformer.RESULT_NO_ITEM_FOUND:
                    Log.d(TAG, "HtmlTransformer return NO ITEM FOUND");
                    ColisService.updateLastUpdate(context, colisDto.getIdColis(), false);
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
     * @param trackingNumber
     * @return
     */
    public static Tracking<SendTrackingData> createTrackingData(String trackingNumber) {
        Tracking<SendTrackingData> tracking = new Tracking<>();
        SendTrackingData trackingDetect = new SendTrackingData();
        trackingDetect.setTrackingNumber(trackingNumber);
        tracking.setTracking(trackingDetect);
        return tracking;
    }

    /**
     * Delete tracking from the AfterShip account.
     *
     * @param context
     * @param colis
     */
    public static void deleteTracking(Context context, ColisEntity colis) {
        if (colis.getSlug() != null && colis.getSlug().length() != 0 && colis.getIdColis() != null && colis.getIdColis().length() != 0) {
            RetrofitClient.deleteTrackingBySlugAndTrackingNumber(colis.getSlug(), colis.getIdColis())
                    .subscribe(trackingDelete -> {
                        Log.d(TAG, "Suppression effective du tracking " + trackingDelete.getId() + " sur l'API AfterShip");
                        FirebaseService.deleteRemoteColis(colis.getIdColis());
                        ColisService.realDelete(context, colis.getIdColis());
                    }, consThrowable);
        }
    }
}
