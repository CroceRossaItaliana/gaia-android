package it.gaiacri.mobile;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi") public class MenuPrincipale extends Activity {

	public String sid = "";
	public MenuPrincipale attivita;
	
	public TextView nome = null;
	public TextView comitato = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principale);
		attivita = this;
		
		sid = getIntent().getExtras().getString("sid");
		
    	HashMap<String, String> data = new HashMap<String, String>();
		RichiestaChiSono richiesta = new RichiestaChiSono(data);
		richiesta.execute();
		
		Log.i("sid", sid);
		
		
        final Button logout = (Button) findViewById(R.id.button1);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	HashMap<String, String> data = new HashMap<String, String>();
            	RichiestaLogout asd = new RichiestaLogout(data);
        		asd.execute();
            }
        });        
        
        final Button scansione = (Button) findViewById(R.id.button2);
        scansione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		IntentIntegrator.initiateScan(attivita);
            }
        });


	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case IntentIntegrator.REQUEST_CODE: {
				if (resultCode != RESULT_CANCELED) {
					IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
					if (scanResult != null) {
						String upc = scanResult.getContents();
						 
						//put whatever you want to do with the code here
						Log.i("Barcode", upc);				
		            	HashMap<String, String> scd = new HashMap<String, String>();
		            	scd.put("code", upc);
		            	RichiestaScansione rs = new RichiestaScansione(scd);
		        		rs.execute();

						}
					}
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu_principale, menu);
		return true;
	}
	
	class RichiestaChiSono extends Richiesta {
		public RichiestaChiSono(HashMap<String, String> data) {
			super(data);
		}
		public String metodo() { return "me"; }
		protected void onPostExecute(String ris) {
			 nome = (TextView) findViewById(R.id.textView1);
			 comitato = (TextView) findViewById(R.id.textView2);
			 try {
				nome.setText(risposta.getJSONObject("anagrafica").getString("nome") + " " + risposta.getJSONObject("anagrafica").getString("cognome") );
				comitato.setText(((JSONObject) risposta.getJSONArray("appartenenze").get(0)).getJSONObject("comitato").getString("nome"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 

		}
	}	
	
	class RichiestaLogout extends Richiesta {
		public RichiestaLogout(HashMap<String, String> data) {
			super(data);
		}
		public String metodo() { return "logout"; }
		protected void onPostExecute(String ris) {
			setResult(Activity.RESULT_OK);
			finish();
		}
	}	
	
	class RichiestaScansione extends Richiesta {
		public RichiestaScansione(HashMap<String, String> data) {
			super(data);
		}
		public String metodo() { return "scansione"; }
		protected void onPostExecute(String ris) {
			Context context = getApplicationContext();
			try {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(attivita);
		        builder.setMessage(risposta.getString("nomeCompleto")+ "\n" + risposta.getString("comitato"))
		               .setPositiveButton("Qualcosa", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       // FIRE ZE MISSILES!
		                   }
		               })
		               .setNegativeButton("Dell'altro", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                       // User cancelled the dialog
		                   }
		               });
		        // Create the AlertDialog object and return it
		        builder.create().show();
		        
				
			} catch (JSONException e) {
				Toast.makeText(context, "Volontario non trovato!", Toast.LENGTH_LONG).show();

				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	


}
