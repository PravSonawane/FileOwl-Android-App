package com.merryapps.diskhero.ui;

import com.merryapps.diskhero.model.FileStat;

/**
 * a view holder
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */

public class FileViewHolder {

    private FileStat fileStat;
    private int imageIconId;

    FileViewHolder(FileStat fileStat, int imageIconId) {
        this.fileStat = fileStat;
        this.imageIconId = imageIconId;
    }

    String getName() {
        return fileStat.getName();
    }

    String getAbsolutePath() {
        return fileStat.getAbsolutePath();
    }

    int getImageIconId() {
        return imageIconId;
    }
}
