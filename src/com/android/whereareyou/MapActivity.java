package com.android.whereareyou;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
 
public class MapActivity extends Activity {
  
 private GoogleMap gMap;
 
 private Location userLocation;
 
 LocationManager myLocationManager;
 String PROVIDER = LocationManager.GPS_PROVIDER;
  
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_map);
  getActionBar().setDisplayHomeAsUpEnabled(true);
   
  myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
   
  //get last known location, if available
  Location location = myLocationManager.getLastKnownLocation(PROVIDER);
  showMyLocation(location); 
 }
  
 @Override
 protected void onPause() {
  // TODO Auto-generated method stub
  super.onPause();
  myLocationManager.removeUpdates(myLocationListener);
 }
 
 @Override
 protected void onResume() {
  // TODO Auto-generated method stub
  super.onResume();
  myLocationManager.requestLocationUpdates(
    PROVIDER,     //provider
    0,       //minTime
    0,       //minDistance
    myLocationListener); //LocationListener
 }
 
 private void showMyLocation(Location l){
  if(l == null){
	  Log.d("NO_LOC", "No Location found!");
  }else{
	  gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	  
	  userLocation = l;
	  LatLng markerLoc = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
	  
	  gMap.addMarker(new MarkerOptions().position(markerLoc).title("User Location!"));
	  gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLoc, 15));
  }
   
 }
  
 private LocationListener myLocationListener
 = new LocationListener(){
 
  @Override
  public void onLocationChanged(Location location) {
   showMyLocation(location);
  }
 
  @Override
  public void onProviderDisabled(String provider) {
   // TODO Auto-generated method stub
    
  }
 
  @Override
  public void onProviderEnabled(String provider) {
   // TODO Auto-generated method stub
    
  }
 
  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
   // TODO Auto-generated method stub
    
  }};
}


