package com.merryapps.diskhero.ui;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.merryapps.diskhero.R;
import com.merryapps.diskhero.model.FileTypeFrequency;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Adapter for FrequentFileListFragment.
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class FrequentFileListAdapter extends RecyclerView.Adapter<FrequentFileListAdapter.FrequentFileViewHolder> {

    private static final String TAG = "FrequentFileListAdapter";
    private static final String NUMBER_FORMAT = "%d";
    private List<FileTypeFrequency> fileTypeFrequencies;
    private Context context;
    private Random random = new Random();

    FrequentFileListAdapter(Context context, List<FileTypeFrequency> fileTypeFrequencies) {
        this.fileTypeFrequencies = fileTypeFrequencies;
        this.context = context;
    }

    @Override
    public FrequentFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frequent_file, parent, false);
        return new FrequentFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FrequentFileViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        FileTypeFrequency fileStat = fileTypeFrequencies.get(position);

        bind(holder, fileStat);
    }

    private void bind(FrequentFileViewHolder holder, FileTypeFrequency fileTypeFrequency) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.fileIconImgVw.setImageDrawable(context.getResources().getDrawable(getRandomIcon(), null));
        } else {
            holder.fileIconImgVw.setImageDrawable(context.getResources().getDrawable(getRandomIcon()));
        }
        holder.fileTypeTxtVw.setText(fileTypeFrequency.getFileType());
        holder.fileRequencyTxtVw.setText(
                String.format(Locale.getDefault(), NUMBER_FORMAT, fileTypeFrequency.getFrequency()));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount() called");
        return fileTypeFrequencies.size();
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

    static class FrequentFileViewHolder extends RecyclerView.ViewHolder {

        private ImageView fileIconImgVw;
        private TextView fileTypeTxtVw;
        private TextView fileRequencyTxtVw;

        FrequentFileViewHolder(View view) {
            super(view);
            fileIconImgVw = (ImageView) view.findViewById(R.id.item_frequent_file_icon_imgVw_id);
            fileTypeTxtVw = (TextView) view.findViewById(R.id.item_frequent_file_fileType_txtVw_id);
            fileRequencyTxtVw = (TextView) view.findViewById(R.id.item_frequent_file_fileFrequency_txtVw_id);
        }
    }

}
