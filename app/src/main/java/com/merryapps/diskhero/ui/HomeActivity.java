package com.merryapps.diskhero.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.merryapps.diskhero.R;
import com.merryapps.diskhero.model.FileSystemUtil;

public class HomeActivity extends AppCompatActivity {

    /**
     * Request code value for READ_EXTERNAL_STORAGE permission.
     * Use this as an argument to
     * {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])}
     */
    private static final int REQ_CODE_READ_EXT_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white,null));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }

        setSupportActionBar(toolbar);

        //load HomeFragment
        loadHomeFragment();
        //loadLargeFileFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void loadHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_home_frm_lyt_placeHolder_id, new HomeFragment())
                .commit();
    }

    private void loadLargeFileFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_home_frm_lyt_placeHolder_id, new LargeFileListFragment())
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQ_CODE_READ_EXT_STORAGE: {
                if (grantResults.length >= 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scan();
                }
            }
        }
    }

    private void scan() {
        new FileSystemUtil().scanFileSystem();
    }

    /**
     * Check if the user has the READ_EXTERNAL_STORAGE permission on M
     * and above and prompt if requried.
     */
    private void checkPermAndScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

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
     * @return {@link android.view.View.OnClickListener} for Scan button.
     */
    private View.OnClickListener newScanBtnOcl() {
        return view -> checkPermAndScan();
    }

    /**
     * Creates and returns an {@link AlertDialog} for explaning why READ_EXTERNAL_STORAGE
     * permission is requied.
     */
    private AlertDialog createAlertDialog() {
        return new AlertDialog.Builder(this)
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
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };
    }

    /**
     * @return {@link android.view.View.OnClickListener} for positive button of explanation
     * alert dialog.
     */
    private DialogInterface.OnClickListener newPositiveBtnOcl() {
        return new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
        return ActivityCompat.shouldShowRequestPermissionRationale(this, readExternalStorage);
    }

    /**
     * Check if the user has the given permission.
     * @param permission the permission to be checked.
     * @return {@code true} if the user has the given permission, {@code false} otherwise.
     */
    private boolean isAllowed(String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
