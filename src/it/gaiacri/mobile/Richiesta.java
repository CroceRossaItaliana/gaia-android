package it.gaiacri.mobile;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class Richiesta extends AsyncTask<String, String, String> {
    private HashMap<String, String> mData = null;// post data

    private static String sid = "";
    public static String base = "http://www.gaiacri.it/api.php?a=";
    public JSONObject risposta = null;
    public JSONObject utente   = null;
    
    /**
     * constructor
     */
    public Richiesta(HashMap<String, String> data) {
        mData = data;
    }
    
    
    public String metodo() { return "welcome"; }


	
    /**
     * background
     */
    @Override
    protected String doInBackground(String... params) {
        byte[] result = null;
        String str = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(base + metodo() );// in this case, params[0] is URL
        try {

        	
            // set up post data
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("sid", getSid()));
            Iterator<String> it = mData.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
        	Log.e("Gaia", "probabilmente non c'è internet");
        }
        try {
			risposta = new JSONObject(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("Gaia", "Errore di comunicazione col server");
		}
        try {
			Log.e("Gaia", risposta.getJSONObject("session").getString("id"));
	        setSid(risposta.getJSONObject("session").getString("id"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.i("Gaia", str);
        try {
			utente	 = risposta.getJSONObject("session").optJSONObject("user");
			risposta = risposta.getJSONObject("response");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //onPostExecute(str);
        return str;
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
		Richiesta.sid = sid;
	}
    
}
