package ru.smarthzkh.blackstork;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import ru.smarthzkh.blackstork.fragments.FragmentBillingList;
import ru.smarthzkh.blackstork.fragments.FragmentGraph;
import ru.smarthzkh.blackstork.fragments.FragmentHelp;
import ru.smarthzkh.blackstork.fragments.FragmentLogin;
import ru.smarthzkh.blackstork.fragments.FragmentSaveResult;
import ru.smarthzkh.blackstork.fragments.FragmentSettings;
import ru.smarthzkh.blackstork.fragments.FragmentStart;
import ru.smarthzkh.blackstork.other.AlarmReceiver;
import ru.smarthzkh.blackstork.other.Bill;
import ru.smarthzkh.blackstork.other.SaveLoadFile;


class BaseActivity extends AppCompatActivity {
    @Override
    public void onStart() {
        super.onStart();

        // Enclose everything in a try block so we can just
        // use the default view if anything goes wrong.
        try {
            // Get the font size value from SharedPreferences.
            SharedPreferences settings =
                    getSharedPreferences("ru.smarthzkh.blackstork", Context.MODE_PRIVATE);

            // Get the font size option.  We use "FONT_SIZE" as the key.
            // Make sure to use this key when you set the value in SharedPreferences.
            // We specify "Medium" as the default value, if it does not exist.
            String fontSizePref = settings.getString("FONT_SIZE", "SMALL");
            SharedPreferences.Editor editor =  settings.edit();
            // Select the proper theme ID.
            // These will correspond to your theme names as defined in themes.xml.
            int themeID = R.style.AppTheme_NoActionBar_Small;
            if (fontSizePref.equals("SMALL")) {
                themeID = R.style.AppTheme_NoActionBar_Small;
                editor.putString("FONT_SIZE", "SMALL");
            } else if (fontSizePref.equals("BIG")) {
                themeID = R.style.AppTheme_NoActionBar_Big;
                editor.putString("FONT_SIZE", "BIG");
            }

            editor.apply();

            // Set the theme for the activity.
            setTheme(themeID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SaveLoadFile s2 = new SaveLoadFile(Objects.requireNonNull(this),"template");
        String s[] = s2.ReadTemplate();
        if(s[0] != null && !s[0].equals("")) {
            Bill.name = s[0];
            Bill.phone = s[1];
            Bill.address = s[2];
            Bill.email = s[3];
        }

        android.support.v4.app.FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.container, new FragmentStart()).commit();


        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Doge", "Doge", importance);
            channel.setDescription("Test");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        createNotification();
    }

    private void createNotification() {
        Intent resultIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(this, 0, resultIntent, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60000, resultPendingIntent);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        TextView textViewEmail = findViewById(R.id.emailView);
        TextView textViewName = findViewById(R.id.firstnameView);
        try {
            textViewEmail.setText(Bill.email);
            textViewName.setText(Bill.name);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        FragmentSettings fragment = new FragmentSettings();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commitAllowingStateLoss();

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();



        if (id == R.id.nav_camera) {
            //fragTrans.replace(R.id.container, fragmentCamera);
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

        } else if (id == R.id.nav_share) {
            fragTrans.replace(R.id.container, new FragmentGraph());

        } else if (id == R.id.nav_send) {
            fragTrans.replace(R.id.container, new FragmentBillingList());

        } else if (id == R.id.nav_manage) {
            fragTrans.replace(R.id.container, new FragmentHelp());

        } else if (id == R.id.nav_login) {
            fragTrans.replace(R.id.container, new FragmentLogin());

        } else if (id == R.id.nav_settings) {
            fragTrans.replace(R.id.container, new FragmentSettings());
        }
            fragTrans.commit();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                String contents = result.getContents();
                    if (result.getFormatName().equals("QR_CODE")) {
                        Map<String, String> map = new LinkedHashMap<>();
                        for (String keyValue : contents.split(" *\\| *")) {
                            String pairs[] = keyValue.split(" *= *", 2);
                            map.put(pairs[0].toLowerCase(), pairs.length == 1 ? "" : pairs[1]);
                            if (pairs.length != 1)
                                pairs[0].toLowerCase();

                            if (map.containsKey("st00012") || map.containsKey("st00011")) {
                                Bundle extras = new Bundle();
                                extras.putSerializable("HashMap", (Serializable) map);
                                getIntent().putExtras(extras);
                                android.support.v4.app.FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
                                fragTrans.replace(R.id.container, new FragmentSaveResult());
                                fragTrans.commitAllowingStateLoss();
                                //fragTrans.commit();

                            }
                            else
                                Toast.makeText(this, "Данный QR код содержит неправильный формат!", Toast.LENGTH_LONG).show();
                        }
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}