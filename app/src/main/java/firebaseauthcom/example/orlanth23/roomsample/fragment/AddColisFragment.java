package firebaseauthcom.example.orlanth23.roomsample.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import firebaseauthcom.example.orlanth23.roomsample.fragment.callback.AddColisCallbackListener;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.Utilities;
import firebaseauthcom.example.orlanth23.roomsample.databinding.FragmentAddColisBinding;
import firebaseauthcom.example.orlanth23.roomsample.fragment.viewmodel.AddColisFragmentViewModel;

public class AddColisFragment extends Fragment {

    private AddColisFragmentViewModel viewModel;

    public AddColisFragment() {
        // Required empty public constructor
    }

    private final AddColisCallbackListener addColisCallbackListener = colisEntity -> {
        if (!colisEntity.getIdColis().isEmpty()) {
            Utilities.hideKeyboard(getActivity());
            colisEntity.setIdColis(colisEntity.getIdColis().toUpperCase());
            colisEntity.setDeleted(0);
            viewModel.insertColis(colisEntity);
            getActivity().finish();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(AddColisFragmentViewModel.class);

        FragmentAddColisBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_add_colis, container, false);
        binding.setCallback(addColisCallbackListener);
        return binding.getRoot();
    }
}
