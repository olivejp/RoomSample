package firebaseauthcom.example.orlanth23.roomsample.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

/**
 * Created by orlanth23 on 11/01/2018.
 */

public interface AbstractDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T... entities);

    @Update
    void update(T... entities);

    @Delete
    void delete(T... entities);
}
