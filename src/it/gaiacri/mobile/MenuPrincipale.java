package it.gaiacri.mobile;


import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuPrincipale extends Activity {

	public String sid = "";

	public TextView nome;
	public TextView comitato;

	public String user_nome;
	public String user_comitato;

	private Context context;
	public SharedPreferences sharedPref;
	/** 
	 * splash screen
	 */

	private View mSplashView;
	private View mMenuView;


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principale);
		Log.d("screen", this.getRequestedOrientation() +"");

		// If the Fragment is non-null, then it is currently being
		// retained across a configuration change.


		mSplashView=findViewById(R.id.splash);
		mMenuView=findViewById(R.id.menu_principale);

		nome = (TextView) findViewById(R.id.tv_nome);
		comitato = (TextView) findViewById(R.id.tv_comitato);

		context=this.getApplicationContext();
		sharedPref = this.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);

		Intent i=getIntent();
		Bundle b=i.getExtras();
		if(b!= null){
			sid = b.getString("sid");
			richiestaDati();
		}else{
			if(user_nome==null && user_comitato==null){
				//splash
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				startSplash();
			}else{
				//in questo caso l'utente ha ruotato lo schemo oppure e arrivato da un activity successiva e io ricarico i dati scaricati precedentemente
				/*if(user_nome != null && user_comitato != null){
					nome.setText(user_nome);
					comitato.setText(user_comitato);
					mSplashView.setVisibility(View.GONE);
					mMenuView.setVisibility(View.VISIBLE);
					//se l'utente non ha comitato al momento disabilito la possibilita'di fargli vedere le attivita
					if(user_comitato.equals(getString(R.string.menu_no_comitato))){
						((Button)attivita.findViewById(R.id.button3)).setEnabled(false);
					}
				}*/
				Log.d("impossible", "qualcosa non va");
			}

			Log.i("sid", sid);


		}
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
				IntentIntegrator.initiateScan(MenuPrincipale.this);
			}
		});      

		final Button adAttivita = (Button) findViewById(R.id.button3);
		adAttivita.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getBaseContext(), ElencoAttivita.class);
				startActivityForResult(myIntent,50);
			}
		});

	}

	private void richiestaDati() {
		//avviare download "io"
		HashMap<String, String> data = new HashMap<String, String>();
        RichiestaChiSono richiesta = new RichiestaChiSono(data);
        richiesta.execute();
	}

	private void startSplash() {


		mSplashView.setVisibility(View.VISIBLE);
		mMenuView.setVisibility(View.GONE);
		((TextView) this.findViewById(R.id.Loading)).setText(this.getString(R.string.login_sessione));
		//avviare download "ciao"
		//TODO
		// Create and execute the background task.
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaWelcome welcome = new RichiestaWelcome(data);
		welcome.execute();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==50 && resultCode==100)
			finish();
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
		//getMenuInflater().inflate(R.menu.activity_menu_principale, menu);
		return true;
	}

	class RichiestaLogout extends Richiesta {
		public RichiestaLogout(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
		}
		public String metodo() { return "logout"; }
		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,MenuPrincipale.this)==0){
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

			if(ErrorJson.Controllo(ris,MenuPrincipale.this)==0){
				try {

					AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipale.this);
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

	public class RichiestaWelcome extends Richiesta {

		public String metodo() { return "ciao"; }

		public RichiestaWelcome(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
			setSid(sharedPref.getString("sid", ""));
		}

		@Override
		protected void onPostExecute(String str) {
			if(ErrorJson.Controllo(str, MenuPrincipale.this)==0){
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("sid", getSid());
				editor.commit();

				if ( utente != null ) {
					Log.e("Login", "Gia Identificato");

					//mCallbacks.onPostUpdate();
					//aggiorna scritta su display
					((TextView) findViewById(R.id.Loading)).setText(getString(R.string.login_download_dati));	
					richiestaDati();


					//mHandler.sendEmptyMessage(FINISH_LOAD);
				} else {
					Log.e("Login", "Non gia Identificato");
					//mHandler.sendEmptyMessage(FINISH_LOAD);
					Intent myIntent = new Intent(context, Accesso.class);
					//myIntent.putExtra("sid", getSid());
					startActivity(myIntent);
					finish();
				}
			}
		}
	}

	class RichiestaChiSono extends Richiesta {
		public RichiestaChiSono(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
		}

		public String metodo() { return "io"; }

		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris, MenuPrincipale.this)==0){
				try {
					user_nome=risposta.getJSONObject("anagrafica").getString("nome") + " " + risposta.getJSONObject("anagrafica").getString("cognome");
					nome.setText(user_nome);
					user_comitato=((JSONObject) risposta.getJSONArray("appartenenze").get(0)).getJSONObject("comitato").getString("nome");
					comitato.setText(user_comitato);
				} catch (JSONException e) {
					//se passo qua e perche non c'e il comitato oppure non c'e l'anagrafica

					user_comitato=getString(R.string.menu_no_comitato);
					comitato.setText(user_comitato);
					((Button)findViewById(R.id.button3)).setEnabled(false);
				}
				mSplashView.setVisibility(View.GONE);
				mMenuView.setVisibility(View.VISIBLE);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		}
	}



}
