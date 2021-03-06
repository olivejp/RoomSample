package firebaseauthcom.example.orlanth23.roomsample.mapper;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.StepEntity;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.Checkpoint;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.TrackingData;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.ColisDto;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.EtapeDto;

import static firebaseauthcom.example.orlanth23.roomsample.mapper.EtapeMapper.createEtapeFromCheckpoint;

/**
 * Created by 2761oli on 23/10/2017.
 */

public class ColisMapper {

    private static final String TAG = ColisMapper.class.getName();

    private ColisMapper() {
    }

    /**
     * Create a new ColisWithSteps
     * Fill the ColisEntity with the {@link ColisDto} informations.
     *
     * @param dto
     * @return {@link ColisWithSteps}
     */
    public static ColisWithSteps convertToActiveEntity(ColisDto dto, ColisWithSteps result) {
        Log.d(TAG, "(convertToActiveEntity)");
        if (dto.getEtapeDtoArrayList() != null && !dto.getEtapeDtoArrayList().isEmpty()) {
            List<StepEntity> listStepEntity = new ArrayList<>();
            for (EtapeDto etapeDto : dto.getEtapeDtoArrayList()) {
                listStepEntity.add(EtapeMapper.convertToEntity(dto.getIdColis(), etapeDto));
            }
            result.stepEntityList = listStepEntity;
        }
        return result;
    }

    /**
     * Fill the ColisWithSteps with the TrackingData informations.
     *
     * @param resultColis
     * @param trackingData
     * @return {@link ColisWithSteps}
     */
    public static ColisWithSteps convertTrackingDataToEntity(final ColisWithSteps resultColis, TrackingData trackingData) {
        Log.d(TAG, "(convertTrackingDataToEntity)");
        resultColis.colisEntity.setAfterShipId(trackingData.getId());
        for (Checkpoint checkpoint : trackingData.getCheckpoints()) {
            Log.d(TAG, "(createEtapeFromCheckpoint)" + checkpoint.toString());
            StepEntity stepEntity = createEtapeFromCheckpoint(resultColis.colisEntity.getIdColis(), checkpoint);
            resultColis.stepEntityList.add(stepEntity);
        }
        return resultColis;
    }
}
