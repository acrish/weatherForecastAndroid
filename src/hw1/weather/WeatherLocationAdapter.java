package hw1.weather;


import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeatherLocationAdapter extends BaseAdapter {

	private Activity context;
	ArrayList<WeatherLocation> whLocs;
	
	public WeatherLocationAdapter(Activity context) {		
		this.context = context;
		whLocs = new ArrayList<WeatherLocation>();
	}

	public int getCount() {
		return whLocs.size();
	}

	public Object getItem(int position) {
		if (position >= 0 && position <= whLocs.size())
			return whLocs.get(position);
		return null;
	}

	public long getItemId(int position) {	
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View elem;
		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			elem = inflater.inflate(R.layout.whlocation, null);
		}
		else elem = convertView;
		
		TextView country = (TextView)elem.findViewById(R.id.locationCountry);
		TextView town = (TextView)elem.findViewById(R.id.locationTown);
		
		WeatherLocation loc = whLocs.get(position);
		if (loc != null) {
			town.setText(loc.getTown());
			country.setText(loc.getCountry());
		}		
		
		return elem;
	}
	
	public boolean addWeatherLocation (String town, String country) {
		WeatherLocation loc = new WeatherLocation(town, country);
		if (!whLocs.contains(loc)) {
			whLocs.add(loc);
			Collections.sort(whLocs);		
			this.notifyDataSetChanged();
			return true;
		}
		return false;		
	}
	
	public boolean removeWeatherLocation (int position) {
		int size = whLocs.size();
		if (position > -1 && position < size){
			whLocs.remove(position);
			if (size > whLocs.size()) {
				this.notifyDataSetChanged();
				return true;
			}
		}
		return false;
	}

}
