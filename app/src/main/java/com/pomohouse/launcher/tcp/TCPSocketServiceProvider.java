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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.fragment.about.presenter.OnPinCodeListener;
import com.pomohouse.launcher.fragment.contacts.presenter.OnContactListener;
import com.pomohouse.launcher.main.OnLauncherCallbackListener;
import com.pomohouse.launcher.main.OnLauncherRequestListener;
import com.pomohouse.launcher.manager.event.EventPrefManagerImpl;
import com.pomohouse.launcher.manager.event.EventPrefModel;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.EventDataListModel;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;

import java.io.UnsupportedEncodingException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import moe.codeest.rxsocketclient.SocketSubscriber;
import timber.log.Timber;


import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;

public class TCPSocketServiceProvider extends Service {


    private static final long INTERVAL_KEEP_ALIVE = 1000 * 60 * 4;

    private static final long INTERVAL_INITIAL_RETRY = 1000 * 10;

    private static final long INTERVAL_MAXIMUM_RETRY = 1000 * 60 * 2;

    private POMOWatchApplication signalApplication;
    private static final String TAG = "TCP_SSP";
    /*private static final byte[] MESSAGE = {0, 1, 3};
    private static final String MESSAGE_STR = "TEST";*/
    private OnLauncherCallbackListener tcpCallbackListener;
    private OnContactListener onContactListener;
    private OnPinCodeListener onPinCodeListener;
    private OnLauncherRequestListener launcherRequestListener;
    private boolean isConnecting = false;
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

