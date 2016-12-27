package com.merryapps.framework;

import android.util.Log;

import com.merryapps.fileowl.model.db.DaoMaster;


/**
 * Created by mephisto on 1/17/16.
 * //TODO fix all Created by mephisto
 */
public abstract class AbstractManagerFactory {

    private static String TAG = "AbstractManagerFactory";
    protected DaoMaster daoMaster;

    public AbstractManagerFactory(DaoMaster daoMaster) {
        Log.d(TAG, "AbstractManagerFactory() called with: " + "daoMaster = [" + daoMaster + "]");
        this.daoMaster = daoMaster;
    }

}
