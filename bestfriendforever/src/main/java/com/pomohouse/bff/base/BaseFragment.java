/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.pomohouse.bff.base;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pomohouse.bff.BestFriendForeverApplication;
import com.pomohouse.bff.R;
import com.pomohouse.bff.dao.WearerInfo;
import com.pomohouse.library.manager.ActivityContextor;
import com.pomohouse.library.networks.ProgressDialogLoader;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;


/**
 * Base {@link android.app.Fragment} class for every fragment in this application.
 */
public abstract class BaseFragment extends Fragment {

    public static final String IS_UPDATE = "IS_UPDATE";
    public static final String WATCH_INFO = "WATCH_INFO";
    public static final String FRIEND = "FRIEND";
    public static final String TITLE_NAME = "TITLE_NAME";
    public static final String AVATAR = "AVATAR";

    private ObjectGraph activityGraph;
    private ProgressDialogLoader dialogLoading;
    protected Context mContext = ActivityContextor.getInstance().getContext();
    private Bundle bundle;
    //private WearerInfo wearerInfo;
    //private String titleName;

/*    public WearerInfo getWatchInfoModel() {
        return wearerInfo;
    }*/

    /*public String getTitleName() {
        return titleName;
    }*/

    /**
     * Shows a {@link Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected Bundle buildBundle(WearerInfo watchInfo) {
        bundle = new Bundle();
        bundle.putParcelable(WATCH_INFO, Parcels.wrap(watchInfo));
        return bundle;
    }

    protected Bundle buildBundle(WearerInfo watchInfo, String titleName) {
        bundle = new Bundle();
        bundle.putParcelable(WATCH_INFO, Parcels.wrap(watchInfo));
        bundle.putString(TITLE_NAME, titleName);
        return bundle;
    }

    protected void buildArguments() {
        setArguments(getBundle());
    }

    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initProgressDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        List<Object> modules = injectModules();
        if (modules != null) {
            activityGraph = ((BestFriendForeverApplication) getActivity().getApplication()).createScopedGraph(modules.toArray());
            activityGraph.inject(this);
        }
        /*try {
            rcv_item_space_vertical = (int) getResources().getDimension(R.dimen.recycle_view_space_vertical);
            rcv_item_space_horizontal = (int) getResources().getDimension(R.dimen.recycle_view_space_horizontal);
        } catch (Exception e) {
            Timber.e("Can't getResources().getDimension");
        }*/
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        injectViews(view);
        /*titleName = getArguments().getString(TITLE_NAME);*/
        /*wearerInfo = Parcels.unwrap(getArguments().getParcelable(WATCH_INFO));*/
    }

    /**
     * Replace every field annotated with ButterKnife annotations like @InjectView with the proper
     * value.
     *
     * @param view to extract each widget injected in the fragment.
     */
    private void injectViews(final View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    public void hideKeyboard() {
        if (getView() == null)
            return;
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    protected abstract List<Object> injectModules();


    public void initProgressDialog() {
        dialogLoading = new ProgressDialogLoader(getActivity());

    }

    public void displayLoadingDialog(ProgressDialogLoader.OnCancelClickListener listener) {
        new Handler().post(() -> {
            hideKeyboard();
            if (dialogLoading == null || getActivity().isFinishing())
                return;
            dialogLoading.setOnCancel(listener);
            dialogLoading.show();
        });
    }

    public void displayLoadingDialog() {
        new Handler().post(() -> {
            hideKeyboard();
            if (dialogLoading == null || getActivity().isFinishing())
                return;
            dialogLoading.show();
        });
    }

    public void hideLoadingDialog() {
        if (dialogLoading == null)
            return;
        if (dialogLoading.isShowing())
            dialogLoading.dismiss();
    }


    public boolean openApp(String packageName) {
        PackageManager manager = getContext().getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.addCategory(Intent.ACTION_MAIN);
        getContext().startActivity(i);
        return true;
    }

    protected void openActivity(Class<?> cls) {
        openActivity(cls, null, false);
    }

    protected void openActivity(Class<?> cls, boolean finishActivity) {
        openActivity(cls, null, finishActivity);
    }

    protected void openActivity(Class<?> cls, Bundle bundle) {
        openActivity(cls, bundle, false);
    }

    protected void openActivity(Class<?> cls, Bundle bundle, boolean finishActivity) {
        Intent intent = new Intent(getActivity(), cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
        if (finishActivity)
            getActivity().finish();
    }

    protected void openActivityResult(Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null)
            intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    protected void openActivityAndClearHistory(Class<?> cls) {
        openActivityAndClearHistory(cls, null);
    }

    /*
        public void openAvatarFragment() {
            replaceFragmentToBackStack(ChooseAvatarFragment.newInstance(), ChooseAvatarFragment.class.getName());
        }
    */
    protected void replaceFragmentToBackStack(Fragment fragment, String TAG) {
        hideKeyboard();
        if (fragment != null && !getActivity().isFinishing()) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(TAG)
                    .commit();
        }
    }

    protected void showAlertDialogFragment(DialogFragment dialogFragment, String TAG) {
        dialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
    }

   /* protected void replaceFragment(Fragment fragment) {
        if (fragment != null && !getActivity().isFinishing()) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.contentContainer, fragment)
                    .commit();
        }
    }*/

    protected void openActivityAndClearHistory(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(getActivity(), cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void crossFadeIn(View view) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        view.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null);

    }

    protected File onConvertBitmapToFile(Bitmap thumbnail) {
        if (thumbnail == null)
            return null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            return destination;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
