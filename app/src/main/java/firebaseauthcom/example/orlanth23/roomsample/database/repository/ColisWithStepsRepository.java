package firebaseauthcom.example.orlanth23.roomsample.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.OptDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.dao.ColisWithStepsDao;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisWithSteps;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class ColisWithStepsRepository {

    private static ColisWithStepsRepository INSTANCE;

    private ColisWithStepsDao colisWithStepsDao;

    private ColisWithStepsRepository(Application application) {
        OptDatabase db = OptDatabase.getInstance(application);
        this.colisWithStepsDao = db.colisWithStepsDao();
    }

    public synchronized static ColisWithStepsRepository getInstance(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new ColisWithStepsRepository(application);
        }
        return INSTANCE;
    }

    public LiveData<List<ColisWithSteps>> getAllColisWithSteps() {
        return this.colisWithStepsDao.getColisWithSteps();
    }
}
