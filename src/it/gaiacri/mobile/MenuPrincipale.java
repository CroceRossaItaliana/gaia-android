package it.gaiacri.mobile;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuPrincipale extends Activity {

	public String sid = "";
	public MenuPrincipale attivita;

	public TextView nome;
	public TextView comitato;

	public static String user_nome;
	public static String user_comitato;

	private RichiestaChiSono richiesta;
	private Context context;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principale);
		attivita = this;

		nome = (TextView) findViewById(R.id.textView1);
		comitato = (TextView) findViewById(R.id.textView2);

		context=this.getApplicationContext();

		sid = getIntent().getExtras().getString("sid");

		richiesta=(RichiestaChiSono)getLastNonConfigurationInstance();

		if(user_nome == null && user_comitato==null && richiesta==null){
			//caso in cui l'utente abbia appena effettuato il login
			HashMap<String, String> data = new HashMap<String, String>();
			richiesta = new RichiestaChiSono(data);
			richiesta.execute();
		}else{//in questo caso l'utente ha ruotato lo schemo oppure e arrivato da un activity successiva e io ricarico i dati scaricati precedentemente
			if(user_nome != null && user_comitato != null){
				nome.setText(user_nome);
				comitato.setText(user_comitato);
			}
		}

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

		final Button adAttivita = (Button) findViewById(R.id.button3);
		adAttivita.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getBaseContext(), ElencoAttivita.class);
				startActivity(myIntent);
			}
		});


	}

	@Override
	public Object onRetainNonConfigurationInstance() {	    
		return(richiesta);
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
			super(data,MenuPrincipale.this.context);
		}

		public String metodo() { return "me"; }

		protected void onPostExecute(String ris) {
			if(ris.equals("Errore Internet"))
				Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
			else{
				try {
					user_nome=risposta.getJSONObject("anagrafica").getString("nome") + " " + risposta.getJSONObject("anagrafica").getString("cognome");
					nome.setText(user_nome);
					user_comitato=((JSONObject) risposta.getJSONArray("appartenenze").get(0)).getJSONObject("comitato").getString("nome");
					comitato.setText(user_comitato);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					//se passo qua e perche non c'e il comitato oppure non c'e l'anagrafica
					comitato.setText("Nessun Comitato");
				}
			}

		}
	}	

	class RichiestaLogout extends Richiesta {
		public RichiestaLogout(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
		}
		public String metodo() { return "logout"; }
		protected void onPostExecute(String ris) {
			if(ris.equals("Errore Internet"))
				Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
			else{
				setResult(Activity.RESULT_OK);
				annulla();
				Intent myIntent = new Intent(MenuPrincipale.this, Accesso.class);
				startActivity(myIntent);
				finish();
			}
		}
	}	

	class RichiestaScansione extends Richiesta {
		public RichiestaScansione(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
		}
		public String metodo() { return "scansione"; }
		protected void onPostExecute(String ris) {

			if(ris.equals("Errore Internet"))
				Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
			else{
				try {

					AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipale.this.attivita);
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
	public void annulla(){
		user_nome=null;
		user_comitato=null;
	}

	@Override
	public void onBackPressed() {
		//se l'utente preme indietro automaticamente vengono invalidati i campi
		annulla();
		super.onBackPressed();
	}




}
