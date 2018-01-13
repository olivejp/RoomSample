package firebaseauthcom.example.orlanth23.roomsample.network;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.DrawableRes;

import com.bumptech.glide.RequestBuilder;

import firebaseauthcom.example.orlanth23.roomsample.glide.GlideApp;
import firebaseauthcom.example.orlanth23.roomsample.glide.SvgSoftwareLayerSetter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by orlanth23 on 12/01/2018.
 */

public class GlideRequester {

    private GlideRequester() {
    }

    public static RequestBuilder<PictureDrawable> getSvgRequester(Context context, @DrawableRes int onError, @DrawableRes int placeholder) {
        return GlideApp.with(context)
                .as(PictureDrawable.class)
                .placeholder(placeholder)
                .error(onError)
                .transition(withCrossFade())
                .listener(new SvgSoftwareLayerSetter());
    }
}
