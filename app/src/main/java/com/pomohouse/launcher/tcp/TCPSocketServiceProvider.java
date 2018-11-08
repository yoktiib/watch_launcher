package com.pomohouse.launcher.tcp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.fragment.contacts.interactor.OnContactListener;
import com.pomohouse.launcher.main.OnTCPCallbackListener;
import com.pomohouse.launcher.manager.event.EventPrefManagerImpl;
import com.pomohouse.launcher.manager.event.EventPrefModel;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import moe.codeest.rxsocketclient.SocketSubscriber;
import timber.log.Timber;


import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;

public class TCPSocketServiceProvider extends Service {
    private POMOWatchApplication signalApplication;
    private static final String TAG = "TCP_SSP";
    private static final byte[] MESSAGE = {0, 1, 3};
    private static final String MESSAGE_STR = "TEST";
    private OnTCPCallbackListener tcpCallbackListener;
    private OnContactListener onContactListener;

    private Disposable ref;

    public static TCPSocketServiceProvider instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public static TCPSocketServiceProvider getInstance() {
        return instance;
    }


    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public TCPSocketServiceProvider getService() {
            return TCPSocketServiceProvider.this;
        }
    }

    public void IsBendable(OnTCPCallbackListener tcpCallbackListener) {
        this.tcpCallbackListener = tcpCallbackListener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        if (isInstanceCreated()) {
            return;
        }
        super.onCreate();
        signalApplication = (POMOWatchApplication) getApplication();
        Log.e(TAG, "Start Service");

/*
        signalApplication.getSocket().on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        signalApplication.getSocket().on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        signalApplication.getSocket().on(Socket.EVENT_CONNECT, onConnect);

        //@formatter:off
        signalApplication.getSocket().on("message"                   , message);
        //@formatter:on

        EventBus.getDefault().register(this);*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isInstanceCreated()) {
            return START_STICKY_COMPATIBILITY;
        }
        super.onStartCommand(intent, flags, startId);
        connectConnection();
        return START_STICKY;
    }

    private void connectConnection() {
        instance = this;
        ref = signalApplication.getSocket().connect().observeOn(AndroidSchedulers.mainThread()).subscribe(new SocketSubscriber() {

            @Override
            public void onConnected() {
                messageReceiver("\n" + "onConnected");
            }

            @Override
            public void onDisconnected() {
                signalApplication.clearSocket();
                messageReceiver("\n" + "onDisconnected");
            }

            @Override
            public void onResponse(@NonNull byte[] data) {
                try {
                   // Log.e(TAG, Arrays.toString(data));
                  //  Log.e(TAG, new String(data, "UTF-8"));
                    messageReceiver(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, throwable -> {
            //onError
            messageReceiver("\n" + "onDisconnected");
            Log.e(TAG, "\n ERROR : " + throwable.toString());

        });
    }

    public void sendMessageFromContact(OnContactListener listener, int cmd, String data) {
        onContactListener = listener;
        sendMessage(cmd, data);
    }

    public void sendMessageFromLauncher(OnTCPCallbackListener listener, int cmd, String data) {
        tcpCallbackListener = listener;
        sendMessage(cmd, data);
    }

    public void sendMessage(int cmd, String data) {
        TCPMessenger packageSender = new TCPMessenger();
        packageSender.setImei(WearerInfoUtils.getInstance().getImei());
        packageSender.setCMD(cmd);
        packageSender.setData(data);
        packageSender.setLauncherVersion(WearerInfoUtils.getInstance().getPomoVersion());
        packageSender.setLength(data.getBytes().length);
        char[] digits1 = String.valueOf(cmd).toCharArray();
        packageSender.setSum(digits1[0] + digits1[1] + digits1[2]);
        packageSender.setModel(WearerInfoUtils.getInstance().getPlatform());
        Log.e(TAG, "Data Sender : " + packageSender.convertModelToValue());
        if (POMOWatchApplication.mSocket != null)
            POMOWatchApplication.mSocket.sendData(packageSender.convertModelToValue());
    }

    private void disconnectConnection() {
        instance = null;
        signalApplication.getSocket().disconnect();
    }

    // <496><S1><357450080000314><1.1><102><{"resCode":0,"resDesc":"Successful","data":[{"contactId":103,"name":"Kungs","gender":"F","phone":"0835676669","avatar":"6","avatarType":0,"imei":"357450080000314","role":0,"authType":"email","contactType":"family","username":"phooripun.tib@gmail.com","callType":"C"},{"contactId":5993,"name":"ploy","gender":"F","phone":"0956769230","avatar":"http://api.pomowaffle.com/v1.2/api/watch/utils/image?imagePath=fam_5993_357450080000314_1533702139970_128.png","avatarType":1,"imei":"357450080000314","role":0,"authType":"phone","contactType":"family","username":"0956769230","callType":"C"},{"contactId":"928091","name":"shh","gender":"M","phone":"0816032900","avatar":"http://api.pomowaffle.com/v1.2/api/watch/utils/image?imagePath=fam_928091_357450080000314_1532494894844_128.png","avatarType":1,"imei":"357450080000314","role":"0","authType":null,"contactType":"other","callType":"C","username":null}]}><56><PMHEnd><PMHStart>

    void messageReceiver(String messenger) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                if (messenger != null && !messenger.equalsIgnoreCase("")) {
                    String[] checkContainer = messenger.split("<PMHEnd><PMHStart>");

                    //if (checkContainer.length != 0) Log.e(TAG, checkContainer[0]);
                    if (checkContainer.length == 0) {
                        String[] dataEvent = messenger.split("><");
                        Log.e(TAG, "Data Length : " + dataEvent.length);
                        if (dataEvent.length > 0 && dataEvent.length == 9)
                            classifyMessage(new TCPMessenger().convertValueToModel(dataEvent));
                    } else {
                        for (int i = 0; i < checkContainer.length; i++) {
                            if (i == 0) checkContainer[i] = checkContainer[i] + "<PMHEnd>";
                            else if (i == checkContainer.length - 1)
                                checkContainer[i] = "<PMHStart>" + checkContainer[i];
                            else checkContainer[i] = "<PMHStart>" + checkContainer[i] + "<PMHEnd>";
                            Log.e(TAG, "checkContainer : " + checkContainer[i]);
                            String[] dataEvent = checkContainer[i].split("><");
                            Log.e(TAG, "Data Length : " + dataEvent.length);
                            if (dataEvent.length > 0 && dataEvent.length == 9)
                                classifyMessage(new TCPMessenger().convertValueToModel(dataEvent));
                        }
                    }
                }
            } catch (Exception ignore) {
                Log.e(TAG, ignore.getLocalizedMessage());
            }
        });
    }

    private void classifyMessage(TCPMessenger messengerModel) {
        try {
            Log.e(TAG, "LENGHT : " + messengerModel.getLength());
            Log.e(TAG, "MODEL : " + messengerModel.getModel());
            Log.e(TAG, "IMEI : " + messengerModel.getImei());
            Log.e(TAG, "Version : " + messengerModel.getLauncherVersion());
            Log.e(TAG, "CMD : " + messengerModel.getCMD());
            Log.e(TAG, "Data : " + messengerModel.getData());
            Log.e(TAG, "Sum : " + messengerModel.getSum());
            MetaDataNetwork network;
            switch (messengerModel.getCMD()) {
                case CMDCode.CMD_EVENT_SETTING:
                    Log.e(TAG, "TCPMessengerModel.CMD_EVENT_SETTING");
                    onConvertEventAndSetting(messengerModel.getData());
                    break;
                case CMDCode.CMD_CONTACT:
                    Log.e(TAG, "TCPMessengerModel.CMD_CONTACT");
                    ContactCollection contact = new GsonBuilder().create().fromJson(messengerModel.getData(), ContactCollection.class);
                    network = new MetaDataNetwork(contact.getResCode(), contact.getResDesc());
                    if (contact.getResCode() == 0) onContactListener.onContactSuccess(network, contact);
                    break;
                case CMDCode.CMD_INIT_DEVICE:
                    Log.e(TAG, "TCPMessengerModel.CMD_INIT_DEVICE");
                    ResultGenerator<DeviceInfoModel> deviceInfoModel = new GsonBuilder().create().fromJson(messengerModel.getData(), new TypeToken<ResultGenerator<DeviceInfoModel>>() {
                    }.getType());
                    network = new MetaDataNetwork(deviceInfoModel.getResCode(), deviceInfoModel.getResDesc());
                    if (deviceInfoModel.getResCode() == 0) {
                        tcpCallbackListener.onInitialDeviceSuccess(network, deviceInfoModel.getData());
                    }
                    break;
                default:
                    break;
            }
        }catch(Exception ignore){

        }
    }

    public void onConvertEventAndSetting(String data) {
        if (data != null && !data.isEmpty()) {
            try {
                EventDataInfo dataEvent = new Gson().fromJson(data, EventDataInfo.class);
                if (dataEvent != null) {
                    IEventPrefManager iEventPrefManager = new EventPrefManagerImpl(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        disconnectConnection();
        if (ref != null) ref.dispose();
    }
}
