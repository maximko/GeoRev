package com.maximko.georev;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final int COPY_ID = 1;
    private final int ABOUT_ID = 2;

    private double latitude = 0;
    private double longitude = 0;

    TextView dataV;
    TextView coordinatesV;
    TextView providerV;
    GetCityData gcd;

    LocationManager lm;
    Location loc;

    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dataV = (TextView)findViewById(R.id.dataV);
        coordinatesV = (TextView)findViewById(R.id.coordinatesV);
        providerV = (TextView)findViewById(R.id.providerV);
        //setLocationProvider();
        gcd = new GetCityData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (lm != null) {
            lm.removeUpdates(LocLis);
        } 
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        setLocationProvider();
    }

    public void setLocationProvider() {
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, LocLis);
            providerV.setText(R.string.providerGPS);
        }
        else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, LocLis);
            providerV.setText(R.string.providerMOBILE);
        }
        else {
            AlertDialog.Builder noproviders = new AlertDialog.Builder(this);
            noproviders.setMessage(R.string.gpsOff)
                       .setCancelable(false)
                       .setTitle(R.string.problems)
                       .setPositiveButton(R.string.yea, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface di, int i) {
                               di.dismiss();
                               startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                           }                                            
                       })
                       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               finish();
                           }    
                       });
            noproviders.show();
        }
    }

    private LocationListener LocLis = new LocationListener() {

        public void onLocationChanged(Location lctn) {
            latitude = lctn.getLatitude();
            longitude = lctn.getLongitude();
            coordinatesV.setText("Координаты устройства:\nШирота: " + latitude + "\nДолгота: "
                                                                    + longitude + "\n");
        }

        public void onStatusChanged(String string, int i, Bundle bundle) {
            Toast.makeText(getApplicationContext(), R.string.onchanged, Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String string) {
            Toast.makeText(getApplicationContext(), R.string.onprovideren, Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String string) {
            Toast.makeText(getApplicationContext(), R.string.onproviderdis, Toast.LENGTH_SHORT).show();
        }

    };

    public void DD(View view) {
        new DisplayData().execute();
    }

    public void DJD(View view) {
        new DisplayJSONData().execute();
    }

    private class DisplayData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... paramss) {
            return gcd.getData(latitude, longitude);
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, MainActivity.this.getString(R.string.wait),
                                                            MainActivity.this.getString(R.string.loading), true);
        }

        @Override
        protected void onPostExecute(String result) {
            dataV.setText(result);
            dialog.dismiss();
        }

    }

    private class DisplayJSONData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... paramss) {
            return gcd.getJSONData(latitude, longitude);
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, MainActivity.this.getString(R.string.wait),
                                                            MainActivity.this.getString(R.string.jsonloading), true);
        }

        @Override
        protected void onPostExecute(String result) {
            dataV.setText(result);
            dialog.dismiss();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, COPY_ID, 0, R.string.clipboard).setIcon(android.R.drawable.ic_menu_set_as);
        menu.add(0, ABOUT_ID, 0, R.string.about).setIcon(android.R.drawable.ic_menu_info_details);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case COPY_ID:
                ClipboardManager c = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                c.setText(coordinatesV.getText() + "\n" + dataV.getText());
                Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
                return true;
            case ABOUT_ID:
                new AlertDialog.Builder(this).setMessage(R.string.aboutmsg)
                                             .setNeutralButton(R.string.ok, null)
                                             .setIcon(android.R.drawable.ic_dialog_info)
                                             .setTitle(R.string.about).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}
