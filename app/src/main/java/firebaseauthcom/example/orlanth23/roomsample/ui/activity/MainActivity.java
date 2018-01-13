package firebaseauthcom.example.orlanth23.roomsample.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.database.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel.MainActivityViewModel;
import firebaseauthcom.example.orlanth23.roomsample.ui.adapter.ColisAdapter;
import firebaseauthcom.example.orlanth23.roomsample.ui.fragment.HistoriqueColisFragment;

public class MainActivity extends AppCompatActivity {

    private ColisAdapter colisAdapter;
    private MainActivityViewModel viewModel;
    private boolean mTwoPane;
    private static final String TAG_PARCEL_RESULT_SEARCH_FRAGMENT = "TAG_PARCEL_RESULT_SEARCH_FRAGMENT";

    @BindView(R.id.recycler_colis_list)
    public RecyclerView colisList;

    private View.OnClickListener mOnClickListener = (View v) -> {
        ColisWithSteps colis = (ColisWithSteps) v.getTag();
        viewModel.setSelectedColis(colis);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mTwoPane) {
            ft.replace(R.id.frame_detail, new HistoriqueColisFragment(), TAG_PARCEL_RESULT_SEARCH_FRAGMENT).commit();
        } else {
            ft.replace(R.id.frame_master, new HistoriqueColisFragment(), TAG_PARCEL_RESULT_SEARCH_FRAGMENT).addToBackStack(null).commit();
        }
    };

    @OnClick(R.id.fab_add_colis)
    public void addColis() {
        Intent intent = new Intent(MainActivity.this, AddColisActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        setContentView(R.layout.activity_main);
        mTwoPane = findViewById(R.id.frame_detail) != null;

        ButterKnife.bind(this);

        colisAdapter = new ColisAdapter(viewModel.getGlideRequester(), mOnClickListener);
        colisList.setAdapter(colisAdapter);

        viewModel.getListColis().observe(this, colisEntities -> {
            if (colisEntities != null && !colisEntities.isEmpty()) {
                colisAdapter.setColisList(colisEntities);
            }
        });
    }
}
