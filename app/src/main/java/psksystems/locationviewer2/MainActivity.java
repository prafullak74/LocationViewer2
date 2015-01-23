package psksystems.locationviewer2;

import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.view.MenuItem;
import android.util.Log;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.location.LocationManager;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import android.location.Geocoder;
import android.location.Address;


public class MainActivity extends ActionBarActivity implements LocationListener {

    private LocationManager mgr;
    private Location location;
    private static final String tag = "LV20";
    private static final String STATUS[] = {"Out of Service", "Temporarily Unavailable", "Available"};
    private static final String POWERREQ[] = {"Invalid", "N/A", "Low", "Medium", "High"};
    private String best;
    private String provider = "Unknown";
    private String lg;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        criteria = new Criteria();
        //criteria.setAccuracy(1);
        lg = "*************CRITERIA\r\n";
        lg = lg + "Accuracy: " + criteria.getAccuracy() + "\r\n";
        lg = lg + "Horizontal Accuracy: " + criteria.getHorizontalAccuracy() + "\r\n";
        lg = lg + "Power Requirement: " + criteria.getPowerRequirement() + "\r\n";
        lg = lg + "Speed Requirement: " + criteria.getSpeedAccuracy() + "\r\n";
        lg = lg + "\r\n*************CRITERIA\r\n";
        Log.i(tag, lg);

        best = mgr.getBestProvider(criteria, true);
        lg = "Best provider = " + best;
        //Log.i(tag, lg);
        location = getLastLoc();


        // PSK_ToDo: implement updateUI kind of function and call it here
        UpdateUI();

    }

    public void UpdateUI() {
/*        lg = "************************ Updates *******************\r\n";
        lg = lg + "Provider: " + provider + "\r\n";
        lg = lg + location.toString();
        lg = lg + "************************ End Updates ***************\r\n";
        Log.i(tag, lg);
*/
        TextView tvLat = (TextView) findViewById(R.id.txtLatitude);
        TextView tvLan = (TextView) findViewById(R.id.txtLongitude);
        TextView tvTime = (TextView) findViewById(R.id.txtTime);
        TextView tvAccuracy = (TextView) findViewById(R.id.txtAccuracy);
        TextView tvProvider = (TextView) findViewById(R.id.txtProvider);
        TextView tvPower = (TextView) findViewById(R.id.txtPower);
        TextView tvAltitude = (TextView) findViewById(R.id.txtAltitude);
        TextView tvSpeed = (TextView) findViewById(R.id.txtSpeed);
        TextView tvAddr = (TextView) findViewById(R.id.txtAddress);

        if(location == null)
        {
            tvLat.setText("Unknown");
            tvLan.setText("Unknown");
            tvTime.setText("Unknown");
            tvAccuracy.setText("N/A");
            tvPower.setText("N/A");
            tvAltitude.setText("N/A");
            tvSpeed.setText("N/A");
            tvAddr.setText("N/A");
            return;
        }

        tvLat.setText(location.getLatitude() + "");

        tvLan.setText(location.getLongitude() + "");

        Long longdt = Long.valueOf(location.getTime());
        Calendar cal = Calendar.getInstance();
        //int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
        Date da = new Date();
        da = new Date(longdt); // subtract if needed - da = new Date(longdt - (long)offset);
        cal.setTime(da);
        String tmp = cal.getTime().toString();
        String tm = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(da);
        tvTime.setText(tm);

        LocationProvider locProvider = mgr.getProvider(location.getProvider());

        tvAccuracy.setText(location.getAccuracy() + "");
        //tvAccuracy.setText(locProvider.getAccuracy() + "");

        //tvProvider.setText(location.getProvider());
        tvProvider.setText(locProvider.getName());

        tvPower.setText(POWERREQ[locProvider.getPowerRequirement()]);

        if (location.hasAltitude())
            tvAltitude.setText(location.getAltitude() + "");
        else
            tvAltitude.setText("N/A");

        if (location.hasSpeed())
            tvSpeed.setText(location.getSpeed() + "");
        else
            tvSpeed.setText("N/A");

        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        Address address;
        String addr = "";
        try {
            List<Address> lst = gcoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = lst.get(0);
            for(int i=0;i <= address.getMaxAddressLineIndex();i++)
            {
                addr += address.getAddressLine(i) + "\r\n";
            }
            //Log.i(tag, addr);
        }
        catch(Exception ignored) {

        }

        tvAddr.setText(addr);



    }

    public Location getLastLoc() {
        Location loc = null;
        boolean isGps;
        boolean isNetwork;
        boolean isBest;

        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGps = mgr.isProviderEnabled(mgr.GPS_PROVIDER);
        isNetwork = mgr.isProviderEnabled(mgr.NETWORK_PROVIDER);
        best = mgr.getBestProvider(criteria, true);
        isBest = mgr.isProviderEnabled(best);

        if(isBest) {
            provider = best;
            mgr.requestLocationUpdates(best, 25000, 20, this);
            loc = mgr.getLastKnownLocation(provider);
//            return loc;
        }
        else {
            if(isNetwork) {
                provider = "Network";
                mgr.requestLocationUpdates(mgr.NETWORK_PROVIDER, 25000, 20, this);
                loc = mgr.getLastKnownLocation(provider);
                }
            if(isGps) {
                provider = "GPS";
                mgr.requestLocationUpdates(mgr.NETWORK_PROVIDER, 25000, 20, this);
                loc = mgr.getLastKnownLocation(provider);
            }
        }

        if(!isGps && !isNetwork) {
            provider = "Unknown";
        }
        return loc;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(tag, "Starting location updates");
        mgr.requestLocationUpdates(best, 15000, 1, this);

        // For testing only
        //mgr.requestLocationUpdates("gps", 15000, 1, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(tag, "Stopping location updates");
        mgr.removeUpdates(this);
    }

    public void onLocationChanged(Location location) {
        // PSK_ToDo: callupdateUI here
        this.location = location;
        Log.i(tag, "Location changed, updating UI...\r\n");
        UpdateUI();

    }

    public void onProviderDisabled(String provider) {
        lg = "Provider disabled: " + provider;
        Log.i(tag, lg);
    }

    public void onProviderEnabled(String provider) {
        lg = "Provider enabled: " + provider;
        Log.i(tag, lg);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        lg = "Provider status changed: " + provider + " , status " + STATUS[status] + " , extras " + extras;
        Log.i(tag, lg);
    }

}
