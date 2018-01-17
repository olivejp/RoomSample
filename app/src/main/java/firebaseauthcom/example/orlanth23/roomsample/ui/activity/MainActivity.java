package firebaseauthcom.example.orlanth23.roomsample.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel.MainActivityViewModel;
import firebaseauthcom.example.orlanth23.roomsample.ui.fragment.HistoriqueColisFragment;
import firebaseauthcom.example.orlanth23.roomsample.ui.fragment.MainActivityFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_MASTER_FRAGMENT = "TAG_MASTER_FRAGMENT";
    public static final String TAG_DETAIL_FRAGMENT = "TAG_DETAIL_FRAGMENT";

    private Fragment masterFragment;
    private Fragment detailFragment;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        setContentView(R.layout.activity_main);
        viewModel.setTwoPane(findViewById(R.id.frame_detail) != null);

        // Manage the fragments
        masterFragment = getSupportFragmentManager().findFragmentByTag(TAG_MASTER_FRAGMENT);
        if (masterFragment == null) {
            masterFragment = new MainActivityFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_master, masterFragment, TAG_MASTER_FRAGMENT).commit();


        detailFragment = getSupportFragmentManager().findFragmentByTag(TAG_DETAIL_FRAGMENT);
        if (detailFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(detailFragment).commit();
            if (viewModel.isTwoPane()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_detail, new HistoriqueColisFragment(), TAG_DETAIL_FRAGMENT).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_master, new HistoriqueColisFragment(), TAG_DETAIL_FRAGMENT).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount >= 1) {
            getSupportFragmentManager().popBackStack();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(backStackCount != 1);
                setTitle(R.string.app_name);
            }
        } else {
            super.onBackPressed();
        }
    }

}
