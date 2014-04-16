package it.gaiacri.mobile;


import it.gaiacri.mobile.MainActivity.RichiestaLogout;
import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuPrincipale extends Fragment {


	public String sid = "";

	public TextView nome;

	public String user_nome;

	private Context context;
	public SharedPreferences sharedPref;
	private FragmentActivity activity;
	private RelativeLayout v;
	/** 
	 * splash screen
	 */

	private View mSplashView;
	private View mMenuView;



	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v= (RelativeLayout)inflater.inflate(R.layout.activity_menu_principale, container, false);
		context=super.getActivity().getApplicationContext();
		Log.d("screen", super.getActivity().getRequestedOrientation() +"");

		mSplashView=v.findViewById(R.id.splash);
		mMenuView=v.findViewById(R.id.menu_principale);

		nome = (TextView) v.findViewById(R.id.tv_nome);

		context=super.getActivity();
		activity=super.getActivity();
		sharedPref = context.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);

		Bundle b=this.getArguments();
		//Log.d("prova", b+ "");
		if(b!= null){
			richiestaDati();
		}else{
			if(user_nome==null){
				//splash
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		return v;
	}	

	private void richiestaDati() {
		//avviare download "io"
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaChiSono richiesta = new RichiestaChiSono(data);
		richiesta.execute();
	}
	public void richiestaWelcome(){
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaWelcome welcome = new RichiestaWelcome(data);
		welcome.execute();
	}

	private void startSplash() {
		mSplashView.setVisibility(View.VISIBLE);
		mMenuView.setVisibility(View.GONE);
		TextView t=((TextView) v.findViewById(R.id.Loading));
		t.setText(this.getString(R.string.login_connessione));
		richiestaWelcome();
	}

	/*
	class RichiestaScansione extends Richiesta {
		public RichiestaScansione(HashMap<String, String> data) {
			super(data,MenuPrincipale_1.this.context);
		}
		public String metodo() { return "scansione"; }
		protected void onPostExecute(String ris) {

			if(ErrorJson.Controllo(ris,MenuPrincipale_1.this,risposta)==0){
				try {

					AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipale_1.this);
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
	}*/

	public void annulla(){
		user_nome=null;
	}

	public class RichiestaWelcome extends Richiesta {

		public String metodo() { return "ciao"; }

		public RichiestaWelcome(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
			setSid(sharedPref.getString("sid", ""));
		}

		@Override
		protected void onPostExecute(String str) {
			if(ErrorJson.Controllo(str, activity,risposta)==0){
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("sid", getSid());
				editor.commit();

				if ( utente != null ) {
					Log.e("Login", "Gia Identificato");
					richiestaDati();
				} else {
					Log.e("Login", "Non gia Identificato");
					Intent myIntent = new Intent(context, Accesso.class);
					startActivity(myIntent);
					activity.finish();
				}
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(MenuPrincipale.this.getActivity());
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaWelcome();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}

	class RichiestaChiSono extends Richiesta {
		public RichiestaChiSono(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
		}

		public String metodo() { return "io"; }

		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris, activity,risposta)==0){
				try {
					user_nome=risposta.getJSONObject("anagrafica").getString("nome") ;//+ " " + risposta.getJSONObject("anagrafica").getString("cognome");
					nome.setText("Ciao, "+user_nome);
					JSONArray user_comitato=risposta.getJSONArray("appartenenze");//+ " " + risposta.getJSONObject("anagrafica").getString("cognome");
					if(user_comitato.length()==0) {
						utenteSenzaComitato();
					}
				} catch (JSONException e) {
					//se passo qua e perche non c'e il comitato oppure non c'e l'anagrafica
				}
				mSplashView.setVisibility(View.GONE);
				mMenuView.setVisibility(View.VISIBLE);
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				MainActivity.enable();
				AddPosta();

			}
		}

		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(MenuPrincipale.this.getActivity());
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaDati();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}

	public void utenteSenzaComitato(){
		AlertDialog.Builder miaAlert = new AlertDialog.Builder(this.getActivity());
		miaAlert.setMessage("Ciao, "+user_nome+", "+getString(R.string.error_comitato));
		miaAlert.setCancelable(false);
		miaAlert.setNegativeButton(R.string.error_internet_no, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
				  getActivity().setResult(100);
				  getActivity().finish();
			  }
			});
		miaAlert.setPositiveButton(R.string.ns_menu_setting_logout, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {  
				richiestaLogout();
			}
		});
		
		AlertDialog alert = miaAlert.create();
		alert.show();		
		
	}
	
	class RichiestaLogout extends Richiesta {
		public RichiestaLogout(HashMap<String, String> data) {
			super(data,MenuPrincipale.this.context);
		}
		public String metodo() { return "logout"; }
		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,MenuPrincipale.this.getActivity(),risposta)==0){
				getActivity().setResult(Activity.RESULT_OK);
				//annulla();
				Intent myIntent = new Intent(MenuPrincipale.this.getActivity(), Accesso.class);
				startActivity(myIntent);
				getActivity().finish();
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(MenuPrincipale.this.getActivity());
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaLogout();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}

	public void richiestaLogout(){
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaLogout asd = new RichiestaLogout(data);
		asd.execute();
	}
	
	
	//potrebbe dare problemi...sicuramente dara problemi :P
	public void AddPosta(){
		FragmentActivity activity=this.getActivity();
		if(activity!= null){
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.posta_frame, new PostaIngresso()).commit(); //
		}
	}

}
