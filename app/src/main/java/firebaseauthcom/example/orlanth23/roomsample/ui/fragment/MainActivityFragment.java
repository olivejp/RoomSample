package firebaseauthcom.example.orlanth23.roomsample.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import firebaseauthcom.example.orlanth23.roomsample.R;
import firebaseauthcom.example.orlanth23.roomsample.Utilities;
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisWithSteps;
import firebaseauthcom.example.orlanth23.roomsample.ui.NoticeDialogFragment;
import firebaseauthcom.example.orlanth23.roomsample.ui.RecyclerItemTouchHelper;
import firebaseauthcom.example.orlanth23.roomsample.ui.activity.AddColisActivity;
import firebaseauthcom.example.orlanth23.roomsample.ui.activity.viewmodel.MainActivityViewModel;
import firebaseauthcom.example.orlanth23.roomsample.ui.adapter.ColisAdapter;

import static firebaseauthcom.example.orlanth23.roomsample.ui.activity.MainActivity.TAG_DETAIL_FRAGMENT;

/**
 * Created by orlanth23 on 14/01/2018.
 */

public class MainActivityFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = MainActivityFragment.class.getName();
    public static final String ARG_NOTICE_BUNDLE_COLIS = "ARG_NOTICE_BUNDLE_COLIS";
    public static final String ARG_NOTICE_BUNDLE_POSITION = "ARG_NOTICE_BUNDLE_POSITION";
    public static final String DIALOG_TAG_DELETE = "DIALOG_TAG_DELETE";

    @BindView(R.id.recycler_colis_list)
    public RecyclerView recyclerViewColisList;

    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;

    private MainActivityViewModel viewModel;
    private AppCompatActivity appCompatActivity;
    private NoticeDialogFragment.NoticeDialogListener noticeDialogListener;
    private ColisAdapter colisAdapter;
    private RecyclerView.ItemDecoration itemDecoration;
    private RecyclerItemTouchHelper recyclerItemTouchHelper = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerItemTouchHelper);
    private final Handler refreshTimeDelayHandler = new Handler();

    /**
     * mOnClickListener use in the Adapter to display the {@link HistoriqueColisFragment}
     */
    private View.OnClickListener mOnClickListener = (View v) -> {
        ColisWithSteps colis = (ColisWithSteps) v.getTag();
        viewModel.setSelectedColis(colis);
        displayHistorique(viewModel.isTwoPane());
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appCompatActivity = (AppCompatActivity) context;
        try {
            noticeDialogListener = (NoticeDialogFragment.NoticeDialogListener) appCompatActivity;
        } catch (ClassCastException e) {
            Log.e(TAG, "L'activité appelant MainActivityFragment doit implémenter NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(appCompatActivity).get(MainActivityViewModel.class);
        itemDecoration = new DividerItemDecoration(appCompatActivity, DividerItemDecoration.VERTICAL);

        // Initialize adapter
        colisAdapter = new ColisAdapter(viewModel.getGlideRequester(), mOnClickListener);

        // Retrieve data from the ViewModel to populate the adapter
        viewModel.getLiveListActiveColis().observe(appCompatActivity, colisAdapter::setColisList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel.setSelectedColis(null);

        // View creation
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);
        ButterKnife.bind(this, rootView);

        // Add OnRefreshListener to the recyclerView
        swipeRefreshLayout.setOnRefreshListener(this);

        // Add Swipe to the recycler view
        recyclerViewColisList.setAdapter(colisAdapter);
        recyclerViewColisList.addItemDecoration(itemDecoration);
        itemTouchHelper.attachToRecyclerView(recyclerViewColisList);
        return rootView;
    }

    /**
     * Call the Activity to add a new Colis
     *
     * @param v
     */
    @OnClick(R.id.fab_add_colis)
    public void addColis(View v) {
        Intent intent = new Intent(getContext(), AddColisActivity.class);
        startActivity(intent);
    }

    /**
     * Depending on the parameter twoPane we display the HistoriqueColisFragment in a different frame
     *
     * @param twoPane
     */
    private void displayHistorique(boolean twoPane) {
        if (getFragmentManager() != null) {
            if (twoPane) {
                getFragmentManager().beginTransaction().replace(R.id.frame_detail, new HistoriqueColisFragment(), TAG_DETAIL_FRAGMENT).commit();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.frame_master, new HistoriqueColisFragment(), TAG_DETAIL_FRAGMENT).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ColisAdapter.ViewHolderColisAdapter) {
            ColisAdapter.ViewHolderColisAdapter r = (ColisAdapter.ViewHolderColisAdapter) viewHolder;

            // Création d'un bundle dans lequel on va passer nos items
            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_NOTICE_BUNDLE_COLIS, r.getColisWithSteps().colisEntity);
            bundle.putInt(ARG_NOTICE_BUNDLE_POSITION, position);

            // Appel d'un fragment qui va demander à l'utilisateur s'il est sûr de vouloir supprimer le colis.
            Utilities.sendDialogByFragmentManager(getFragmentManager(),
                    String.format("Etes-vous sûr de vouloir supprimer le colis %s ?", r.getColisWithSteps().colisEntity.getIdColis()),
                    NoticeDialogFragment.TYPE_BOUTON_YESNO,
                    NoticeDialogFragment.TYPE_IMAGE_INFORMATION,
                    DIALOG_TAG_DELETE,
                    bundle,
                    noticeDialogListener);
        }
    }

    @Override
    public void onRefresh() {
        viewModel.refresh();
        swipeRefreshLayout.setRefreshing(true);
        refreshTimeDelayHandler.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
    }
}
