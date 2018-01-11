package firebaseauthcom.example.orlanth23.roomsample.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.activity.adapter.ColisAdapter;
import firebaseauthcom.example.orlanth23.roomsample.databinding.ActivityMainBinding;
import firebaseauthcom.example.orlanth23.roomsample.activity.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private ColisAdapter colisAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        ActivityMainBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);

        colisAdapter = new ColisAdapter();
        binding.colisList.setAdapter(colisAdapter);

        viewModel.getListColis().observe(this, colisEntities -> {
            if (colisEntities != null && !colisEntities.isEmpty()){
                colisAdapter.setProductList(colisEntities);
            }
            binding.executePendingBindings();
        });
    }
}
