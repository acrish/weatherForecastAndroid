package hw1.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Andreea Hodea
 * 
 * Display forecast for a city if the city name is known. Weather forecast is gathered with an API from Google: 
 * "http://www.google.com/ig/api?weather=". After loading the XML file, it is parsed using XmlPullParser, data of 
 * interest is saved to forecast list and eventually 4 day weather forecast is displayed.
 *
 * Note: Feature to be added - progress dialog when loading and parsing xml file. 
 */
public class LocForecastActivity extends Activity{
	
	private List<String> keyWords, forecast;
	
	
	public void parseXmlFile(String fileContent) {
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput( new StringReader (fileContent) );
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG && keyWords.contains(xpp.getName()))
					trackData(xpp, eventType);
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(forecast);
	}
	
	public void trackData(XmlPullParser xpp, int eventType) throws XmlPullParserException, IOException {
		String principalTag = xpp.getName();
		do {
			eventType = xpp.next();
			if (eventType == XmlPullParser.END_DOCUMENT || (eventType == XmlPullParser.END_TAG && xpp.getName().equals(principalTag)))
				break;
			if (eventType == XmlPullParser.START_TAG && keyWords.contains(xpp.getName()))
				trackArgument(xpp, eventType, "data");
		}while(true);
	}
	
	public void trackArgument(XmlPullParser xpp, int eventType, String argument) {
		for (int i = 0, n = xpp.getAttributeCount(); i < n; i++)
			if (xpp.getAttributeName(i).equals(argument)) {
				String val = xpp.getAttributeValue(i);
				forecast.add(val);
				Log.d("Xml parsing", xpp.getName() + ": " + val);
				break;
			}
	}
	
	/**
	 * Downloads xml file containing weather forecast for a town. Weather parameter within URL escapes blanks.
	 *  
	 * @param town town/city
	 * @return false if an error took place, otherwise true.
	 */
	public boolean downloadFile(String town) {
		town = town.toLowerCase().replace(" ", "%20");
		try {
			HttpGet getMethod=new HttpGet("http://www.google.com/ig/api?weather=" + town);
			HttpClient client = new DefaultHttpClient ();

			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			
			String responseBody=client.execute(getMethod, responseHandler);
			// responseBody reprezinta continutul fisierului RSS luat de pe Internet
			if (!responseBody.contains("forecast_information"))
				return false;
			parseXmlFile(responseBody);
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		} 
		return true;
	}
	
	public void setCurrentForecast() {

		TextView currCond = (TextView)findViewById(R.id.curr_condition);
		currCond.setText(forecast.get(1));
		
		TextView currTemp = (TextView)findViewById(R.id.curr_temperature);
		currTemp.setText("Temperature: " + forecast.get(2) + " C");
		
		TextView currHumidity = (TextView)findViewById(R.id.curr_humidity);
		currHumidity.setText(forecast.get(3));
		
		TextView currWindCond = (TextView)findViewById(R.id.curr_wind_condition);
		currWindCond.setText(forecast.get(5));
		
		ImageView imageView = (ImageView)findViewById(R.id.curr_icon);
		loadImage(imageView, 4);
	}
	
	private void loadImage(ImageView i, int index) {
		try {
			  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL("http://www.google.com/" + 
					  										forecast.get(index)).getContent());
			  i.setImageBitmap(bitmap); 
			} catch (MalformedURLException e) {
			  e.printStackTrace();
			} catch (IOException e) {
			  e.printStackTrace();
			}
	}
	
	/**
	 * Set parameters for a day.
	 * 
	 * @param day week day
	 * @param low lowest temperature
	 * @param i icon
	 * @param cond condition
	 * @param code index of the day within the set of four days.
	 */
	public void setDayForecast(TextView day, TextView low, ImageView i, TextView cond, int code){
		int index = 6 + code * 4;
		if (code == 0)			
			day.setText("Today");
		else 
			day.setText(forecast.get(index));
		low.setText("Low: " + ((Long.parseLong(forecast.get(index + 1)) - 32) * 5 / 9) + " C");
		loadImage(i, index+2);
		//imageView.setImageResource(R.drawable.sunny);
		cond.setText(forecast.get(index + 3));
	}
	
	/**
	 * Set parameters for all four days.
	 */
	public void setForecastPerDays(){		
		TextView day = (TextView)findViewById(R.id.day1);
		TextView low = (TextView)findViewById(R.id.low1);
		ImageView imageView = (ImageView)findViewById(R.id.icon1);
		TextView cond = (TextView)findViewById(R.id.cond1);
		setDayForecast(day, low, imageView, cond, 0);
		
		day = (TextView)findViewById(R.id.day2);
		low = (TextView)findViewById(R.id.low2);
		imageView = (ImageView)findViewById(R.id.icon2);
		cond = (TextView)findViewById(R.id.cond2);
		setDayForecast(day, low, imageView, cond, 1);
		
		day = (TextView)findViewById(R.id.day3);
		low = (TextView)findViewById(R.id.low3);
		imageView = (ImageView)findViewById(R.id.icon3);
		cond = (TextView)findViewById(R.id.cond3);
		setDayForecast(day, low, imageView, cond, 2);
		
		day = (TextView)findViewById(R.id.day4);
		low = (TextView)findViewById(R.id.low4);
		imageView = (ImageView)findViewById(R.id.icon4);
		cond = (TextView)findViewById(R.id.cond4);
		setDayForecast(day, low, imageView, cond, 3);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.weather_forecast);
		Intent intent = getIntent();
		
		TextView loc = (TextView)findViewById(R.id.loc);		
		
		String town = intent.getStringExtra("loc");
		loc.setText("Prognoza meteo pentru " + town);
		Log.d("intent", "nume: " + town);
		
		keyWords = new LinkedList<String>();
		forecast = new ArrayList<String>();
		String[] aux = new String[] {"forecast_information", "forecast_date", "current_conditions", "condition", 
				"temp_c", "humidity", "icon", "wind_condition", "forecast_conditions", "day_of_week", "low"};
		keyWords = Arrays.asList(aux);
		if (downloadFile(town)) {
			setCurrentForecast(); 
			setForecastPerDays();			
		}
		else {
			loc.setText(R.string.city_not_found);
		}
		
		Button backButton = (Button)findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent backIntent = new Intent();
				backIntent.setClass(LocForecastActivity.this, WeatherForecastActivity.class);
				startActivity(backIntent);
			}
		});
		
	}

}
