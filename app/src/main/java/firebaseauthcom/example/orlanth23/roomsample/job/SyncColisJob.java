package firebaseauthcom.example.orlanth23.roomsample.job;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import static firebaseauthcom.example.orlanth23.roomsample.Constants.INTERVAL_SYNC_JOB_MINS;
import static firebaseauthcom.example.orlanth23.roomsample.Constants.PERIODIC_SYNC_JOB_MINS;

/**
 * Created by 2761oli on 10/10/2017.
 */

public class SyncColisJob extends Job {
    static final String TAG = "sync_colis_job";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // A job run on the background thread, so no need to call SyncTask here
        SyncColisService.launchSynchroFromScheduler(getContext());
        return Result.SUCCESS;
    }

    /**
     * Schedule a periodic job which will be launch every 30 minutes.
     */
    public static void scheduleJob() {
        new JobRequest.Builder(SyncColisJob.TAG)
                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .setPeriodic(TimeUnit.MINUTES.toMillis(PERIODIC_SYNC_JOB_MINS), TimeUnit.MINUTES.toMillis(INTERVAL_SYNC_JOB_MINS))
                .build()
                .schedule();
    }

    /**
     * Launch immediately the sync job
     */
    public static void launchImmediateJob() {
        new JobRequest.Builder(SyncColisJob.TAG)
                .startNow();
    }
}
