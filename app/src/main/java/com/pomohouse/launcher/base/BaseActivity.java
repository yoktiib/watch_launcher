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

package com.pomohouse.launcher.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.library.languages.LocalizationActivity;
import com.pomohouse.library.manager.ActivityContextor;

import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

/**
 * Created by Akexorcist on 1/7/2016 AD.
 */
public abstract class BaseActivity extends LocalizationActivity {
    private ObjectGraph activityGraph;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityContextor.getInstance().init(this);
        ActivityContextor.getInstance().initActivity(this);
        mContext = this;
        List<Object> modules = getModules();
        if (modules != null) {
            activityGraph = ((POMOWatchApplication) getApplication()).createScopedGraph(getModules().toArray());
            activityGraph.inject(this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    protected abstract List<Object> getModules();
}
