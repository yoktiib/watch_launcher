package com.pomohouse.launcher.tcp;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.activity.pincode.PinCodeActivity;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.fragment.about.interactor.OnPinCodeListener;
import com.pomohouse.launcher.fragment.about.interactor.OnQRCodeListener;
import com.pomohouse.launcher.fragment.contacts.presenter.OnContactListener;
import com.pomohouse.launcher.main.OnLauncherCallbackListener;
import com.pomohouse.launcher.manager.event.EventPrefManagerImpl;
import com.pomohouse.launcher.manager.event.EventPrefModel;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResponseDao;
import com.pomohouse.library.networks.ResultGenerator;
import com.pomohouse.library.networks.ResultModel;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import moe.codeest.rxsocketclient.RxSocketClient;
import moe.codeest.rxsocketclient.SocketClient;
import moe.codeest.rxsocketclient.SocketObservable;
import moe.codeest.rxsocketclient.SocketSubscriber;
import moe.codeest.rxsocketclient.meta.SocketConfig;
import moe.codeest.rxsocketclient.meta.SocketOption;
import moe.codeest.rxsocketclient.meta.ThreadStrategy;
import timber.log.Timber;


import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;

public class TCPSocketServiceProvider extends Service {
    public static SocketClient mSocket;
    private static final long INTERVAL_KEEP_ALIVE = 1000 * 60;
    private static final int INTERVAL_TIME_OUT = 1000 * 20;
    private static final long INTERVAL_INCREASE = 1000 * 2;
    private static final long INTERVAL_INITIAL_RETRY = 1000 * 8;
    private static final long INTERVAL_MAXIMUM_RETRY = 1000 * 40;
    private static final String IP = "k8-api.myjollywatch.com";
    //private static final String IP = "203.151.93.176";
    //private static final String IP = "13.228.58.26";

    //http://203.151.93.176:3000/v1.1/api/app/
    //private static final String IP = "178.128.27.215";
    //private static final String IP = "203.151.93.176";
    private static final int PORT = 4848;
    public static Resources resources;

    private final String START_TAG = "<PMHStart>";
    private final String END_TAG = "<PMHEnd>";
    /*private static final byte[] HEAD = {1, 2};
    private static final byte[] TAIL = {5, 7};*/

    private static final String TAG = "TCP_SSP";
    private OnLauncherCallbackListener tcpCallbackListener;
    private OnTCPStatusListener tcpStatusListener;
    private OnContactListener onContactListener;
    private OnPinCodeListener onPinCodeListener;
    private OnQRCodeListener onQRCodeListener;
    private boolean isDelayUp = false;

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

    public void screenOff() {
        if (mSocket != null && mSocket.isConnecting()) {
            if (mSocket.getMObservable() instanceof SocketObservable)
                ((SocketObservable) mSocket.getMObservable()).updateTimeSleep(INTERVAL_INITIAL_RETRY, isDelayUp = true);
        }
    }

    public void screenOn() {
        if (mSocket != null && mSocket.isConnecting()) {
            if (mSocket.getMObservable() instanceof SocketObservable)
                ((SocketObservable) mSocket.getMObservable()).updateTimeSleep(INTERVAL_INITIAL_RETRY, isDelayUp = false);
        }
    }

    public void screenOn(int time) {
        if (mSocket != null && mSocket.isConnecting()) {
            if (mSocket.getMObservable() instanceof SocketObservable)
                ((SocketObservable) mSocket.getMObservable()).updateTimeSleep(time, isDelayUp = false);
        }
    }

    public void setTCPStatusListener(OnTCPStatusListener onTCPStatusListener) {
        this.tcpStatusListener = onTCPStatusListener;
    }


    public class LocalBinder extends Binder {
        public TCPSocketServiceProvider getService() {
            return TCPSocketServiceProvider.this;
        }
    }

    public void IsBendable(OnTCPStatusListener launcherRequestListener) {
        this.tcpStatusListener = launcherRequestListener;
        checkSocketIsRunning();
        Timber.e("Set tcpStatusListener");
    }

