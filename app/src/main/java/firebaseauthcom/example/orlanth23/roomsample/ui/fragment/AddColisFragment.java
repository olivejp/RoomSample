package firebaseauthcom.example.orlanth23.roomsample.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.Utilities;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
import firebaseauthcom.example.orlanth23.roomsample.database.local.repository.ColisRepository;
import firebaseauthcom.example.orlanth23.roomsample.job.SyncTask;
import firebaseauthcom.example.orlanth23.roomsample.ui.fragment.viewmodel.AddColisFragmentViewModel;

public class AddColisFragment extends Fragment {

    private AddColisFragmentViewModel viewModel;

    @BindView(R.id.edit_id_parcel)
    EditText editIdParcel;

    @BindView(R.id.edit_description_parcel)
    EditText editDescriptionParcel;

    public AddColisFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.fab_add_colis)
    public void addColisCallbackListener(View v) {
        Utilities.hideKeyboard(getActivity());
        if (!editIdParcel.getText().toString().isEmpty()) {
            String idColis = editIdParcel.getText().toString();

            ColisRepository.getInstance(getContext()).findById(idColis).subscribe(colisFromDb -> {
                if (colisFromDb.isDeleted()) {
                    colisFromDb.setDeleted(0);
                    viewModel.saveColis(colisFromDb);
                } else {
                    Snackbar.make(v, String.format("Le colis %s est déjà suivi", idColis), Snackbar.LENGTH_LONG).show();
                }
                getActivity().finish();
            }, throwable -> {
                ColisEntity colisEntity = new ColisEntity();
                colisEntity.setIdColis(idColis);
                colisEntity.setDescription((editDescriptionParcel.getText() != null) ? editDescriptionParcel.getText().toString() : null);
                colisEntity.setDeleted(0);
                viewModel.saveColis(colisEntity);

                // Snackbar to say that we follow this new item
                Snackbar.make(v, String.format("%s ajouté au suivi", idColis), Snackbar.LENGTH_LONG).show();

                // Launch asyncTask to query the server
                new SyncTask(getContext(), idColis).execute();

                getActivity().finish();
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(AddColisFragmentViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_add_colis, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
