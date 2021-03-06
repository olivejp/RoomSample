package firebaseauthcom.example.orlanth23.roomsample.database.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import io.reactivex.Maybe;


/**
 * Created by orlanth23 on 10/01/2018.
 */
@Dao
public interface ColisDao extends AbstractDao<ColisEntity>{

    @Query("SELECT COUNT(*) FROM colis WHERE idColis = :idColis")
    Maybe<Integer> count(String idColis);

    @Query("SELECT * FROM colis WHERE DELETED <> '1'")
    Maybe<List<ColisEntity>> listMaybeColisActifs();

    @Query("SELECT * FROM colis WHERE DELETED = '1'")
    Maybe<List<ColisEntity>> listMaybeColisSupprimes();

    @Query("SELECT * FROM colis WHERE idColis = :idColis")
    Maybe<ColisEntity> findById(String idColis);

    @Query("SELECT * FROM colis WHERE DELETED <> '1' AND DELIVERED <> '1'")
    Maybe<List<ColisEntity>> listMaybeColisActifsAndNotDelivered();
}
