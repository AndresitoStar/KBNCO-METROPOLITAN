package cu.tko.kbnco_metro;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cu.tko.kbnco_metro.fragments.HistorialFrg;
import cu.tko.kbnco_metro.fragments.Home;
import cu.tko.kbnco_metro.fragments.TransacFrg;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        Home.HomeFragmentListener {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private boolean isHome;
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestMultiplePermission();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                String USSD = "*444*40*03" + Uri.encode("#");
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + USSD));
                startActivity(callIntent);
            }
        });

        goHome();

        IntentFilter filter = new IntentFilter(ACTION);
        this.registerReceiver(SmsReceiver, filter);
    }

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isHome) {
                goHome();
            } else {
                Toast toast = Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT);
                if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
                    toast.cancel();
                    super.onBackPressed();
                    return;
                } else {
                    toast.show();
                }
                tiempoPrimerClick = System.currentTimeMillis();
            }
        }
    }

    private void showAlertAfterSms(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message.replaceAll("\n", "").replaceAll("\r", ""));
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public final BroadcastReceiver SmsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION.equals(action)) {
                Bundle intentExtras = intent.getExtras();

                if (intentExtras != null) {
                    Object[] sms = (Object[]) intentExtras.get("pdus");
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < sms.length; ++i) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                        String phone = smsMessage.getOriginatingAddress();
                        if (phone != null && phone.equalsIgnoreCase("pagoxmovil")) {
                            String message = smsMessage.getMessageBody().toString();
                            sb.append(message);
                            sb.append(" ");
                        }
                    }
                    showAlertAfterSms(sb.toString());
                }
            }
        }
    };

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
        if (!item.isChecked()) {
            int id = item.getItemId();

            if (id == R.id.menu_historial) {
                goHistorial();
            } else if (id == R.id.menu_home) {
                goHome();
            } else if (id == R.id.menu_transacciones){
                goTransac();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goTransac() {
        isHome = false;
        setTitle(R.string.transacciones);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment mFragment = new TransacFrg();
        mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
    }

    private void goHistorial() {
        isHome = false;
        setTitle(R.string.historial);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment mFragment = new HistorialFrg();
        mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
    }

    private void goHome() {
        isHome = true;
        setTitle(R.string.app_name);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment mFragment = new Home();
        mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
    }



//    @TargetApi(Build.VERSION_CODES.M)
//    private void chequearPermisos(String requestedPermision) {
//        // Should we show an explanation?
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, requestedPermision)) {
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{requestedPermision}, REQUEST_CODE_ASK_PERMISSIONS);
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
//            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //conPermisos = true;
//            } else {
//                //conPermisos = false;
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    @Override
    public void goHistorialFromHome() {
        goHistorial();
    }

    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        RECEIVE_SMS,
                        READ_SMS,
                        CALL_PHONE
                }, REQUEST_CODE_ASK_PERMISSIONS);

    }

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudioPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean SendSMSPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

//                    if (CameraPermission && RecordAudioPermission && SendSMSPermission) {
//
//                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
//
//                    }
                }

                break;
        }
    }
}
