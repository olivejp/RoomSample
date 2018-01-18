package firebaseauthcom.example.orlanth23.roomsample.database.local.entity;

import android.arch.persistence.room.TypeConverter;

import firebaseauthcom.example.orlanth23.roomsample.database.local.StepOrigine;

import static firebaseauthcom.example.orlanth23.roomsample.database.local.StepOrigine.AFTER_SHIP;
import static firebaseauthcom.example.orlanth23.roomsample.database.local.StepOrigine.OPT;


/**
 * Created by orlanth23 on 10/01/2018.
 */

public class OrigineConverter {
    @TypeConverter
    public static StepOrigine getValue(String value) {
        if (value.equals(AFTER_SHIP.getValue())) {
            return AFTER_SHIP;
        } else if (value.equals(OPT.getValue())) {
            return OPT;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static String toValue(StepOrigine origine) {
        return origine.getValue();
    }
}
