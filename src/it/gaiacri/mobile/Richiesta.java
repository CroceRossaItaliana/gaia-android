package it.gaiacri.mobile;


import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class Richiesta extends AsyncTask<String, String, String> {
	private HashMap<String, String> mData = null;// post data

	private static String sid = "";
	public static String base = "https://gaia.cri.it/api.php?a=";
	public Context context;
	public JSONObject risposta = null;
	public JSONObject utente   = null;
	public JSONArray attivita= null;

	/**
	 * constructor
	 */
	public Richiesta(HashMap<String, String> data,Context context) {
		setmData(data);
		this.context=context;
	}


	public String metodo() { return "welcome"; }



	/**
	 * background
	 */
	@Override
	protected String doInBackground(String... params) {
		int errore=0;
		String str = "";
		if(isDeviceConnected()){
			byte[] result = null;			
			
			HttpClient client = new DefaultHttpClient();
					//getClient(20);//new DefaultHttpClient();

			//TODO parte che andra commentata
			/*String string="";

			string="&sid="+sid;
			Iterator<String> it = getmData().keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				string=string.concat("&"+key+"="+getmData().get(key));
			}
			HttpPost post = new HttpPost(base + metodo() +string);// in this case, params[0] is URL
			//TODO fine parte che andra commentata
			*/
			HttpPost post = new HttpPost(base + metodo());

			try {
				//inizio parte da decommentare
				// set up post data
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("sid", getSid()));
				Iterator<String> it = mData.keySet().iterator();
            	while (it.hasNext()) {
                	String key = it.next();
                	nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
            	}
				post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
				//Log.d("request",string+" ");
				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}else
					errore=1;
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				errore=2;
			}
			catch (Exception e) {
				Log.e("Gaia", "probabilmente non c'è internet");
				Log.e("Error", e.getMessage());
				errore=3;
			}
			if(errore==0){//continua solo se nello stadio precedente non ci sono stati errori
				try {
					risposta = new JSONObject(str);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("Gaia", "Errore di comunicazione col server");
					errore=4;
				}
				if(errore==0){
					//TODO va gestito errore in caso di JSON malformattato 
					try {
						Log.e("Gaia", risposta.getJSONObject("session").getString("id"));
						setSid(risposta.getJSONObject("session").getString("id"));

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						errore=5;
					}
					if(errore==0){
						Log.i("Gaia", str);
						try {
							utente	 = risposta.getJSONObject("session").optJSONObject("user");
							if(metodo().equals("attivita"))
								attivita=risposta.getJSONArray("response");
							else
								risposta = risposta.getJSONObject("response");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							errore=6;
						}
					}
				}
			}
		}else{
			str="Errore Internet";
		}


		if(errore==0)
			return str;
		else
			return "Errore";
	}

	/**
	 * controlla se e' presenta la connessione ad internet, sia mobile che wi-fi
	 *
	 * @param context il context dell'applicazione
	 * @return true se il dispositivo e' connesso ad internet (wi-fi o mobile), false altrimenti
	 */
	public Boolean isDeviceConnected() {

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo mobileDataInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if(mobileDataInfo == null){
			//gestione nel caso siamo stati avviati su un tablet senza modulo telefonico(tutti i device che non hanno lo slot sim)
			if (wifiInfo.getState() != NetworkInfo.State.CONNECTED) {
				return false;
			}
		}else{
			//caso generico di device con modulo 3g
			if (wifiInfo.getState() != NetworkInfo.State.CONNECTED && mobileDataInfo.getState() != NetworkInfo.State.CONNECTED) {
				return false;
			}
		}

		return true;
	}
	
	/*
	 * Autenticazione che ignora il certificato
	 * soluzione temporanea di test
	 */
	/*public static DefaultHttpClient getClient(int timeout) {
		
		// Log.i(TAG,"getClient()");
		DefaultHttpClient ret = null;

		//versione 1.1

		try {
			SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
			sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			// Enable HTTP parameters
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			//gestione timeout
			//HttpConnectionParams.setConnectionTimeout(params, timeout);
			//HttpConnectionParams.setSoTimeout(params, timeout*1000);
			
			
			// Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sslFactory, 443));

			// Create a new connection manager using the newly created registry and then create a new HTTP client
			// using this connection manager
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			ret = new DefaultHttpClient(ccm, params);

		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}*/
	
	
	
	

	/**
	 * on getting result
	 */
	@Override
	protected void onPostExecute(String result) {
	}

	public static String getSid() {
		return sid;
	}

	public static void setSid(String sid) {
		Richiesta.sid = sid;
	}


	public HashMap<String, String> getmData() {
		return mData;
	}


	public void setmData(HashMap<String, String> mData) {
		this.mData = mData;
	}

}
