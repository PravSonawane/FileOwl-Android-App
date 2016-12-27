package com.merryapps.fileowl.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merryapps.fileowl.R;
import com.merryapps.fileowl.model.FileTypeFrequency;
import com.merryapps.fileowl.model.ScanResult;

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

        if (this.getArguments() != null
            && this.getArguments().getParcelable("SCAN_RESULT") != null) {
                ScanResult scanResult = this.getArguments().getParcelable("SCAN_RESULT");
                assert scanResult != null;
                ((FrequentFileListAdapter)recyclerView.getAdapter()).setFileTypeFrequencies(scanResult.getMostFrequentFileTypes());
                recyclerView.getAdapter().notifyDataSetChanged();
        }
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
        List<FileTypeFrequency> fileTypeFrequencies = new ArrayList<>();
        fileTypeFrequencies.add(new FileTypeFrequency("txt", 2324));
        fileTypeFrequencies.add(new FileTypeFrequency("per", 324));
        fileTypeFrequencies.add(new FileTypeFrequency("jpg", 24));
        fileTypeFrequencies.add(new FileTypeFrequency("abc", 4));

        recyclerView.setAdapter(new FrequentFileListAdapter(getActivity(),fileTypeFrequencies));
    }
}
