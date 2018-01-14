package firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task;

import android.os.AsyncTask;

import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.EtapeDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class EtapeRepositoryTask extends AsyncTask<EtapeEntity, Void, Void> {

    private TypeTask typeTask;
    private EtapeDao etapeDao;

    public EtapeRepositoryTask(EtapeDao etapeDao, TypeTask typeTask) {
        this.typeTask = typeTask;
        this.etapeDao = etapeDao;
    }

    @Override
    protected Void doInBackground(EtapeEntity... etapeEntities) {
        switch (this.typeTask) {
            case DELETE:
                etapeDao.delete(etapeEntities);
                break;
            case INSERT:
                etapeDao.insert(etapeEntities);
                break;
            case UPDATE:
                etapeDao.update(etapeEntities);
                break;
        }
        return null;
    }
}
