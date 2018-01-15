package firebaseauthcom.example.orlanth23.roomsample.mapper;


import android.support.annotation.NonNull;

import firebaseauthcom.example.orlanth23.roomsample.DateConverter;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.Checkpoint;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.EtapeDto;

/**
 * Created by 2761oli on 23/10/2017.
 */

public class EtapeMapper {

    private EtapeMapper() {
    }

    /**
     * Convert an {@link EtapeDto} to an {@link EtapeEntity}
     *
     * @param idColis Foreign key to {@link firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity}
     * @param etapeDto to transform
     * @return {@link EtapeEntity}
     */
    static EtapeEntity convertToEntity(@NonNull String idColis, @NonNull EtapeDto etapeDto) {
        EtapeEntity etapeEntity = new EtapeEntity();
        etapeEntity.setIdColis(idColis);
        etapeEntity.setDate(DateConverter.convertDateDtoToEntity(etapeDto.getDate()));
        etapeEntity.setCommentaire(etapeDto.getCommentaire());
        etapeEntity.setDescription(etapeDto.getDescription());
        etapeEntity.setLocalisation(etapeDto.getLocalisation());
        etapeEntity.setStatus(etapeDto.getStatus());
        etapeEntity.setPays(etapeDto.getPays());
        etapeEntity.setOrigine(EtapeEntity.EtapeOrigine.OPT);
        return etapeEntity;
    }

    /**
     * Convert an {@link Checkpoint} to an {@link EtapeEntity}
     *
     * @param idColis Foreign key to {@link firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity}
     * @param checkpoint to transform
     * @return {@link EtapeEntity}
     */
    static EtapeEntity createEtapeFromCheckpoint(@NonNull String idColis, @NonNull Checkpoint checkpoint) {
        EtapeEntity etapeEntity = new EtapeEntity();
        etapeEntity.setIdColis(idColis);
        if (checkpoint.getCheckpointTime() != null) {
            etapeEntity.setDate(DateConverter.convertDateAfterShipToEntity(checkpoint.getCheckpointTime()));
        } else {
            etapeEntity.setDate(0L);
        }
        etapeEntity.setCommentaire("");
        etapeEntity.setLocalisation((checkpoint.getLocation() != null) ? checkpoint.getLocation().toString() : "");
        etapeEntity.setStatus((checkpoint.getTag() != null) ? checkpoint.getTag() : "");
        etapeEntity.setDescription((checkpoint.getMessage() != null) ? checkpoint.getMessage() : "");
        etapeEntity.setPays((checkpoint.getCountryName() != null) ? checkpoint.getCountryName().toString() : "");
        etapeEntity.setOrigine(EtapeEntity.EtapeOrigine.AFTER_SHIP);
        return etapeEntity;
    }

    /**
     * Return Drawable correponding on the step status
     * @param status
     * @return {@link android.support.annotation.DrawableRes}
     */
    public static int getStatusDrawable(@NonNull String status) {
        switch (status) {
            case "InfoReceived":
                return R.drawable.ic_status_info_receive;
            case "AttemptFail":
                return R.drawable.ic_status_attemptfail;
            case "Delivered":
                return R.drawable.ic_status_delivered;
            case "Exception":
                return R.drawable.ic_status_exception;
            case "Expired":
                return R.drawable.ic_status_expired;
            case "InTransit":
                return R.drawable.ic_status_in_transit;
            case "OutForDelivery":
                return R.drawable.ic_status_out_for_delivery;
            case "Pending":
                return R.drawable.ic_status_pending;
            default:
                return R.drawable.ic_status_pending;
        }
    }
}
