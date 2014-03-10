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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class RichiestaMultipla extends Richiesta {
	private HashMap<String,HashMap<String,String>> mDataMulti = null;// post data
	/**
	 * constructor
	 */
	public RichiestaMultipla(HashMap<String,HashMap<String,String>> data,Context context) {
		super();
		setmDataMulti(data);
		this.context=context;
	}


	public String metodo() { return "multi"; }

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
				//object.put("key", "eb88e6f401ff19d1ce9f0a07c28fddbf08e661d3"); //server gaia.cri.it
				object.put("key", "bb2c08ff4da11f0b590a7ae884412e2bfd8ac28a"); //server
				JSONArray objectmulti= new JSONArray();
				Iterator<String> ita = mDataMulti.keySet().iterator();			
				while (ita.hasNext()) {
					String key = ita.next();
					mData=mDataMulti.get(key);
					Iterator<String> ita1 = mData.keySet().iterator();
					JSONObject objectsingle = new JSONObject();
					objectsingle.put("metodo", "utente");
					JSONObject parametri=new JSONObject();
					while (ita1.hasNext()) {
						String key1 = ita1.next();
						parametri.put(key1, mData.get(key1));
					}
					objectsingle.put("parametri", parametri);
					objectmulti.put(objectsingle);
                }
				object.put("richieste", objectmulti);
            	//Log.d("json", json);
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
				e.printStackTrace();
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
						e.printStackTrace();
						errore=5;
					}
					if(errore==0){
						Log.i("Gaia", str);
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
		RichiestaMultipla.sid = sid;
	}

	public HashMap<String, HashMap<String, String>> getmDataMulti() {
		return mDataMulti;
	}
	public void setmDataMulti(HashMap<String, HashMap<String, String>> data) {
		this.mDataMulti = data;
	}
}
