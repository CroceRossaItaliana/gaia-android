package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Partecipazioni;
import it.gaiacri.mobile.Object.Turno;
import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PartecipazioniAttivita extends Fragment{

	private ArrayList<Partecipazioni> partecipazioni;
	private static ListView listView ;
	private Context context;
	private boolean passati;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v=inflater.inflate(R.layout.activity_display_miei_turni, container, false);
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaPartecipazioni richiesta=new RichiestaPartecipazioni(data);
		richiesta.execute();
		listView = (ListView)v.findViewById(R.id.listTurni);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				TextView tv = (TextView) arg1.findViewById(R.id.text_id);
				Intent i= new Intent(context,DisplayAttivita.class);
				i.putExtra("id", tv.getText().toString());
				startActivityForResult(i, 0);

			}
		});
		setHasOptionsMenu(true);
		context= this.getActivity();
		return v;
	}	

	@Override
	public void onCreateOptionsMenu(
			Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_miei_turni, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.turni_passati:
			if (item.isChecked()){
				item.setChecked(false);
				passati=false;
			}else{ 
				item.setChecked(true);
				passati=true;
			}
			aggiornalist();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class RichiestaPartecipazioni extends Richiesta {
		public RichiestaPartecipazioni(HashMap<String, String> data) {
			super(data,PartecipazioniAttivita.this.getActivity().getApplicationContext());
		}

		public String metodo() { return "partecipazioni"; }

		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,PartecipazioniAttivita.this.getActivity(),risposta)==0){
				Log.d("Json",risposta.toString());

				if(partecipazioni==null)
					partecipazioni= new ArrayList<Partecipazioni>();

				try {
					//String TAG="Risposta: ";;
					JSONArray res=risposta.getJSONArray("risultati");
					//recupera anche mittente da mostrare e salvare
					for(int i=0;i<res.length();i++){
						JSONObject obj=res.getJSONObject(i);
						String part_att_id=obj.getString("attivita");
						String part_att_name=obj.getString("att_name");
						String part_id=obj.getString("id");
						JSONObject part_stato=obj.optJSONObject("stato");
						String part_stato_id=part_stato.optString("id");
						String part_stato_value=part_stato.optString("nome");
						JSONObject part_turno=obj.optJSONObject("turno");
						partecipazioni.add(new Partecipazioni(part_att_id,part_att_name,part_id,part_stato_id,part_stato_value,Turno.create(part_turno)));					
					}
					aggiornalist();
				} catch (JSONException e) {
					Log.e("ERROR" ,e.getMessage());
					//e.printStackTrace();
				}
				//da gestire la risposta
				//in base a come viene ritornata
			}

		}
	}	

	private void aggiornalist() {

		if(partecipazioni!=null){
			//Questa è la lista che rappresenta la sorgente dei dati della listview
			//ogni elemento è una mappa(chiave->valore)
			ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

			HashMap<String,Object> ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
			Partecipazioni pos=null;
			for(int i=partecipazioni.size()-1;i>=0;i--){
				ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
				pos=partecipazioni.get(i);
				if(passati||pos.getTurno().isFuturo()){
					ServiceMap.put("miei_attivita", pos.getAttivita_name());
					ServiceMap.put("miei_turno",pos.getTurno().getDesc());
					ServiceMap.put("miei_turno_start",pos.getTurno().getStart());
					ServiceMap.put("miei_id",pos.getAttivita_id());
					ServiceMap.put("miei_stato",pos.getStato_value());
					data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
				}
			}
			String[] from={"miei_attivita","miei_turno","miei_turno_start","miei_id","miei_stato"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.text_attivita,R.id.text_turno,R.id.text_start,R.id.text_id,R.id.text_stato};//agli id delle view

			//costruzione dell adapter
			SimpleAdapter adapter=new SimpleAdapter(
					context,
					data,//sorgente dati
					R.layout.riga_miei_turni, //layout contenente gli id di "to"
					from,
					to);
			//utilizzo dell'adapter
			listView.setAdapter(adapter);
		}else{
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_miei_turni, R.id.text_attivita,new String[]{"Caricamento.."});
			listView.setAdapter(arrayAdapter);
		}
	}

}
