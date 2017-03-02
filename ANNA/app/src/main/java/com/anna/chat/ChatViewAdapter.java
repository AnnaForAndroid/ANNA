package com.anna.chat;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.R;
import com.anna.notification.NotificationData;
import com.anna.util.IndexedHashMap;
import com.anna.util.Module;
import com.anna.util.MyApplication;


public class ChatViewAdapter extends RecyclerView
        .Adapter<ChatViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "ChatViewAdapter";
    private IndexedHashMap<String, NotificationData> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        private ImageView profileImg;
        private ImageView appImg;
        private TextView sender;
        private TextView message;
        private TextView time;

        public DataObjectHolder(View itemView) {
            super(itemView);
            profileImg = (ImageView) itemView.findViewById(R.id.profileImg);
            appImg = (ImageView) itemView.findViewById(R.id.applicationImg);
            sender = (TextView) itemView.findViewById(R.id.sender);
            message = (TextView) itemView.findViewById(R.id.message);
            time = (TextView) itemView.findViewById(R.id.time);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ChatViewAdapter(IndexedHashMap<String, NotificationData> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_card_view, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        if (mDataset.getValueAt(position).getIcon() == null) {
            Drawable defaultProfile = MyApplication.getAppContext().getResources().getDrawable(R.drawable.default_profile);
            holder.profileImg.setImageDrawable(defaultProfile);
        }
        if (holder.profileImg.getDrawable() == null) {
            holder.profileImg.setImageBitmap(mDataset.getValueAt(position).getIcon());
        }
        holder.sender.setText(mDataset.getValueAt(position).getTitle());
        holder.message.setText(mDataset.getValueAt(position).getText());
        holder.time.setText(mDataset.getValueAt(position).getTime());
        for (Module m : Module.modules) {
            if (mDataset.getValueAt(position).getPackageName().equals(m.getPackageName())) {
                holder.appImg.setImageDrawable(m.getIcon());
            }
        }
    }

    public void addItem(NotificationData dataObj, String index) {
        mDataset.put(index, dataObj);
        notifyItemInserted(mDataset.getPositionOfKey(index));
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}