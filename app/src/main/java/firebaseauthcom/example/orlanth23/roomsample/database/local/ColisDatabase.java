package firebaseauthcom.example.orlanth23.roomsample.database.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.ColisDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.ColisWithStepsDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.dao.EtapeDao;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.EtapeEntity;

/**
 * Created by orlanth23 on 01/07/2017.
 */

@Database(version = 28, entities = {ColisEntity.class, EtapeEntity.class})
public abstract class ColisDatabase extends RoomDatabase {
    private static ColisDatabase INSTANCE;

    public synchronized static ColisDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ColisDatabase.class, "colis-database").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    abstract public ColisDao colisDao();
    abstract public EtapeDao etapeDao();
    abstract public ColisWithStepsDao colisWithStepsDao();
}