package com.android.moven;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.moven.CompletedJob.ListOfCompletedRides;
import com.android.moven.Fragments.DashboardFragment;
import com.android.moven.Fragments.ProgressDialogClass;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tv_nav_head_name, tv_mobile;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //do other stuff here
            String message = intent.getStringExtra("message");
            String type = intent.getStringExtra("type");
            String fare = intent.getStringExtra("fare");
            if (type.equals("PICKEDUP")) {
                //handler.removeCallbacks(runnable);

            } else if (type.equals("FINISHED")) {
                ConfigURL.PICK_UP_LAT = "";
                ConfigURL.PICK_UP_LNG = "";
                ConfigURL.DROP_OF_LAT = "";
                ConfigURL.DROP_OF_LNG = "";

                showFare(fare);

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tv_nav_head_name = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tv_nav_head_name.setText(ConfigURL.getName(this));

        tv_mobile = navigationView.getHeaderView(0).findViewById(R.id.tv_mobile);
        tv_mobile.setText(ConfigURL.getMobileNumber(this));
        /*String type = getA
        if (bundle != null) {
            van = (String) bundle.get("van");
            //Toast.makeText(getActivity(), "" + van, Toast.LENGTH_SHORT).show();
            callURL(van);
        }*/
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type != null && type.equals("FINISHED")) {

            String fare = intent.getStringExtra("fare");
            ConfigURL.PICK_UP_LAT = "";
            ConfigURL.PICK_UP_LNG = "";
            ConfigURL.DROP_OF_LAT = "";
            ConfigURL.DROP_OF_LNG = "";

            showFare(fare);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

        }
        getRideIfHave();

    }

    @Override
    public void onBackPressed() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            }
            /*if (ConfigURL.getOnStatus(DrawerMainActivity.this).equals("ONLINE")) {
                Toast.makeText(this, "Please Go Offline Or Press Home Button", Toast.LENGTH_LONG).show();
            } */
            else {
                super.onBackPressed();
            }
        }
    }

    public void share_with_your_friend() {

        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "TutorBay");
            String sAux = "Download Movan Application and get nearest van easily\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?com.abc.admin.pik";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share via"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            getRideIfHave();
        } else if (id == R.id.nav_setting) {
            Fragment fragmentName = null;
            Fragment ProfileFragment = new ProfileFragment();
            fragmentName = ProfileFragment;
            replaceFragment(fragmentName);
        } else if (id == R.id.nav_share) {
            share_with_your_friend();
        } else if (id == R.id.nav_history) {
            Fragment fragmentName = null;
            Fragment ListOfCompletedRides = new ListOfCompletedRides();
            fragmentName = ListOfCompletedRides;
            replaceFragment(fragmentName);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(this, SignInSignUpForgotActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //stopService(new Intent(DrawerMainActivity.this, MyService.class));
            startActivity(intent);
            ConfigURL.clearshareprefrence(this);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;


        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = false;
        try {

            fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commitAllowingStateLoss();
        }

    }

    public void getRideIfHave() {
        ProgressDialogClass.showRoundProgress(Drawer.this, "Please Wait While..!");
        AndroidNetworking.get(ConfigURL.URL_VAN_NUM_IF_RIDE_IS_ON_GOING)
                .addQueryParameter("customer_num", ConfigURL.getMobileNumber(Drawer.this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogClass.dismissRoundProgress();
                        try {
                            JSONArray jsonArray = response.getJSONArray("Rides");
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String van_number = jsonObject.getString("Van_Number");
                                String status = jsonObject.getString("Customer_Status");

                                if (status.equals("PICKEDUP")) {
                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();

                                    Fragment fragmentName = null;
                                    Fragment OnTheWay = new OnTheWay();
                                    fragmentName = OnTheWay;
                                    replaceFragment(fragmentName);
                                } else {
                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();
                                    Fragment fragmentName = null;
                                    Fragment SignInFragment = new StartrideFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("van", van_number);
                                    fragmentName = SignInFragment;
                                    fragmentName.setArguments(bundle);
                                    replaceFragment(fragmentName);
                                }

                            } else {
                               /* ConfigURL.PICK_UP_LAT = "";
                                ConfigURL.PICK_UP_LNG = "";
                                ConfigURL.DROP_OF_LAT = "";
                                ConfigURL.DROP_OF_LNG = "";*/
                                Fragment fragmentName = null;
                                Fragment SignInFragment = new DashboardFragment();
                                fragmentName = SignInFragment;
                                replaceFragment(fragmentName);
                            }

                        } catch (
                                JSONException e)

                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProgressDialogClass.dismissRoundProgress();
                    }
                });
    }

    public void showFare(String fare) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Drawer.this);
        builder1.setMessage("Fare for this journey is " + fare);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Dismiss",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mMessageReceiver, new IntentFilter(ConfigURL.PUSH_NOTIFICATION));

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);

    }

}
