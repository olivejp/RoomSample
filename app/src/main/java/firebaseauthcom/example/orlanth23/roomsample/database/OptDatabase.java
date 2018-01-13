package firebaseauthcom.example.orlanth23.roomsample.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import firebaseauthcom.example.orlanth23.roomsample.database.dao.ColisDao;
import firebaseauthcom.example.orlanth23.roomsample.database.dao.ColisWithStepsDao;
import firebaseauthcom.example.orlanth23.roomsample.database.dao.EtapeDao;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.EtapeEntity;

/**
 * Created by orlanth23 on 01/07/2017.
 */

@Database(version = 24, entities = {ColisEntity.class, EtapeEntity.class})
public abstract class OptDatabase extends RoomDatabase {
    private static OptDatabase INSTANCE;

    public synchronized static OptDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), OptDatabase.class, "colis-database").build();
        }
        return INSTANCE;
    }

    abstract public ColisDao colisDao();
    abstract public EtapeDao etapeDao();
    abstract public ColisWithStepsDao colisWithStepsDao();
}