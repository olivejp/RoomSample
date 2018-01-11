package firebaseauthcom.example.orlanth23.roomsample.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisEntity;


/**
 * Created by orlanth23 on 10/01/2018.
 */
@Dao
public interface ColisDao extends AbstractDao<ColisEntity>{
    @Query("SELECT * FROM COLIS")
    List<ColisEntity> listAllColis();

    @Query("SELECT * FROM COLIS WHERE DELETED <> '1'")
    List<ColisEntity> listColisActifs();

    @Query("SELECT * FROM COLIS WHERE DELETED = '1'")
    List<ColisEntity> listColisSupprimes();

    @Query("SELECT * FROM COLIS")
    LiveData<List<ColisEntity>> liveListAllColis();

    @Query("SELECT * FROM COLIS WHERE DELETED <> '1'")
    LiveData<List<ColisEntity>> liveListColisActifs();

    @Query("SELECT * FROM COLIS WHERE DELETED = '1'")
    LiveData<List<ColisEntity>> liveListColisSupprimes();
}
