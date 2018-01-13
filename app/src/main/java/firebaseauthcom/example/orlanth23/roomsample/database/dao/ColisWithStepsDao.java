package firebaseauthcom.example.orlanth23.roomsample.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisWithSteps;

/**
 * Created by orlanth23 on 12/01/2018.
 */
@Dao
public interface ColisWithStepsDao {
    @Transaction
    @Query("SELECT * FROM colis")
    public LiveData<List<ColisWithSteps>> getColisWithSteps();
}
