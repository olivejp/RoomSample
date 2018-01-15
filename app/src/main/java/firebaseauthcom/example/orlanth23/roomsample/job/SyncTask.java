package firebaseauthcom.example.orlanth23.roomsample.job;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * Created by 2761oli on 27/12/2017.
 * <p>
 * This class is used to call the SyncColisService in the background with an AsyncTask
 * Contains a {@link WeakReference} with a {@link Context}
 * If idColis is filled we sync only one colis, all if not.
 */

public class SyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SyncTask.class.getName();

    private WeakReference<Context> weakContext;
    private String idColis;

    /**
     * Constructor to launch a SyncTask for only one idColis
     *
     * @param context
     * @param idColis
     */
    public SyncTask(Context context, @NonNull String idColis) {
        if (context != null) {
            this.weakContext = new WeakReference<>(context);
        }
        this.idColis = idColis;
    }

    /**
     * Constructor to launch a SyncTask for all the colis list.
     *
     * @param context
     */
    public SyncTask(Context context) {
        if (context != null) {
            this.weakContext = new WeakReference<>(context);
        }
        this.idColis = null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (this.weakContext != null && this.weakContext.get() != null) {
            Context context = this.weakContext.get();
            if (this.idColis != null) {
                SyncColisService.launchSynchroByIdColis(context, this.idColis);
            } else {
                SyncColisService.launchSynchroForAll(context);
            }
        }
        return null;
    }
}

