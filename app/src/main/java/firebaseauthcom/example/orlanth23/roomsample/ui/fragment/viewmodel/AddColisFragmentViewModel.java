package firebaseauthcom.example.orlanth23.roomsample.ui.fragment.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.job.SyncTask;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

import static firebaseauthcom.example.orlanth23.roomsample.job.SyncTask.TypeSyncTask.SOLO;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class AddColisFragmentViewModel extends AndroidViewModel {

    private ColisRepository colisRepository;
    private MutableLiveData<String> idColis = new MutableLiveData<>();

    public AddColisFragmentViewModel(@NonNull Application application) {
        super(application);
        colisRepository = ColisRepository.getInstance(application);
    }

    // Launch asyncTask to query the server
    public void syncColis(String idColis) {
        new SyncTask(getApplication(), SOLO, idColis).execute();
    }

    public void deleteColis(String idColis) {
        colisRepository.delete(idColis);
    }

    public void saveColis(String idColis, String description) {
        ColisEntity colisEntity = new ColisEntity();
        colisEntity.setIdColis(idColis);
        colisEntity.setDescription(description);
        colisEntity.setDeleted(0);
        colisEntity.setDelivered(0);
        colisRepository.insert(colisEntity);
    }

    public Maybe<ColisEntity> findBy(String idColis) {
        return ColisRepository.getInstance(getApplication()).findById(idColis)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public LiveData<String> getIdColis() {
        return idColis;
    }

    public void setIdColis(String idColis) {
        this.idColis.postValue(idColis);
    }
}
