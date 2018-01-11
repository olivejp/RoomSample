package firebaseauthcom.example.orlanth23.roomsample.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.OptDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.dao.ColisDao;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.repository.task.ColisRepositoryTask;
import firebaseauthcom.example.orlanth23.roomsample.database.repository.task.TypeTask;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class ColisRepository {

    private static ColisRepository INSTANCE;

    private ColisDao colisDao;

    private ColisRepository(Application application) {
        OptDatabase db = OptDatabase.getInstance(application);
        this.colisDao = db.colisDao();
    }

    public synchronized static ColisRepository getInstance(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new ColisRepository(application);
        }
        return INSTANCE;
    }

    public void update(ColisEntity... colisEntities) {
        new ColisRepositoryTask(colisDao, TypeTask.UPDATE).execute(colisEntities);
    }

    public void insert(ColisEntity... colisEntities) {
        new ColisRepositoryTask(colisDao, TypeTask.INSERT).execute(colisEntities);
    }

    public void delete(ColisEntity... colisEntities) {
        new ColisRepositoryTask(colisDao, TypeTask.DELETE).execute(colisEntities);
    }

    public LiveData<List<ColisEntity>> getAllActiveColis() {
        return this.colisDao.liveListColisActifs();
    }
}
