package firebaseauthcom.example.orlanth23.roomsample.job;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by 2761oli on 27/12/2017.
 */

public class SyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SyncTask.class.getName();

    public enum TypeTask {
        SOLO,
        ALL
    }

    private WeakReference<Context> weakContext;
    private String idColis;
    private TypeTask typeTask;

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
        this.typeTask = TypeTask.SOLO;
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
        this.typeTask = TypeTask.ALL;
        this.idColis = null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (this.weakContext != null && this.weakContext.get() != null) {
            Context context = this.weakContext.get();
            if (this.typeTask == TypeTask.SOLO) {
                if (this.idColis != null) {
                    SyncColisService.launchSynchroByIdColis(context, this.idColis);
                } else {
                    Log.e(TAG, "Try to call SyncColisService.launchSynchroByIdColis but don't get the idColis");
                }
            } else if (this.typeTask == TypeTask.ALL) {
                SyncColisService.launchSynchroForAll(context);
            }
        }
        return null;
    }
}

