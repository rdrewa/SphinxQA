package pl.nemolab.sphinxqa.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.model.Video;

public class VideoExpandableAdapter extends BaseExpandableListAdapter {

    private static final int GROUP_LAYOUT = R.layout.group_video;
    private static final int CHILD_LAYOUT = R.layout.child_video;
    private static final int TOAST_LENGTH = Toast.LENGTH_SHORT;

    private Activity activity;
    private List<Video> videos;
    private int expandedItem;
    private View expandedView;
    private final View.OnClickListener listenerPickSrc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPickSrcDialog();
        }
    };
    private final View.OnClickListener listenerPickDst = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPickDstDialog();
        }
    };
    private final View.OnClickListener listenerStartActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startPlayerActivity();
        }
    };
    private String videoFile, srcFile, dstFile, videoPath, videoDir;
    private Video video;

    public VideoExpandableAdapter(Activity activity, List<Video> videos) {
        this.activity = activity;
        this.videos = videos;
    }

    public int getExpandedItem() {
        return expandedItem;
    }

    public void setExpandedItem(int expandedItem) {
        this.expandedItem = expandedItem;
    }

    @Override
    public int getGroupCount() {
        return videos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return videos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return videos.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(GROUP_LAYOUT, null);
            holder = new GroupHolder();
            holder.title = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.duration = (TextView) convertView.findViewById(R.id.txtDuration);
            convertView.setTag(holder);
        }
        holder = (GroupHolder) convertView.getTag();
        Video video = (Video) getGroup(groupPosition);
        holder.title.setText(video.getTitle());
        holder.duration.setText(video.getDuration());
        expandedItem = groupPosition;
        expandedView = convertView;
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        Video video = (Video) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(CHILD_LAYOUT, null);
            holder = new ChildHolder();
            holder.path = (TextView) convertView.findViewById(R.id.txtPath);
            holder.src = (EditText) convertView.findViewById(R.id.edtSrcFile);
            holder.dst = (EditText) convertView.findViewById(R.id.edtDstFile);
            holder.btnSrc = (Button) convertView.findViewById(R.id.btnSrc);
            holder.btnSrc.setOnClickListener(listenerPickSrc);
            holder.btnDst = (Button) convertView.findViewById(R.id.btnDst);
            holder.btnDst.setOnClickListener(listenerPickDst);
            holder.btnPlay = (Button) convertView.findViewById(R.id.btnPlay);
            holder.btnPlay.setOnClickListener(listenerStartActivity);
            convertView.setTag(holder);
        }
        holder = (ChildHolder) convertView.getTag();
        holder.path.setText(video.getPath());
        return convertView;
    }

    private String[] findFiles(String videoPath) {
        List<String> files = new ArrayList<>();
        File file = new File(videoPath);
        if (file != null && file.exists()) {
            File parent = file.getParentFile();
            parent.getAbsolutePath();
            for (String item : parent.list()) {
                if (item.endsWith(".srt")) {
                    files.add(item);
                }
            }
        }
        return files.toArray(new String[files.size()]);
    }

    private void showPickSrcDialog() {
        video = videos.get(expandedItem);
        final String[] files = findFiles(video.getPath());
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.dlg_src_title));
        builder.setItems(files, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file = files[which];
                srcFile = videoDir + "/" + file;
                String msg = "Title: " + file + "\n"
                        + "Path: " + srcFile;
                TextView edtSrcFile = (TextView) expandedView.findViewById(R.id.edtSrcFile);
                edtSrcFile.setText(file);
                Toast.makeText(activity, msg, TOAST_LENGTH).show();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();

    }

    private void showPickDstDialog() {

    }

    private void startPlayerActivity() {
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupHolder {
        public TextView title;
        public TextView duration;
    }

    static class ChildHolder {
        public TextView path;
        public EditText src;
        public EditText dst;
        public Button btnSrc;
        public Button btnDst;
        public Button btnPlay;
    }
}
