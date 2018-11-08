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

import android.content.ContentValues;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pomohouse.launcher.manager.event.EventPrefManagerImpl;
import com.pomohouse.launcher.manager.event.EventPrefModel;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.library.networks.MetaDataNetwork;

import timber.log.Timber;

import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    IEventPrefManager iEventPrefManager;

    */
/**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     *//*

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        if (remoteMessage != null) {
            //Timber.e("From: " + remoteMessage.getFrom());
            if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
                try {
                    Timber.e("Message data payload: " + remoteMessage.getData());
                    EventDataInfo dataEvent = new Gson().fromJson(remoteMessage.getData().get("event"), EventDataInfo.class);
                    if (dataEvent != null) {
                        iEventPrefManager = new EventPrefManagerImpl(this);
                        EventPrefModel eventPrefModel = iEventPrefManager.getEvent();
                        if (eventPrefModel != null) {
                            eventPrefModel.getListEvent().add(String.valueOf(dataEvent.getEventId()));
                            iEventPrefManager.addEvent(eventPrefModel);
                        }
                        Timber.e("Parser Okay");
                        final Intent intent = new Intent(SEND_EVENT_UPDATE_INTENT, null);
                        MetaDataNetwork network = new MetaDataNetwork(0, "", MetaDataNetwork.MetaType.SUCCESS);
                        intent.putExtra(EVENT_STATUS_EXTRA, network);
                        intent.putExtra(EVENT_EXTRA, dataEvent);
                        sendBroadcast(intent);
                        insertEventContentProvider(dataEvent);
                    } else {
                        Timber.e("Message data Event : Error Null");
                    }
                } catch (Exception ignore) {
                    Timber.e("Exception : " + ignore.toString());
                }
            }
        }
    }


    void insertEventContentProvider(EventDataInfo event) {
        if (AppContextor.getInstance().getContext() != null) {
            ContentValues values = new ContentValues();
            values.put(POMOContract.EventEntry.EVENT_ID, event.getEventId());
            values.put(POMOContract.EventEntry.EVENT_CODE, event.getEventCode());
            values.put(POMOContract.EventEntry.EVENT_TYPE, event.getEventType());
            values.put(POMOContract.EventEntry.SENDER, event.getSenderId());
            values.put(POMOContract.EventEntry.RECEIVE, event.getReceiveId());
            values.put(POMOContract.EventEntry.SENDER_INFO, event.getSenderInfo());
            values.put(POMOContract.EventEntry.RECEIVE_INFO, event.getReceiverInfo());
            values.put(POMOContract.EventEntry.CONTENT, event.getContent());
            values.put(POMOContract.EventEntry.STATUS, event.getStatus());
            values.put(POMOContract.EventEntry.TIME_STAMP, event.getTimeStamp());
            AppContextor.getInstance().getContext().getContentResolver().insert(POMOContract.EventEntry.CONTENT_URI, values);
        }
    }
}
*/
