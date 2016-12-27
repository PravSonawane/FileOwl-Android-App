package com.merryapps.framework;

import android.util.Log;

import com.merryapps.fileowl.model.db.DaoMaster;


/**
 * AbstractFactory to be extended by custom manager factories for apps.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public abstract class AbstractManagerFactory {

    private static String TAG = "AbstractManagerFactory";
    protected DaoMaster daoMaster;

    public AbstractManagerFactory(DaoMaster daoMaster) {
        Log.d(TAG, "AbstractManagerFactory() called with: " + "daoMaster = [" + daoMaster + "]");
        this.daoMaster = daoMaster;
    }

}
