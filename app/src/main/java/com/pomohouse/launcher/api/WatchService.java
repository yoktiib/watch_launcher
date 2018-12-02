package com.pomohouse.launcher.api;

import com.pomohouse.launcher.activity.camera.presenter.UploadModel;
import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.api.requests.InitDeviceRequest;
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;
import com.pomohouse.launcher.api.requests.RefreshLocationRequest;
import com.pomohouse.launcher.api.requests.StepRequest;
import com.pomohouse.launcher.api.requests.TimezoneUpdateRequest;
import com.pomohouse.launcher.api.requests.UpdateFirebaseRequest;
import com.pomohouse.launcher.api.requests.WearerStatusRequest;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataListModel;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.launcher.models.events.CallContact;
import com.pomohouse.library.networks.ResponseDao;
import com.pomohouse.library.networks.ResultGenerator;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Admin on 8/17/16 AD.
 */
public interface WatchService {

    @POST("initDevice")
    Observable<ResultGenerator<DeviceInfoModel>> callInitialDevice(@Body InitDeviceRequest param);

    @POST("pair/pinCode")
    Observable<ResultGenerator<PinCodeModel>> callPinCode(@Body ImeiRequest param);

    @POST("contact/requestAllContacts")
    Observable<ContactCollection> callReadyRequestFriend(@Body ImeiRequest param);

    @POST("requestEventAndUpdateInfo")
    Observable<EventDataListModel> callUpdateInfoAndGetEventService(@Body LocationUpdateRequest request);

    @POST("requestUpdateWearerStatus")
    Observable<ResponseDao> callSenderWearerStatusService(@Body WearerStatusRequest request);

    @POST("requestAccident")
    Observable<ResponseDao> callFallStatusService(@Body ImeiRequest request);

    @POST("requestSOS")
    Observable<ResponseDao> callSOS(@Body ImeiRequest param);

    @POST("requestStatusShutdown")
    Observable<ResponseDao> callShutdownDevice(@Body ImeiRequest param);

    @POST("setting/setTimeZone")
    Observable<ResponseDao> callUpdateTimezone(@Body TimezoneUpdateRequest timeZoneParam);

    @POST("fitness/sendFitness")
    Observable<ResponseDao> callUpdateStep(@Body StepRequest param);

    @Multipart
    @POST("gallery/uploadPhoto")
    Observable<ResultGenerator<UploadModel>> callImageService(@PartMap Map<String, RequestBody> param, @Part MultipartBody.Part file);

    @POST("location/updateLocation")
    Observable<ResponseDao> callUpdateLocation(@Body RefreshLocationRequest locationInfo);

    @POST("location/requestQRCode")
    Observable<ResultGenerator<QRCodeModel>> callRequestQRCode(@Body ImeiRequest param);

    /*@GET("contact/checkAllowCalling")
    Observable<ResultGenerator<CallContact>> callCheckAllowCalling(@Query("to") String toContactId, @Query("from") String fromContactId);*/
/*
    @POST("event/updateFCMToken")
    Observable<ResponseDao> callUpdateFCMToken(@Body UpdateFirebaseRequest requestParam);*/
}