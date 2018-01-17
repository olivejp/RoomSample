package firebaseauthcom.example.orlanth23.roomsample.database.local.repository;

import android.content.Context;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.ColisDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.local.ColisDatabase;
import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.EtapeDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task.EtapeRepositoryTask;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task.TypeTask.DELETE;
import static firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task.TypeTask.INSERT;
import static firebaseauthcom.example.orlanth23.roomsample.database.local.repository.task.TypeTask.UPDATE;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class EtapeRepository {

    private static EtapeRepository INSTANCE;

    private EtapeDao etapeDao;

    private EtapeRepository(Context context) {
        ColisDatabase db = ColisDatabase.getInstance(context);
        this.etapeDao = db.etapeDao();
    }

    public synchronized static EtapeRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new EtapeRepository(context);
        }
        return INSTANCE;
    }

    public void insert(EtapeEntity... etapeEntities) {
        new EtapeRepositoryTask(this.etapeDao, INSERT).execute(etapeEntities);
    }

    public void insert(List<EtapeEntity> etapeEntities) {
        new EtapeRepositoryTask(this.etapeDao, INSERT).execute((EtapeEntity[]) etapeEntities.toArray());
    }

    public void update(EtapeEntity... etapeEntities) {
        new EtapeRepositoryTask(this.etapeDao, UPDATE).execute(etapeEntities);
    }

    public void delete(EtapeEntity... etapeEntities) {
        new EtapeRepositoryTask(this.etapeDao, DELETE).execute(etapeEntities);
    }

    private Maybe<Integer> count(EtapeEntity etapeEntity) {
        return this.etapeDao.exist(etapeEntity.getIdColis(), etapeEntity.getOrigine().getValue(), etapeEntity.getDate(), etapeEntity.getDescription())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void save(List<EtapeEntity> etapeEntities) {
        for (EtapeEntity etapeEntity : etapeEntities) {
            count(etapeEntity).subscribe(count -> {
                if (count > 0) {
                    update(etapeEntity);
                } else {
                    insert(etapeEntity);
                }
            });
        }
    }
}
