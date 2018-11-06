package com.pomohouse.bff.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pomohouse.bff.R;
import com.pomohouse.bff.dao.FriendCollection;
import com.pomohouse.bff.dao.FriendItemModel;
import com.pomohouse.bff.fragment.avatar.AvatarCollection;
import com.pomohouse.library.manager.AppContextor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 8/29/16 AD.
 */
public class BestFriendForeverAdapter extends RecyclerView.Adapter<BestFriendForeverAdapter.BestFriendViewHolder> {
    protected AvatarCollection avatarCollection = AvatarCollection.getInstance();
    private final OnItemClickListener mListener;
    private Context mContext;

    public void setDataNotifyDataChange(FriendCollection friendCollection) {
        this.friendCollection = friendCollection;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onItemClick(View view, FriendItemModel model, boolean isLongClick);

    }


    private FriendCollection friendCollection;

    public BestFriendForeverAdapter(FriendCollection friendCollection, OnItemClickListener listener) {
        this.friendCollection = friendCollection;
        this.mListener = listener;
        this.mContext = AppContextor.getInstance().getContext();
    }


    @Override
    public BestFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        ButterKnife.bind(itemView);
        return new BestFriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BestFriendViewHolder holder, int position) {
        FriendItemModel familyModel = friendCollection.getFriendDaoList().get(position);
        String name = familyModel.getName();
        if (name.length() > 12) {
            name = name.substring(0, 12) + "..";
        }
        holder.tvName.setText(name);
        //holder.itContent.setOnClickListener(v -> mListener.onItemClick(v, familyModel, false));
        if (familyModel.getAvatarType() == 0 && familyModel.getAvatar() != null && !familyModel.getAvatar().isEmpty()) {
            holder.ivContactAvatarIcon.setVisibility(View.VISIBLE);
            holder.ivContactAvatar.setVisibility(View.GONE);
            holder.ivContactAvatarIcon.setImageResource(avatarCollection.getAvatarMap().get(familyModel.getAvatar()));
        } else {
            holder.ivContactAvatarIcon.setVisibility(View.GONE);
            holder.ivContactAvatar.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(familyModel.getAvatar())
                    .error(R.drawable.placeholder).transform(new CircleTransform(mContext)).into(holder.ivContactAvatar);
        }
    }

    public void setAvatarFromDrawable(String avatar, ImageView ivAvatar) {
        try {
            if (avatarCollection != null) {
                Drawable icon = VectorDrawableCompat.create(mContext.getResources(), avatarCollection.getAvatarMap().get(avatar), mContext.getTheme());
                if (icon != null)
                    ivAvatar.setImageDrawable(icon);
            }
        } catch (Exception ignore) {
            //ivAvatar.setImageResource(R.drawable.ic);
        }
    }


    @Override
    public int getItemCount() {
        if (friendCollection == null || friendCollection.getFriendDaoList() == null)
            return 0;
        return friendCollection.getFriendDaoList().size();
    }

    class BestFriendViewHolder extends RecyclerView.ViewHolder
            /*implements View.OnClickListener, View.OnLongClickListener */ {

        /*private OnItemClickListener clickListener;

        public void setClickListener(OnItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }*/

        @BindView(R.id.tvName)
        public TextView tvName;
        @BindView(R.id.ivContactAvatar)
        AppCompatImageView ivContactAvatar;
        @BindView(R.id.ivContactAvatarIcon)
        AppCompatImageView ivContactAvatarIcon;
        @BindView(R.id.itContent)
        public LinearLayout itContent;

        public BestFriendViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(v -> mListener.onItemClick(v, friendCollection.getFriendDaoList().get(getLayoutPosition()), false));
            /*itemView.setOnLongClickListener(this);*/
        }

      /*  @Override
        public void onClick(View view) {
            clickListener.onItemClick(view, friendCollection.getFriendDaoList().get(getLayoutPosition()), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemClick(view, friendCollection.getFriendDaoList().get(getLayoutPosition()), true);
            return true;
        }*/
    }
}