package firebaseauthcom.example.orlanth23.roomsample.utils;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.Checkpoint;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.TrackingData;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.ColisDto;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.EtapeDto;

import static firebaseauthcom.example.orlanth23.roomsample.utils.EtapeService.createEtapeFromCheckpoint;

/**
 * Created by 2761oli on 23/10/2017.
 */

public class ColisService {

    private static final String TAG = ColisService.class.getName();

    private ColisService() {
    }

    /**
     * @param dto
     * @return
     */
    public static ColisEntity convertToEntity(ColisDto dto) {
        Log.d(TAG, "(convertToEntity)");
        ColisEntity entity = new ColisEntity();
        entity.setIdColis(dto.getIdColis());
        if (dto.getEtapeDtoArrayList() != null && !dto.getEtapeDtoArrayList().isEmpty()) {
            List<EtapeEntity> listEtapeEntity = new ArrayList<>();
            for (EtapeDto etapeDto : dto.getEtapeDtoArrayList()) {
                listEtapeEntity.add(EtapeService.convertToEntity(etapeDto));
            }
            entity.setEtapeAcheminementArrayList(listEtapeEntity);
        }
        return entity;
    }

    /**
     * Fill the ColisEntity with the TrackingData informations.
     *
     * @param colis
     * @param trackingData
     * @return
     */
    public static ColisEntity convertTrackingDataToEntity(ColisEntity colis, TrackingData trackingData) {
        Log.d(TAG, "(convertTrackingDataToEntity)");
        colis.setDeleted(0);
        for (Checkpoint c : trackingData.getCheckpoints()) {
            EtapeEntity etapeEntity = createEtapeFromCheckpoint(colis.getIdColis(), c);
            colis.getEtapeAcheminementArrayList().add(etapeEntity);
        }
        return colis;
    }
}
