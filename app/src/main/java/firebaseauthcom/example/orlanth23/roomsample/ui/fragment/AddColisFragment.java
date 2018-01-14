package firebaseauthcom.example.orlanth23.roomsample.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
        if (!editIdParcel.getText().toString().isEmpty()) {
            Utilities.hideKeyboard(getActivity());
            ColisEntity colisEntity = new ColisEntity();
            colisEntity.setIdColis(editIdParcel.getText().toString().toUpperCase());
            colisEntity.setDescription((editDescriptionParcel.getText() != null) ? editDescriptionParcel.getText().toString() : null);
            colisEntity.setDeleted(0);
            viewModel.insertColis(colisEntity);
            getActivity().finish();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(AddColisFragmentViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_add_colis, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
