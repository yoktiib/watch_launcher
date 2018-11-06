/*
Copyright 2016 Nextzy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.pomohouse.bff.base;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;

import com.pomohouse.bff.BestFriendForeverApplication;
import com.pomohouse.library.languages.LocalizationActivity;
import com.pomohouse.library.manager.ActivityContextor;
import com.pomohouse.library.manager.bus.BusProvider;
import com.pomohouse.library.networks.ProgressDialogLoader;

import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

/**
 * Created by Akexorcist on 1/7/2016 AD.
 */
public abstract class BaseActivity extends LocalizationActivity {

    private ProgressDialogLoader dialogLoading;
    private ObjectGraph activityGraph;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityContextor.getInstance().init(this);
        mContext = this;
        List<Object> modules = getModules();
        if (modules != null) {
            activityGraph = ((BestFriendForeverApplication) getApplication()).createScopedGraph(getModules().toArray());
            activityGraph.inject(this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);
        initProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //overridePendingTransition(R.anim.anim_open_scale, R.anim.activity_close_translate);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }


    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }



    public void initProgressDialog() {
        dialogLoading = new ProgressDialogLoader(this);

    }

    public void displayLoadingDialog(ProgressDialogLoader.OnCancelClickListener listener) {
        new Handler().post(() -> {
            hideKeyboard();
            if (dialogLoading == null || isFinishing())
                return;
            dialogLoading.setOnCancel(listener);
            dialogLoading.show();
        });
    }

    public void displayLoadingDialog() {
        new Handler().post(() -> {
            hideKeyboard();
            if (dialogLoading == null || isFinishing())
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

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    protected abstract List<Object> getModules();

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
        Intent intent = new Intent(this, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
        if (finishActivity)
            finish();
    }

    protected void openActivityAndClearHistory(Class<?> cls) {
        openActivityAndClearHistory(cls, null);
    }

    protected void openActivityAndClearHistory(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void openActivityForResult(Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }
}
