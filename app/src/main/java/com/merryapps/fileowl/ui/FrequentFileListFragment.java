package com.merryapps.fileowl.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private static final String TAG = "FrequentFileFragment";

    private RecyclerView recyclerView;
    private TextView titleTxtVw;

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
        titleTxtVw = (TextView) view.findViewById(R.id.fragment_list_title_txtVw_id);
        titleTxtVw.setText(R.string.fragment_list_title_frequent_files);
        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_rcyclrVw_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<FileTypeFrequency> fileTypeFrequencies = new ArrayList<>();
        recyclerView.setAdapter(new FrequentFileListAdapter(getActivity(),fileTypeFrequencies));
    }
}
