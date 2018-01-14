package firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.drawable.PictureDrawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private MutableLiveData<AtomicBoolean> twoPane;

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

    public LiveData<AtomicBoolean> isTwoPane() {
        return twoPane;
    }

    public void setTwoPane(boolean twoPane) {
        if (this.twoPane == null) {
            this.twoPane = new MutableLiveData<>();
            this.twoPane.setValue(new AtomicBoolean(twoPane));
        } else {
            this.twoPane.postValue(new AtomicBoolean(twoPane));
        }
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
