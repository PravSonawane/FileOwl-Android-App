package com.merryapps.fileowl.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database helper for upgrade
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class FileOwlOpenHelper extends DaoMaster.DevOpenHelper {

    private static final String TAG = "FileOwlOpenHelper";

    public FileOwlOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        Log.d(TAG, "FileOwlOpenHelper() called with: " + "context = [" + context + "], name = [" + name + "], factory = [" + factory + "]");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() called with: " + "db = [" + db + "], oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");

        switch(oldVersion) {

            case 1:
                //upgrade logic from version 1 to 2
                /* break was omitted by purpose. */
            case 2:
                //upgrade logic from version 2 to 3
                /* break was omitted by purpose. */
            case 3:
                //upgrade logic from version 3 to 4
                break;
            default:
                throw new IllegalStateException(
                        "unknown oldVersion " + oldVersion);
        }
    }
}