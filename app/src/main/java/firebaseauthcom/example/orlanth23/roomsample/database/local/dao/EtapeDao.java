package firebaseauthcom.example.orlanth23.roomsample.database.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;
import io.reactivex.Maybe;


/**
 * Created by orlanth23 on 10/01/2018.
 */
@Dao
public interface EtapeDao extends AbstractDao<EtapeEntity>{
    @Query("SELECT COUNT(*) FROM  etape WHERE idColis = :idColis AND origine = :origine AND date = :date AND description = :description")
    Maybe<Integer> exist(String idColis, String origine, Long date, String description);
}

