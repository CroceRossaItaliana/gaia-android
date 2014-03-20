package it.gaiacri.mobile;

import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.HashMap;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@SuppressLint("SetJavaScriptEnabled")
public class Accesso extends ActionBarActivity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	public Context context;
	public static WebView webview;
	private static RichiestaLogin login;
	private String sid ="";
	public SharedPreferences sharedPref;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		supportRequestWindowFeature(Window.FEATURE_PROGRESS);
		sharedPref=this.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
		if(savedInstanceState != null)
		{
			if (savedInstanceState.isEmpty())
				Log.i("prova", "Can't restore state because bundle is empty.");
			else
			{
				if (webview.restoreState(savedInstanceState) == null)
					Log.i("prova" , "Restoring state FAILED!");      
				else
					Log.i("prova", "Restoring state succeeded.");      
			}

		}else{		
			loadWebView();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		if(webview!=null){
			if(webview.saveState(outState) == null)
				Log.i("prova","Saving state FAILED!");
			else
				Log.i("prova", "Saving state succeeded.");      
		}
	}

	@Override
	protected void onPause() {
		Log.d("prova","pausa");
		super.onPause();
	}

	/*@Override
	protected void onStop() {
		Log.d("prova","stop");
		if(webview!=null){
			webview.clearView();
			webview=null;
		}
		super.onStop();
	}

	@Override
	protected void onRestart() {
		Log.d("prova","restart");
		loadWebView();
		super.onRestart();
	}*/



	private void loadWebView() {
		getSupportActionBar();
		context = this.getApplicationContext();
		if(webview == null){
			Log.i("create","webview");
			webview = new WebView(this);
			webview.getSettings().setJavaScriptEnabled(true);

			final Activity activity = this;
			webview.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView view, int progress) {
					setSupportProgress(progress * 100);
					if(progress == 100) {
						setSupportProgressBarIndeterminateVisibility(false);
						setSupportProgressBarVisibility(false);
					}//activity.setProgress(progress * 1000);
				}
			});
			webview.setWebViewClient(new WebViewClient() {
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
				}
				public void onPageFinished(WebView view, String url) {
					richistaLogin();
				}
			});

			//setProgressBarIndeterminateVisibility(true);
			//setProgressBarVisibility(true);
		}
		setContentView(webview);
		setSupportProgressBarVisibility(true);
		setSupportProgressBarIndeterminateVisibility(true);

	}
	public void richistaLogin(){
		HashMap<String, String> data = new HashMap<String, String>();
		login = new RichiestaLogin(data);
		login.execute();
	}
	public void richiestaWelcome(){
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaWelcome welcome = new RichiestaWelcome(data);
		welcome.execute();
	}

	@Override
	protected void onDestroy() {
		Log.d("prova","quando passo qui");
		if(webview!=null){
			ViewGroup parentViewGroup = (ViewGroup) webview.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
		}
		super.onDestroy();
	}




	class RichiestaLogin extends Richiesta {
		public RichiestaLogin(HashMap<String, String> data) {
			super(data,Accesso.this.context);
		}
		public String metodo() { return "login"; }
		protected void onPostExecute(String ris) {

			if(ErrorJson.Controllo(ris,Accesso.this,risposta)==0){
				if(risposta==null){
					//TODO
					finish();
				}
				//TODO elabora risposta
				//TODO aggiorna tabella turni della view
				String url_login="";
				try {
					url_login = risposta.getString("url");
					sid= sessione.getString("id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//per gestire errore crash
				if(webview == null)
					loadWebView();
				else
					webview.loadUrl(url_login);
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(Accesso.this);
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richistaLogin();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}

	public class RichiestaWelcome extends Richiesta {

		public String metodo() { return "ciao"; }

		public RichiestaWelcome(HashMap<String, String> data) {
			super(data,Accesso.this.context);
			setSid(sharedPref.getString("sid", ""));
		}

		@Override
		protected void onPostExecute(String str) {
			if(ErrorJson.Controllo(str,Accesso.this,risposta)==0){
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("sid", getSid());
				editor.commit();

				if ( utente != null ) {
					Log.e("Login", "Gia Identificato");
					Intent i= new Intent(Accesso.this,MainActivity.class);
					i.putExtra("sid", sid);		   
					startActivity(i);
					webview=null;
					Accesso.this.finish();
				}
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(Accesso.this);
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaWelcome();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Log.i("down","back");
			if(webview!= null){
				if(webview.canGoBack() == true){
					webview.goBack();
					return true;
				}else{
					webview=null;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
