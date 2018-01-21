package firebaseauthcom.example.orlanth23.roomsample.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;

import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.barcodreader.BarcodeCaptureActivity;
import firebaseauthcom.example.orlanth23.roomsample.ui.fragment.AddColisFragment;
import firebaseauthcom.example.orlanth23.roomsample.ui.fragment.viewmodel.AddColisFragmentViewModel;


@SuppressWarnings("squid:MaximumInheritanceDepth")
public class AddColisActivity extends AppCompatActivity {

    public static final int RC_BARCODE_CAPTURE = 9001;
    private AddColisFragmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AddColisFragmentViewModel.class);
        setContentView(R.layout.activity_add_colis);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_add_colis, AddColisFragment.getInstance(null)).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CommonStatusCodes.SUCCESS) {
            if (data != null) {
                String codeBarResult = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeString);
                viewModel.setIdColis(codeBarResult);
                Log.d("TAG", "Code barre récupéré = " + codeBarResult);
            } else {
                Log.d("TAG", "Aucun barre code récupérer");
            }
        }
    }
}
