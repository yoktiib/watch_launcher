/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pomohouse.bff.di;

import android.app.Application;

import com.pomohouse.bff.BestFriendForeverApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module(library = true,
        injects = {
                BestFriendForeverApplication.class
        },
        includes = {
                InteractorModule.class
        }
)
public class ApplicationModule {

    private final BestFriendForeverApplication application;

    public ApplicationModule(BestFriendForeverApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplicationContext() {
        return application;
    }
}