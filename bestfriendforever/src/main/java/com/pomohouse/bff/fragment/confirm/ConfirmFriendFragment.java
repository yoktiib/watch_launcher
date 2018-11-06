package com.pomohouse.bff.fragment.confirm;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.pomohouse.bff.R;
import com.pomohouse.bff.base.BaseFragment;
import com.pomohouse.bff.dao.FetchFriend;
import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.bff.di.presenter.ConfirmFriendPresenterModule;
import com.pomohouse.bff.fragment.avatar.AvatarCollection;
import com.pomohouse.bff.fragment.CircleTransform;
import com.pomohouse.bff.fragment.confirm.presenter.IConfirmFriendPresenter;
import com.pomohouse.bff.fragment.result.ResultFriendFragment;
import com.pomohouse.library.networks.MetaDataNetwork;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sirawit on 8/28/16 AD.
 */
public class ConfirmFriendFragment extends BaseFragment implements IConfirmFriendView {
    protected AvatarCollection avatarCollection;
    @Inject
    IConfirmFriendPresenter presenter;
    private FetchFriend fetchFriend;
    @BindView(R.id.dot_progress_bar)
    DotProgressBar dot_progress_bar;

    @BindView(R.id.ivContactAvatar)
    AppCompatImageView ivContactAvatar;
    @BindView(R.id.ivContactAvatarIcon)
    AppCompatImageView ivContactAvatarIcon;
    @BindView(R.id.tvName)
    TextView tvName;

    public static ConfirmFriendFragment newInstance(FetchFriend fetchFriend) {
        ConfirmFriendFragment fragment = new ConfirmFriendFragment();
        Bundle args = new Bundle();
        args.putParcelable(FRIEND, Parcels.wrap(fetchFriend));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        avatarCollection = AvatarCollection.getInstance();
        fetchFriend = Parcels.unwrap(getArguments().getParcelable(FRIEND));
        dot_progress_bar.setVisibility(View.GONE);
        presenter.onInitial(fetchFriend);
    }

    public void setAvatarFromDrawable(String avatar, ImageView ivAvatar) {
        try {
            if (avatarCollection != null) {
                Drawable icon = VectorDrawableCompat.create(getActivity().getResources(), avatarCollection.getAvatarMap().get(avatar), getActivity().getTheme());
                if (icon != null)
                    ivAvatar.setImageDrawable(icon);
            }
        } catch (Exception ignore) {
            //ivAvatar.setImageResource(R.drawable.ic);
        }
    }

    @OnClick(R.id.btnCancel)
    void onCancelFriend() {
        dot_progress_bar.setVisibility(View.VISIBLE);
        presenter.requestCancelFriend();
    }

    @OnClick(R.id.btnConfirm)
    void onAcceptFriend() {
        dot_progress_bar.setVisibility(View.VISIBLE);
        presenter.requestAcceptFriend();
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new ConfirmFriendPresenterModule(this));
    }

    @Override
    public void failureAddFriend(MetaDataNetwork error) {
        dot_progress_bar.setVisibility(View.GONE);
        ResponseFriend responseFriend = new ResponseFriend();
        responseFriend.setF_status("N");
        replaceFragmentToBackStack(ResultFriendFragment.newInstance(responseFriend), ResultFriendFragment.class.getName());
    }

    @Override
    public void successAddFriend(ResponseFriend responseFriend) {
        dot_progress_bar.setVisibility(View.GONE);
        if (responseFriend == null) {
            responseFriend = new ResponseFriend();
            responseFriend.setF_status("N");
        } else {
            if (responseFriend.getF_status() == null || responseFriend.getF_status().isEmpty())
                responseFriend.setF_status("N");
        }
        replaceFragmentToBackStack(ResultFriendFragment.newInstance(responseFriend), ResultFriendFragment.class.getName());
    }

    @Override
    public void setAvatar(String avatar) {
        if (fetchFriend.getAvatarType() == 0 && fetchFriend.getAvatar() != null && !fetchFriend.getAvatar().isEmpty()) {
            ivContactAvatarIcon.setVisibility(View.VISIBLE);
            ivContactAvatar.setVisibility(View.GONE);
            ivContactAvatarIcon.setImageResource(avatarCollection.getAvatarMap().get(avatar));
        } else {
            ivContactAvatarIcon.setVisibility(View.GONE);
            ivContactAvatar.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(avatar)
                    .error(R.drawable.placeholder).transform(new CircleTransform(mContext)).into(ivContactAvatar);
        }
    }

    @Override
    public void setName(String name) {
        if (name.length() > 8) {
            name = name.substring(0, 8) + "..";
        }
        tvName.setText(name);
    }
}
