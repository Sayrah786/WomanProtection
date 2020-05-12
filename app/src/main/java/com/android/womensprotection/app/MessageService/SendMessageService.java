package com.android.womensprotection.app.MessageService;

import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.womensprotection.app.Home;
import com.android.womensprotection.app.LocationTracer.EnableGPS;
import com.android.womensprotection.app.LocationTracer.GoogleService;
import com.android.womensprotection.app.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.android.womensprotection.app.Home.REQUEST_LOCATION;


/**
 * Created by Sumanth on 12-02-2018.
 */

public class SendMessageService extends AsyncTask<Void,Integer,Integer> {

    Context c;
    Button lv,b;
    String data;
    String longg, lat;

    //double longitude,latitude;
    LocationManager locationManager;
    String latitude, longitude;
    String cur;

    private static final int REQUEST_SMS = 0;


    public String txtmsg;


    ArrayList<String> players = new ArrayList<>();
    ArrayList<String> number = new ArrayList<>();

    ProgressDialog pd;
    public SendMessageService(Context c, String data, Button lv) {
        this.c = c;
        this.data = data;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.setTitle("Parser");
        pd.setMessage("Parsing ....Please wait");

        pd.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {

        return this.parse();

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (integer == 1) {
            sendMySMS();
        } else {
            AlertDialog.Builder builder=new AlertDialog.Builder(c);
            builder.setMessage("Contact list is empty,Add Contacts to send alert Message")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent i1=new Intent(c,ImportContacts.class);
                            c.startActivity(i1);

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alertDialog=builder.create();

            alertDialog.show();
        }
        pd.dismiss();
    }

    //PARSE RECEIVED DATA
    private int parse() {
        try {
            //ADD THAT DATA TO JSON ARRAY FIRST


            JSONArray ja = new JSONArray(data);

            //CREATE JO OBJ TO HOLD A SINGLE ITEM
            JSONObject jo = null;


            players.clear();

            //LOOP THRU ARRAY
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);

                //RETRIOEVE NAME
                String name = jo.getString("name");
                String cont = jo.getString("mobile_no");


                //ADD IT TO OUR ARRAYLIST
                players.add(name);
                number.add(cont);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public void sendMySMS() {
        String phone;
        c.startService(new Intent(c, GoogleService.class));
        locationManager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
            SharedPreferences sh=c.getSharedPreferences("mssg",Context.MODE_PRIVATE);
            txtmsg=sh.getString("message"," ");
            if(txtmsg.length()==0 || txtmsg==" ") {
                txtmsg="I am in trouble please help me,find me at this location ";
            }
            String mmm = txtmsg + cur;
            if (longg==null || lat==null) {
                Toast.makeText(c, "Location is not found", Toast.LENGTH_SHORT).show();
            } else {
                for (int i1 = 0; i1 < number.size(); i1++) {

                    phone = number.get(i1);
                    //Check if the phoneNumber is empty
                    SmsManager sms = SmsManager.getDefault();
                    // if message length is too long messages are divided
                    List<String> messages = sms.divideMessage(mmm);
                    for (String msg : messages) {

                        PendingIntent sentIntent = PendingIntent.getBroadcast(c, 0, new Intent("SMS_SENT"), 0);
                        PendingIntent deliveredIntent = PendingIntent.getBroadcast(c, 0, new Intent("SMS_DELIVERED"), 0);
                        sms.sendTextMessage(phone, null, "Women's protection: Alert! "+msg, sentIntent, deliveredIntent);
                    }
                }
                Toast.makeText(c, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) c, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                cur=("https://www.google.com/maps/search/?api=1&query=" + latitude+","
                        + longitude);
                longg=longitude;
                lat=latitude;


            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                cur=( "https://www.google.com/maps/search/?api=1&query=" + latitude+","
                        + longitude);
                longg=longitude;
                lat=latitude;


            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                latitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                cur=(  "https://www.google.com/maps/search/?api=1&query=" + latitude+","
                        + longitude);
                longg=longitude;
                lat=latitude;


            }else{

                Toast.makeText(c,"Unable to Trace your location",Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("GPS is disabled");
        builder.setMessage("Turn on the GPS to access your location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                            CheckGPS();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    /** Checks whether the GPS is on */
    protected static final String TAG = "LocationOnOff";
    private GoogleApiClient googleApiClient;
    //final static int REQUEST_LOCATION = 199;
    public void CheckGPS() {
        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(c)) {
            Toast.makeText(c,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }
        // Todo Location Already on  ... end

        if(!hasGPSDevice(c)){
            Toast.makeText(c,"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(c)) {
            Log.e("Sumanth","Gps already enabled");
            Toast.makeText(c,"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Log.e("Sumanth","Gps already enabled");
            Toast.makeText(c,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }
    }


    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(c)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final com.google.android.gms.common.api.Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult()
                                status.startResolutionForResult((Activity) c,REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

}




