package com.pomohouse.bff.fragment.wait;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pomohouse.bff.R;
import com.pomohouse.bff.base.BaseFragment;
import com.pomohouse.bff.dao.FriendItemModel;
import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.bff.di.presenter.WaitingFriendPresenterModule;
import com.pomohouse.bff.fragment.CircleTransform;
import com.pomohouse.bff.fragment.avatar.AvatarCollection;
import com.pomohouse.bff.fragment.result.ResultFriendFragment;
import com.pomohouse.bff.fragment.wait.presenter.IWaitingFriendPresenter;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.networks.MetaDataNetwork;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Admin on 8/29/16 AD.
 */
public class WaitingFriendFragment extends BaseFragment implements IWaitingFriendView {
    protected AvatarCollection avatarCollection;
    @Inject
    IWaitingFriendPresenter presenter;
    FriendItemModel friendDao;

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.ivContactAvatar)
    AppCompatImageView ivContactAvatar;
    @BindView(R.id.viewWaiting)
    AppCompatImageView viewWaiting;
    @BindView(R.id.ivContactAvatarIcon)
    AppCompatImageView ivContactAvatarIcon;

    private GifDrawable gifLoader;

    public static WaitingFriendFragment newInstance(FriendItemModel friendDao) {
        WaitingFriendFragment fragment = new WaitingFriendFragment();
        Bundle args = new Bundle();
        args.putParcelable(FRIEND, Parcels.wrap(friendDao));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        avatarCollection = AvatarCollection.getInstance();
        friendDao = Parcels.unwrap(getArguments().getParcelable(FRIEND));
        presenter.onInitial(friendDao);
        try {
            gifLoader = new GifDrawable(getResources(), R.drawable.waiting_friend);
            viewWaiting.setImageDrawable(gifLoader);
            gifLoader.addAnimationListener(loopNumber -> gifLoader.reset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.requestStatusAnswerFriend(WearerInfoUtils.getInstance().getImei());
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new WaitingFriendPresenterModule(this));
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.stopRequestResponseFriend();
    }

    @Override
    public void gotoResultFriend(ResponseFriend responseFriend) {
        if (!responseFriend.getF_status().equalsIgnoreCase("R")) {
            presenter.stopRequestResponseFriend();
            replaceFragmentToBackStack(ResultFriendFragment.newInstance(responseFriend), ResultFriendFragment.class.getName());
        }
    }

    @Override
    public void failureRequestFriend(MetaDataNetwork error) {

    }

    @Override
    public void setName(String name) {
        String friendName = name;
        if (friendName.length() > 8) {
            friendName = name.substring(0, 8) + "..";
        }
        tvName.setText(String.format(Locale.ENGLISH, getString(R.string.waiting_format), friendName));
    }

    @Override
    public void setAvatar(String avatar) {
        if (friendDao.getAvatarType() == 0 && friendDao.getAvatar() != null && !friendDao.getAvatar().isEmpty()) {
            ivContactAvatarIcon.setVisibility(View.VISIBLE);
            ivContactAvatar.setVisibility(View.GONE);
            ivContactAvatarIcon.setImageResource(avatarCollection.getAvatarMap().get(avatar));
        } else {
            ivContactAvatarIcon.setVisibility(View.GONE);
            ivContactAvatar.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(friendDao.getAvatar())
                    .error(R.drawable.placeholder).transform(new CircleTransform(getContext())).into(ivContactAvatar);
        }
    }
}
