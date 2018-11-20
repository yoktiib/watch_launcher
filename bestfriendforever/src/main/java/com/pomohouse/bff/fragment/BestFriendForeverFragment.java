package com.pomohouse.bff.fragment;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.pomohouse.bff.LocationBroadcast;
import com.pomohouse.bff.R;
import com.pomohouse.bff.base.BaseFragment;
import com.pomohouse.bff.base.LocationListener;
import com.pomohouse.bff.base.POMOContract;
import com.pomohouse.bff.dao.FetchFriendCollection;
import com.pomohouse.bff.dao.FriendCollection;
import com.pomohouse.bff.dao.FriendItemModel;
import com.pomohouse.bff.dao.WearerInfo;
import com.pomohouse.bff.di.presenter.BestFriendForeverPresenterModule;
import com.pomohouse.bff.fragment.confirm.ConfirmFriendFragment;
import com.pomohouse.bff.fragment.presenter.IBestFriendForeverPresenter;
import com.pomohouse.bff.fragment.wait.WaitingFriendFragment;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.networks.MetaDataNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import pl.droidsonroids.gif.GifDrawable;
import timber.log.Timber;

/**
 * Created by Admin on 8/26/16 AD.
 */
public class BestFriendForeverFragment extends BaseFragment implements IBestFriendForeverView {
    BluetoothAdapter mBluetoothAdapter;
    @BindView(R.id.rcvFriend)
    RecyclerView rcRecyclerView;
    @BindView(R.id.ivLoading)
    ImageView ivLoading;
    @BindView(R.id.dot_progress_bar)
    DotProgressBar dot_progress_bar;
    private GifDrawable gifLoader;
    private BestFriendForeverAdapter mAdapter;
    private FriendCollection currentFriendCollection;

    @Inject
    IBestFriendForeverPresenter presenter;
    WearerInfo wearerInfo;

    //public static boolean isCancel = false;
    //private List<BluetoothDevice> usableList = new ArrayList<BluetoothDevice>();

