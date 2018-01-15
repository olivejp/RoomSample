package firebaseauthcom.example.orlanth23.roomsample.network.opt;


import firebaseauthcom.example.orlanth23.roomsample.network.StringConverter;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static firebaseauthcom.example.orlanth23.roomsample.Constants.URL_SUIVI_COLIS;

/**
 * Created by orlanth23 on 12/12/2017.
 */
public class RetrofitOptClient {

    private static Retrofit retrofitHtml = null;

    private RetrofitOptClient() {
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
     * Return an Observable of <{@link String}>
     *
     * @param trackingNumber
     * @return Observable<String>
     */
    public static Observable<String> getTrackingOpt(String trackingNumber) {
        RetrofitOptCall retrofitOptCall = RetrofitOptClient.getHtmlClient().create(RetrofitOptCall.class);
        return retrofitOptCall.getTrackingOpt(trackingNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }
}
