package com.merryapps.diskhero.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merryapps.diskhero.R;
import com.merryapps.diskhero.model.FileStat;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists the most frequent files
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FrequentFileListFragment extends Fragment {

    private static final String TAG = "FrequentFileListFragment";

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_rcyclrVw_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<FileStat> fileStats = new ArrayList<>();
        recyclerView.setAdapter(new LargeFileListAdapter(getActivity(),fileStats));
    }
}
