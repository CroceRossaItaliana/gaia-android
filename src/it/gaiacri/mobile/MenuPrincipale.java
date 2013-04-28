package it.gaiacri.mobile;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MenuPrincipale extends Activity {

	public String sid = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principale);
		
		sid = getIntent().getExtras().getString("sid");
		
		Log.i("sid", sid);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu_principale, menu);
		return true;
	}

}
