package firebaseauthcom.example.orlanth23.roomsample.database.local.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.DateConverter;
import firebaseauthcom.example.orlanth23.roomsample.database.local.OptDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.ColisDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task.ColisRepositoryTask;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task.TypeTask;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class ColisRepository {

    private static ColisRepository INSTANCE;

    private ColisDao colisDao;

    private ColisRepository(Context context) {
        OptDatabase db = OptDatabase.getInstance(context);
        this.colisDao = db.colisDao();
    }

    public synchronized static ColisRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ColisRepository(context);
        }
        return INSTANCE;
    }

    public void update(ColisEntity... colisEntities) {
        new ColisRepositoryTask(colisDao, TypeTask.UPDATE).execute(colisEntities);
    }

    public void updateLastUpdate(ColisEntity... colisEntities) {
        Long now = DateConverter.getNowEntity();
        for (ColisEntity colis : colisEntities) {
            colis.setLastUpdate(now);
        }
        new ColisRepositoryTask(colisDao, TypeTask.UPDATE).execute(colisEntities);
    }

    public void updateLastSuccessfulUpdate(ColisEntity... colisEntities) {
        Long now = DateConverter.getNowEntity();
        for (ColisEntity colis : colisEntities) {
            colis.setLastUpdate(now);
            colis.setLastUpdateSuccessful(now);
        }
        new ColisRepositoryTask(colisDao, TypeTask.UPDATE).execute(colisEntities);
    }

    public void insert(ColisEntity... colisEntities) {
        new ColisRepositoryTask(colisDao, TypeTask.INSERT).execute(colisEntities);
    }

    public void delete(ColisEntity... colisEntities) {
        new ColisRepositoryTask(colisDao, TypeTask.DELETE).execute(colisEntities);
    }

    public void delete(String idColis) {
        new ColisRepositoryTask(colisDao, TypeTask.DELETE).execute(findById(idColis));
    }

    public LiveData<List<ColisEntity>> getLiveAllColis(boolean active) {
        if (active) {
            return this.colisDao.liveListColisActifs();
        } else {
            return this.colisDao.liveListColisSupprimes();
        }
    }

    public List<ColisEntity> getAllColis(boolean active) {
        if (active) {
            return this.colisDao.listColisActifs();
        } else {
            return this.colisDao.listColisSupprimes();
        }
    }

    public boolean exist(String idColis) {
        return (1 <= this.colisDao.exist(idColis));
    }

    public void save(ColisEntity... colisEntities) {
        for (ColisEntity colisEntity : colisEntities) {
            if (exist(colisEntity.getIdColis())) {
                this.colisDao.update(colisEntity);
            } else {
                this.colisDao.insert(colisEntity);
            }
        }
    }

    public ColisEntity findById(String idColis) {
        return this.colisDao.findById(idColis);
    }
}
