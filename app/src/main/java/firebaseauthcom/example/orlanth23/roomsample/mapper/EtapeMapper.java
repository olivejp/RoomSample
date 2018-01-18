package firebaseauthcom.example.orlanth23.roomsample.mapper;


import android.support.annotation.NonNull;

import firebaseauthcom.example.orlanth23.roomsample.DateConverter;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.StepOrigine;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.StepEntity;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.Checkpoint;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.EtapeDto;

/**
 * Created by 2761oli on 23/10/2017.
 */

public class EtapeMapper {

    private EtapeMapper() {
    }

    /**
     * Convert an {@link EtapeDto} to an {@link StepEntity}
     *
     * @param idColis Foreign key to {@link firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity}
     * @param etapeDto to transform
     * @return {@link StepEntity}
     */
    static StepEntity convertToEntity(@NonNull String idColis, @NonNull EtapeDto etapeDto) {
        StepEntity stepEntity = new StepEntity();
        stepEntity.setIdColis(idColis);
        stepEntity.setDate(DateConverter.convertDateDtoToEntity(etapeDto.getDate()));
        stepEntity.setCommentaire(etapeDto.getCommentaire());
        stepEntity.setDescription(etapeDto.getDescription());
        stepEntity.setLocalisation(etapeDto.getLocalisation());
        stepEntity.setStatus(etapeDto.getStatus());
        stepEntity.setPays(etapeDto.getPays());
        stepEntity.setOrigine(StepOrigine.OPT);
        return stepEntity;
    }

    /**
     * Convert an {@link Checkpoint} to an {@link StepEntity}
     *
     * @param idColis Foreign key to {@link firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity}
     * @param checkpoint to transform
     * @return {@link StepEntity}
     */
    static StepEntity createEtapeFromCheckpoint(@NonNull String idColis, @NonNull Checkpoint checkpoint) {
        StepEntity stepEntity = new StepEntity();
        stepEntity.setIdColis(idColis);
        if (checkpoint.getCheckpointTime() != null) {
            stepEntity.setDate(DateConverter.convertDateAfterShipToEntity(checkpoint.getCheckpointTime()));
        } else {
            stepEntity.setDate(0L);
        }
        stepEntity.setCommentaire("");
        stepEntity.setLocalisation((checkpoint.getLocation() != null) ? checkpoint.getLocation().toString() : "");
        stepEntity.setStatus((checkpoint.getTag() != null) ? checkpoint.getTag() : "");
        stepEntity.setDescription((checkpoint.getMessage() != null) ? checkpoint.getMessage() : "");
        stepEntity.setPays((checkpoint.getCountryName() != null) ? checkpoint.getCountryName().toString() : "");
        stepEntity.setOrigine(StepOrigine.AFTER_SHIP);
        return stepEntity;
    }
}