    public boolean checkSocketIsRunning() {
        Timber.e("checkSocketIsRunning");
        if (mSocket == null) {
            connectConnection();
        } else {
            if (!mSocket.isConnecting()) {
                if (isConnecting) return false;
                isConnecting = true;
                connectConnection();
            } else return true;
        }
        return false;
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
        instance = this;
        Timber.e("Start Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*if (isInstanceCreated()) {
            return START_STICKY_COMPATIBILITY;
        }*/
        super.onStartCommand(intent, flags, startId);
        //connectConnection();
        return START_STICKY;
    }

    public void sendLocation(String cmd, String data) {
        sendMessage(cmd, data);
    }

    public void sendMessagePairCode(OnPinCodeListener listener, String cmd, String data) {
        onPinCodeListener = listener;
        sendMessage(cmd, data);
    }


    public void sendMessageQRCode(OnQRCodeListener listener, String cmd, String data) {
        onQRCodeListener = listener;
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
        packageSender.setImei(WearerInfoUtils.getInstance().getImei(this));
        packageSender.setCMD(cmd);
        packageSender.setData(data);
        packageSender.setLauncherVersion(WearerInfoUtils.getInstance().getPomoVersion());
        char[] digits1 = String.valueOf(cmd).toCharArray();
        packageSender.setSum(digits1[0] + digits1[1] + digits1[2]);
        packageSender.setModel(WearerInfoUtils.getInstance().getPlatform());
        packageSender.setLength(packageSender.convertModelToValue().length());
        Timber.e("Data Sender : " + packageSender.convertModelToValue());
        if (checkSocketIsRunning()) mSocket.sendData(packageSender.convertModelToValue());
    }

