package com.merryapps.diskhero.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.merryapps.diskhero.R;
import com.merryapps.diskhero.model.FileScanService;

/**
 * Fragment for the home screen.
 * Displays the scan information.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    /**
     * Request code value for READ_EXTERNAL_STORAGE permission.
     * Use this as an argument to
     * {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}
     */
    private static final int REQ_CODE_READ_EXT_STORAGE = 100;

    private FloatingActionButton scanFab;
    private RelativeLayout scanProgressRelLyt;
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        initViews(view);
        initFields();
        return view;
    }

    private void initViews(View view) {
        //init largest files view all button
        Button largestFilesViewAllBtn = (Button) view.findViewById(R.id.lyt_largest_files_overview_btn_viewAll_id);
        largestFilesViewAllBtn.setOnClickListener(newLargestFilesViewAllOcl());

        //init freuqent files view all button
        Button frequentFilesViewAllBtn = (Button) view.findViewById(R.id.lyt_frequent_files_overview_btn_viewAll_id);
        frequentFilesViewAllBtn.setOnClickListener(newFrequentFilesViewAllOcl());

        //init scan progress bar
        scanProgressRelLyt = (RelativeLayout) view.findViewById(R.id.fragment_home_relLyt_scanProgress_id);

        //init scan fab
        scanFab = (FloatingActionButton) view.findViewById(R.id.fragment_home_scan_fab_id);
        scanFab.setOnClickListener(newScanFabOcl());
    }

    private void initFields() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(receiver, new IntentFilter("com.merryapps.updatedownloadprogress.DOWNLOAD_PROGRESS"));
        super.onResume();

    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    private View.OnClickListener newLargestFilesViewAllOcl() {
        return view -> this.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_home_frm_lyt_placeHolder_id, new LargeFileListFragment())
                .addToBackStack("LargeFileListFragment").commit();
    }

    private View.OnClickListener newFrequentFilesViewAllOcl() {
        return view -> this.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_home_frm_lyt_placeHolder_id, new FrequentFileListFragment())
                .addToBackStack("FrequentFileListFragment").commit();
    }

    private View.OnClickListener newScanFabOcl() {
        Log.d(TAG, "newScanFabOcl() called");
        return view -> checkPermAndScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQ_CODE_READ_EXT_STORAGE: {
                if (grantResults.length >= 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "onRequestPermissionsResult: Permission granted.");
                    scan();
                }
            }
        }
    }

    private void scan() {
        Log.v(TAG, "scan: Starting file scan");
        scanProgressRelLyt.setVisibility(View.VISIBLE);
        getActivity().startService(new Intent(getActivity(), FileScanService.class));
        ((HomeActivity)getActivity()).setShareMenuItemEnabled(false);
    }

    /**
     * Check if the user has the READ_EXTERNAL_STORAGE permission on M
     * and above and prompt if requried.
     */
    private void checkPermAndScan() {

        if (isAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            scan();
        } else {
            if (shouldShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog dialog = createAlertDialog();
                dialog.show();
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

    }

    /**
     * Creates and returns an {@link AlertDialog} for explaning why READ_EXTERNAL_STORAGE
     * permission is requied.
     */
    private AlertDialog createAlertDialog() {
        return new AlertDialog.Builder(this.getActivity())
                .setTitle(R.string.read_ext_storage_perm_explanation_title)
                .setMessage(R.string.read_ext_storage_perm_explanation)
                .setPositiveButton(R.string.read_ext_storage_perm_explanation_positive_btn,
                        newPositiveBtnOcl())
                .setNegativeButton(R.string.read_ext_storage_perm_explanation_negative_btn,
                        newNegativeBtnOcl()).create();
    }

    /**
     * @return {@link android.view.View.OnClickListener} for negative button of explanation
     * alert dialog.
     */
    private DialogInterface.OnClickListener newNegativeBtnOcl() {
        return (dialogInterface, i) -> dialogInterface.dismiss();
    }

    /**
     * @return {@link android.view.View.OnClickListener} for positive button of explanation
     * alert dialog.
     */
    private DialogInterface.OnClickListener newPositiveBtnOcl() {
        return (dialogInterface, i) -> {

            //Request permissions if on M and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        };
    }

    /**
     * Calls {@link ActivityCompat#requestPermissions(Activity, String[], int)} for M and above
     * @param permission the permission to request for
     */
    private void requestPermission(String permission) {
        //Request permissions if on M and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {permission},
                    REQ_CODE_READ_EXT_STORAGE);
        }
    }

    /**
     * Check if an explanation needs to be shown to the user.
     * @param readExternalStorage the permission to be checked against.
     * @return {@code true} if an explanation needs to be shown, {@code false} otherwise.
     * }
     */
    private boolean shouldShowRationale(String readExternalStorage) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), readExternalStorage);
    }

    /**
     * Check if the user has the given permission.
     * @param permission the permission to be checked.
     * @return {@code true} if the user has the given permission, {@code false} otherwise.
     */
    private boolean isAllowed(String permission) {
        return ContextCompat.checkSelfPermission(this.getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static class ProgressBroadcastReceiver extends BroadcastReceiver {

        private ProgressBar progressBar;

        ProgressBroadcastReceiver(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            this.progressBar.setProgress(intent.getExtras().getInt("progress"));
        }
    }
}
