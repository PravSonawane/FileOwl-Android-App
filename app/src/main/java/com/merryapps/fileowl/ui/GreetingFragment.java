package com.merryapps.fileowl.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merryapps.fileowl.R;
import com.merryapps.fileowl.model.FileScanService;

/**
 * Backs up the Greetings/No data screen
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class GreetingFragment extends Fragment {

    private static final String TAG = "GreetingFragment";

    /**
     * Request code value for READ_EXTERNAL_STORAGE permission.
     * Use this as an argument to
     * {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}
     */
    private static final int REQ_CODE_READ_EXT_STORAGE = 100;

    private FloatingActionButton scanFab;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_greeting, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        rootView = view;
        scanFab = (FloatingActionButton) view.findViewById(R.id.fragment_greeting_scan_fab_id);
        scanFab.setOnClickListener(newScanFabOcl());
    }

    private View.OnClickListener newScanFabOcl() {
        return view -> checkPermAndScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQ_CODE_READ_EXT_STORAGE: {
                if (grantResults.length >= 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "onRequestPermissionsResult: Permission granted.");
                    startScanService();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Snackbar.make(scanFab, R.string.no_permissions_text, Snackbar.LENGTH_LONG)
                                .setAction("SETTINGS", view -> {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                })
                                .show();
                    }

                }
            }
        }
    }

    private void startScanService() {
        Log.v(TAG, "startScanService: Starting file startScanService");
        getActivity().startService(new Intent(getActivity(), FileScanService.class));
        this.getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.activity_home_frm_lyt_placeHolder_id, new HomeFragment())
            .commitAllowingStateLoss();
    }

    /**
     * Check if the user has the READ_EXTERNAL_STORAGE permission on M
     * and above and prompt if requried.
     */
    private void checkPermAndScan() {

        if (isAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            startScanService();
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
}
