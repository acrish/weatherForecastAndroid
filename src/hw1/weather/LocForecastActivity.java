package hw1.weather;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
 
/** 
 * Display forecast for a city if the city name is known, otherwise display a message. Data needed for displaying the 
 * forecast is loaded through intents and eventually 4 day weather forecast is displayed.
 * 
 *  @author acrish
 */
public class LocForecastActivity extends Activity{
	
	ArrayList<String> forecast;
		
	public void setCurrentForecast() {

		TextView currCond = (TextView)findViewById(R.id.curr_condition);
		currCond.setText(forecast.get(1));
		
		TextView currTemp = (TextView)findViewById(R.id.curr_temperature);
		String text = getString(R.string.temperature) + " " + forecast.get(2) + " C";
		currTemp.setText(text);
		
		TextView currHumidity = (TextView)findViewById(R.id.curr_humidity);
		currHumidity.setText(forecast.get(3));
		
		TextView currWindCond = (TextView)findViewById(R.id.curr_wind_condition);
		currWindCond.setText(forecast.get(5));
		
		ImageView imageView = (ImageView)findViewById(R.id.curr_icon);
		loadImage(imageView, 4);
	}
	
	private void loadImage(ImageView i, int index) {
		if (index < forecast.size()) {
			String pic = forecast.get(index);
			try {
				String picName = "drawable/" + pic.substring(pic.lastIndexOf('/')+1, pic.lastIndexOf('.'));
				Log.i("drawable", picName);
				Drawable image = getResources().getDrawable(
													getResources().getIdentifier(picName, null, getPackageName()));
				i.setImageDrawable(image);
			}catch(Throwable t) {
				t.printStackTrace();
			}
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
		
		TextView loc = (TextView)findViewById(R.id.loc);
		
		Intent intent = getIntent();
		String town = intent.getStringExtra("loc");
		String message = getString(R.string.display_forecast_msg) + " " + town;
		loc.setText(message);
		Log.d("intent", "nume: " + town);	
		forecast = intent.getStringArrayListExtra("forecast");
		
		if (forecast != null && forecast.size() > 0){
			setCurrentForecast();
			setForecastPerDays();
		}
		else ((TextView)findViewById(R.id.forecast_now_text)).setText(R.string.selection_not_found);	
		
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
