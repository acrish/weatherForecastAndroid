package hw1.weather;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Weather forecast is gathered with an API from Google: "http://www.google.com/ig/api?weather=". After loading the XML 
 * file, it is parsed using XmlPullParser, data of interest is saved to forecast list. A progress bar shows the 
 * application is working while parsing the XML file.
 * 
 * @author acrish
 *
 */
public class ForecastParser extends Activity implements Runnable{

	private ArrayList<String> forecast = new ArrayList<String>();
	private List<String> keyWords;
	private String town, originalTown;
	private ProgressDialog dialog;
	private Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {		
			dialog.dismiss();
			Intent intent = new Intent();
			intent.setClass(ForecastParser.this, LocForecastActivity.class);
			intent.putExtra("loc", originalTown);
			intent.putStringArrayListExtra("forecast", forecast);
			startActivity(intent);
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		town = intent.getStringExtra("loc");
		originalTown = town;
		String message = getString(R.string.parse_forecast_xml_message) + " " + town;
		dialog = ProgressDialog.show(this, "", message);
		
		new Thread(this).start();
	}
	
	public void run() {
		String[] aux = new String[] {"forecast_information", "forecast_date", "current_conditions", "condition", 
				"temp_c", "humidity", "icon", "wind_condition", "forecast_conditions", "day_of_week", "low"};
		keyWords = Arrays.asList(aux);
		downloadFile();
		handler.sendEmptyMessage(0);
	}

	/**
	 * Downloads xml file containing weather forecast for a town. Weather parameter within URL escapes blanks.
	 *  
	 * @param town town/city
	 * @return false if an error took place, otherwise true.
	 */
	public boolean downloadFile() {
		town = town.toLowerCase().replace(" ", "%20");
		try {
			HttpGet getMethod=new HttpGet("http://www.google.com/ig/api?weather=" + town);
			HttpClient client = new DefaultHttpClient ();

			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			
			String responseBody=client.execute(getMethod, responseHandler);
			// responseBody represents the body of RSS file loaded from Internet
			if (!responseBody.contains("forecast_information"))
				return false;
			parseXmlFile(responseBody);
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		} 
		return true;
	}
	
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
	
}
