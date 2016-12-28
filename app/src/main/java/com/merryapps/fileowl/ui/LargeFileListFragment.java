package com.merryapps.fileowl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merryapps.FileOwlApp;
import com.merryapps.fileowl.R;
import com.merryapps.fileowl.model.BackupManager;
import com.merryapps.fileowl.model.FileOwlManagerFactory;

import java.util.ArrayList;

/**
 * Fragment to show the largest files.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class LargeFileListFragment extends Fragment {

    private static final String TAG = "LargeFileListFragment";

    private RecyclerView recyclerView;
    private TextView titleTxtVw;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);

        initViews(view);

        ((LargeFileListAdapter)recyclerView.getAdapter()).setFileStats(
                backupManager().getLastScanResult().getLargestFiles());
        recyclerView.getAdapter().notifyDataSetChanged();

        return view;
    }

    private void initViews(View view) {
        titleTxtVw = (TextView) view.findViewById(R.id.fragment_list_title_txtVw_id);
        titleTxtVw.setText(R.string.fragment_list_title_largest_files);
        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_rcyclrVw_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new LargeFileListAdapter(getActivity(),new ArrayList<>()));
    }

    private FileOwlManagerFactory getFactory() {
        return (FileOwlManagerFactory)((FileOwlApp) getActivity().getApplication()).getManagerFactory();
    }

    private BackupManager backupManager() {
        return getFactory().backupManager();
    }

    public void share() {
        Log.d(TAG, "share() called");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "File Owl: Latest scan statistics");
        shareIntent.putExtra(Intent.EXTRA_TEXT, UiUtil.shareMessageGenerator(backupManager().getLastScanResult()));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "SEND TO"));
    }
}
