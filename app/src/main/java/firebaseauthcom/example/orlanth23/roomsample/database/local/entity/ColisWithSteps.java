package firebaseauthcom.example.orlanth23.roomsample.database.local.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orlanth23 on 12/01/2018.
 */

public class ColisWithSteps {
    @Embedded
    public ColisEntity colisEntity;

    @Relation(parentColumn = "idColis", entityColumn = "idColis")
    public List<StepEntity> stepEntityList;

    public ColisWithSteps() {
        this.colisEntity = new ColisEntity();
        this.stepEntityList = new ArrayList<>();
    }
}
