package com.pomohouse.launcher.fragment.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.fragment.avatar.AvatarCollection;
import com.pomohouse.launcher.fragment.avatar.CircleTransform;
import com.pomohouse.launcher.models.contacts.ContactModel;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.manager.AppContextor;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 10/13/2016 AD.
 */

public class ContactItemFragment extends Fragment {

    protected AvatarCollection avatarCollection = AvatarCollection.getInstance();
    private final static String CONTACT = "CONTACT";
    private final static String POSITION = "POSITION";

    @BindView(R.id.ivContactAvatar)
    AppCompatImageView ivContactAvatar;
    @BindView(R.id.ivContactAvatarIcon)
    AppCompatImageView ivContactAvatarIcon;
    @BindView(R.id.ivContactType)
    ImageView ivContactType;
    @BindView(R.id.tvContactName)
    TextView tvContactName;
    @BindView(R.id.tvContactMissCall)
    TextView tvContactMissCall;
    @BindView(R.id.boxContact)
    RelativeLayout boxContact;
    @BindView(R.id.ivCall)
    ImageView ivCall;
    @BindView(R.id.boxNotification)
    RelativeLayout boxNotification;

    @BindView(R.id.boxAvatarUrl)
    RelativeLayout boxAvatarUrl;

    @BindView(R.id.boxAvatarIcon)
    RelativeLayout boxAvatarIcon;

    ContactModel contactModel;
    private ContactFragment.OnContactItemClickListener itemClickListener;

    public static ContactItemFragment newInstance(ContactModel menuModel, ContactFragment.OnContactItemClickListener itemClickListener, int position) {
        ContactItemFragment fragment = new ContactItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(CONTACT, menuModel);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        fragment.itemClickListener = itemClickListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        contactModel = getArguments().getParcelable(CONTACT);
        int position = getArguments().getInt(POSITION);
        if (contactModel != null) {
            int missCalls = CombineObjectConstance.getInstance().getCallEntity().calculateMissCall(contactModel.getContactId());
            if (missCalls > 0) {
                boxNotification.setVisibility(View.VISIBLE);
                tvContactMissCall.setText(String.valueOf(missCalls));
                AppContextor.getInstance().getContext().getContentResolver().delete(POMOContract.CallEntry.CONTENT_URI, POMOContract.CallEntry.NUMBER + "=" + contactModel.getContactId(), new String[]{});
            } else {
                boxNotification.setVisibility(View.GONE);
            }
            if (contactModel.getAvatarType() == 0 && contactModel.getAvatar() != null && !contactModel.getAvatar().isEmpty()) {
                boxAvatarIcon.setVisibility(View.VISIBLE);
                boxAvatarUrl.setVisibility(View.GONE);
                ivContactAvatarIcon.setImageResource(avatarCollection.getAvatarMap().get(contactModel.getAvatar()));
            } else {
                boxAvatarUrl.setVisibility(View.VISIBLE);
                boxAvatarIcon.setVisibility(View.GONE);
                Glide.with(this).load(contactModel.getAvatar()).error(R.drawable.placeholder).transform(new CircleTransform(getContext())).into(ivContactAvatar);
            }
            if (contactModel.getContactType() != null && !contactModel.getContactType().isEmpty())
                setContactTypeFromDrawable(contactModel.getContactType(), ivContactType);
            String name = contactModel.getName();
            if (name.length() > 12) {
                name = name.substring(0, 10) + "..";
                tvContactName.setText(name);
            } else
                tvContactName.setText(name);
            if ((position % 2) == 0)
                boxContact.setBackgroundColor(getResources().getColor(R.color.waffle_white_color));
            else
                boxContact.setBackgroundColor(getResources().getColor(R.color.waffle_grey_01_color));
            ivCall.setOnClickListener(v -> {
                if (itemClickListener != null)
                    itemClickListener.onItemClickListener(contactModel);
            });
        }
    }

    public void setContactTypeFromDrawable(String type, ImageView ivAvatar) {
        if (type.equalsIgnoreCase("friend"))
            ivAvatar.setImageResource(R.drawable.contact_type_bff);
        else if (type.equalsIgnoreCase("family"))
            ivAvatar.setImageResource(R.drawable.contact_type_family);
        else
            ivAvatar.setVisibility(View.GONE);
    }
}