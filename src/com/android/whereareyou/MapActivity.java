package com.android.whereareyou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
 
public class MapActivity extends Activity {
  
 private GoogleMap gMap;
 
 private Location userLocation;
 
 private LatLng userLocationLatLng = null;
 private LatLng phoneLatLng = null;
 
 String recipientPhoneNumber = null;
 String userPhoneNumber = null;
 
 boolean selfMarkerCreated = false;
 boolean otherMarkerCreated = false;
 Marker userMarkerLocation = null;
 
 Marker otherMarkerLocation = null;
 
 LocationManager myLocationManager;
 String PROVIDER = LocationManager.GPS_PROVIDER;
  
 @Override
 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_map);
	  getActionBar().setDisplayHomeAsUpEnabled(true);
	  
		// Get the message from the intent
		Intent intent = getIntent();
		recipientPhoneNumber = intent.getStringExtra(HomeActivity.EDIT_TEXT1);
		
		
		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
	    userPhoneNumber = tMgr.getLine1Number();
	    System.out.println("Returned phone number = " + userPhoneNumber);
	    if(userPhoneNumber == null) {
	    	userPhoneNumber = "55555";
	    	System.out.println("Null phone number returned, setting to default value of 55555");
	    }
		
	  myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	   
	  //get last known location, if available
	  Location lastKnownLoc = myLocationManager.getLastKnownLocation(PROVIDER);
	  userLocation = lastKnownLoc;
	  
	  // create the LatLng object needed for gmap
	  userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
	  
	  showMyLocation(lastKnownLoc);
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
	  userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
	  LatLng markerLoc = userLocationLatLng;
	  DoPOST mDoPOST = new DoPOST(MapActivity.this);
	  mDoPOST.execute("");
	  DoGET mDoGET = new DoGET(MapActivity.this);
	  mDoGET.execute("");
	  
	  if (selfMarkerCreated) {
		  animateMarker(userMarkerLocation, markerLoc, false);
	  } else {
		  System.out.println("hello");
		  userMarkerLocation = gMap.addMarker(new MarkerOptions().position(markerLoc).title("User Location!"));
		  selfMarkerCreated = true;
		  gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationLatLng, 15));
	  }
	  
	  // see if user entered a phone number recipient to watch, if so then create and move to that marker
	  if (recipientPhoneNumber != null && !recipientPhoneNumber.isEmpty() && phoneLatLng != null) {
		  if (otherMarkerCreated){
			  animateMarker(otherMarkerLocation, phoneLatLng, false);
		  } else {
			  System.out.println("hello2");
			  System.out.println(recipientPhoneNumber);
			  otherMarkerLocation = gMap.addMarker(new MarkerOptions().position(phoneLatLng).title("Other user's location!"));
			  gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(phoneLatLng, 15));
			  otherMarkerCreated = true;
		  }
	  }
	  
  } 
 }
 
 public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {
	 
	 final Handler handler = new Handler();
	 final long start = SystemClock.uptimeMillis();
	 Projection proj = gMap.getProjection();
	 Point startPoint = proj.toScreenLocation(marker.getPosition());
	 final LatLng startLatLng = proj.fromScreenLocation(startPoint);
	 final long duration = 500;
	 
	 final Interpolator interpolator = new LinearInterpolator();
	 
	 handler.post(new Runnable() {
		 @Override
		 public void run() {
			 long elapsed = SystemClock.uptimeMillis() - start;
			 float t = interpolator.getInterpolation((float) elapsed / duration);
			 double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
			 double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
			 marker.setPosition(new LatLng(lat, lng));
			 
			 if (t < 1.0) {
				 // Post again 16ms later.
				 handler.postDelayed(this, 16);
			 } else {
				 if (hideMarker) {
					 marker.setVisible(false);
				 } else {
					 marker.setVisible(true);
				 }
			 }
		 }
	 });
	 
 }
 
 public class DoGET extends AsyncTask<String, Void, Boolean> {

	 Context mContext = null;
	 
	 //Result data
	 double latitudeOfOther;
	 double longitudeOfOther;
	 
	 Exception exception = null;
	 
	 String phoneNumOfOther, latitude, longitude = null;
	 
	 InputStream is = null;
	 
	 String url = "http://192.168.1.76/whereareyouserver/httpget.php";
	 
	 JSONObject jObj = null;
	 String json="";
	 
    JSONArray numLocArray = null;
	 
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NUMLOC = "numLoc";
	 
	 DoGET(Context context) {
		 mContext = context;
	 }
	 
	@Override
	protected Boolean doInBackground(String... arg0) {
		
		// Defined URL  where to send data
		HttpClient httpclient = new DefaultHttpClient();
		
		phoneNumOfOther = getPhoneNumber();
		
		try {
			
			//Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("serial", getPhoneNumber()));
			String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
			url += "?" + paramString;
			HttpGet httpget = new HttpGet(url);
			
			HttpResponse httpresponse = httpclient.execute(httpget);
			HttpEntity httpentity = httpresponse.getEntity();
			is = httpentity.getContent();
			System.out.println("Sending out GET!");
			System.out.println("Using serial = " + phoneNumOfOther);
			
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        
        try {
        	System.out.println(jObj.toString());
        	
			// Checking for SUCCESS TAG
			int success = jObj.getInt(TAG_SUCCESS);
			 
			if (success == 1) {
				numLocArray = jObj.getJSONArray(TAG_NUMLOC);
			 
			    // looping through All Products
				JSONObject jObj = numLocArray.getJSONObject(0);
	        	System.out.println("GET numLoc json object = " + jObj.toString());
				setLatitudeOfOther(jObj.getDouble("latitude"));
				setLongitudeOfOther(jObj.getDouble("longitude"));
			} else {
				System.out.println("no numLoc found in received JSON object from server");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        LatLng otherLatLng = new LatLng(getLatitudeOfOther(), getLongitudeOfOther());
        
        phoneLatLng = otherLatLng;

		return true;
	}
	
	protected void onGetExecute(Boolean valid) {
		//Update the UI here
	}
	
	public String getPhoneNumber() {
		return recipientPhoneNumber;
	}
	
	public double getLatitudeOfOther() {
		return latitudeOfOther;
	}
	
	public double getLongitudeOfOther() {
		return longitudeOfOther;
	}
	
	public void setLatitudeOfOther(double lat) {
		latitudeOfOther = lat;
	}
	
	public void setLongitudeOfOther(double lon) {
		longitudeOfOther = lon;
	}
	 
 }
 
 public class DoPOST extends AsyncTask<String, Void, Boolean> {

	 Context mContext = null;
//	 String strSerialToSearch = "";
	 
	 //Result data
	 double latitudeOfOther;
	 double longitudeOfOther;
	 
	 Exception exception = null;
	 
	 String userPhoneNumberPost, latitude, longitude = null;
	 
	 DoPOST(Context context) {
		 mContext = context;
	 }
	 
	@Override
	protected Boolean doInBackground(String... arg0) {
		
		// Defined URL  where to send data
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.76/whereareyouserver/httppost.php");
		
		latitude = String.valueOf(getLat());
		longitude = String.valueOf(getLon());
		try {
			
			//Add data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("serial", getPhoneNumber()));
			nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
			nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			System.out.println("Sending out POST!");
			System.out.println("Latitude = " + latitude);
			System.out.println("Longitude = " + longitude);
			
			//Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			
			System.out.println(response.toString());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean valid){
		//Update the UI here
	}
	
	public String getPhoneNumber() {
		userPhoneNumberPost = userPhoneNumber;
		return userPhoneNumberPost;
	}
	
	public double getLat() {
		return userLocationLatLng.latitude;
	}
	
	public double getLon() {
		return userLocationLatLng.longitude;
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


