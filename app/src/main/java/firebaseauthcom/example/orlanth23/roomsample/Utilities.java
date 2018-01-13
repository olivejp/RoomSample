package firebaseauthcom.example.orlanth23.roomsample;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by orlanth23 on 13/08/2017.
 */

public class Utilities {

    private Utilities() {
    }

    /**
     * Hide the keyboard
     *
     * @param ctx
     */
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * Retrieve the associated drawable id from the etape status
     * @param   status The etape status
     * @return  Drawable Id corresponding on the etape status
     */
    @DrawableRes
    public static int getStatusDrawable(@NonNull String status) {
        switch (status) {
            case "InfoReceived":
                return R.drawable.ic_status_info_receive;
            case "AttemptFail":
                return R.drawable.ic_status_attemptfail;
            case "Delivered":
                return R.drawable.ic_status_delivered;
            case "Exception":
                return R.drawable.ic_status_exception;
            case "Expired":
                return R.drawable.ic_status_expired;
            case "InTransit":
                return R.drawable.ic_status_in_transit;
            case "OutForDelivery":
                return R.drawable.ic_status_out_for_delivery;
            case "Pending":
                return R.drawable.ic_status_pending;
            default:
                return R.drawable.ic_status_pending;
        }
    }
}
