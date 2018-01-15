package firebaseauthcom.example.orlanth23.roomsample.network.aftership;


import firebaseauthcom.example.orlanth23.roomsample.job.aftership.ResponseAfterShip;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.ResponseDataDetectCourier;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.SendTrackingData;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.Tracking;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.TrackingData;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.TrackingDelete;
import firebaseauthcom.example.orlanth23.roomsample.network.StringConverter;
import firebaseauthcom.example.orlanth23.roomsample.network.opt.RetrofitOptCall;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static firebaseauthcom.example.orlanth23.roomsample.Constants.AFTER_SHIP_KEY;
import static firebaseauthcom.example.orlanth23.roomsample.Constants.URL_AFTERSHIP_BASE_URL;
import static firebaseauthcom.example.orlanth23.roomsample.Constants.URL_SUIVI_COLIS;

/**
 * Created by orlanth23 on 12/12/2017.
 */
public class RetrofitAfterShipClient {

    private static Retrofit retrofitJson = null;
    private static Retrofit retrofitHtml = null;


    private RetrofitAfterShipClient() {
    }

    /**
     * Va créer un client http qui enverra toujours les mêmes headers
     * A savoir : afership-api-key et Content-Type
     * Puis va attacher ce client dans un retrofit.
     * Ainsi toutes les requêtes passées avec ce retrofit porteront les mêmes headers.
     *
     * @return {@link Retrofit}
     */
    private static Retrofit getJsonClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("aftership-api-key", AFTER_SHIP_KEY)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    return chain.proceed(request);
                })
                .build();


        if (retrofitJson == null) {
            retrofitJson = new Retrofit.Builder()
                    .baseUrl(URL_AFTERSHIP_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
        }
        return retrofitJson;
    }

    /**
     * Créer un retrofit mais sans headers spécifiés.
     * Les headers peuvent être attachées mais via un appel.
     *
     * @return {@link Retrofit}
     */
    private static Retrofit getHtmlClient() {
        if (retrofitHtml == null) {
            retrofitHtml = new Retrofit.Builder()
                    .baseUrl(URL_SUIVI_COLIS)
                    .addConverterFactory(new StringConverter())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofitHtml;
    }

    /**
     * Detect courier for the tracking number passed
     *
     * @param trackingNumber
     * @return Observable<ResponseDataDetectCourier>
     */
    public static Observable<ResponseDataDetectCourier> detectCourier(String trackingNumber) {
        RetrofitAfterShipCall retrofitAfterShipCall = RetrofitAfterShipClient.getJsonClient().create(RetrofitAfterShipCall.class);
        Tracking<SendTrackingData> trackingDataTracking = createTrackingData(trackingNumber);
        return retrofitAfterShipCall.detectCourier(trackingDataTracking)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(r -> Observable.just(r.getData()));
    }

    /**
     * Return an Observable of TrackingDelete
     *
     * @return Observable<TrackingDelete>
     */
    public static Observable<TrackingDelete> deleteTrackingBySlugAndTrackingNumber(String slug, String trackingNumber) {
        RetrofitAfterShipCall retrofitAfterShipCall = RetrofitAfterShipClient.getJsonClient().create(RetrofitAfterShipCall.class);
        return retrofitAfterShipCall.deleteTracking(slug, trackingNumber)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap(dataGetResponseAfterShip -> Observable.just(dataGetResponseAfterShip.getData().getTracking()));
    }

    /**
     * Return an Observable of TrackingData
     *
     * @return
     */
    public static Observable<TrackingData> getTrackingByTrackingId(String trackingId) {
        RetrofitAfterShipCall retrofitAfterShipCall = RetrofitAfterShipClient.getJsonClient().create(RetrofitAfterShipCall.class);
        return retrofitAfterShipCall.getTracking(trackingId)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap(trackingResponseAfterShip -> Observable.just(trackingResponseAfterShip.getData().getTracking()));
    }

    /**
     * Return an Observable of TrackingData
     *
     * @return
     */
    public static Observable<TrackingData> getTrackingBySlugAndTrackingNumber(String slug, String trackingNumber) {
        RetrofitAfterShipCall retrofitAfterShipCall = RetrofitAfterShipClient.getJsonClient().create(RetrofitAfterShipCall.class);
        return retrofitAfterShipCall.getTracking(slug, trackingNumber)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap(trackingResponseAfterShip -> Observable.just(trackingResponseAfterShip.getData().getTracking()));
    }

    /**
     * Return an Observable of <TrackingData>
     *
     * @param trackingNumber
     * @return Observable<TrackingData>
     */
    public static Observable<TrackingData> postTracking(String trackingNumber) {
        Function<ResponseAfterShip<Tracking<TrackingData>>, TrackingData> funPostTrackingData = trackingResponseAfterShip -> trackingResponseAfterShip.getData().getTracking();

        Tracking<SendTrackingData> trackingDataTracking = createTrackingData(trackingNumber);

        RetrofitAfterShipCall retrofitAfterShipCall = RetrofitAfterShipClient.getJsonClient().create(RetrofitAfterShipCall.class);
        Observable<ResponseAfterShip<Tracking<TrackingData>>> observable = retrofitAfterShipCall.postTracking(trackingDataTracking)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());

        return observable.map(funPostTrackingData);
    }

    /**
     * Return an Observable of <{@link String}>
     *
     * @param trackingNumber
     * @return Observable<String>
     */
    public static Observable<String> getTrackingOpt(String trackingNumber) {
        RetrofitOptCall retrofitOptCall = RetrofitAfterShipClient.getHtmlClient().create(RetrofitOptCall.class);
        return retrofitOptCall.getTrackingOpt(trackingNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
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
}
