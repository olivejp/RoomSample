package nc.opt.mobile.optmobile.database.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import nc.opt.mobile.optmobile.database.local.dao.ColisWithStepsDao;
import nc.opt.mobile.optmobile.database.local.dao.StepDao;
import nc.opt.mobile.optmobile.database.local.entity.ColisEntity;
import nc.opt.mobile.optmobile.database.local.entity.StepEntity;
import nc.opt.mobile.optmobile.database.local.dao.ColisDao;

/**
 * Created by orlanth23 on 01/07/2017.
 */

@Database(version = 30, entities = {ColisEntity.class, StepEntity.class})
public abstract class ColisDatabase extends RoomDatabase {
    private static ColisDatabase INSTANCE;

    public static synchronized ColisDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ColisDatabase.class, "colis-database").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public abstract ColisDao colisDao();

    public abstract StepDao stepDao();

    public abstract ColisWithStepsDao colisWithStepsDao();
}