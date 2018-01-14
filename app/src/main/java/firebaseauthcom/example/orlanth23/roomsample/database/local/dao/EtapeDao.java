package firebaseauthcom.example.orlanth23.roomsample.database.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;


/**
 * Created by orlanth23 on 10/01/2018.
 */
@Dao
public interface EtapeDao extends AbstractDao<EtapeEntity>{
    @Query("SELECT * FROM ETAPE WHERE idColis = :idColis")
    List<EtapeEntity> listEtapeByIdColis(String idColis);

    @Query("SELECT * FROM ETAPE WHERE idColis = :idColis")
    LiveData<List<EtapeEntity>> liveListEtapeByIdColis(String idColis);

    @Query("SELECT * FROM ETAPE WHERE idColis = :idColis AND origine = :origine")
    List<EtapeEntity> listEtapeByIdColisAndOrigine(String idColis, String origine);

    @Query("SELECT * FROM ETAPE WHERE idColis = :idColis AND origine = :origine")
    LiveData<List<EtapeEntity>> liveListEtapeByIdColisAndOrigine(String idColis, String origine);
}

