package firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.broadcast.NetworkReceiver;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.StepEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisWithStepsRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.StepRepository;
import firebaseauthcom.example.orlanth23.roomsample.job.SyncTask;
import firebaseauthcom.example.orlanth23.roomsample.ui.glide.GlideApp;
import firebaseauthcom.example.orlanth23.roomsample.ui.glide.SvgSoftwareLayerSetter;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class MainActivityViewModel extends AndroidViewModel {

    private ColisWithStepsRepository colisWithStepsRepository;
    private StepRepository stepRepository;
    private RequestBuilder<PictureDrawable> requester;
    private MutableLiveData<ColisWithSteps> colisWithStepsSelected = new MutableLiveData<>();
    private MutableLiveData<List<ColisWithSteps>> colisWithStepsList = new MutableLiveData<>();
    private String idColisSelected;
    private boolean twoPane;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        colisWithStepsRepository = ColisWithStepsRepository.getInstance(application);
        stepRepository = StepRepository.getInstance(application);
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
        if (colis != null && colis.colisEntity != null) {
            idColisSelected = colis.colisEntity.getIdColis();
        }
    }

    public String getSelectedIdColis() {
        return idColisSelected;
    }

    public LiveData<List<StepEntity>> liveListStepsOrderedByIdColis(String idColis) {
        return stepRepository.liveListStepsOrderedByIdColis(idColis);
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

    private void launchSyncTask(SyncTask.TypeSyncTask type, @Nullable String idColis) {
        new SyncTask(getApplication(), type, idColis).execute();
    }

    /**
     * Tag the colis to deleted = 1 if it had a link to Firebase or AfterShip
     * Delete it from the DB otherwise
     *
     * @param colisEntity
     */
    public void markAsDeleted(ColisEntity colisEntity) {
        if (!ColisRepository.getInstance(getApplication()).markAsDeleted(colisEntity)) {
            launchSyncTask(SyncTask.TypeSyncTask.DELETE, null);
        }
    }

    public void refresh() {
        if (NetworkReceiver.checkConnection(getApplication())) {
            if (colisWithStepsSelected.getValue() != null) {
                launchSyncTask(SyncTask.TypeSyncTask.SOLO, colisWithStepsSelected.getValue().colisEntity.getIdColis());
            } else {
                launchSyncTask(SyncTask.TypeSyncTask.ALL, null);
            }
        }
    }
}
