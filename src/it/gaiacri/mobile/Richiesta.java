package it.gaiacri.mobile;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class Richiesta extends AsyncTask<String, String, String> {
	protected HashMap<String, String> mData = null;// post data

	protected static String sid = "";
	public static String base = "https://gaia.cri.it/api.php";
	//public static String base = "http://192.168.1.125/gaia/api.php";
	public Context context;
	public JSONObject risposta = null;
	public JSONObject richiesta = null;
	public JSONObject sessione = null;
	public JSONObject utente   = null;

	/**
	 * constructor
	 */
	public Richiesta(HashMap<String, String> data,Context context) {
		setmData(data);
		this.context=context;
	}


	public Richiesta() {
	}


	public String metodo() { return "ciao"; }

	@Override
	protected void onProgressUpdate (String... values){
		Log.d("update", "via");
		restore();
	}
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
			//HttpPost post = new HttpPost(base+metodo());
			HttpPost post = new HttpPost(base);
			try {
				// set up post data
				
				JSONObject object = new JSONObject();
				object.put("metodo", metodo());
				object.put("sid", getSid());
				//object.put("key", "bb2c08ff4da11f0b590a7ae884412e2bfd8ac28a"); //server 
				Iterator<String> ita = mData.keySet().iterator();
            	while (ita.hasNext()) {
                	String key = ita.next();
                	object.put(key, mData.get(key));
                }
            	//Log.d("json", object.toString());
				StringEntity se = new StringEntity(object.toString());
				//sets the post request as the resulting string
    			post.setEntity(se);
    			//sets a request header so the page receving the request
    			//will know what to do with it
    			post.setHeader("Accept", "application/json");
    			post.setHeader("Content-type", "application/json");
				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}else
					errore=1;
			}
			catch (UnsupportedEncodingException e) {
				//e.printStackTrace();
				errore=2;
			}
			catch (Exception e) {
				Log.e("Gaia", "probabilmente non c'e internet");
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
						//Log.e("Gaia", risposta.getJSONObject("sessione").getString("id"));
						setSid(risposta.getJSONObject("sessione").getString("id"));

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						errore=5;
					}
					if(errore==0){
						//Log.i("Gaia", str);
						try {
							utente	 = risposta.getJSONObject("sessione").optJSONObject("utente");
							sessione=risposta.getJSONObject("sessione");
							richiesta=risposta.getJSONObject("richiesta");
							//if(metodo().equals("attivita")){
							//	attivita=risposta.getJSONArray("risposta");
							//}else{
							risposta = risposta.getJSONObject("risposta");
							if(risposta.has("errore")){
								errore=7;
								return "Errore "+risposta.getJSONObject("errore").getString("messaggio");
							}
							//}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							errore=6;
						}
					}
				}
			}
		}else{
			str="Errore Internet";
			//ErrorJson.AssenzaInternet(context);
			publishProgress("");
			
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
	
	/**
	 * on getting result
	 */
	@Override
	protected void onPostExecute(String result) {
	}
	
	public void restore(){
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
