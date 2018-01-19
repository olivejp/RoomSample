package firebaseauthcom.example.orlanth23.roomsample.database.local.repository;

import android.content.Context;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.ColisDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.ColisWithStepsDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class ColisWithStepsRepository {

    private static ColisWithStepsRepository INSTANCE;
    private ColisWithStepsDao colisWithStepsDao;

    private ColisWithStepsRepository(Context context) {
        ColisDatabase db = ColisDatabase.getInstance(context);
        this.colisWithStepsDao = db.colisWithStepsDao();
    }

    public synchronized static ColisWithStepsRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ColisWithStepsRepository(context);
        }
        return INSTANCE;
    }

    public Maybe<ColisWithSteps> findActiveColisWithStepsByIdColis(String idColis) {
        return this.colisWithStepsDao.findMaybeActiveColisWithStepsByIdColis(idColis)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<List<ColisWithSteps>> getActiveColisWithSteps() {
        return this.colisWithStepsDao.getMaybeActiveColisWithSteps()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<ColisWithSteps>> getActiveFlowableColisWithSteps() {
        return this.colisWithStepsDao.getFlowableActiveColisWithSteps();
    }
}
