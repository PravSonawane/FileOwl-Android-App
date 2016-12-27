package com.merryapps.fileowl.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.merryapps.fileowl.R;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private boolean shareMenuItemEnabled = true;
    private HomeFragment homeFragment;

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

        if (savedInstanceState == null) {
            //load HomeFragment
            loadFragment();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("SHARED_MENU_ITEM_ENABLED",shareMenuItemEnabled);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareMenuItemEnabled = savedInstanceState.getBoolean("SHARED_MENU_ITEM_ENABLED");
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (shareMenuItemEnabled) {
            menu.removeItem(1);
            menu.add(0, 1, Menu.NONE, "Share").setIcon(R.drawable.ic_share)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            menu.removeItem(1);
            menu.add(0, 1, Menu.NONE, "Share").setIcon(R.drawable.ic_share_disabled)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            homeFragment.share();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    /**
     * Enables/disables the 'Share' menu item. To be used by fragments.
     * @param enabled the new status
     */
    void setShareMenuItemEnabled(boolean enabled) {
        shareMenuItemEnabled = enabled;
        invalidateOptionsMenu();
    }

    private void loadFragment() {

        if (getPreferences(MODE_PRIVATE).getBoolean("IS_FIRST_TIME_SCAN", true)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_home_frm_lyt_placeHolder_id, new GreetingFragment())
                    .commit();
        } else {
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_home_frm_lyt_placeHolder_id, homeFragment)
                .commit();
        }
    }
}
