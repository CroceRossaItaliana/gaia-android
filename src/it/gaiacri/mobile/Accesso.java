package it.gaiacri.mobile;


import java.util.HashMap;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
public class Accesso extends Activity {

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
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		sharedPref=this.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
		//setContentView(R.layout.activity_accesso);
		loadWebView();
		// Let's display the progress in the activity title bar, like the
		// browser app does.
		//getWindow().requestFeature(Window.FEATURE_PROGRESS);

	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_accesso, menu);
		return true;
	}

	@Override
	protected void onPause() {
		Log.d("prova","pausa");
		super.onPause();
	}

	@Override
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
	}



	private void loadWebView() {
		context = this.getApplicationContext();
		if(webview == null){
			Log.i("create","webview");
			webview = new WebView(this);
			webview.getSettings().setJavaScriptEnabled(true);

			final Activity activity = this;
			webview.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView view, int progress) {
					setProgress(progress * 100);
					if(progress == 100) {
						setProgressBarIndeterminateVisibility(false);
						setProgressBarVisibility(false);
					}//activity.setProgress(progress * 1000);
				}
			});
			webview.setWebViewClient(new WebViewClient() {
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
				}
				public void onPageFinished(WebView view, String url) {
					HashMap<String, String> data = new HashMap<String, String>();
					RichiestaWelcome hello=new RichiestaWelcome(data);
					hello.execute();

					/*if(url_test==null){
						Log.d("1", "1");
						url_test=webview.getUrl();
					}else{
						Log.d("2", "2");
						if(!url_test.equals(webview.getUrl())){
							url_test=webview.getUrl();
							Log.d("3", "3");
							Intent i= new Intent(Accesso.this,MenuPrincipale.class);
							i.putExtra("sid", sid);		   
							startActivity(i);
							webview=null;
							finish();
						}
					}*/


				}
			});
			HashMap<String, String> data = new HashMap<String, String>();
			login = new RichiestaLogin(data);
			login.execute();
			setProgressBarIndeterminateVisibility(true);
			setProgressBarVisibility(true);
		}
		setContentView(webview);

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

			if(ris.equals("Errore Internet"))
				Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
			else{
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
				webview.loadUrl(url_login);
			}

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
			if(str.equals("Errore Internet")){
				Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
			}else{
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("sid", getSid());
				editor.commit();

				if ( utente != null ) {
					Log.e("Login", "Gia Identificato");
					Intent i= new Intent(Accesso.this,MenuPrincipale.class);
					i.putExtra("sid", sid);		   
					startActivity(i);
					webview=null;
					Accesso.this.finish();
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( keyCode == KeyEvent.KEYCODE_SEARCH) {
			Log.i("down","search");
			webview=null;
			finish();
			return true;
		}
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Log.i("down","back");
			if(webview.canGoBack() == true){
				webview.goBack();
				return true;
			}else{
				webview=null;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

}
