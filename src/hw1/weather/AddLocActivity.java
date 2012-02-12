package hw1.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Represents a dialog activity for adding a new location to principal list.
 * 
 * @author Hodea Andreea
 *
 */
public class AddLocActivity extends Activity{
	
	public void goTo(WeatherLocation loc) {
		Intent intent = new Intent();
		intent.setClass(AddLocActivity.this, WeatherForecastActivity.class);
		intent.putExtra("loc", loc);
		startActivity(intent);
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_loc);
        
        Button addButton = (Button)findViewById(R.id.addLocButton),
        		cancelButton = (Button)findViewById(R.id.cancelLocButton);
        addButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				EditText town = (EditText)findViewById(R.id.addTown), 
						country = (EditText)findViewById(R.id.addCountry);

				if (town.getText().length() > 0 && country.getText().length() > 0) {
					WeatherLocation loc = new WeatherLocation(town.getText().toString(), country.getText().toString());
					goTo(loc);
				}
				else goTo(null);
			}
		});
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				goTo(null);
			}
		});        
	}
}
