package firebaseauthcom.example.orlanth23.roomsample.database.repository.task;

import android.os.AsyncTask;

import firebaseauthcom.example.orlanth23.roomsample.database.dao.AbstractDao;

/**
 * Created by orlanth23 on 12/01/2018.
 */

public class AbstractRepositoryTask<T> extends AsyncTask<T, Void, Void> {

    private TypeTask typeTask;
    private AbstractDao<T> dao;

    public AbstractRepositoryTask(AbstractDao<T> dao, TypeTask typeTask) {
        this.typeTask = typeTask;
        this.dao = dao;
    }

    @Override
    protected Void doInBackground(T[] ts) {
        switch (this.typeTask) {
            case DELETE:
                dao.delete(ts);
                break;
            case INSERT:
                dao.insert(ts);
                break;
            case UPDATE:
                dao.update(ts);
                break;
        }
        return null;
    }
}
