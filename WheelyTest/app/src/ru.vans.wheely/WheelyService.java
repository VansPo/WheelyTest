package ru.vans.wheely;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by vans on 01.09.14.
 */
public class WheelyService extends IntentService {

    private static final long UPDATE_INTERVAL = 10*1000;
    private static final long FASTEST_INTERVAL = 5*1000;

    private final WebSocketConnection mConnection = new WebSocketConnection();;
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;

    private String login;
    private String password;

    public WheelyService() {
        super("WheelyService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals("ACTION_CONNECT")) {
            prepareConnection();
            login = intent.getStringExtra(ru.vans.wheely.Utils.LOGIN_FIELD);
            password = intent.getStringExtra(ru.vans.wheely.Utils.PASSWORD_FIELD);
            openConnection();
        } else if (action.equals("ACTION_DISCONNECT")) {
            if (mLocationClient.isConnected())
                mLocationClient.removeLocationUpdates(locationListener);
            mLocationClient.disconnect();
            mConnection.disconnect();
        }
    }

    private void prepareConnection(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLocationClient = new LocationClient(this, connectionCallbacks, onConnectionFailedListener);
        mLocationClient.connect();
    }

    private void openConnection() {

        final String wsuri = ru.vans.wheely.Utils.SERVER_ADDRESS + "?username=albert&password=albert";

        try {
            mConnection.connect(wsuri, new WebSocketConnectionHandler() {

                @Override
                public void onOpen() {
                    Log.d("wheely", "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage("{\n" +
                            "    \"lat\": 55.373703,\n" +
                            "    \"lon\": 37.474764\n" +
                            "}");
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d("wheely", "Got echo: " + payload);
                }

                @Override
                public void onClose(int code, Bundle data) {
                    Log.d("wheely", "Connection closed...");
                    mConnection.reconnect();
                }
            });
        } catch (WebSocketException e) {

            Log.d("wheely", e.toString());
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }
    };

    GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            mLocationClient.requestLocationUpdates(mLocationRequest, locationListener);
        }

        @Override
        public void onDisconnected() {

        }
    };

    GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener = new GooglePlayServicesClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    };
}
