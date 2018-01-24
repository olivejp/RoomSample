package nc.opt.mobile.optmobile.database.local.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import nc.opt.mobile.optmobile.database.local.StepOrigine;
import nc.opt.mobile.optmobile.database.local.dao.StepDao;
import nc.opt.mobile.optmobile.database.local.entity.StepEntity;
import nc.opt.mobile.optmobile.database.local.repository.task.EtapeRepositoryTask;
import nc.opt.mobile.optmobile.database.local.ColisDatabase;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static nc.opt.mobile.optmobile.database.local.repository.task.TypeTask.DELETE;
import static nc.opt.mobile.optmobile.database.local.repository.task.TypeTask.INSERT;
import static nc.opt.mobile.optmobile.database.local.repository.task.TypeTask.UPDATE;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public class StepRepository {

    private static StepRepository INSTANCE;

    private StepDao stepDao;

    private StepRepository(Context context) {
        ColisDatabase db = ColisDatabase.getInstance(context);
        this.stepDao = db.stepDao();
    }

    public static synchronized StepRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new StepRepository(context);
        }
        return INSTANCE;
    }

    public LiveData<List<StepEntity>> liveListStepsOrderedByIdColis(String idColis) {
        return this.stepDao.liveListStepsOrderedByIdColis(idColis);
    }

    public LiveData<List<StepEntity>> liveListStepsOrderedByIdColisAndOrigine(String idColis, StepOrigine origine) {
        return this.stepDao.liveListStepsOrderedByIdColisAndOrigine(idColis, origine.getValue());
    }

    public void insert(StepEntity... stepEntities) {
        new EtapeRepositoryTask(this.stepDao, INSERT).execute(stepEntities);
    }

    public void insert(List<StepEntity> stepEntities) {
        new EtapeRepositoryTask(this.stepDao, INSERT).execute((StepEntity[]) stepEntities.toArray());
    }

    public void update(StepEntity... stepEntities) {
        new EtapeRepositoryTask(this.stepDao, UPDATE).execute(stepEntities);
    }

    public void delete(StepEntity... stepEntities) {
        new EtapeRepositoryTask(this.stepDao, DELETE).execute(stepEntities);
    }

    private Maybe<Integer> count(StepEntity stepEntity) {
        return this.stepDao.exist(stepEntity.getIdColis(), stepEntity.getOrigine().getValue(), stepEntity.getDate(), stepEntity.getDescription())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void save(List<StepEntity> stepEntities) {
        for (StepEntity stepEntity : stepEntities) {
            count(stepEntity).subscribe(count -> {
                if (count > 0) {
                    update(stepEntity);
                } else {
                    insert(stepEntity);
                }
            });
        }
    }
}
