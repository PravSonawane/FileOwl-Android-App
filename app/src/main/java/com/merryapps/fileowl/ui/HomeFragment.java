package com.merryapps.fileowl.ui;

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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.merryapps.FileOwlApp;
import com.merryapps.fileowl.R;
import com.merryapps.fileowl.model.FileOwlManagerFactory;
import com.merryapps.fileowl.model.FileScanService;
import com.merryapps.fileowl.model.ScanResult;
import com.merryapps.fileowl.model.ScanStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
    private BroadcastReceiver receiver;
    private CardView largestFilesCrdVw;
    private CardView frequentFilesCrdVw;
    private CardView averageFileSizeCrdVw;
    private ProgressBar scanPrgrssBr;

    //scan statistics
    private TextView lastScanTimeTxtVw;
    private TextView scanStatusTxtVw;

    //largest files overview
    private TextView largestFileNameTxtVw;
    private TextView largestFilePathTxtVw;
    private TextView largestFileSizeTxtVw;
    private Button largestFilesViewAllBtn;

    //frequent files overview
    private TextView mostFrequentFileTypeTxtVw;
    private TextView mostFrequentFileFrequencyTxtVw;
    private Button frequentFilesViewAllBtn;

    //average file size
    private TextView averageFileSizeTxtVw;
    private TextView totalFilesScannedTxtVw;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,false);

        initViews(view);
        initFields();

        if (!doesDataExist()) {
            setupViewForNoData();
        }

        ScanResult scanResult = getFactory().backupManager().getLastScanResult();

        receiver = new FileScanBroadcastReceiver(this);
        return view;
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(receiver, new IntentFilter(FileScanService.SCAN_STATUS_ACTION));
        super.onResume();

    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    private void initViews(View view) {

        //init scan fab
        scanFab = (FloatingActionButton) view.findViewById(R.id.fragment_home_scan_fab_id);
        scanFab.setOnClickListener(newScanFabOcl());

        largestFilesCrdVw = (CardView) view.findViewById(R.id.fragment_home_largest_files_crdVw_id);
        frequentFilesCrdVw = (CardView) view.findViewById(R.id.fragment_home_frequent_files_crdVw_id);
        averageFileSizeCrdVw = (CardView) view.findViewById(R.id.fragment_home_average_file_size_crdVw_id);

        //progress bar
        scanPrgrssBr = (ProgressBar) view.findViewById(R.id.lyt_scan_stat_scanStatus_prgRssBr_id);

        //scan statistics
        lastScanTimeTxtVw = (TextView) view.findViewById(R.id.lyt_scan_stat_lastScannedValue_txtVw_id);
        scanStatusTxtVw = (TextView) view.findViewById(R.id.lyt_scan_stat_stanStatusValue_txtVw_id);

        //init largest files overview section
        largestFileNameTxtVw = (TextView) view.findViewById(R.id.item_large_file_fileName_txtVw_id);
        largestFilePathTxtVw = (TextView) view.findViewById(R.id.item_large_file_filePath_txtVw_id);
        largestFileSizeTxtVw = (TextView) view.findViewById(R.id.item_large_file_fileSize_txtVw_id);
        largestFilesViewAllBtn = (Button) view.findViewById(R.id.lyt_largest_files_overview_btn_viewAll_id);
        largestFilesViewAllBtn.setOnClickListener(newLargestFilesViewAllOcl());

        //init frequent files overview section
        mostFrequentFileTypeTxtVw = (TextView) view.findViewById(R.id.item_frequent_file_fileType_txtVw_id);
        mostFrequentFileFrequencyTxtVw = (TextView) view.findViewById(R.id.item_frequent_file_fileFrequency_txtVw_id);
        frequentFilesViewAllBtn = (Button) view.findViewById(R.id.lyt_frequent_files_overview_btn_viewAll_id);
        frequentFilesViewAllBtn.setOnClickListener(newFrequentFilesViewAllOcl());

        //init average file size section
        averageFileSizeTxtVw = (TextView) view.findViewById(R.id.lyt_average_fileSize_txtVw_id);
        totalFilesScannedTxtVw = (TextView) view.findViewById(R.id.lyt_average_totalFilesScanned_txtVw_id);

    }

    private void initFields() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
    }

    private void setupViewForNoData() {

        largestFilesCrdVw.setVisibility(GONE);
        frequentFilesCrdVw.setVisibility(GONE);
        averageFileSizeCrdVw.setVisibility(GONE);
    }

    private boolean doesDataExist() {
        return getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("DATA_EXISTS", false);
    }

    private FileOwlManagerFactory getFactory() {
        return (FileOwlManagerFactory)((FileOwlApp) getActivity().getApplication()).getManagerFactory();
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
        Animation animation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.anim_scan_progress);
        scanFab.setAnimation(animation);
        animation.start();
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
        getActivity().startService(new Intent(getActivity(), FileScanService.class));
        ((HomeActivity)getActivity()).setShareMenuItemEnabled(false);
        //scanFab.setEnabled(false);
        //TODO animate FAB here. Show rotating refresh arrows
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

    private void setLargestFilesOverviewSection(@NonNull ScanResult scanResult) {
        if (scanResult.getFileStats().isEmpty()) {
            largestFileNameTxtVw.setText(R.string.no_data_found);
            largestFilePathTxtVw.setText(R.string.no_data_found);
            largestFileSizeTxtVw.setText(R.string.zero_size);
            largestFilesViewAllBtn.setEnabled(false);
        } else {
            largestFileNameTxtVw.setText(scanResult.getFileStats().get(0).getName());
            largestFilePathTxtVw.setText(scanResult.getFileStats().get(0).getAbsolutePath());
            largestFileSizeTxtVw.setText(scanResult.getFileStats().get(0).getSizeHumanReadable());
            largestFilesViewAllBtn.setEnabled(true);
        }
    }

    private void setFrequentFilesOverviewSection(ScanResult scanResult) {
        if (scanResult.getMostFrequentFileTypes().isEmpty()) {
            mostFrequentFileTypeTxtVw.setText(R.string.no_data_found);
            mostFrequentFileFrequencyTxtVw.setText(R.string.no_data_found);
            frequentFilesViewAllBtn.setEnabled(false);
        } else {
            String fileType = scanResult.getMostFrequentFileTypes().get(0).getFileType();
            if (!fileType.isEmpty()) {
                fileType = fileType.toUpperCase();
            }
            mostFrequentFileTypeTxtVw.setText(
                    String.format("%s %s", fileType, getString(R.string.file_type_suffix)));
            mostFrequentFileFrequencyTxtVw.setText(
                    String.format("%s %s", scanResult.getMostFrequentFileTypes().get(0).getFrequency(),
                    getString(R.string.file_frequency_suffix))
            );
            frequentFilesViewAllBtn.setEnabled(true);
        }
    }

    private void setAverageFileSizeSection(ScanResult scanResult) {
        averageFileSizeTxtVw.setText(String.format(Locale.getDefault(),"%d",scanResult.getAverageFileSize()));
        totalFilesScannedTxtVw.setText(String.format(Locale.getDefault(),"%d",scanResult.getTotalFilesScanned()));
    }

    private void saveScanResult(ScanResult scanResult) {
        Log.d(TAG, "saveScanResult: saving scan results");
        getFactory().backupManager().save(scanResult);
        Log.d(TAG, "saveScanResult: save complete");
    }

    /**
     * A broadcast receiver to receive file system scan udpates from the {@link FileScanService}.
     * Handles the following broadcasted messages:
     * <ol>
     *     <li>Scan status</li>
     *     <li>Scan result</li>
     * </ol>
     */
    private static class FileScanBroadcastReceiver extends BroadcastReceiver {

        private HomeFragment homeFragment;

        FileScanBroadcastReceiver(HomeFragment homeFragment) {
            this.homeFragment = homeFragment;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Serializable scanStatusSerializable = intent.getExtras().getSerializable(FileScanService.EXTRA_SCAN_STATUS);

            if (scanStatusSerializable != null) {
                ScanStatus status = (ScanStatus) scanStatusSerializable;
                Log.d(TAG, "onReceive: scanStatus:" + status);

                if (status == ScanStatus.SCANNING_FILE_SYSTEM) {
                    homeFragment.lastScanTimeTxtVw.setText(R.string.lyt_scan_last_scanned_value_text);
                    homeFragment.scanStatusTxtVw.setText(R.string.lyt_scan_scanStatusValue_scanInProgress);
                    homeFragment.scanStatusTxtVw.setTextColor(ContextCompat.getColor(homeFragment.getActivity(),R.color.scan_in_progress_color));
                    return;
                }

                Parcelable scanResultParcelable = intent.getExtras().getParcelable(FileScanService.EXTRA_SCAN_RESULT);
                ScanResult scanResult = null;
                if (scanResultParcelable != null) {
                    scanResult = (ScanResult) scanResultParcelable;
                    Log.d(TAG, "onReceive: scanResult:" + scanResult);
                }

                if (status == ScanStatus.SCAN_ERROR) {
                    homeFragment.lastScanTimeTxtVw.setText(DateUtils.getRelativeDateTimeString(
                            homeFragment.getActivity(),
                            new Date().getTime(),
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.WEEK_IN_MILLIS,0));
                    homeFragment.scanStatusTxtVw.setText(R.string.lyt_scan_scanStatusValue_scanError);
                    homeFragment.scanStatusTxtVw.setTextColor(ContextCompat.getColor(homeFragment.getActivity(),R.color.scan_error_color));
                    return;
                }

                if (status == ScanStatus.SCAN_COMPLETE) {
                    homeFragment.lastScanTimeTxtVw.setText(DateUtils.getRelativeDateTimeString(
                            homeFragment.getActivity(),
                            new Date().getTime(),
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.WEEK_IN_MILLIS,0));
                    homeFragment.scanStatusTxtVw.setText(R.string.lyt_scan_scanStatusValue_scanComplete);
                    homeFragment.scanStatusTxtVw.setTextColor(ContextCompat.getColor(homeFragment.getActivity(),R.color.scan_complete_color));
                    homeFragment.largestFilesCrdVw.setVisibility(VISIBLE);
                    homeFragment.frequentFilesCrdVw.setVisibility(VISIBLE);
                    homeFragment.averageFileSizeCrdVw.setVisibility(VISIBLE);
                }

                assert scanResult != null;
                homeFragment.setLargestFilesOverviewSection(scanResult);
                homeFragment.setFrequentFilesOverviewSection(scanResult);
                homeFragment.setAverageFileSizeSection(scanResult);
                homeFragment.saveScanResult(scanResult);


            }

            //this.progressBar.setProgress(anInt);
        }
    }
}
