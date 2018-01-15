package firebaseauthcom.example.orlanth23.roomsample.job;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;

/**
 * This class is called by SyncTask or by SyncColisJobCreator
 */
public class SyncColisService extends IntentService {

    private static final String TAG = SyncColisService.class.getName();

    public static final String ARG_ID_COLIS = "ARG_ID_COLIS";
    public static final String ARG_ACTION = "ARG_ACTION";
    public static final String ARG_ACTION_SYNC_COLIS = "ARG_ACTION_SYNC_COLIS";
    public static final String ARG_ACTION_SYNC_ALL = "ARG_ACTION_SYNC_ALL";
    public static final String ARG_ACTION_SYNC_ALL_FROM_SCHEDULER = "ARG_ACTION_SYNC_ALL_FROM_SCHEDULER";
    public static final String ARG_NOTIFICATION = "ARG_NOTIFICATION";

    public SyncColisService() {
        super("SyncColisService");
    }


    /**
     * Launch the sync service with an idColis
     * Only the colis specified with idColis will be updated
     *
     * @param context
     * @param idColis
     */
    public static void launchSynchroByIdColis(@NonNull Context context, @NonNull String idColis) {
        Intent syncService = new Intent(context, SyncColisService.class);
        syncService.putExtra(SyncColisService.ARG_ACTION, SyncColisService.ARG_ACTION_SYNC_COLIS);
        syncService.putExtra(SyncColisService.ARG_ID_COLIS, idColis);
        syncService.putExtra(SyncColisService.ARG_NOTIFICATION, false);
        context.startService(syncService);
    }

    /**
     * Launch the sync service for all the colis
     *
     * @param context
     */
    public static void launchSynchroForAll(@NonNull Context context) {
        Intent syncService = new Intent(context, SyncColisService.class);
        syncService.putExtra(SyncColisService.ARG_ACTION, SyncColisService.ARG_ACTION_SYNC_ALL);
        syncService.putExtra(SyncColisService.ARG_NOTIFICATION, false);
        context.startService(syncService);
    }

    /**
     * Lancement du service de synchro pour tous les objets mais à partir du scheduler
     *
     * @param context
     */
    public static void launchSynchroFromScheduler(@NonNull Context context) {
        Intent syncService = new Intent(context, SyncColisService.class);
        syncService.putExtra(SyncColisService.ARG_ACTION, SyncColisService.ARG_ACTION_SYNC_ALL_FROM_SCHEDULER);
        syncService.putExtra(SyncColisService.ARG_NOTIFICATION, true);
        context.startService(syncService);
    }

    /**
     * Lecture de tous les colis deleted dans la base de données pour suppression dans l'API AfterShip.
     *
     * @param context
     */
    public static void launchSynchroDelete(@NonNull Context context) {
        List<ColisEntity> listColisDeleted = ColisRepository.getInstance(context).getAllColis(false);
        for (ColisEntity colis : listColisDeleted) {
            CoreSync.deleteAfterShipTracking(context, colis);
        }
    }

    /**
     * @param bundle
     */
    private void handleActionSyncColis(Bundle bundle) {
        if (bundle.containsKey(ARG_ID_COLIS)) {
            String idColis = bundle.getString(ARG_ID_COLIS);
            if (idColis != null) CoreSync.callOptTracking(this, idColis);
        }
    }

    /**
     *
     */
    private void handleActionSyncAll() {
        CoreSync.callGetAllTracking(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.containsKey(ARG_ACTION)) {
                String action = bundle.getString(ARG_ACTION);
                boolean sendNotification = (bundle.containsKey(ARG_NOTIFICATION)) && bundle.getBoolean(ARG_NOTIFICATION);
                if (action != null) {
                    switch (action) {
                        case ARG_ACTION_SYNC_COLIS:
                            handleActionSyncColis(bundle);
                            break;
                        case ARG_ACTION_SYNC_ALL:
                            handleActionSyncAll();
                            break;
                        case ARG_ACTION_SYNC_ALL_FROM_SCHEDULER:
                            handleActionSyncAll();
                            break;
                        default:
                            break;
                    }
                    launchSynchroDelete(this);

                    // ToDo Réactiver la synchro avec Fb
                    // updateFirebase(this, listFromProvider(this, true));
                }
            }
        }
    }
}
