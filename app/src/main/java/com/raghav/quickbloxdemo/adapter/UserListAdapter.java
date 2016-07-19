package com.raghav.quickbloxdemo.adapter;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;
import com.raghav.quickbloxdemo.R;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.DataObjectHolder> {
    private static MyClickListener myClickListener;
    private ArrayList<QBUser> users;

    public UserListAdapter(ArrayList<QBUser> users) {
        this.users = users;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        UserListAdapter.myClickListener = myClickListener;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_list, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.txtUserName.setText(users.get(position).getEmail());
    }

    public void addItem(QBUser s, int index) {
        users.add(s);
        notifyItemInserted(index);
    }

    public QBUser getItem(int i) {
        return users.get(i);
    }

    public void deleteItem(int index) {
        users.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteAll() {
        users.clear();
        notifyDataSetChanged();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView txtUserName;
        AppCompatImageButton btnStatus;

        DataObjectHolder(View itemView) {
            super(itemView);
            txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);
            btnStatus = (AppCompatImageButton) itemView.findViewById(R.id.btnStatus);
            txtUserName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getLayoutPosition(), v);
        }
    }
}