package it.gaiacri.mobile;


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
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuPrincipale extends android.support.v4.app.FragmentActivity implements TaskFragment.TaskCallbacks {

	public String sid = "";
	public MenuPrincipale attivita;

	public TextView nome;
	public TextView comitato;

	public static String user_nome;
	public static String user_comitato;

	private Context context;

	/** 
	 * splash screen
	 */

	private View mSplashView;
	private View mMenuView;

	private TaskFragment mTaskFragment;

	private FragmentManager fm;


	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principale);
		Log.d("screen", this.getRequestedOrientation() +"");
		attivita = this;
		
		
		fm = getSupportFragmentManager();
	    mTaskFragment = (TaskFragment) fm.findFragmentByTag("task");
	 
	    // If the Fragment is non-null, then it is currently being
	    // retained across a configuration change.
	    

		mSplashView=findViewById(R.id.splash);
		mMenuView=findViewById(R.id.menu_principale);

		nome = (TextView) findViewById(R.id.tv_nome);
		comitato = (TextView) findViewById(R.id.tv_comitato);

		context=this.getApplicationContext();

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
				if(user_nome != null && user_comitato != null){
					nome.setText(user_nome);
					comitato.setText(user_comitato);
					mSplashView.setVisibility(View.GONE);
					mMenuView.setVisibility(View.VISIBLE);
				}
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

	private void richiestaDati() {
		if (mTaskFragment == null) {
		      mTaskFragment = new TaskFragment();
		      fm.beginTransaction().add(mTaskFragment, "task").commit();
		}
		
	}

	private void startSplash() {

		
		mSplashView.setVisibility(View.VISIBLE);
		mMenuView.setVisibility(View.GONE);
		if (mTaskFragment == null) {
			  ((TextView) this.findViewById(R.id.Loading)).setText(this.getString(R.string.login_sessione));
		      mTaskFragment = new TaskFragment();
		      fm.beginTransaction().add(mTaskFragment, "task").commit();
		}
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
		//getMenuInflater().inflate(R.menu.activity_menu_principale, menu);
		return true;
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

	public void AssenzaInternet(){
		Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onPostExecuteW(String ret) {
		// TODO Auto-generated method stub
		if(ret.equals("Errore Internet"))
			AssenzaInternet();
		else{
			//mHandler.sendEmptyMessage(FINISH_LOAD);
			Intent myIntent = new Intent(context, Accesso.class);
			//myIntent.putExtra("sid", getSid());
			startActivity(myIntent);
			finish();
		}
			
		
	}

	@Override
	public void onPostExecuteC_1() {
		if(user_nome != null && user_comitato != null){
			nome.setText(user_nome);
			comitato.setText(user_comitato);
		}
		
	}

	@Override
	public void onPostExecuteC_2(String ris,JSONObject risposta) {
		if(ris.equals("Errore Internet"))
			AssenzaInternet();
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
		mSplashView.setVisibility(View.GONE);
		mMenuView.setVisibility(View.VISIBLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		
	}

	@Override
	public void onPostUpdate() {
		((TextView) this.findViewById(R.id.Loading)).setText(this.getString(R.string.login_download_dati));
		
	}




}
