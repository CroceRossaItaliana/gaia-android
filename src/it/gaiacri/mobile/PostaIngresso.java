package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Posta;
import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class PostaIngresso extends Fragment{

	private ArrayList<Posta> posta;
	private ArrayList<String> mitt;
	private static ListView listView;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v=inflater.inflate(R.layout.activity_display_posta, container, false);
		richiestaNotifiche();
		listView = (ListView)v.findViewById(R.id.listPosta);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				AlertDialog.Builder alert = new AlertDialog.Builder(PostaIngresso.this.getActivity()); 
				String html=posta.get(pos).getBody();
				WebView wv = new WebView(PostaIngresso.this.getActivity());
				String mime = "text/html";
				String encoding = "utf-8";
				wv.loadDataWithBaseURL(null, "<style type='text/css'>img {max-width: 100%;height:initial;}</style>"+html, mime, encoding, null);
				wv.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						view.loadUrl(url);

						return true;
					}
				});
				alert.setView(wv);
				alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
		context= this.getActivity();
		return v;
	}

	class RichiestaNotifiche extends Richiesta {
		public RichiestaNotifiche(HashMap<String, String> data) {
			super(data,PostaIngresso.this.getActivity().getApplicationContext());
		}

		public String metodo() { return "posta_cerca"; }

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
					mitt=new ArrayList<String>(); 

					//recupera anche mittente da mostrare e salvare
					for(int i=0;i<res.length();i++){
						JSONObject obj=res.getJSONObject(i);
						String posta_id=obj.getString("id");
						String posta_corpo=obj.getString("corpo");
						JSONObject mittente=obj.optJSONObject("mittente");
						String posta_mittente="";
						if(!(mittente == null)){
							posta_mittente=obj.getJSONObject("mittente").getString("id");
							if(!mitt.contains(posta_mittente))
								mitt.add(posta_mittente);
						}
						String posta_oggetto=obj.getString("oggetto");						
						posta.add(new Posta(posta_id,posta_oggetto,posta_corpo,posta_mittente));//Log.d("ciao", );
					}


					if(mitt.size()== 0){
						fixMittente();
						aggiornalist();
					}else{
						richiestaMittenti();
					}


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
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(PostaIngresso.this.getActivity());
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaNotifiche();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}	
	
	public void fixMittente(){
		for(int i=0;i<posta.size();i++){
			if(posta.get(i).getMittente().equals(""))
				posta.get(i).setNomeMittente(getString(R.string.posta_mittente_gaia));
		}
	}

	class RichiestaMittenti extends RichiestaMultipla {
		public RichiestaMittenti(HashMap<String, HashMap<String, String>> data) {
			super(data,PostaIngresso.this.getActivity().getApplicationContext());
		}

		public String metodo() { return "multi"; }

		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,PostaIngresso.this.getActivity(),risposta)==0){
				Log.d("Json",risposta.toString());
				try {

					JSONArray risultati = risposta.getJSONArray("risultato");
					for(int i=0;i<risultati.length();i++){
						JSONObject temp=risultati.getJSONObject(i);
						String id=temp.getJSONObject("risposta").optString("id");
						String nome=temp.getJSONObject("risposta").optString("nomeCompleto");
						for(int k=0;k<posta.size();k++){
							if(posta.get(k).getMittente().equals(id)){
								posta.get(k).setNomeMittente(nome);
							}
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				fixMittente();
				//da gestire la risposta
				//in base a come viene ritornata
				aggiornalist();		
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(PostaIngresso.this.getActivity());
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaMittenti();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}	

	private void aggiornalist() {

		if(posta!=null){
			//Questa è la lista che rappresenta la sorgente dei dati della listview
			//ogni elemento è una mappa(chiave->valore)
			ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

			HashMap<String,Object> ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
			Posta pos=null;
			for(int i=0;i<posta.size();i++){
				ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
				pos=posta.get(i);

				ServiceMap.put("posta_object", pos.getOggetto());
				ServiceMap.put("posta_mittente",pos.getNomeMittente());
				ServiceMap.put("posta_id",pos.getId());
				data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
			}
			String[] from={"posta_object","posta_mittente","posta_id"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.posta_oggetto,R.id.posta_mittente,R.id.posta_id};//agli id delle view

			//costruzione dell adapter
			SimpleAdapter adapter=new SimpleAdapter(
					context,
					data,//sorgente dati
					R.layout.riga_posta, //layout contenente gli id di "to"
					from,
					to);
			//utilizzo dell'adapter
			listView.setAdapter(adapter);
		}else{
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_posta, R.id.posta_oggetto,new String[]{"Caricamento.."});
			listView.setAdapter(arrayAdapter);
		}
	}

	public void richiestaMittenti(){
		HashMap<String,HashMap<String,String>> date=new HashMap<String,HashMap<String,String>>();

		for(int i=0;i<mitt.size();i++){					
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("metodo", "utente");
			data.put("id",mitt.get(i));
			date.put(""+i, data);	
		}
		RichiestaMittenti richiesta=new RichiestaMittenti(date);
		richiesta.execute();
		
	}
	
	public void richiestaNotifiche(){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("direzione", "ingresso");
		data.put("pagina", "1");
		data.put("perPagina", "5");
		RichiestaNotifiche richiesta=new RichiestaNotifiche(data);
		richiesta.execute();
	}
	
}
