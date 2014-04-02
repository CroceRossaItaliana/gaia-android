package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Partecipazioni;
import it.gaiacri.mobile.Object.Turno;
import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
		richiestaPartecipazione();
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
					JSONArray res=risposta.getJSONArray("risultati");
					//recupera anche mittente da mostrare e salvare
					for(int i=0;i<res.length();i++){
						JSONObject obj=res.getJSONObject(i);
						partecipazioni.add(Partecipazioni.createPartecipazioni(obj));					
					}
					aggiornalist();
				} catch (JSONException e) {
					Log.e("ERROR" ,e.getMessage());
					//e.printStackTrace();
				}
			}

		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(PartecipazioniAttivita.this.getActivity());
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaPartecipazione();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
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
					ServiceMap.put("miei_stato","  "+pos.getStato_value()+"  ");
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
					to){
				
				@SuppressWarnings("deprecation")
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (convertView==null){
						LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView=inflater.inflate(R.layout.riga_miei_turni, null);
					}
					View row = super.getView(position, convertView, parent);
					if(partecipazioni!=null && partecipazioni.size()!=0){
						TextView tv= (TextView) row.findViewById(R.id.text_stato);
						//caricare backgroud rettangolare
						GradientDrawable back= (GradientDrawable) getResources().getDrawable(R.drawable.rectanglesmussato);
						//settare il colore in base al valore assunto
						back.setColor(color(tv.getText().toString().trim()));
						//settare il backgroud
						tv.setBackgroundDrawable(back);
						//settare il testo come BOLD
						tv.setTypeface(null, Typeface.BOLD);
						//tv.setBackgroundColor(color(tv.getText().toString()));
						//oppure posso aggiungere uno sfondo quadrato alla parola concessa/in attesa/negata con il colore rispettivo usato in gaia
						//settare backgroud che bisogna rendere piu chiaro perche ora troppo acceso
						//oppure sento dagli altri che preferiscono fare
						//row.setBackgroundColor(color(tv.getText().toString()));
						//Drawable test=row.getBackground();
						//test.setAlpha(95);
						}
					return row;

				}
			};
			//utilizzo dell'adapter
			listView.setAdapter(adapter);
		}else{
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_miei_turni, R.id.text_attivita,new String[]{"Caricamento.."});
			listView.setAdapter(arrayAdapter);
		}
	}

	protected int color(String text) {
		String c="#FFFFFF";
		if(text.equals(getString(R.string.partecipa_confermata)))
			c=getString(R.color.label_success);
		if(text.equals(getString(R.string.partecipa_attesa)))
			c=getString(R.color.label_warning);
		if(text.equals(getString(R.string.partecipa_negata)))
			c=getString(R.color.label_important);		
		return Color.parseColor(c) ;
	}

	public void richiestaPartecipazione(){
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaPartecipazioni richiesta=new RichiestaPartecipazioni(data);
		richiesta.execute();
	}

}
