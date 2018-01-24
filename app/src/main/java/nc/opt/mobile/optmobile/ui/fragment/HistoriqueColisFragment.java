package nc.opt.mobile.optmobile.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nc.opt.mobile.optmobile.PreferenceManager;
import nc.opt.mobile.optmobile.R;
import nc.opt.mobile.optmobile.database.local.entity.StepEntity;
import nc.opt.mobile.optmobile.ui.activity.viewmodel.MainActivityViewModel;
import nc.opt.mobile.optmobile.ui.adapter.EtapeAdapter;

import static nc.opt.mobile.optmobile.PreferenceManager.AFTERSHIP;
import static nc.opt.mobile.optmobile.PreferenceManager.OPT;


public class HistoriqueColisFragment extends Fragment {

    @BindView(R.id.recycler_etape_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.text_object_not_found)
    TextView textObjectNotFound;

    @BindView(R.id.ah_bottom_navigation)
    AHBottomNavigation bottomNavigationView;

    @BindView(R.id.step_line)
    View stepView;

    private MainActivityViewModel viewModel;
    private AppCompatActivity appCompatActivity;
    private EtapeAdapter etapeAdapter;
    private AHBottomNavigation.OnTabSelectedListener bottomListener;
    private PreferenceManager preferenceManager;

    private AHBottomNavigationItem itemOpt;
    private AHBottomNavigationItem itemAfterShip;

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
        preferenceManager = new PreferenceManager(appCompatActivity);
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(appCompatActivity).get(MainActivityViewModel.class);
        etapeAdapter = new EtapeAdapter();

        // Paramétrage d'un BottomNavigation
        itemOpt = new AHBottomNavigationItem("OPT", R.drawable.ic_opt);
        itemAfterShip = new AHBottomNavigationItem("AfterShip", R.drawable.ic_aftership);

        bottomListener = (position, wasSelected) -> {
            if (position == 0) {
                viewModel.getListStepFromOpt(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                preferenceManager.setPrefOrigine(OPT);
                return true;
            }
            if (position == 1) {
                viewModel.getListStepFromAfterShip(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                preferenceManager.setPrefOrigine(AFTERSHIP);
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

        // Paramétrage de notre BottomNavigation
        bottomNavigationView.addItem(itemOpt);
        bottomNavigationView.addItem(itemAfterShip);
        bottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
        bottomNavigationView.setAccentColor(getResources().getColor(R.color.colorAccent));
        bottomNavigationView.setInactiveColor(getResources().getColor(R.color.colorAccentDark));
        bottomNavigationView.setColored(true);

        viewModel.getCountOptSteps(viewModel.getSelectedIdColis()).observe(this, integer -> bottomNavigationView.setNotification(integer.toString(), 0));
        viewModel.getCountAfterShipSteps(viewModel.getSelectedIdColis()).observe(this, integer -> bottomNavigationView.setNotification(integer.toString(), 1));

        mRecyclerView.setAdapter(etapeAdapter);

        // Change title
        appCompatActivity.setTitle(viewModel.getSelectedIdColis());

        // Manage the back button in the navigation bar
        if (!viewModel.isTwoPane() && appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
            appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switch (preferenceManager.getPrefOrigine()) {
            case OPT:
                // Populate the adapter with the steps from the colis
                viewModel.getListStepFromOpt(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                bottomNavigationView.setCurrentItem(0);
                break;
            case AFTERSHIP:
                // Populate the adapter with the steps from the colis
                viewModel.getListStepFromAfterShip(viewModel.getSelectedIdColis()).observe(this, this::initViews);
                bottomNavigationView.setCurrentItem(1);
                break;
        }

        bottomNavigationView.setOnTabSelectedListener(bottomListener);

        return rootView;
    }

    private void initViews(List<StepEntity> stepEntities) {
        etapeAdapter.setEtapes(stepEntities);
        boolean isEtapeListEmpty = stepEntities == null || stepEntities.isEmpty();
        textObjectNotFound.setVisibility(isEtapeListEmpty ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(isEtapeListEmpty ? View.GONE : View.VISIBLE);
        stepView.setVisibility(isEtapeListEmpty ? View.GONE : View.VISIBLE);
    }
}
