package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Posta;
import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostaIngresso extends Fragment{

	private ArrayList<Posta> posta;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("direzione", "ingresso");
		data.put("pagina", "1");
		data.put("perPagina", "20");
		RichiestaNotifiche richiesta=new RichiestaNotifiche(data);
		richiesta.execute();
        return inflater.inflate(R.layout.activity_rubrica_delegati, container, false);
    }
	
	class RichiestaNotifiche extends Richiesta {
		public RichiestaNotifiche(HashMap<String, String> data) {
			super(data,PostaIngresso.this.getActivity().getApplicationContext());
		}

		public String metodo() { return "posta_cerca"; }

		@SuppressLint("SetJavaScriptEnabled")
		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,PostaIngresso.this.getActivity(),risposta)==0){
				Log.d("Json",risposta.toString());
				
				if(posta==null)
					posta= new ArrayList<Posta>();
				
				try {
				//	prin
					//String TAG="Risposta: ";
					//att_title=risposta.getString("nome");
					//att_luogo=risposta.getString("luogo");
					//int info_totale=risposta.getInt("totale");
					JSONArray res=risposta.getJSONArray("risultati");
					
					//recupera anche mittente da mostrare e salvare
					for(int i=0;i<res.length();i++){
						JSONObject obj=res.getJSONObject(i);
						posta.add(new Posta(obj.getString("oggetto"),obj.getString("corpo")));//Log.d("ciao", );
					}
					/** 
					 * creare interfaccia grafica per la posta e il fatto che dopo essere stata scaricata venga anche mostrata
					 */
					//mostrare posta a video
					//oggetto e body
					//aggirnalist();
					
					//String att_referente=risposta.getString("referente");
					//String att_referentenum=risposta.getString("referentenum");
					//String att_referenteemail=risposta.getString("referenteemail");

				} catch (JSONException e) {
					Log.e("ERROR" ,e.getMessage());
					//e.printStackTrace();
				}
				//da gestire la risposta
				//in base a come viene ritornata
			}

		}
	}	
	
	
	
	
	
	}
