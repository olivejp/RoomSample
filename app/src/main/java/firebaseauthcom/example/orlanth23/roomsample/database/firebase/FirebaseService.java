package firebaseauthcom.example.orlanth23.roomsample.database.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import firebaseauthcom.example.orlanth23.roomsample.Constants;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisWithStepsRepository;

import static firebaseauthcom.example.orlanth23.roomsample.Constants.PREF_USER;

/**
 * Created by orlanth23 on 19/11/2017.
 */

public class FirebaseService {

    private static final String TAG = FirebaseService.class.getName();
    private static FirebaseService INSTANCE;

    /**
     * Constructor
     */
    private FirebaseService() {
    }

    /**
     * FirebaseService is a singleton, so getInstance return the INSTANCE.
     *
     * @return
     */
    public static FirebaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseService();
        }
        return INSTANCE;
    }

    /**
     * Get the Firebase user reference
     *
     * @return
     */
    private static DatabaseReference getUsersRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.DATABASE_USERS_REFERENCE);
    }

    /**
     * @param context
     * @param listColis
     */
    private static void updateRemoteDatabase(Context context, List<ColisWithSteps> listColis) {
        String uid = getUidOfFirebaseUser(context);
        if (uid != null) {
            for (ColisWithSteps colisWithSteps : listColis) {
                getUsersRef().child(uid).child(colisWithSteps.colisEntity.getIdColis()).setValue(colisWithSteps)
                        .addOnSuccessListener(aVoid ->
                                Log.d(TAG, "(updateRemoteDatabase) : Insertion RÉUSSIE dans Firebase : " + colisWithSteps.colisEntity.getIdColis())
                        )
                        .addOnFailureListener(e ->
                                Log.d(TAG, "(updateRemoteDatabase) : Insertion ÉCHOUÉE dans Firebase : " + colisWithSteps.colisEntity.getIdColis())
                        );
            }
        }
    }

    /**
     * Delete Colis from its idColis into the Firebase Database
     *
     * @param idColis
     */
    public static void deleteRemoteColis(String idColis) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getUsersRef()
                    .child(user.getUid())
                    .child(idColis)
                    .removeValue((databaseError, databaseReference) -> Log.d(TAG, "(deleteRemoteColis) : Delete successful of : " + idColis));
        }
    }

    /**
     * Catch the Firebase UID stored in shared preference
     *
     * @param context
     * @return
     */
    @Nullable
    private static String getUidOfFirebaseUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_USER, null);
    }

    /**
     * @param context
     */
    public static void catchDbFromFirebase(Context context) {
        // ValueListener which wait the complete list of colisWithSteps
        ValueEventListener getFromRemoteValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new SyncFirebaseTask(context, dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        };

        // ValueListener which wait the list of users
        // When we receive datasnapshot we look for the UID of the connected user.
        // Then we try to catch
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser connectedUser = FirebaseAuth.getInstance().getCurrentUser();
                if (connectedUser != null) {
                    if (dataSnapshot.hasChild(connectedUser.getUid())) {
                        DatabaseReference userReference = FirebaseService.getUsersRef().child(connectedUser.getUid());
                        userReference.addValueEventListener(getFromRemoteValueEventListener);
                    } else {
                        ColisWithStepsRepository.getInstance(context).getActiveColisWithSteps().subscribe(colisWithSteps ->
                                FirebaseService.updateRemoteDatabase(context, colisWithSteps)
                        );
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Do Nothing
            }
        };

        FirebaseService.getUsersRef().addListenerForSingleValueEvent(valueEventListener);
    }
}
