package balajianoopgupta.projects.com.sjsuinteractivemap;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MapActivity extends AppCompatActivity implements  LocationListener,SearchView.OnQueryTextListener {

    private ArrayAdapter<String> adapter;
    public final static String LOCATION = "balajianoopgupta.projects.com.sjsuinteractivemap";
    LocationManager locationManager;
    String provider;
    Location location;
    ArrayList<String> removalList;
    ListView lv;
    String[] arrayBuildings;


    @Override
    public void onLocationChanged(Location location) {

        Log.i("Status","In onLocationChanged");

        //2. Get the current location
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        Log.i("Status", "Latitude is: "+ lat.toString());
        Log.i("Status", "Longitude is: "+ lng.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        lv = (ListView) findViewById(R.id.listViewBuildings);

        //1. Initialize the locationManager and the provider
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false); //To return only enabled providers

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider);

        if(location != null){
            onLocationChanged(location);
        }



        View myMap = (View) findViewById(R.id.map);
        myMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float xValue = event.getX();
                float yValue = event.getY();
//                Log.i("Coordinates","X:"+xValue);
//                Log.i("Coordinates","Y:"+yValue);

                Intent building = new Intent(MapActivity.this,BuildingDetailActivity.class);
                Bundle data = new Bundle();
                float[] arr = new float[2];
                arr[0] = (float) location.getLatitude();
                arr[1] = (float) location.getLongitude();
                data.putFloatArray("Location",arr);
                building.putExtra(LOCATION,location);

                if( (xValue>=730 && xValue<=940) && (yValue>=590 && yValue<=900)){
                    Toast.makeText(MapActivity.this, "Engineering", Toast.LENGTH_SHORT).show();
                    data.putString("Building","eng");
                }
                else if( (xValue>=160 && xValue<=280) && (yValue>=590 && yValue<=830)){
                    Toast.makeText(MapActivity.this, "Kings Library", Toast.LENGTH_SHORT).show();
                    data.putString("Building","king");
                }
                else if( (xValue>=1150 && xValue<=1300) && (yValue>=1080 && yValue<=1230)){
                    Toast.makeText(MapActivity.this, "BBC", Toast.LENGTH_SHORT).show();
                    data.putString("Building","bbc");
                }
                else if( (xValue>=135 && xValue<=275) && (yValue>=1225 && yValue<=1425)){
                    Toast.makeText(MapActivity.this, "Yoshihiro", Toast.LENGTH_SHORT).show();
                    data.putString("Building","yoshihiro");
                }
                else if( (xValue>=723 && xValue<=915) && (yValue>=950 && yValue<=1060)){
                    Toast.makeText(MapActivity.this, "Student Union", Toast.LENGTH_SHORT).show();
                    data.putString("Building","union");
                }
                else if( (xValue>=430 && xValue<=700) && (yValue>=1710 && yValue<=1950)){
                    Toast.makeText(MapActivity.this, "South Parking", Toast.LENGTH_SHORT).show();
                    data.putString("Building","parking");
                }
                else{
                    //Do Nothing when clicked outside any of the boundaries mentioned above
                    return true;
                }

                building.putExtras(data);
                startActivity(building);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =(SearchView) menu.findItem(R.id.menuSearch).getActionView();

        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }



    private void handleItems() {

        arrayBuildings = new String[]{"King Library","Engineering Building","Yoshihiro Uchida Hall","Student Union","BBC","South Parking Garage"};
        //Copying all the buildings into removalList array and removing items when matched in search string
        removalList = new ArrayList<>(Arrays.asList(arrayBuildings));

        adapter = new ArrayAdapter<String>(MapActivity.this,android.R.layout.simple_list_item_1,removalList);

        lv.setAdapter(adapter);
        lv.setVisibility(View.INVISIBLE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String) parent.getItemAtPosition(position);
                Toast.makeText(MapActivity.this, "Option Selected: "+item, Toast.LENGTH_LONG).show();

                //Hide keypad after selecting an option in the listView
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.isEmpty()){
            handleItems();
        }
        else {

            for (String str: arrayBuildings) {
                    if(!(str.toLowerCase().contains(newText.toLowerCase()))){
                        removalList.remove(str);
                    }
            }
            lv.setVisibility(View.VISIBLE);
            lv.bringToFront();
            adapter.notifyDataSetChanged();
        }

        return false;
    }
}
