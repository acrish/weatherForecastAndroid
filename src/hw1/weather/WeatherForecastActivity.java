package hw1.weather;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 
 * @author Andreea-Cristina Hodea
 * Date: 2011
 *
 * Weather Forecast Activity handles a list of possible weather forecasts locations.
 * 
 * Click on a city starts another activity which shows forecast for selected location, long click popups a dialog box 
 * for removal confirmation and eventually remove selected city from list. A button at the bottom of the screen is used
 * to add other cities to the list. For the list to persist, it is saved to an intern file and reloaded at every run. 
 * Both removal and adding bring the file up to date.  
 * 
 * Format of history file is: city (country).
 */
public class WeatherForecastActivity extends ListActivity {
	WeatherLocationAdapter adapter;
	private final String historyFileName = "forecastHistory.txt";
	private final int DIALOG_CONFIRM_REMOVE = 0;
	private int selectedPos;
	
	private void readForecastHistory(){
		InputStream fis = null;
		BufferedReader br = null;
		String line;
		try {
			//fis = getResources().openRawResource(R.raw.history);
			fis = openFileInput(historyFileName);
			br = new BufferedReader(new InputStreamReader(fis));
			while ((line = br.readLine()) != null) {
				//line = town (country)
				int index = line.indexOf("(");
				if (index  > -1 && index < line.length())
					adapter.addWeatherLocation(line.substring(0, index-1), line.substring(index + 1, line.length() - 1));
				
			}
		} catch (FileNotFoundException e) {
			System.err.println("Forecast history not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading locations history.");
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void updateForecastHistory() {
		FileOutputStream fos = null;
		BufferedWriter out = null;

		try {
			fos = openFileOutput(historyFileName, Context.MODE_WORLD_WRITEABLE);
			out = new BufferedWriter(new OutputStreamWriter(fos));
			for (int i = 0, n = adapter.getCount(); i < n; i++){
				String loc = adapter.getItem(i).toString();
				out.append(loc);
				out.newLine();
				out.flush();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void addToForecastHistory(String town, String country) {

		if (adapter.addWeatherLocation(town, country))			
			updateForecastHistory();
	}
	
	private void removeFromForecastHistory(int position) {
		
		if (adapter.removeWeatherLocation(position)) {			
			updateForecastHistory();
		}
		
	}
	
	protected Dialog onCreateDialog (int id) {
		switch(id) {
		case DIALOG_CONFIRM_REMOVE: 
			return new AlertDialog.Builder(WeatherForecastActivity.this)
			.setTitle(R.string.dialog_remove_loc_title)
			.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					removeFromForecastHistory(selectedPos);
				}
			})
			.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			}).create();
		}
		return null;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        adapter = new WeatherLocationAdapter(this);
        setListAdapter(adapter);
        
        readForecastHistory();
        
        Button addLocButton = (Button)findViewById(R.id.addLocButton);
        addLocButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(WeatherForecastActivity.this, AddLocActivity.class);
				startActivity(intent);
			}
		}); 
        
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> listAdapter, View view, int position, long id) {
				selectedPos = position;
				showDialog(DIALOG_CONFIRM_REMOVE);				
				return true;
			}
		});
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> listAdapter, View view, int position, long id) {
        		WeatherLocation loc = (WeatherLocation)adapter.getItem (position);
        		if (loc != null) {
        			Intent goToIntent = new Intent();
        			goToIntent.setClass(WeatherForecastActivity.this, LocForecastActivity.class);
        			goToIntent.putExtra("loc", loc.getTown());
        			startActivity(goToIntent);
        		}
        		
			}
		});
    }
    
    public void onResume() {
    	super.onResume();
    	Intent intent = getIntent();
    	WeatherLocation loc = (WeatherLocation) intent.getSerializableExtra("loc");
    	if (loc != null) {
    		//resume after adding new location
    		addToForecastHistory(loc.getTown(), loc.getCountry());
    	}
    }
}