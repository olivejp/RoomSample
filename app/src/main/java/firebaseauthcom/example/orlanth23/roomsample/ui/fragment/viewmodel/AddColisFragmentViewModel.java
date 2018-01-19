package firebaseauthcom.example.orlanth23.roomsample.ui.fragment.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.job.SyncTask;
import io.reactivex.Single;

import static firebaseauthcom.example.orlanth23.roomsample.job.SyncTask.TypeSyncTask.SOLO;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class AddColisFragmentViewModel extends AndroidViewModel {

    private ColisRepository colisRepository;

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
        colisRepository.insert(colisEntity);
    }

    public Single<ColisEntity> findById(String idColis) {
        return ColisRepository.getInstance(getApplication()).findById(idColis);
    }
}
