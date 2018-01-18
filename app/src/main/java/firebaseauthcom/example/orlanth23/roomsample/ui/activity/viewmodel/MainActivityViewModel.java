package firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisWithStepsRepository;
import firebaseauthcom.example.orlanth23.roomsample.job.SyncTask;
import firebaseauthcom.example.orlanth23.roomsample.ui.glide.GlideApp;
import firebaseauthcom.example.orlanth23.roomsample.ui.glide.SvgSoftwareLayerSetter;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class MainActivityViewModel extends AndroidViewModel {

    private ColisWithStepsRepository colisWithStepsRepository;
    private RequestBuilder<PictureDrawable> requester;
    private MutableLiveData<ColisWithSteps> colisWithStepsSelected = new MutableLiveData<>();
    private MutableLiveData<List<ColisWithSteps>> colisWithStepsList = new MutableLiveData<>();
    private boolean twoPane;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        colisWithStepsRepository = ColisWithStepsRepository.getInstance(application);
        colisWithStepsRepository.getActiveFlowableColisWithSteps().subscribe(colisWithSteps -> colisWithStepsList.postValue(colisWithSteps));
        requester = GlideApp.with(application)
                .as(PictureDrawable.class)
                .placeholder(R.drawable.ic_archive_grey_900_48dp)
                .error(R.drawable.ic_archive_grey_900_48dp)
                .listener(new SvgSoftwareLayerSetter());
    }

    /**
     * Return True if we have two panes, false otherwise
     *
     * @return boolean
     */
    public boolean isTwoPane() {
        return twoPane;
    }

    public void setTwoPane(boolean twoPane) {
        this.twoPane = twoPane;
    }

    /**
     * Record the selected colis
     *
     * @param colis
     */
    public void setSelectedColis(ColisWithSteps colis) {
        colisWithStepsSelected.postValue(colis);
    }

    /**
     * Return the selected colis
     *
     * @return ColisWithSteps (could be null)
     */
    public LiveData<ColisWithSteps> getSelectedColis() {
        return colisWithStepsSelected;
    }

    public RequestBuilder<PictureDrawable> getGlideRequester() {
        return this.requester;
    }

    /**
     * Return a LiveData containing the List of ALL the colis with their steps in the DB
     *
     * @return LiveData<List<ColisWithSteps>>
     */
    public LiveData<List<ColisWithSteps>> getLiveListActiveColis() {
        return this.colisWithStepsList;
    }

    public void launchSynchroDelete() {
        new SyncTask(getApplication(), SyncTask.TypeSyncTask.DELETE, null).execute();
    }
}
