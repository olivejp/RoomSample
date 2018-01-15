package firebaseauthcom.example.orlanth23.roomsample.database.local.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.OptDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.EtapeDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class EtapeRepository {

    private static EtapeRepository INSTANCE;

    private EtapeDao etapeDao;

    private EtapeRepository(Context context) {
        OptDatabase db = OptDatabase.getInstance(context);
        this.etapeDao = db.etapeDao();
    }

    public synchronized static EtapeRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new EtapeRepository(context);
        }
        return INSTANCE;
    }

    public void update(EtapeEntity... etapeEntities) {
        this.etapeDao.update(etapeEntities);
    }

    public void update(List<EtapeEntity> etapeEntities) {
        this.etapeDao.update(etapeEntities);
    }

    public void insert(EtapeEntity... etapeEntities) {
        this.etapeDao.insert(etapeEntities);
    }

    public void insert(List<EtapeEntity> etapeEntities) {
        this.etapeDao.insert(etapeEntities);
    }

    public void delete(EtapeEntity... etapeEntities) {
        this.etapeDao.delete(etapeEntities);
    }

    public LiveData<List<EtapeEntity>> getAllEtapeByIdColis(String idColis) {
        return this.etapeDao.liveListEtapeByIdColis(idColis);
    }

    private boolean exist(EtapeEntity etapeEntity) {
        return (1 <= this.etapeDao.exist(etapeEntity.getIdColis(), etapeEntity.getOrigine().getValue(), etapeEntity.getDate(), etapeEntity.getDescription()));
    }

    public void save(List<EtapeEntity> etapeEntities) {
        for (EtapeEntity etapeEntity : etapeEntities) {
            if (exist(etapeEntity)) {
                this.etapeDao.update(etapeEntity);
            } else {
                this.etapeDao.insert(etapeEntity);
            }
        }
    }
}
