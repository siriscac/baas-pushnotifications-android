package com.apigee.notificationsample;

/**
 * Created by muthu on 26/10/16.
 */

import android.util.Log;

import com.apigee.sdk.ApigeeClient;
import com.apigee.sdk.data.client.ApigeeDataClient;
import com.apigee.sdk.data.client.callbacks.ApiResponseCallback;
import com.apigee.sdk.data.client.callbacks.DeviceRegistrationCallback;
import com.apigee.sdk.data.client.entities.Device;
import com.apigee.sdk.data.client.response.ApiResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class NotificationFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIIDService";
    ApigeeClient client;
    ApigeeDataClient dataClient;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        dataClient = getClient().getDataClient();
        dataClient.registerDeviceForPushAsync(dataClient.getUniqueDeviceID(), "test-app", token, null, new DeviceRegistrationCallback() {
            @Override
            public void onDeviceRegistration(Device device) {

            }

            @Override
            public void onResponse(Device device) {
                // Associate the logged in user with this device.
                if (dataClient.getLoggedInUser() != null) {
                    dataClient.connectEntitiesAsync("users",
                            dataClient.getLoggedInUser().getUuid().toString(),
                            "devices", device.getUuid().toString(),
                            new ApiResponseCallback() {
                                @Override
                                public void onResponse(ApiResponse apiResponse) {
                                    Log.i(TAG, "connect response: " + apiResponse);
                                }

                                @Override
                                public void onException(Exception e) {
                                    Log.i(TAG, "connect exception: " + e);
                                }
                            });
                }
            }

            @Override
            public void onException(Exception e) {

            }
        });

    }

    synchronized ApigeeClient getClient() {
        if (client == null) {
            client = new ApigeeClient("mviswanathan", "brewery", this.getApplicationContext());
        }
        return client;
    }
}