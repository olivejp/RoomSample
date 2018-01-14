package firebaseauthcom.example.orlanth23.roomsample.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.ui.activity.AddColisActivity;
import firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel.MainActivityViewModel;
import firebaseauthcom.example.orlanth23.roomsample.ui.adapter.ColisAdapter;

/**
 * Created by orlanth23 on 14/01/2018.
 */

public class MainActivityFragment extends Fragment {

    private static final String TAG_HISTO = "TAG_HISTO";
    @BindView(R.id.recycler_colis_list)
    public RecyclerView colisList;

    private ColisAdapter colisAdapter;
    private MainActivityViewModel viewModel;
    private AppCompatActivity appCompatActivity;

    private View.OnClickListener mOnClickListener = (View v) -> {
        ColisWithSteps colis = (ColisWithSteps) v.getTag();
        viewModel.setSelectedColis(colis);
        displayHistorique(viewModel.isTwoPane());
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appCompatActivity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(appCompatActivity).get(MainActivityViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);
        ButterKnife.bind(this, rootView);

        colisAdapter = new ColisAdapter(viewModel.getGlideRequester(), mOnClickListener);
        colisList.setAdapter(colisAdapter);

        viewModel.getListColis().observe(appCompatActivity, colisEntities -> {
            if (colisEntities != null && !colisEntities.isEmpty()) {
                colisAdapter.setColisList(colisEntities);
            }
        });

        return rootView;
    }

    @OnClick(R.id.fab_add_colis)
    public void addColis(View v) {
        Intent intent = new Intent(getContext(), AddColisActivity.class);
        startActivity(intent);
    }

    private void displayHistorique(boolean twoPane) {
        if (getFragmentManager() != null) {
            if (twoPane) {
                getFragmentManager().beginTransaction().replace(R.id.frame_detail, new HistoriqueColisFragment(), TAG_HISTO).commit();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.frame_master, new HistoriqueColisFragment(), TAG_HISTO).addToBackStack(null).commit();
            }
        }
    }
}