    public void connectConnection() {
        Timber.e("create Connection");
        isConnecting = true;
        //final byte[] HEART_BEAT = ("<PMHStart><00109><K8><" + WearerInfoUtils.getInstance().getImei(this) + "><1.0><UTH><{}><168><PMHEnd>").getBytes();
        mSocket = RxSocketClient.create(new SocketConfig.Builder().setIp(IP).setPort(PORT).setCharset(Charset.forName("UTF-8")).setThreadStrategy(ThreadStrategy.ASYNC).setTimeout(INTERVAL_TIME_OUT).setDelayTime(INTERVAL_INITIAL_RETRY).setMaxDelayTime(INTERVAL_MAXIMUM_RETRY).setIncreaseDelayTime(INTERVAL_INCREASE).build()).option(new SocketOption.Builder()/*.setHeartBeat(HEART_BEAT, INTERVAL_KEEP_ALIVE).setHead(HEAD).setTail(TAIL)*/.build());
        if (ref != null && !ref.isDisposed()) ref.dispose();
        ref = mSocket.connect().observeOn(AndroidSchedulers.mainThread()).subscribe(new SocketSubscriber() {

            @Override
            public void onConnected() {
                Timber.e("onConnected " + (tcpStatusListener != null));
                isConnecting = false;
                if (tcpStatusListener != null) tcpStatusListener.onConnected();
            }

            @Override
            public void onDisconnected() {
                Timber.e("onDisconnected");
                isConnecting = false;
                if (ref != null && !ref.isDisposed()) ref.dispose();
                if (mSocket.isConnecting()) mSocket.disconnect();
                mSocket = null;
                if (tcpStatusListener != null) tcpStatusListener.onDisconnected();
                new Handler().postDelayed(() -> connectConnection(), 20000);
            }

            @Override
            public void onResponse(@NonNull byte[] data) {
                try {
                    Timber.e("Received : " + new String(data, "UTF-8"));
                    messageReceiver(new String(data, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, throwable -> {
            //onError
            isConnecting = false;
            //  messageReceiver("\n" + "on Error : " + throwable.toString());
            Timber.e("\n ERROR : " + throwable.toString());

        });
    }

    private void disconnectConnection() {
        instance = null;
        if (mSocket != null && mSocket.isConnecting()) mSocket.disconnect();
        mSocket = null;
    }

    public void clearSocket() {
        if (mSocket != null && mSocket.isConnecting()) mSocket.disconnect();
        mSocket = null;
        //createTCPSocket();
    }

    void messageReceiver(String messenger) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                if (messenger != null && !messenger.equalsIgnoreCase("")) {
                    String[] checkContainer = messenger.split(END_TAG + START_TAG);
                    //if (checkContainer.length != 0) Log.e(TAG, checkContainer[0]);
                 /*   if (checkContainer.length <= 1) {
                        String[] dataEvent = messenger.split("><");
                        Log.e(TAG, "Data Length : " + dataEvent.length);
                        if (dataEvent.length > 0 && dataEvent.length == 9)
                            classifyMessage(new TCPMessenger().convertValueToModel(dataEvent));
                    } else {*/
                    for (int i = 0; i < checkContainer.length; i++) {
                        if (checkContainer.length != 1) {
                            if (i == 0) checkContainer[i] = checkContainer[i] + END_TAG;
                            else if (i == checkContainer.length - 1)
                                checkContainer[i] = START_TAG + checkContainer[i];
                            else checkContainer[i] = START_TAG + checkContainer[i] + END_TAG;
                        }
                        Timber.e("checkContainer : " + checkContainer[i]);
                        String[] dataEvent = checkContainer[i].split("><");
                        Timber.e("Data Length : " + dataEvent.length);
                        if (dataEvent.length > 0 && dataEvent.length == 9)
                            classifyMessage(new TCPMessenger().convertValueToModel(dataEvent));
                    }
                    /*}*/
                }
            } catch (Exception ignore) {
                Timber.e("Error messageReceiver: " + ignore.getLocalizedMessage());
            }
        });
    }

    private void classifyMessage(TCPMessenger messengerModel) {
        try {
            //Log.e(TAG, "LENGHT : " + messengerModel.getLength());
            Timber.e("IMEI : " + messengerModel.getImei());
            Timber.e("CMD : " + messengerModel.getCMD());
            Timber.e("Data : " + messengerModel.getData());
            //Log.e(TAG, "Sum : " + messengerModel.getSum());

            if (checkDataIsNotError(messengerModel.getData())) {
                MetaDataNetwork network;
                if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_SETTING_EVENT)) {
                    Timber.e("WORK : " + CMDCode.CMD_SETTING_EVENT);
                    onConvertEventAndSetting(messengerModel.getData());
                } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_CONTACT)) {

                    Timber.e("TCPMessengerModel.CMD_CONTACT");
                    ContactCollection contact = new GsonBuilder().create().fromJson(messengerModel.getData(), ContactCollection.class);
                    network = new MetaDataNetwork(contact.getResCode(), contact.getResDesc());
                    if (onContactListener == null) return;
                    if (contact.getResCode() == 0)
                        onContactListener.onContactSuccess(network, contact);
                    else onContactListener.onContactFailure(network);

                } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_INIT_DEVICE)) {

                    Timber.e("TCPMessengerModel.CMD_INIT_DEVICE");
                    ResultGenerator<DeviceInfoModel> deviceInfoModel = new GsonBuilder().create().fromJson(messengerModel.getData(), new TypeToken<ResultGenerator<DeviceInfoModel>>() {
                    }.getType());
                    network = new MetaDataNetwork(deviceInfoModel.getResCode(), deviceInfoModel.getResDesc());
                    if (tcpCallbackListener == null) return;
                    if (deviceInfoModel.getResCode() == 0)
                        tcpCallbackListener.onInitialDeviceSuccess(network, deviceInfoModel.getData());
                    else tcpCallbackListener.onInitialDeviceFailure(network);

                } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_PAIR_CODE)) {

                    Timber.e("TCPMessengerModel.CMD_PAIR_CODE");
                    ResultGenerator<PinCodeModel> pinCodeModel = new GsonBuilder().create().fromJson(messengerModel.getData(), new TypeToken<ResultGenerator<PinCodeModel>>() {
                    }.getType());
                    network = new MetaDataNetwork(pinCodeModel.getResCode(), pinCodeModel.getResDesc());
                    if (onPinCodeListener == null) return;
                    if (pinCodeModel.getResCode() == 0)
                        onPinCodeListener.onPinCodeSuccess(network, pinCodeModel.getData());
                    else onPinCodeListener.onPinCodeFailure(network);

                } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_QR_CODE)) {

                    Timber.e("TCPMessengerModel.CMD_QR_CODE");
                    ResultGenerator<QRCodeModel> QRCodeModel = new GsonBuilder().create().fromJson(messengerModel.getData(), new TypeToken<ResultGenerator<QRCodeModel>>() {
                    }.getType());
                    network = new MetaDataNetwork(QRCodeModel.getResCode(), QRCodeModel.getResDesc());
                    if (onQRCodeListener == null) return;
                    if (QRCodeModel.getResCode() == 0)
                        onQRCodeListener.onQRCodeSuccess(network, QRCodeModel.getData());
                    else onQRCodeListener.onQRCodeFailure(network);
                } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_PIN_CODE)) {

                    Timber.e("TCPMessengerModel.CMD_PIN_CODE");
                    PinCodeModel pinCode = new GsonBuilder().create().fromJson(messengerModel.getData(), PinCodeModel.class);
                    Intent intent = new Intent(AppContextor.getInstance().getContext(), PinCodeActivity.class);
                    intent.putExtra(PinCodeActivity.EXTRA_PIN_CODE, pinCode.getCode());
                    AppContextor.getInstance().getContext().startActivity(intent);
                } else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_EVENT_AND_LOCATION)) {
                    Timber.e("WORK : " + CMDCode.CMD_EVENT_AND_LOCATION);
                    new EventPrefManagerImpl(this).removeEvent();
                }
            /* else if (messengerModel.getCMD().equalsIgnoreCase(CMDCode.CMD_LOCATION_UPDATE)) {

                Log.e(TAG, "TCPMessengerModel.CMD_LOCATION_UPDATE");
              *//*  EventDataListModel locationUpdate = new GsonBuilder().create().fromJson(messengerModel.getData(), EventDataListModel.class);
                network = new MetaDataNetwork(locationUpdate.getResCode(), locationUpdate.getResDesc());
                if (tcpCallbackListener == null) return;
                if (locationUpdate.getResCode() == 0)
                    tcpCallbackListener.onCallEventSuccess(network, locationUpdate);
                else tcpCallbackListener.onCallEventFailure(netwo*//*rk);

            } */
                else {

                    Timber.e("Else TCPMessengerModel." + messengerModel.getCMD());
                    //new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(AppContextor.getInstance().getContext(), "Else TCPMessengerModel." + messengerModel.getCMD(), Toast.LENGTH_SHORT).show());

                }
            }
        } catch (Exception ignore) {
            Timber.e("Error ClassifyMessage : " + ignore.getLocalizedMessage());
        }
    }

    public boolean checkDataIsNotError(String data) {
        ResponseDao pinCode = new GsonBuilder().create().fromJson(data, ResponseDao.class);
        return pinCode.getResCode() == 0;
    }

    public void onConvertEventAndSetting(String data) {
        if (data != null && !data.isEmpty()) {
            try {
                EventDataInfo dataEvent = new Gson().fromJson(data, EventDataInfo.class);
                if (dataEvent != null) {
                    IEventPrefManager iEventPrefManager = new EventPrefManagerImpl(this);
                    EventPrefModel eventPrefModel = iEventPrefManager.getEvent();
                    if (eventPrefModel != null && dataEvent.getEventId() != 0) {
                        eventPrefModel.getListEvent().add(String.valueOf(dataEvent.getEventId()));
                        iEventPrefManager.addEvent(eventPrefModel);
                    }
                    Timber.e("Parser Okay");
                    final Intent intent = new Intent(SEND_EVENT_UPDATE_INTENT, null);
                    MetaDataNetwork network = new MetaDataNetwork(0, "", MetaDataNetwork.MetaType.SUCCESS);
                    intent.putExtra(EVENT_STATUS_EXTRA, network);
                    intent.putExtra(EVENT_EXTRA, dataEvent);
                    AppContextor.getInstance().getContext().sendBroadcast(intent);
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
