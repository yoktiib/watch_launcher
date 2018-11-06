package com.pomohouse.launcher.fragment.settings;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pomohouse.launcher.R;
import com.pomohouse.library.base.listener.OnItemClickListener;
import com.pomohouse.library.manager.AppContextor;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 8/29/16 AD.
 */
public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> {
    private final OnItemClickListener mListener;
    private Context mContext;
    private ArrayList<SettingMenuData> settingMenuData;

    public void setDataNotifyDataChange(ArrayList<SettingMenuData> settingMenuData) {
        this.settingMenuData = settingMenuData;
        notifyDataSetChanged();
    }

    public SettingAdapter(ArrayList<SettingMenuData> settingMenuData, OnItemClickListener listener) {
        this.settingMenuData = settingMenuData;
        this.mListener = listener;
        this.mContext = AppContextor.getInstance().getContext();
    }


    @Override
    public SettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adapter_menu, parent, false);
        ButterKnife.bind(itemView);
        return new SettingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SettingViewHolder holder, int position) {
        SettingMenuData setting = settingMenuData.get(position);
        holder.tvName.setText(setting.getName());
        holder.ivIcon.setImageResource(setting.getIcon());
    }

    @Override
    public int getItemCount() {
        if (settingMenuData == null)
            return 0;
        return settingMenuData.size();
    }

    public class SettingViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.ivIcon)
        AppCompatImageView ivIcon;
        @BindView(R.id.fContainer)
        FrameLayout fContainer;

        private SettingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(v -> mListener.onItemClick(v, getLayoutPosition(), false));
            //itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //clickListener.onItemClick(view, getLayoutPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            //clickListener.onItemClick(view, getLayoutPosition(), true);
            return true;
        }
    }
}