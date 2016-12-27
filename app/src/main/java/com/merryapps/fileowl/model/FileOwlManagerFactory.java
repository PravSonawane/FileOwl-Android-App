package com.merryapps.fileowl.model;

import com.merryapps.fileowl.model.db.DaoMaster;
import com.merryapps.framework.AbstractManagerFactory;

/**
 * //TODO add description here
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileOwlManagerFactory extends AbstractManagerFactory {

    public FileOwlManagerFactory(DaoMaster daoMaster) {
        super(daoMaster);
    }

    public BackupManager backupManager() {
        return new BackupManager(daoMaster.newSession().getLargeFileEntityDao(),
                daoMaster.newSession().getFrequentFileEntityDao(),
                daoMaster.newSession().getScanStatEntityDao());
    }
}
