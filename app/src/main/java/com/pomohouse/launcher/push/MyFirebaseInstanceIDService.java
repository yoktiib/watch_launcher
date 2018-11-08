/**
 * Copyright 2016 Google Inc. All Rights Reserved.
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
 *//*


package com.pomohouse.launcher.push;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.launcher.api.requests.UpdateFirebaseRequest;


import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_UPDATE_FCM_TOKEN_CODE;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    */
/**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     *//*

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    */
/**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     *//*

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        try {
            ISettingManager settingManager = new SettingPrefManager(this);
            SettingPrefModel setting = settingManager.getSetting();
            if (setting != null) {
                setting.setFCMToken(token);
                settingManager.addMiniSetting(setting);
            }
            EventDataInfo eventDataInfo = new EventDataInfo();
            UpdateFirebaseRequest firebaseRequest = new UpdateFirebaseRequest();
            firebaseRequest.setFireBaseWatchToken(token);
            firebaseRequest.setImei(WearerInfoUtils.getInstance().getImei());
            eventDataInfo.setEventCode(EVENT_UPDATE_FCM_TOKEN_CODE);
            eventDataInfo.setContent(new Gson().toJson(firebaseRequest));
            onSendEventToBroadcast(eventDataInfo);
        } catch (Exception ignore) {
        }
    }

    public void onSendEventToBroadcast(EventDataInfo eventDataInfo) {
        final Intent intent = new Intent(SEND_EVENT_UPDATE_INTENT, null);
        intent.putExtra(EVENT_STATUS_EXTRA, new MetaDataNetwork(0, "success"));
        intent.putExtra(EVENT_EXTRA, eventDataInfo);
        sendBroadcast(intent);
    }
}
*/
