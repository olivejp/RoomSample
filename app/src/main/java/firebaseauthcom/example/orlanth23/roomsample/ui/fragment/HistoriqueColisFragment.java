package firebaseauthcom.example.orlanth23.roomsample.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel.MainActivityViewModel;
import firebaseauthcom.example.orlanth23.roomsample.ui.adapter.EtapeAdapter;


public class HistoriqueColisFragment extends Fragment {

    @BindView(R.id.recycler_etape_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.text_object_not_found)
    TextView textObjectNotFound;

    private AppCompatActivity appCompatActivity;

    public HistoriqueColisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appCompatActivity = (AppCompatActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historique_colis, container, false);
        ButterKnife.bind(this, rootView);

        MainActivityViewModel viewModel = ViewModelProviders.of(appCompatActivity).get(MainActivityViewModel.class);

        EtapeAdapter mEtapeAdapter = new EtapeAdapter();
        mRecyclerView.setAdapter(mEtapeAdapter);

        if (!viewModel.isTwoPane()) {
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
                appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        ColisWithSteps colisWithSteps = viewModel.getSelectedColis();
        if (colisWithSteps != null) {
            mEtapeAdapter.setEtapes(colisWithSteps.etapeEntityList);
            boolean isEtapeListEmpty = colisWithSteps.etapeEntityList == null || colisWithSteps.etapeEntityList.isEmpty();
            textObjectNotFound.setVisibility(isEtapeListEmpty ? View.VISIBLE : View.GONE);
            mRecyclerView.setVisibility(isEtapeListEmpty ? View.GONE : View.GONE);
            appCompatActivity.setTitle(colisWithSteps.colisEntity.getIdColis());
        }

        return rootView;
    }
}
