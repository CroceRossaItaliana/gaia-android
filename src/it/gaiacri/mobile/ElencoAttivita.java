package it.gaiacri.mobile;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ElencoAttivita extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elenco_attivita);
		setTitle("Prossime attività");
		
        ListView listView = (ListView)findViewById(R.id.listElenco);
        String [] array = {"Questa","cosa","è","ancora", "in", "costruzione", "..."};
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.riga_attivita, R.id.textViewList, array);
        listView.setAdapter(arrayAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_elenco_attivita, menu);
		return true;
	}

}
