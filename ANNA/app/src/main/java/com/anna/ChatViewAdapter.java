package com.anna;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.util.IndexedHashMap;


public class ChatViewAdapter extends RecyclerView
        .Adapter<ChatViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "ChatViewAdapter";
    private IndexedHashMap<String, NotificationData> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView img;
        TextView sender;
        TextView message;
        TextView time;

        public DataObjectHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.profileImg);
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
        holder.img.setImageBitmap(mDataset.getValueAt(position).getIcon());
        holder.sender.setText(mDataset.getValueAt(position).getTitle());
        holder.message.setText(mDataset.getValueAt(position).getText());
        holder.time.setText(mDataset.getValueAt(position).getTime());
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
        public void onItemClick(int position, View v);
    }
}