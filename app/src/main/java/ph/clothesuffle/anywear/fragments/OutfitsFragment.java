package ph.clothesuffle.anywear.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.adapters.OutfitsRecyclerAdapter;
import ph.clothesuffle.anywear.interfaces.AdapterInterface;
import ph.clothesuffle.anywear.models.Outfit;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 3/12/2016.
 */
public class OutfitsFragment extends Fragment implements AdapterInterface, SearchView.OnQueryTextListener {

    private View root;
    private RecyclerView recyclerView;
    private OutfitsRecyclerAdapter outfitsRecyclerAdapter;
    private LinearLayout linearLayout_emptyView;
    private ArrayList<Outfit> outfits = new ArrayList<>();
    private TextView textView_add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_fragments, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView_images);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        linearLayout_emptyView = (LinearLayout) root.findViewById(R.id.linearLayout_empty);
        textView_add = (TextView) root.findViewById(R.id.textView_apparel);
        setHasOptionsMenu(true);
        getData();
        outfitsRecyclerAdapter = new OutfitsRecyclerAdapter(getActivity(), outfits, this);
        SlideInBottomAnimationAdapter slideInBottomAnimationAdapter = new SlideInBottomAnimationAdapter(outfitsRecyclerAdapter);
        slideInBottomAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(slideInBottomAnimationAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        //SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.string_hint_outfit));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_shuffle:
                randomSelect();
        }
        return super.onOptionsItemSelected(item);
    }

    private void randomSelect() {

        if (outfits.size() >= 1) {
            outfitsRecyclerAdapter.startViewing((int) (outfits.size() * Math.random()));
        }
    }

    public void getData() {
        outfits = FileUtils.getAllOutfits();
        if (outfits.size() < 1) {
            linearLayout_emptyView.setVisibility(View.VISIBLE);
            textView_add.setText(String.format("(Tap the circle button to add %s)", Constants.FOLDER_OUTFITS));
        }
    }


    public static OutfitsFragment newInstance() {
        return new OutfitsFragment();
    }

    @Override
    public void loadRename() {
        getData();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (outfits.size() > 1) {
            outfitsRecyclerAdapter.filter(newText);
        }
        return false;
    }
}
