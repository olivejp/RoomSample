package firebaseauthcom.example.orlanth23.roomsample.database.local.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.OptDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.ColisWithStepsDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class ColisWithStepsRepository {

    private static ColisWithStepsRepository INSTANCE;
    private ColisWithStepsDao colisWithStepsDao;

    private ColisWithStepsRepository(Context context) {
        OptDatabase db = OptDatabase.getInstance(context);
        this.colisWithStepsDao = db.colisWithStepsDao();
    }

    public synchronized static ColisWithStepsRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ColisWithStepsRepository(context);
        }
        return INSTANCE;
    }

    public ColisWithSteps findActiveColisWithStepsByIdColis(String idColis) {
        return this.colisWithStepsDao.findActiveColisWithStepsByIdColis(idColis);
    }

    public LiveData<List<ColisWithSteps>> getAllColisWithSteps() {
        return this.colisWithStepsDao.getLiveColisWithSteps();
    }

    public List<ColisWithSteps> getActiveColisWithSteps() {
        return this.colisWithStepsDao.getActiveColisWithSteps();
    }
}
