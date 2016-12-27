package com.merryapps.fileowl.ui;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.merryapps.fileowl.R;
import com.merryapps.fileowl.model.FileStat;

import java.util.List;
import java.util.Random;

/**
 * Adapter for LargeFileListFragment.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class LargeFileListAdapter extends RecyclerView.Adapter<LargeFileListAdapter.LargeFileViewHolder> {

    private static final String TAG = "LargeFileListAdapter";
    private static final String NUMBER_FORMAT = "%d";
    private List<FileStat> fileStats;
    private Context context;
    private Random random = new Random();

    LargeFileListAdapter(Context context, List<FileStat> fileStats) {
        this.fileStats = fileStats;
        this.context = context;
    }

    @Override
    public LargeFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_large_file, parent, false);
        return new LargeFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LargeFileViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        FileStat fileStat = fileStats.get(position);

        bind(holder, fileStat);
    }

    private void bind(LargeFileViewHolder holder, FileStat fileStat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.fileIconImgVw.setImageDrawable(context.getResources().getDrawable(getRandomIcon(), null));
        } else {
            holder.fileIconImgVw.setImageDrawable(context.getResources().getDrawable(getRandomIcon()));
        }
        holder.fileNameTxtVw.setText(fileStat.getName());
        holder.filePathTxtVw.setText(fileStat.getAbsolutePath());
        holder.fileSizeTxtVw.setText(fileStat.getSizeHumanReadable());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount() called");
        return fileStats.size();
    }

    private int getRandomIcon() {
        Log.d(TAG, "getRandomIcon() called");
        int randomNumber = random.nextInt(8);
        switch (randomNumber) {
            case 1:
                return R.drawable.ic_plain_file_blue;
            case 2:
                return R.drawable.ic_plain_file_brown;
            case 3:
                return R.drawable.ic_plain_file_orange;
            case 4:
                return R.drawable.ic_plain_file_pink;
            case 5:
                return R.drawable.ic_plain_file_purple;
            case 6:
                return R.drawable.ic_plain_file_red;
            case 7:
                return R.drawable.ic_plain_file_teal;
            default:
                return R.drawable.ic_plain_file_teal;
        }
    }

    static class LargeFileViewHolder extends RecyclerView.ViewHolder {

        private ImageView fileIconImgVw;
        private TextView fileNameTxtVw;
        private TextView filePathTxtVw;
        private TextView fileSizeTxtVw;

        LargeFileViewHolder(View view) {
            super(view);
            fileIconImgVw = (ImageView) view.findViewById(R.id.item_large_file_icon_imgVw_id);
            fileNameTxtVw = (TextView) view.findViewById(R.id.item_large_file_fileName_txtVw_id);
            filePathTxtVw = (TextView) view.findViewById(R.id.item_large_file_filePath_txtVw_id);
            fileSizeTxtVw = (TextView) view.findViewById(R.id.item_large_file_fileSize_txtVw_id);
        }
    }

}
