package edu.ncsu.vmallad.soc_project1;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.location.LocationListener;
import android.content.ContentUris;
import android.net.Uri;
import android.database.Cursor;

public class AlertMeActivity extends MapActivity {

  LocationManager locationManager;
  MapController mapController;
  AlertsPositionOverlay positionOverlay;

  
  
  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);

    MapView myMapView = (MapView) findViewById(R.id.myMapView);
    mapController = myMapView.getController();

    // Configure the map display options
    myMapView.setSatellite(true);

    // Zoom in
    mapController.setZoom(17);

    // Add the AlertsPositionOverlay
    positionOverlay = new AlertsPositionOverlay(this);
    List<Overlay> overlays = myMapView.getOverlays();
    overlays.add(positionOverlay);

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    criteria.setAltitudeRequired(false);
    criteria.setBearingRequired(false);
    criteria.setCostAllowed(true);
    criteria.setPowerRequirement(Criteria.POWER_LOW);

    // setting up the location manager
    String provider = locationManager.getBestProvider(criteria, true);
    LocationListener locationListener = new LocationListener()
	 {
	     public void onLocationChanged(Location location) { updateWithNewLocation(location); }
	
	     public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	     public void onProviderEnabled(String provider) {}
	
	     public void onProviderDisabled(String provider) {}
	 };

    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    Location location = locationManager.getLastKnownLocation(provider);

    if (location != null)
      updateWithNewLocation(location);
  }

  /** Update UI with a new location */
  private void updateWithNewLocation(Location location) {
    TextView myLocationText = (TextView) findViewById(R.id.myLocationText);
    String latLongString;
    double   lat  = 0; 	// latitude of the entered location
    double lng =0; 		//longitude of the entered location
    String alert  = ""; 
    double lati = 0;	//latitude of the  location saved
    double lngi = 0;	//longitude of the  location saved
    double min = 999999999; // sufficiently large distance
    if (location != null) {
      // Update the map location.
      Double geoLat = location.getLatitude() * 1E6;
      Double geoLng = location.getLongitude() * 1E6;
      GeoPoint point = new GeoPoint(geoLat.intValue(), geoLng.intValue());

      mapController.animateTo(point);

      // update my position marker
      positionOverlay.setLocation(location);

       lat = location.getLatitude();
       lng = location.getLongitude();

      latLongString = "Lat:" + lat + "\nLong:" + lng;
    } else {
      latLongString = "No location found";
    }

  //code to display the alert.

    Cursor cur = managedQuery(AlertProvider.CONTENT_URI, null, null, null, null);
    System.out.println(cur.getColumnIndex(AlertProvider.KEY_ALERT));
    int alertColumn = cur.getColumnIndex(AlertProvider.KEY_ALERT); 
    int latColumn = cur.getColumnIndex(AlertProvider.KEY_PLACE_LAT);
    int lngColumn = cur.getColumnIndex(AlertProvider.KEY_PLACE_LNG);
        if (cur.moveToFirst()) {
	            
            do {
           	
            	System.out.println("I am here");
                lati= (double)cur.getInt(latColumn)/1000000;
                lngi= (double)cur.getInt(lngColumn)/1000000; 
                System.out.println(lati+","+lngi );
                System.out.println(lat+","+lng );
                double dist = getDistance(lati, lngi, lat, lng);

                // code to find the nearest bookmarked location(alert entered)
                System.out.println(dist);
                             
                if(dist<min){
                	min = dist;
                alert = cur.getString(alertColumn);	
                }
            } while (cur.moveToNext());
            }
        System.out.println(min);
        if(min<50)
        	myLocationText.setText("Your Current Position is: \n" + latLongString  + "\n" + alert);
        else
        	myLocationText.setText("Your Current Position is: \n" + latLongString  + "\n" );
                  
  }

  
  /**
   * Finds distance between two coordinate pairs.
   *
   * @param lat1 First latitude in degrees
   * @param lon1 First longitude in degrees
   * @param lat2 Second latitude in degrees
   * @param lon2 Second longitude in degrees
   * @return distance in meters
   */
  public static double getDistance(double lat1, double lon1, double lat2, double lon2) {

    final double Radius = 6371 * 1E3; // Earth's mean radius

    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return Radius * c;
  }

  
 
}
