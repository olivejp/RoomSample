package firebaseauthcom.example.orlanth23.roomsample.database.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;


/**
 * Created by orlanth23 on 10/01/2018.
 */
@Dao
public interface ColisDao extends AbstractDao<ColisEntity>{

    @Query("SELECT COUNT(*) FROM colis WHERE idColis = :idColis")
    int exist(String idColis);

    @Query("SELECT * FROM colis")
    List<ColisEntity> listAllColis();

    @Query("SELECT * FROM colis WHERE DELETED <> '1'")
    List<ColisEntity> listColisActifs();

    @Query("SELECT * FROM colis WHERE DELETED = '1'")
    List<ColisEntity> listColisSupprimes();

    @Query("SELECT * FROM colis")
    LiveData<List<ColisEntity>> liveListAllColis();

    @Query("SELECT * FROM colis WHERE DELETED <> '1'")
    LiveData<List<ColisEntity>> liveListColisActifs();

    @Query("SELECT * FROM colis WHERE DELETED = '1'")
    LiveData<List<ColisEntity>> liveListColisSupprimes();

    @Query("SELECT * FROM colis WHERE idColis = :idColis")
    ColisEntity findById(String idColis);
}
