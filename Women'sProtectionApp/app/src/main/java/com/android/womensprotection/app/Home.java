package com.android.womensprotection.app;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.womensprotection.app.CallingService.Call_Contacts;
import com.android.womensprotection.app.LocationTracer.GoogleService;
import com.android.womensprotection.app.MessageService.ContactList;
import com.android.womensprotection.app.Tools.Delete_Ac;
import com.android.womensprotection.app.Tools.show_contacts;
import com.android.womensprotection.app.UserInfo.Profile;
import com.android.womensprotection.app.UserInfo.Profile_Data;
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

import java.io.File;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button police, call, locat, cam;
    Boolean flag = false;
    public double L_latitude, L_longitude;
    public double longitude, latitude;
    public Geocoder geocoder;
    private static final int CAMERA_REQUEST = 111;
    static final int DRAG = 1;
    static final int NONE = 0;
    private static int RESULT_LOAD_IMAGE = DRAG;
    String filePath;
    Uri outputFileUri;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
   public static final int REQUEST_LOCATION = 199;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Button alert;
    TextView TV_Details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Intent intent = new Intent(Home.this, GoogleService.class);
        startService(intent);
        SharedPreferences spp = getSharedPreferences("login", MODE_PRIVATE);
        if (spp.contains("")) {
            startActivity(new Intent(Home.this, Login.class));
            finish();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Women's protection");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        nv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.menu_profile);
            }
        });
        fun_onLoad();
        Intent intent1 = new Intent(Home.this, GoogleService.class);
        startService(intent1);

        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        Button alert = (Button) findViewById(R.id.Alert);
        L_latitude = latitude;
        L_longitude = longitude;
        permessionCheck();
        CheckGPS();

        //TODO Sets the name of the user in navigation view layout.
        my_ac();

        SharedPreferences s=getSharedPreferences("mssg",MODE_PRIVATE);
        SharedPreferences.Editor e=s.edit();
        String msg=s.getString("message","");
        if(msg.length()<1){
            e.putString("message","I am in trouble please help me,find me at this location ");
            e.apply();
            e.commit();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            toolDialog();

        } else if (id == R.id.nav_exit) {
            final AlertDialog.Builder exitDialog=new AlertDialog.Builder(this);
            exitDialog.setTitle("Exit");
            exitDialog.setMessage("Exiting Women's protection?");
            exitDialog.setCancelable(true);
            exitDialog.setIcon(R.drawable.d_alert);
            exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            exitDialog.create();
            exitDialog.show();
        } else if (id == R.id.nav_logut) {
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sessionLogout();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setTitle("Log Out");
            dialog.setMessage("Are you sure to logout?");
            dialog.setIcon(R.drawable.d_alert);
            dialog.show();

        } else if (id == R.id.nav_share) {
            SharedPreferences spp = getSharedPreferences("login", MODE_PRIVATE);
            Toast.makeText(Home.this, spp.getString("username", "   "), Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_aboutus) {
            startActivity(new Intent(Home.this,About_us.class));
        }
        //NOTE:  Closing the drawer after selecting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    /**Creates a function that loads button and other controls*/
    public void fun_onLoad() {
        final Animation animEfct = AnimationUtils.loadAnimation(this, R.anim.butt_click_effect);

        final Animation shAnim = AnimationUtils.loadAnimation(this, R.anim.small_butt_click);

        final AnimationSet newA_Set = new AnimationSet(false);
        police = (Button) findViewById(R.id.Police);
        call = (Button) findViewById(R.id.Call);
        locat = (Button) findViewById(R.id.Location);
        cam = (Button) findViewById(R.id.Camera);


        Button alert = (Button) findViewById(R.id.Alert);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shAnim);
                storePic();

            }
        });
        locat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shAnim);
                SharedPreferences m=getSharedPreferences("mssg",MODE_PRIVATE);
                String txt=m.getString("message","");
                Toast.makeText(Home.this, "Location: " + L_latitude + ", " + L_longitude, Toast.LENGTH_LONG).show();
                if (L_longitude != 0 && L_latitude != 0) {
                    Intent share = new Intent("android.intent.action.SEND");
                    share.setType("text/*");
                    share.putExtra(Intent.EXTRA_SUBJECT, "Women's protection: Alert!");
                    share.putExtra(Intent.EXTRA_TEXT,  "Women's protection: Alert!  "+txt
                            + "https://www.google.com/maps/search/?api=1&query=" + L_latitude + "," + L_longitude);
                    startActivity(Intent.createChooser(share, "Share Location with"));
                }
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shAnim);
                flag = true;
                Intent intent = new Intent(Home.this, Call_Contacts.class);
                startActivity(intent);
                //pw = new PopupWindow(300, 470, true);
            }
        });
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(shAnim);
                Uri location = Uri.parse("geo:" + L_longitude + "," + L_latitude + "?q=Police station");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    public void profile(View v) {
        Intent intent = new Intent(Home.this, Profile.class);
        startActivity(intent);
    }

    public void Alert(View v) {
        final Animation animEfct = AnimationUtils.loadAnimation(this, R.anim.butt_click_effect);
        final AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(animEfct);
        v.startAnimation(animationSet);
        Intent intent = new Intent(Home.this, GoogleService.class);
        startService(intent);
        String url = "http://192.168.43.172/fetch/alertload.php";
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String user = sp.getString("username", " ");
        ContactList cl = new ContactList(this, url, alert);
        cl.execute(user);
        Intent b_intent = new Intent("" + L_latitude + "" + L_longitude + "");
        sendBroadcast(b_intent);
    }

    /***
     * # Capturing image and sharing#
     ****/
    public void storePic() {
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        this.outputFileUri = Uri.fromFile(file);
        Intent captureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        captureIntent.putExtra("output", this.outputFileUri);
        this.startActivityForResult(captureIntent, CAMERA_REQUEST);
    }

    private void shareImage() {
        msg = getSharedPreferences("mssg", MODE_PRIVATE);
        String ms=msg.getString("message"," ");
        if(ms.length()<5) {
            ms="I am in trouble please help me,find me at this location ";
        }
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("image/*");
        share.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(this.filePath)));
        share.putExtra(Intent.EXTRA_SUBJECT, "Women's protection: Alert!");
        share.putExtra(Intent.EXTRA_TEXT, "Women's protection: Alert! "+ms + "    https://www.google.com/maps/search/?api=1&query=" + L_latitude + "," + L_longitude);
        startActivity(Intent.createChooser(share, "Share Image with!"));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == -1 && this.outputFileUri != null) {
            BitmapFactory.Options ops = new BitmapFactory.Options();
            ops.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(this.outputFileUri.getPath(), ops);
            int scaleFactor = Math.min(ops.outWidth / 320, ops.outHeight / 240);
            ops.inJustDecodeBounds = false;
            ops.inSampleSize = scaleFactor;
            ops.inPurgeable = true;
            this.filePath = this.outputFileUri.getPath();
            shareImage();
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = new String[DRAG];
            filePathColumn[NONE] = "_data";
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[NONE]);
            this.filePath = cursor.getString(columnIndex);
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));
            L_longitude = longitude;
            L_latitude = latitude;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void permessionCheck() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.WRITE_CONTACTS,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.SEND_SMS};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    /**Displays the tool dialog box*/
    Delete_Ac DA=new Delete_Ac(this);

    public void toolDialog() {
        final TextView tv=new TextView(this);
        SharedPreferences spp = getSharedPreferences("login", MODE_PRIVATE);
        final String u_name1=spp.getString("username"," ");
        final String u_name2=u_name1;

        AlertDialog.Builder d_tool = new AlertDialog.Builder(this);

        final AlertDialog.Builder confirm_dialog=new AlertDialog.Builder(this);
        confirm_dialog.setTitle("Delete account?");
        confirm_dialog.setMessage("Account and details of "+u_name1+" will be deleted");
        confirm_dialog.setCancelable(true);
        confirm_dialog.setIcon(R.drawable.d_alert);

        d_tool.setIcon(R.drawable.ic_tool);
        d_tool.setTitle(R.string.d_tool_title);
        d_tool.setItems(R.array.tool_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    startActivity(new Intent(Home.this, show_contacts.class));
                }
                else if(which==2) {
                    confirm_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DA.execute("delete_ac",u_name1,u_name2);
                            sessionLogout();
                        }
                    });
                    confirm_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                confirm_dialog.create();
                confirm_dialog.show();
                }else if(which==1) {
                    EditMessageDialog();
                }
            }
        });
        d_tool.create();
        d_tool.show();
    }
    /**Shared preference for message text*/
    String m;
    SharedPreferences msg;
    protected void EditMessageDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Home.this);
        View promptView = layoutInflater.inflate(R.layout.edit_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home.this);
        alertDialogBuilder.setView(promptView);

        final EditText et = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        final TextView tv = (TextView) promptView.findViewById(R.id.msessg);
       // et.requestFocus();
        et.selectAll();
        et.setSelectAllOnFocus(true);
        et.didTouchFocusSelect();
        msg = getSharedPreferences("mssg", MODE_PRIVATE);
        m=msg.getString("message"," ");
        if(m.length()>5) {
            tv.setText(m);
            et.setText(m);
        }else {
            tv.setText("I am in trouble please help me,find me at this location ");
            et.setText("I am in trouble please help me,find me at this location ");
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>=50) {
                    Toast.makeText(Home.this,"Limit exceed",Toast.LENGTH_SHORT).show();

                }else {
                    tv.setText(s);
                }
            }
        });
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor e=msg.edit();
                        if(tv.getText().toString().length()<6) {
                            m="I am in trouble please help me,find me at this location ";
                        }else {
                            m=tv.getText().toString();
                        }
                        e.putString("message", m);
                        e.apply();
                        e.commit();
                        Toast.makeText(Home.this,"Message saved",Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog editDialog = alertDialogBuilder.create();
        editDialog.show();
    }

    /** Function that clears the session*/
    public void sessionLogout() {
        SharedPreferences profile=getSharedPreferences("profile",MODE_PRIVATE);
        SharedPreferences.Editor ed=profile.edit();
        ed.clear();
        ed.apply();
        ed.commit();

        SharedPreferences mm=getSharedPreferences("mssg",MODE_PRIVATE);
        SharedPreferences.Editor msgEdit= mm.edit();
        msgEdit.clear();
        msgEdit.apply();
        msgEdit.commit();

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.clear();
        e.apply();
        e.commit();
        startActivity(new Intent(Home.this, Login.class));
        finish();

    }
    private void my_ac(){
        SharedPreferences spp = getSharedPreferences("login", MODE_PRIVATE);

        SharedPreferences sp = getSharedPreferences("profile", MODE_PRIVATE);
        NavigationView n = (NavigationView) findViewById(R.id.nav_view);
        View header = n.getHeaderView(0);
        TV_Details = (TextView) header.findViewById(R.id.user_details);
        TextView u_name=(TextView)header.findViewById(R.id.U_NAME);
        if (sp.contains("name")) {
            TV_Details.setText(sp.getString("name", ""));
            u_name.setText(spp.getString("username",""));

        } else {
            String url = "http://192.168.43.172/fetch/load_personInfo.php";
            Profile_Data p = new Profile_Data(this, url, TV_Details, null, null, null);
            p.execute(spp.getString("username",""));
            u_name.setText(spp.getString("username",""));
        }
    }






    /**Checks GPS whether set on*/
    protected static final String TAG = "LocationOnOff";
    private GoogleApiClient googleApiClient;
    //final static int REQUEST_LOCATION = 199;
    public void CheckGPS() {
        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) Home.this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(Home.this)) {
            Toast.makeText(Home.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }
        // Todo Location Already on  ... end

        if(!hasGPSDevice(Home.this)){
            Toast.makeText(Home.this,"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(Home.this)) {
            Log.e("Sumanth","Gps already enabled");
            Toast.makeText(Home.this,"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Log.e("Sumanth","Gps already enabled");
            Toast.makeText(Home.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
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
            googleApiClient = new GoogleApiClient.Builder(Home.this)
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
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(Home.this, REQUEST_LOCATION);

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
