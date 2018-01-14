package firebaseauthcom.example.orlanth23.roomsample.database.firebase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.lang.ref.WeakReference;

import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.EtapeRepository;


/**
 * Created by 2761oli on 27/12/2017.
 */

public class SyncFirebaseTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private DataSnapshot dataSnapshot;
    private ColisRepository repoColis;
    private EtapeRepository repoEtape;


    SyncFirebaseTask(Context context, DataSnapshot dataSnapshot) {
        if (context != null) {
            this.context = new WeakReference<>(context);
            repoColis = ColisRepository.getInstance(context);
            repoEtape = EtapeRepository.getInstance(context);
        }
        this.dataSnapshot = dataSnapshot;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (context != null && context.get() != null) {
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                ColisWithSteps colisWithSteps = postSnapshot.getValue(ColisWithSteps.class);
                insertOrDelete(colisWithSteps);
            }
        } else {
            Log.d("SyncFirebaseTask", "DoInBackGround receive no context");
        }
        return null;
    }

    /**
     * Pour chaque colis récupéré de Firebase,
     * On va regarder si le colis existe dans la DB locale :
     * - Si il existe et qu'il a été supprimé  en local, on tente de le supprimer sur FB
     * - S'il n'existe pas on tente de l'insérer avec ses étapes.
     *
     * @param fbColisWithSteps ColisWithSteps récupéré de Fb
     */
    private void insertOrDelete(ColisWithSteps fbColisWithSteps) {
        if (fbColisWithSteps != null) {
            String idColis = fbColisWithSteps.colisEntity.getIdColis();

            ColisEntity colis = repoColis.findById(idColis);
            if (colis != null) {
                if (colis.isDeleted()) {
                    FirebaseService.deleteRemoteColis(idColis);
                    repoColis.delete(idColis);
                }
            } else {
                // Colis don't exist in local DB, we insert it.
                repoColis.insert(fbColisWithSteps.colisEntity);
                repoEtape.insert(fbColisWithSteps.etapeEntityList);
            }
        }
    }
}

