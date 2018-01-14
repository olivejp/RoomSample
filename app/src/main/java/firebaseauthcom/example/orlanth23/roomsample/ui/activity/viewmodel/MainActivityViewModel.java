package firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.repository.ColisWithStepsRepository;
import firebaseauthcom.example.orlanth23.roomsample.glide.GlideApp;
import firebaseauthcom.example.orlanth23.roomsample.glide.SvgSoftwareLayerSetter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class MainActivityViewModel extends AndroidViewModel {

    private ColisWithStepsRepository colisWithStepsRepository;
    private ColisRepository colisRepository;
    private LiveData<List<ColisWithSteps>> listColis;
    private RequestBuilder<PictureDrawable> requester;
    private ColisWithSteps colisWithStepsSelected;
    private boolean twoPane;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        colisWithStepsRepository = ColisWithStepsRepository.getInstance(application);
        colisRepository = ColisRepository.getInstance(application);
        listColis = colisWithStepsRepository.getAllColisWithSteps();
        requester = GlideApp.with(application)
                .as(PictureDrawable.class)
                .placeholder(R.drawable.ic_archive_grey_900_48dp)
                .error(R.drawable.ic_archive_grey_900_48dp)
                .transition(withCrossFade())
                .listener(new SvgSoftwareLayerSetter());
    }

    public boolean isTwoPane() {
        return twoPane;
    }

    public void setTwoPane(boolean twoPane) {
        this.twoPane = twoPane;
    }

    public void setSelectedColis(ColisWithSteps colis) {
        this.colisWithStepsSelected = colis;
    }

    public ColisWithSteps getSelectedColis() {
        return this.colisWithStepsSelected;
    }

    public RequestBuilder<PictureDrawable> getGlideRequester() {
        return this.requester;
    }

    public LiveData<List<ColisWithSteps>> getListColis() {
        return listColis;
    }

    public void insert(ColisEntity colisEntity) {
        colisRepository.insert(colisEntity);
    }
}