    public static BestFriendForeverFragment newInstance() {
        BestFriendForeverFragment fragment = new BestFriendForeverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter !=null)
            return;
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable(); // used to open bluetooth
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
        presenter.requestRequestIntervalFriend();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Timber.e("Action : " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                presenter.updateBTNameAddress(device.getName());
                Timber.e(device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.e("BT", "Entered the Finished ");
//                presenter.requestSearchFriend();
                //presenter.resetBluetoothMacAddress();
                //mBluetoothAdapter.startDiscovery();
                //Toast.makeText(getActivity(), "BluetoothDevice.ACTION_DISCOVERY_FINISHED", Toast.LENGTH_SHORT).show();
                if (isBluetoothMusicRunning()){
                    mBluetoothAdapter.cancelDiscovery();
                }else {
                    mBluetoothAdapter.startDiscovery();
                }
                Log.e("BT", "BluetoothDevice.ACTION_DISCOVERY_FINISHED");
            }
        }
    };

    private boolean  isBluetoothMusicRunning() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        //if your music playing is in service thread
        for (ActivityManager.RunningServiceInfo serviceTask : serviceList) {
            ComponentName serviceComponent = serviceTask.service;
            if (serviceComponent != null && serviceComponent.getClassName().equals("com.pomohouse.music")) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dot_progress_bar.setVisibility(View.GONE);
        try {
            String language = getContext().createPackageContext("com.pomohouse.launcher",  Context.CONTEXT_IGNORE_SECURITY).
                    getSharedPreferences("pomo_pref_language", Context.MODE_MULTI_PROCESS).getString("key_language","en");
            if(language.equalsIgnoreCase("es")){
                gifLoader = new GifDrawable(getResources(), R.drawable.searching_es);
            }else{
                gifLoader = new GifDrawable(getResources(), R.drawable.searching);
            }


            //gifLoader = new GifDrawable(getResources(), R.drawable.searching);
            ivLoading.setImageDrawable(gifLoader);
            gifLoader.addAnimationListener(loopNumber -> gifLoader.reset());
        } catch (IOException e) {
            e.printStackTrace();
        }catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rcRecyclerView.setLayoutManager(mLayoutManager);
        rcRecyclerView.setItemAnimator(new DefaultItemAnimator());
        rcRecyclerView.setAdapter(mAdapter = new BestFriendForeverAdapter(new FriendCollection(), (view1, model, isLongClick) -> {
            dot_progress_bar.setVisibility(View.VISIBLE);
            presenter.requestAddFriend(model);
        }));
        onInitWearerFromContentProvider();
        LocationBroadcast.getInstance(getContext()).startLocationService().initLocationListener(locationChangeListener);
    }

    LocationListener locationChangeListener = location -> {
        if (wearerInfo == null)
            return;
        wearerInfo.setImei(WearerInfoUtils.getInstance().getImei(getActivity()));
        wearerInfo.setLat(location.getLatitude());
        wearerInfo.setLng(location.getLongitude());
        presenter.onInitial(wearerInfo);
    };

    public void onInitWearerFromContentProvider() {
        getLoaderManager().initLoader(1, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getContext(),
                        POMOContract.WearerEntry.CONTENT_URI, null, null, null,
                        null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (cursor != null) {
                    cursor.moveToFirst();
                    wearerInfo = new WearerInfo();
                    while (!cursor.isAfterLast()) {
                        wearerInfo.setImei(cursor.getString(cursor.getColumnIndex(POMOContract.WearerEntry.IMEI)));
                        wearerInfo.setLat(cursor.getString(cursor.getColumnIndex(POMOContract.WearerEntry.LATITUDE)));
                        wearerInfo.setLng(cursor.getString(cursor.getColumnIndex(POMOContract.WearerEntry.LONGITUDE)));
                        cursor.moveToNext();
                    }
                    Timber.i(wearerInfo.getImei() + " : " + wearerInfo.getLat() + " : " + wearerInfo.getLat());
                    wearerInfo.setLat(wearerInfo.getLat());
                    wearerInfo.setLng(wearerInfo.getLng());
                    wearerInfo.setImei(WearerInfoUtils.getInstance().getImei(getActivity()));

                    presenter.onInitial(wearerInfo);
                    presenter.requestReadyRequestFriend();
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {

            }
        });
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new BestFriendForeverPresenterModule(this));
    }

    @Override
    public void successReadyAddNewFriend(MetaDataNetwork metaData) {
        //presenter.requestSearchFriend();
    }

    @Override
    public void failureReadyAddNewFriend(MetaDataNetwork response) {

    }

    @Override
    public void isNullObjectWearerInfo() {
        Toast.makeText(getActivity(), "Null Wearer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFriendList(FriendCollection friendCollection) {
        if (friendCollection.getFriendDaoList().size() > 0) {
            gifLoader.stop();
            ivLoading.setVisibility(View.GONE);
        } else {
            gifLoader.start();
            ivLoading.setVisibility(View.VISIBLE);
            return;
        }
        if (currentFriendCollection == null) {
            currentFriendCollection = friendCollection;
            mAdapter.setDataNotifyDataChange(this.currentFriendCollection);
            return;
        }

        ArrayList<FriendItemModel> results = new ArrayList<>();
        for (FriendItemModel person2 : friendCollection.getFriendDaoList()) {
            boolean found = false;
            for (FriendItemModel person1 : currentFriendCollection.getFriendDaoList())
                if (person2.getImei().equalsIgnoreCase(person1.getImei()))
                    found = true;
            if (!found)
                results.add(person2);
        }
        Timber.e(String.valueOf("updateFriendList(FriendCollection friendCollection) : " + results.size()) + " ==== friendCollection : " + friendCollection.getFriendDaoList().size());
        if (results.size() > 0) {
            currentFriendCollection.setFriendDaoList(results);
            mAdapter.setDataNotifyDataChange(currentFriendCollection);
        } else {
            if (currentFriendCollection.getFriendDaoList().size() > 0) {
                mAdapter.setDataNotifyDataChange(currentFriendCollection);
            }
        }
    }

    @Override
    public void failureSearchFriend(MetaDataNetwork error) {
        Timber.e("Error failureSearchFriend");
        ivLoading.setVisibility(View.VISIBLE);
        gifLoader.start();
        gifLoader.addAnimationListener(loopNumber -> gifLoader.reset());
        //mAdapter.setDataNotifyDataChange(currentFriendCollection = null);
    }


    @Override
    public void goToWaitingFriend(FriendItemModel friendDao) {
        currentFriendCollection = null;
        dot_progress_bar.setVisibility(View.GONE);
        replaceFragmentToBackStack(WaitingFriendFragment.newInstance(friendDao), WaitingFriendFragment.class.getName());
    }

    @Override
    public void goToConfirmFriend(FetchFriendCollection friendDao) {
        currentFriendCollection = null;
        dot_progress_bar.setVisibility(View.GONE);
        replaceFragmentToBackStack(ConfirmFriendFragment.newInstance(friendDao.getFetchFriendList().get(0)), ConfirmFriendFragment.class.getName());
    }

    @Override
    public void failureAddFriend(MetaDataNetwork error) {

    }

    @Override
    public void failureRequestAddFriend(MetaDataNetwork error) {
    }

    @Override
    public void failureNotAdmin(MetaDataNetwork metaData) {
        Toast.makeText(getActivity(), metaData.getResDesc(), Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    @Override
    public void failureInternetConnection() {
        /*if (getActivity() != null)
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onDestroyView() {
        //getActivity().unregisterReceiver(mReceiver);
        presenter.stopRequestFriend();
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        Timber.e("onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Timber.e("onStop");
        presenter.stopRequestFriend();
        presenter.onDestroy();
        super.onStop();
    }

    @Override
    public void onPause() {
        Timber.e("onPause");
        presenter.stopRequestFriend();
        presenter.onDestroy();
        super.onPause();
    }
}