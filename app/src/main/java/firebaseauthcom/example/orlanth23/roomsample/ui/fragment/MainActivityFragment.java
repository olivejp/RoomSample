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
import firebaseauthcom.example.orlanth23.roomsample.database.local.entity.ColisEntity;
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

public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerItemTouchHelper.SwipeListener {

    public static final String TAG = MainActivityFragment.class.getName();
    public static final String ARG_NOTICE_BUNDLE_COLIS = "ARG_NOTICE_BUNDLE_COLIS";
    public static final String DIALOG_TAG_DELETE = "DIALOG_TAG_DELETE";
    public static final String DIALOG_TAG_DELIVERED = "DIALOG_TAG_DELIVERED";

    @BindView(R.id.recycler_colis_list)
    public RecyclerView recyclerViewColisList;

    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;

    private MainActivityViewModel viewModel;
    private AppCompatActivity appCompatActivity;
    private NoticeDialogFragment.NoticeDialogListener noticeDialogListener;
    private ColisAdapter colisAdapter;
    private RecyclerView.ItemDecoration itemDecoration;
    private RecyclerItemTouchHelper recyclerItemTouchHelper = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerItemTouchHelper);
    private final Handler refreshTimeDelayHandler = new Handler();

    /**
     * onClickDisplay use in the Adapter to display the {@link HistoriqueColisFragment}
     */
    private View.OnClickListener onClickDisplay = (View v) -> {
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
        colisAdapter = new ColisAdapter(viewModel.getGlideRequester(), onClickDisplay);

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

        // Attach adapter to the recyclerView
        recyclerViewColisList.setAdapter(colisAdapter);

        // Add OnRefreshListener to the recyclerView
        swipeRefreshLayout.setOnRefreshListener(this);

        // Add Swipe to the recycler view
        itemTouchHelper.attachToRecyclerView(recyclerViewColisList);

        // Add itemDecorator to the recycler view
        recyclerViewColisList.addItemDecoration(itemDecoration);

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
    public void onRefresh() {
        viewModel.refresh();
        swipeRefreshLayout.setRefreshing(true);
        refreshTimeDelayHandler.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction) {
        try {
            ColisAdapter.ViewHolderColisAdapter viewHolderColisAdapter = (ColisAdapter.ViewHolderColisAdapter) viewHolder;
            ColisWithSteps colis = viewHolderColisAdapter.getColisWithSteps();

            // Création d'un bundle dans lequel on va passer nos items
            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_NOTICE_BUNDLE_COLIS, colis.colisEntity);

            if (direction == ItemTouchHelper.LEFT) {
                // Appel d'un fragment qui va demander à l'utilisateur s'il est sûr de vouloir supprimer le colis.
                Utilities.sendDialogByFragmentManagerWithRes(getFragmentManager(),
                        String.format("Supprimer le colis %s ?", colis.colisEntity.getIdColis()),
                        NoticeDialogFragment.TYPE_BOUTON_YESNO,
                        R.drawable.ic_delete_grey_900_24dp,
                        DIALOG_TAG_DELETE,
                        bundle,
                        noticeDialogListener);
            } else {
                // Appel d'un fragment qui va demander à l'utilisateur s'il est sûr de vouloir délivrer le colis.
                Utilities.sendDialogByFragmentManagerWithRes(getFragmentManager(),
                        String.format("Marquer comme délivré le colis %s ?", colis.colisEntity.getIdColis()),
                        NoticeDialogFragment.TYPE_BOUTON_YESNO,
                        R.drawable.ic_check_circle_grey_900_48dp,
                        DIALOG_TAG_DELIVERED,
                        bundle,
                        noticeDialogListener);
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "La vue doit contenir un ColisEntity comme Tag");
        }
    }
}
