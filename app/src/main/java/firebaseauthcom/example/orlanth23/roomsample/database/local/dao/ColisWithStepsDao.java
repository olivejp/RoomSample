package firebaseauthcom.example.orlanth23.roomsample.database.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;

/**
 * Created by orlanth23 on 12/01/2018.
 */
@Dao
public interface ColisWithStepsDao {
    @Transaction
    @Query("SELECT * FROM colis")
    LiveData<List<ColisWithSteps>> getLiveColisWithSteps();

    @Transaction
    @Query("SELECT * FROM colis WHERE deleted <> 1")
    LiveData<List<ColisWithSteps>> getLiveActiveColisWithSteps();

    @Transaction
    @Query("SELECT * FROM colis")
    List<ColisWithSteps> getColisWithSteps();

    @Transaction
    @Query("SELECT * FROM colis WHERE deleted <> 1")
    List<ColisWithSteps> getActiveColisWithSteps();
}
