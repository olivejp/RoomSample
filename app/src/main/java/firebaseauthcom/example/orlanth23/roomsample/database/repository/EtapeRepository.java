package firebaseauthcom.example.orlanth23.roomsample.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.OptDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.dao.EtapeDao;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.EtapeEntity;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class EtapeRepository {

    private static EtapeRepository INSTANCE;

    private EtapeDao etapeDao;

    private EtapeRepository(Application application) {
        OptDatabase db = OptDatabase.getInstance(application);
        this.etapeDao = db.etapeDao();
    }

    public synchronized static EtapeRepository getInstance(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new EtapeRepository(application);
        }
        return INSTANCE;
    }

    public void update(EtapeEntity... etapeEntities) {
        this.etapeDao.update(etapeEntities);
    }

    public void insert(EtapeEntity... etapeEntities) {
        this.etapeDao.insert(etapeEntities);
    }

    public void delete(EtapeEntity... etapeEntities) {
        this.etapeDao.delete(etapeEntities);
    }

    public LiveData<List<EtapeEntity>> getAllEtapeByIdColis(String idColis) {
        return this.etapeDao.liveListEtapeByIdColis(idColis);
    }
}