    public void IsBendable(OnLauncherRequestListener launcherRequestListener) {
        this.launcherRequestListener = launcherRequestListener;
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

    public void connectConnection() {
        Log.e(TAG, "connectConnection");
        instance = this;
        isConnecting = true;
        ref = signalApplication.getSocket().connect().observeOn(AndroidSchedulers.mainThread()).subscribe(new SocketSubscriber() {

            @Override
            public void onConnected() {
                messageReceiver("\n" + "onConnected");
                isConnecting = false;
                /*launcherRequestListener.onInitial();
                launcherRequestListener.onContactRequest();*/
            }

            @Override
            public void onDisconnected() {
                isConnecting = false;
                signalApplication.clearSocket();
                messageReceiver("\n" + "onDisconnected");
            }

            @Override
            public void onResponse(@NonNull byte[] data) {
                try {
                    Log.e(TAG, "Received : " + new String(data, "UTF-8"));
                    messageReceiver(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, throwable -> {
            //onError
            isConnecting = false;
            messageReceiver("\n" + "on Error : " + throwable.toString());
            Log.e(TAG, "\n ERROR : " + throwable.toString());

        });
    }

    public void sendMessagePairCode(OnPinCodeListener listener, String cmd, String data) {
        onPinCodeListener = listener;
        sendMessage(cmd, data);
    }

    public void sendMessageFromContact(OnContactListener listener, String cmd, String data) {
        onContactListener = listener;
        sendMessage(cmd, data);
    }

    public void sendMessageFromLauncher(OnLauncherCallbackListener listener, String cmd, String data) {
        tcpCallbackListener = listener;
        sendMessage(cmd, data);
    }

    public void sendMessage(String cmd, String data) {
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
        if (POMOWatchApplication.mSocket != null) {
            if (!POMOWatchApplication.mSocket.isConnecting()) {
                if (isConnecting) return;
                isConnecting = true;
                ((POMOWatchApplication) getApplication()).createTCPSocket();
                connectConnection();
            } else {
                POMOWatchApplication.mSocket.sendData(packageSender.convertModelToValue());
            }
        } else {
            signalApplication.createTCPSocket();
        }
    }

    private void disconnectConnection() {
        instance = null;
        signalApplication.getSocket().disconnect();
    }

    void messageReceiver(String messenger) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                if (messenger != null && !messenger.equalsIgnoreCase("")) {
                    String[] checkContainer = messenger.split("<PMHEnd><PMHStart>");
                    //if (checkContainer.length != 0) Log.e(TAG, checkContainer[0]);
                 /*   if (checkContainer.length <= 1) {
                        String[] dataEvent = messenger.split("><");
                        Log.e(TAG, "Data Length : " + dataEvent.length);
                        if (dataEvent.length > 0 && dataEvent.length == 9)
                            classifyMessage(new TCPMessenger().convertValueToModel(dataEvent));
                    } else {*/
                    for (int i = 0; i < checkContainer.length; i++) {
                        if (checkContainer.length != 1) {
                            if (i == 0) checkContainer[i] = checkContainer[i] + "<PMHEnd>";
                            else if (i == checkContainer.length - 1)
                                checkContainer[i] = "<PMHStart>" + checkContainer[i];
                            else checkContainer[i] = "<PMHStart>" + checkContainer[i] + "<PMHEnd>";
                        }
                        Log.e(TAG, "checkContainer : " + checkContainer[i]);
                        String[] dataEvent = checkContainer[i].split("><");
                        Log.e(TAG, "Data Length : " + dataEvent.length);
                        if (dataEvent.length > 0 && dataEvent.length == 9)
                            classifyMessage(new TCPMessenger().convertValueToModel(dataEvent));
                    }
                    /*}*/
                }
            } catch (Exception ignore) {
                Log.e(TAG, "Error messageReceiver: " + ignore.getLocalizedMessage());
            }
        });
    }

    private void classifyMessage(TCPMessenger messengerModel) {
        try {
            Log.e(TAG, "LENGHT : " + messengerModel.getLength());
            Log.e(TAG, "IMEI : " + messengerModel.getImei());
            Log.e(TAG, "CMD : " + messengerModel.getCMD());
            Log.e(TAG, "Data : " + messengerModel.getData());
            Log.e(TAG, "Sum : " + messengerModel.getSum());
            MetaDataNetwork network;
            if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_EVENT_SETTING)) {

                Log.e(TAG, "TCPMessengerModel.CMD_EVENT_SETTING");
                onConvertEventAndSetting(messengerModel.getData());

            } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_CONTACT)) {

                Log.e(TAG, "TCPMessengerModel.CMD_CONTACT");
                ContactCollection contact = new GsonBuilder().create().fromJson(messengerModel.getData(), ContactCollection.class);
                network = new MetaDataNetwork(contact.getResCode(), contact.getResDesc());
                if (onContactListener == null) return;
                if (contact.getResCode() == 0) onContactListener.onContactSuccess(network, contact);
                else onContactListener.onContactFailure(network);

            } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_INIT_DEVICE)) {

                Log.e(TAG, "TCPMessengerModel.CMD_INIT_DEVICE");
                ResultGenerator<DeviceInfoModel> deviceInfoModel = new GsonBuilder().create().fromJson(messengerModel.getData(), new TypeToken<ResultGenerator<DeviceInfoModel>>() {
                }.getType());
                network = new MetaDataNetwork(deviceInfoModel.getResCode(), deviceInfoModel.getResDesc());
                if (tcpCallbackListener == null) return;
                if (deviceInfoModel.getResCode() == 0)
                    tcpCallbackListener.onInitialDeviceSuccess(network, deviceInfoModel.getData());
                else tcpCallbackListener.onInitialDeviceFailure(network);

            } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_PAIR_CODE)) {

                Log.e(TAG, "TCPMessengerModel.CMD_PAIR_CODE");
                ResultGenerator<PinCodeModel> pinCodeModel = new GsonBuilder().create().fromJson(messengerModel.getData(), new TypeToken<ResultGenerator<PinCodeModel>>() {
                }.getType());
                network = new MetaDataNetwork(pinCodeModel.getResCode(), pinCodeModel.getResDesc());
                if (onPinCodeListener == null) return;
                if (pinCodeModel.getResCode() == 0)
                    onPinCodeListener.onPinCodeSuccess(network, pinCodeModel.getData());
                else onPinCodeListener.onPinCodeFailure(network);

            } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_LOCATION_UPDATE)) {

                Log.e(TAG, "TCPMessengerModel.CMD_LOCATION_UPDATE");
                EventDataListModel locationUpdate = new GsonBuilder().create().fromJson(messengerModel.getData(), EventDataListModel.class);
                network = new MetaDataNetwork(locationUpdate.getResCode(), locationUpdate.getResDesc());
                if (tcpCallbackListener == null) return;
                if (locationUpdate.getResCode() == 0)
                    tcpCallbackListener.onCallEventSuccess(network, locationUpdate);
                else tcpCallbackListener.onCallEventFailure(network);

            } else {

                Log.e(TAG, "Else TCPMessengerModel." + messengerModel.getCMD());
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(AppContextor.getInstance().getContext(), "Else TCPMessengerModel." + messengerModel.getCMD(), Toast.LENGTH_SHORT).show());

            }
        } catch (Exception ignore) {
            Log.e(TAG, "Error ClassifyMessage : " + ignore.getLocalizedMessage());
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
