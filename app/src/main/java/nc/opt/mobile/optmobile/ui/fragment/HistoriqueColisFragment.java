package nc.opt.mobile.optmobile.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nc.opt.mobile.optmobile.database.local.entity.StepEntity;
import nc.opt.mobile.optmobile.ui.activity.viewmodel.MainActivityViewModel;
import nc.opt.mobile.optmobile.ui.adapter.EtapeAdapter;
import nc.opt.mobile.optmobile.R;

import static nc.opt.mobile.optmobile.Constants.PREF_USER;


public class HistoriqueColisFragment extends Fragment {

    private static final String OPT = "OPT";
    private static final String AFTERSHIP = "AFTERSHIP";

    @BindView(R.id.recycler_etape_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.text_object_not_found)
    TextView textObjectNotFound;

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.step_line)
    View stepView;

    private MainActivityViewModel viewModel;
    private AppCompatActivity appCompatActivity;
    private EtapeAdapter etapeAdapter;
    private BottomNavigationView.OnNavigationItemSelectedListener bottomListener;

    public HistoriqueColisFragment() {
        // Required empty public constructor
        Fade enterFade = new Fade();
        Fade exitFade = new Fade();
        enterFade.setDuration(200);
        exitFade.setDuration(200);
        this.setEnterTransition(enterFade);
        this.setExitTransition(exitFade);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appCompatActivity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(appCompatActivity).get(MainActivityViewModel.class);
        etapeAdapter = new EtapeAdapter();
        bottomListener = item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_opt) {
                viewModel.getListStepFromOpt(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                savePrefOrigine(appCompatActivity, OPT);
                return true;
            }
            if (id == R.id.navigation_aftership) {
                viewModel.getListStepFromAfterShip(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                savePrefOrigine(appCompatActivity, AFTERSHIP);
                return true;
            }
            return false;
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historique_colis, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setAdapter(etapeAdapter);

        // Change title
        appCompatActivity.setTitle(viewModel.getSelectedIdColis());

        // Manage the back button in the navigation bar
        if (!viewModel.isTwoPane() && appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
            appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switch (getPrefOrigine(appCompatActivity)) {
            case OPT:
                // Populate the adapter with the steps from the colis
                viewModel.getListStepFromOpt(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                bottomNavigationView.setSelectedItemId(R.id.navigation_opt);
                break;
            case AFTERSHIP:
                // Populate the adapter with the steps from the colis
                viewModel.getListStepFromAfterShip(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                bottomNavigationView.setSelectedItemId(R.id.navigation_aftership);
                break;
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomListener);

        return rootView;
    }

    private void initViews(List<StepEntity> stepEntities) {
        etapeAdapter.setEtapes(stepEntities);
        boolean isEtapeListEmpty = stepEntities == null || stepEntities.isEmpty();
        textObjectNotFound.setVisibility(isEtapeListEmpty ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(isEtapeListEmpty ? View.GONE : View.VISIBLE);
        stepView.setVisibility(isEtapeListEmpty ? View.GONE : View.VISIBLE);
    }

    private String getPrefOrigine(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_USER, OPT);
    }

    private void savePrefOrigine(@NonNull Context context, String prefOrigine) {
        // Save the UID of the user in the SharedPreference
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USER, prefOrigine);
        editor.apply();
    }
}
