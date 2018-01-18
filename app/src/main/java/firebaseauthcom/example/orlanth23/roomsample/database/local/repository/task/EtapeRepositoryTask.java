package firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task;

import android.os.AsyncTask;

import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.StepDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.StepEntity;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class EtapeRepositoryTask extends AsyncTask<StepEntity, Void, Void> {

    private TypeTask typeTask;
    private StepDao stepDao;

    public EtapeRepositoryTask(StepDao stepDao, TypeTask typeTask) {
        this.typeTask = typeTask;
        this.stepDao = stepDao;
    }

    @Override
    protected Void doInBackground(StepEntity... etapeEntities) {
        switch (this.typeTask) {
            case DELETE:
                stepDao.delete(etapeEntities);
                break;
            case INSERT:
                stepDao.insert(etapeEntities);
                break;
            case UPDATE:
                stepDao.update(etapeEntities);
                break;
        }
        return null;
    }
}
