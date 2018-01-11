package firebaseauthcom.example.orlanth23.roomsample.activity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.repository.ColisRepository;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class MainActivityViewModel extends AndroidViewModel {

    private ColisRepository colisRepository;
    private LiveData<List<ColisEntity>> listColis;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        colisRepository = ColisRepository.getInstance(application);
        listColis = colisRepository.getAllActiveColis();
    }

    public LiveData<List<ColisEntity>> getListColis() {
        return listColis;
    }

    public void insert(ColisEntity colisEntity) {
        colisRepository.insert(colisEntity);
    }
}
