package com.example.jokeoftheday;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Aniket Pansare and Sagar Abhang
 *
 */
public class MainActivity extends Activity 
{	
	/*---------------------------- Class Variables ----------------------------*/
	private String location_provider = "";
	private double altitude = 0;
	private double latitude = 0;
	private double longitude = 0;
	protected LocationManager location_manager = null;
	protected Location location = null;
	protected static String joke = "";
	
	/*---------------------------- Overridden Methods -------------------------*/
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override	
	protected void onStart() 
	{
		super.onStart();
	}

	/*-------------------------------------------------------------------------*/
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Get location manager instance  
        location_manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        if(location_manager != null) 
        {
            Criteria criteria = new Criteria();
            //Specify different criteria to get best location provider
        	criteria.setAccuracy(Criteria.ACCURACY_FINE);
        	criteria.setAltitudeRequired(true);
        	criteria.setBearingRequired(true);
        	criteria.setCostAllowed(true);
        	criteria.setPowerRequirement(Criteria.POWER_LOW); 
        	location_provider = location_manager.getBestProvider(criteria, true);
        	
        	Log.d("Verify: ","provider : "+location_provider);
	        
        	if(location_provider != null) 
        	{
        		//Using location manager and location provider get last known location
	        	location = location_manager.getLastKnownLocation(location_provider);
	        	//Display Location
		    	display_location(location);
	        }
	        else
	        {
	        	Log.d("Error: ", "location_provider == null");
	        }
        	
        	Button get_joke = (Button) findViewById(R.id.button_get_joke);
        	
        	get_joke.setOnClickListener(new OnClickListener() 
	        	{
					/* (non-Javadoc)
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					public void onClick(View v) 
					{
						ConnectGetThread connect_get_joke_thread = new ConnectGetThread();
						Thread thread_connect = new Thread(connect_get_joke_thread);
						
						//Start the thread to fetch the joke from joke server
						thread_connect.start();
						
						//Join the thread with main thread so that main thread will start executing 
						//after thread_connect has finished it's execution 
						try 
						{
							thread_connect.join();
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
						
						TextView joke_text = (TextView) findViewById(R.id.Text_joke);
						// Provide ScrollBar to TextView cause joke can have multiple lines. 
						joke_text.setMovementMethod(new ScrollingMovementMethod());
						joke = "Joke of the day is: \n\n" + joke;
						joke_text.setText(joke);
						
					}
				}
        	);        	
        	
        }
        else
        {
        	Log.d("Error: ", "location_manager == null");
        }
    }
    
    /*---------------------------------------------------------------------*/
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /*---------------------------------------------------------------------*/
    /**
     * @param location
     */
    public void display_location(Location location) 
    {
    	if(location != null) 
    	{
    		//Get altitude using location
    		altitude = location.getAltitude();
    		//Get altitude using latitude
    		latitude = location.getLatitude();
    		//Get altitude using longitude
    		longitude = location.getLongitude();
    		
    		TextView text_altitude = (TextView) findViewById(R.id.value_altitude);
    		TextView text_latitude = (TextView) findViewById(R.id.value_latitude);
    		TextView text_longitude = (TextView) findViewById(R.id.value_longitude);
    		
    		//Display values on the screen 
    		text_altitude.setText(String.valueOf(altitude));
    		text_latitude.setText(String.valueOf(latitude));
    		text_longitude.setText(String.valueOf(longitude));
    		
    		//Get Geocoder instance
    		Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
    		List<Address> address_list = null;
    		
    		try 
    		{
    			//Using Geocoder instance get address_list by passing parameters latitude and longitude
    			//Get only max number of addresses = 1
    			address_list = geocoder.getFromLocation(latitude, longitude, 1);
    		}
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
    		
    		if((address_list != null) && (address_list.size() > 0)) 
    		{
    			Address first_address = address_list.get(0);
    			String street_name1 = first_address.getAddressLine(0);
    			String street_name2 = first_address.getAddressLine(1);
    			String city_state_zip = first_address.getAddressLine(2);
    			String country = first_address.getAddressLine(3);
    			
    			//Displaying the address
    			TextView text_address = (TextView) findViewById(R.id.value_address);
    			StringBuffer text_addr = new StringBuffer("");
    			
    			if(street_name1 != null)
    			{
    				text_addr.append(street_name1);
    				text_addr.append(", ");
    			}
    			if(street_name2 != null)
    			{
    				text_addr.append(street_name2);
    				text_addr.append(", ");
    			}
    			if(city_state_zip != null)
    			{
    				text_addr.append(city_state_zip);
    				text_addr.append(", ");
    			}
    			if(country != null)
    			{
    				text_addr.append(country);
    			}
    			
    			text_address.setText(text_addr.toString());    				        			
    		}
        }
    	else
        {
    		Log.d("Error: ", "location == null");
        }
    }   
    
}

