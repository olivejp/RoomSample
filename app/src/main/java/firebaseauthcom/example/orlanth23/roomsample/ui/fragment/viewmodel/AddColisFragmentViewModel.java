package firebaseauthcom.example.orlanth23.roomsample.ui.fragment.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class AddColisFragmentViewModel extends AndroidViewModel {

    private ColisRepository colisRepository;

    public AddColisFragmentViewModel(@NonNull Application application) {
        super(application);
        colisRepository = ColisRepository.getInstance(application);
    }

    public void saveColis(ColisEntity colisEntity) {
        colisRepository.save(colisEntity);
    }
}
