package firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisWithStepsRepository;
import firebaseauthcom.example.orlanth23.roomsample.ui.glide.GlideApp;
import firebaseauthcom.example.orlanth23.roomsample.ui.glide.SvgSoftwareLayerSetter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class MainActivityViewModel extends AndroidViewModel {

    private ColisWithStepsRepository colisWithStepsRepository;
    private LiveData<List<ColisWithSteps>> liveListActiveColis;
    private RequestBuilder<PictureDrawable> requester;
    private ColisWithSteps colisWithStepsSelected;
    private boolean twoPane;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        colisWithStepsRepository = ColisWithStepsRepository.getInstance(application);
        liveListActiveColis = colisWithStepsRepository.getAllActiveColisWithSteps();
        requester = GlideApp.with(application)
                .as(PictureDrawable.class)
                .placeholder(R.drawable.ic_archive_grey_900_48dp)
                .error(R.drawable.ic_archive_grey_900_48dp)
                .transition(withCrossFade())
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
        this.colisWithStepsSelected = colis;
    }

    /**
     * Return the selected colis
     *
     * @return ColisWithSteps (could be null)
     */
    public ColisWithSteps getSelectedColis() {
        return this.colisWithStepsSelected;
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
        return liveListActiveColis;
    }

}
