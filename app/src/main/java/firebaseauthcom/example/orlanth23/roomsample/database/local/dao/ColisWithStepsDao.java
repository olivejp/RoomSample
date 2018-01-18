package firebaseauthcom.example.orlanth23.roomsample.database.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Created by orlanth23 on 12/01/2018.
 */
@Dao
public interface ColisWithStepsDao {
    @Transaction
    @Query("SELECT * FROM colis WHERE idColis = :idColis AND deleted <> 1")
    Maybe<ColisWithSteps> findMaybeActiveColisWithStepsByIdColis(String idColis);

    @Transaction
    @Query("SELECT * FROM colis WHERE deleted <> 1")
    Maybe<List<ColisWithSteps>> getMaybeActiveColisWithSteps();

    @Transaction
    @Query("SELECT * FROM colis WHERE deleted <> 1")
    Flowable<List<ColisWithSteps>> getFlowableActiveColisWithSteps();

}
