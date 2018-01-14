package firebaseauthcom.example.orlanth23.roomsample.utils;


import android.support.annotation.NonNull;

import firebaseauthcom.example.orlanth23.roomsample.DateConverter;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;
import firebaseauthcom.example.orlanth23.roomsample.job.aftership.Checkpoint;
import firebaseauthcom.example.orlanth23.roomsample.job.opt.EtapeDto;

/**
 * Created by 2761oli on 23/10/2017.
 */

public class EtapeService {

    private EtapeService() {
    }

    public static EtapeEntity convertToEntity(@NonNull EtapeDto dto) {
        EtapeEntity entity = new EtapeEntity();
        entity.setDate(DateConverter.convertDateDtoToEntity(dto.getDate()));
        entity.setCommentaire(dto.getCommentaire());
        entity.setDescription(dto.getDescription());
        entity.setLocalisation(dto.getLocalisation());
        entity.setStatus(dto.getStatus());
        entity.setPays(dto.getPays());
        return entity;
    }

    /**
     * Va créer une étape à partir d'un checkpoint
     *
     * @param idColis
     * @param checkpoint
     * @return EtapeEntity
     */
    public static EtapeEntity createEtapeFromCheckpoint(@NonNull String idColis, @NonNull Checkpoint checkpoint) {
        EtapeEntity etape = new EtapeEntity();
        etape.setIdColis(idColis);
        if (checkpoint.getCheckpointTime() != null) {
            etape.setDate(DateConverter.convertDateAfterShipToEntity(checkpoint.getCheckpointTime()));
        } else {
            etape.setDate(0L);
        }
        etape.setCommentaire("");
        etape.setLocalisation((checkpoint.getLocation() != null) ? checkpoint.getLocation().toString() : "");
        etape.setStatus((checkpoint.getTag() != null) ? checkpoint.getTag() : "");
        etape.setDescription((checkpoint.getMessage() != null) ? checkpoint.getMessage() : "");
        etape.setPays((checkpoint.getCountryName() != null) ? checkpoint.getCountryName().toString() : "");
        return etape;
    }

    /**
     *
     * @param status
     * @return
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
