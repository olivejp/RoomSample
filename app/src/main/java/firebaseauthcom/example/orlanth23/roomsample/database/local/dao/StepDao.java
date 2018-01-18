package firebaseauthcom.example.orlanth23.roomsample.database.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.StepEntity;
import io.reactivex.Flowable;
import io.reactivex.Maybe;


/**
 * Created by orlanth23 on 10/01/2018.
 */
@Dao
public interface StepDao extends AbstractDao<StepEntity>{
    @Query("SELECT COUNT(*) FROM  etape WHERE idColis = :idColis AND origine = :origine AND date = :date AND description = :description")
    Maybe<Integer> exist(String idColis, String origine, Long date, String description);

    @Query("SELECT * FROM etape WHERE idColis = :idColis ORDER BY date, idEtapeAcheminement")
    Flowable<List<StepEntity>> flowableListStepsOrderedByIdColis(String idColis);
}

