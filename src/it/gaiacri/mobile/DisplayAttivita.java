package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Turno;
import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DisplayAttivita extends Activity {

	public String sid = "";

	public TextView nome;
	public TextView comitato;

	public String id;

	private static ListView listView ;
	private Context context;
	private double lat=0,lon=0;
	private String att_luogo,att_title;
	private ArrayList<Turno> turni;
	private boolean passati;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_attivita);
		passati=false;



		id= (String) this.getIntent().getExtras().get("id");
		nome = (TextView) findViewById(R.id.textView1);
		comitato = (TextView) findViewById(R.id.textView2);

		context=this.getApplicationContext();
		listView = (ListView)findViewById(R.id.listElenco);

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", id);
		RichiestaDettagli richiesta=new RichiestaDettagli(data);
		richiesta.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_attivita, menu);
		menu.findItem(R.id.turni_passati).setChecked(passati);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.turni_maps:
			if(lat!=0 && lon!=0){
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", lat, lon);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(intent);
			}
			return true;
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

	class RichiestaDettagli extends Richiesta {
		public RichiestaDettagli(HashMap<String, String> data) {
			super(data,DisplayAttivita.this.context);
		}

		public String metodo() { return "attivita_dettagli"; }

		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,DisplayAttivita.this,risposta)==0){
				try {
					//String TAG="Risposta: ";
					att_title=risposta.getString("nome");
					att_luogo=risposta.getString("luogo");
					//Log.d(TAG+"nome",att_title);
					//Log.d(TAG+"luogo",risposta.getString("luogo"));
					//Log.d(TAG+"luogo",risposta.getString("luogo"));
					((TextView) findViewById(R.id.NomeAttivita)).setText(att_title);
					((TextView) findViewById(R.id.LuogoAttivita)).setText(att_luogo);
					Object obj=risposta.get("coordinate");
					//Log.d(TAG+"coordinate", obj+"");
					String [] a=obj.toString().split("\"");
					lat=Double.parseDouble(a[1]);
					lon=Double.parseDouble(a[3]);

					turni=new ArrayList<Turno>();
					JSONArray js=risposta.getJSONArray("turni");
					for(int i=0;i<js.length();i++){
						risposta=js.getJSONObject(i);
						//manca descrizione e url
						//descrizione mi interessa
						String tur_id=risposta.getString("id");
						String tur_titolo=risposta.getString("nome");
						String tur_start=(risposta.getJSONObject("inizio")).getString("date");
						String tur_end=(risposta.getJSONObject("fine")).getString("date");
						//TODO bisogna inserire la politica per la scelta del colore
						String tur_color="#3135B0";//risposta.getString("color");
						boolean tur_pieno=risposta.getBoolean("pieno");
						boolean tur_futuro=risposta.getBoolean("futuro");
						boolean tur_scoperto=risposta.getBoolean("scoperto");
						boolean tur_puoRichiedere=risposta.getBoolean("puoRichiedere");
						boolean tur_partecipa=risposta.getBoolean("partecipa");
						String tur_partecipazione=risposta.getString("partecipazione");
						JSONObject tur_durata=risposta.getJSONObject("durata");
						int tur_y=tur_durata.getInt("y");
						int tur_m=tur_durata.getInt("m");
						int tur_d=tur_durata.getInt("d");
						int tur_h=tur_durata.getInt("h");
						int tur_i=tur_durata.getInt("i");
						//Log.d(TAG+"pieno",risposta.getBoolean("pieno")+"");
						//Log.d(TAG+"futuro",risposta.getBoolean("futuro")+"");
						//Log.d(TAG+"scoperto",risposta.getBoolean("scoperto")+"");
						//Log.d(TAG+"puoRichiedere",risposta.getBoolean("puoRichiedere")+"");
						//Log.d(TAG+"partecipa",risposta.getBoolean("partecipa")+"");
						//Log.d(TAG+"partecipazione",risposta.getString("partecipazione")+"");
						Turno t=new Turno(tur_titolo, tur_id, tur_start, tur_end, "",
								tur_color, tur_pieno, tur_futuro,tur_scoperto,
								tur_puoRichiedere, tur_partecipa, tur_partecipazione,
								tur_y,tur_m,tur_d,tur_h,tur_i);		
						turni.add(t);
					}
				} catch (JSONException e) {
					Log.e("ERROR" ,e.getMessage());
					//e.printStackTrace();
				}
				//da gestire la risposta
				//in base a come viene ritornata
				aggiornalist();
			}

		}
	}	


	private void aggiornalist() {

		if(turni!=null){
			//Questa è la lista che rappresenta la sorgente dei dati della listview
			//ogni elemento è una mappa(chiave->valore)
			ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

			HashMap<String,Object> ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
			Turno tur=null;
			for(int i=0;i<turni.size();i++){
				ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
				tur=turni.get(i);

				ServiceMap.put("turno_title", tur.getDesc());
				String date=tur.getStart()+" (" +tur.getDurata()+")";
				ServiceMap.put("turno_data",date);
				//TODO da aggiungere estrazione partecipanti
				ServiceMap.put("turno_iscritti","");
				ServiceMap.put("turno_id", tur.getId());
				if(passati==true){
					data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
				}else{
					//tur.getStart()
					Calendar c=Calendar.getInstance();
					String date2=c.get(Calendar.YEAR)+"-"+Number(c.get(Calendar.MONTH)+ 1)+"-"+c.get(Calendar.DAY_OF_MONTH);
					Log.d("Data", date2);
					Log.d("Data","" + date2.compareTo(tur.getStart()));

					if(date2.compareTo(tur.getStart())<=0){
						data.add(ServiceMap);						
					}
				}
			}


			String[] from={"turno_title","turno_data","turno_iscritti","turno_id"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.textViewNome,R.id.textViewData,R.id.textViewIscritti,R.id.textViewId};//agli id delle view

			//costruzione dell adapter
			SimpleAdapter adapter=new SimpleAdapter(
					context,
					data,//sorgente dati
					R.layout.riga_turno, //layout contenente gli id di "to"
					from,
					to){
				//con questo metodo riesco a inserire il colore nel testo delle attivita
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (convertView==null){
						LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView=inflater.inflate(R.layout.riga_turno, null);
					}
					View row = super.getView(position, convertView, parent);
					/*if(turni!=null && turni.size()!=0){
						String col=turni.get(position).getColor();
						Log.d("Colore Elab:", col);
						((TextView)row.findViewById(R.id.textViewList)).setTextColor(Color.parseColor(col));
					}*/
					String id=(String) ((TextView)row.findViewById(R.id.textViewId)).getText();
					final Turno t=getTurno(id,position);
					((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.parseColor(t.getColor()));
					((TextView)row.findViewById(R.id.textViewData)).setTextColor(Color.parseColor("#000000"));
					((TextView)row.findViewById(R.id.textViewIscritti)).setTextColor(Color.parseColor("#000000"));

					//final int pos=position;
					Log.d("posizione",position + " ");
					//Log.d("num elementi",turni.size() + " tot");
					int num=t.getPartecipa();
					Log.d("num",num +" ");
					switch(num){
					case 0://caso in cui l'utente si puo iscrivere
						((Button)row.findViewById(R.id.buttonPartecipa)).setText(R.string.turni_partecipa);
						((Button)row.findViewById(R.id.buttonPartecipa)).setEnabled(true);
						break;
					case 1://caso in cui l'utente risulta gia essere iscritto
						((Button)row.findViewById(R.id.buttonPartecipa)).setText(R.string.turni_cancella);
						((Button)row.findViewById(R.id.buttonPartecipa)).setEnabled(true);
						break;
					case 2://caso in cui il turno e pieno
						((Button)row.findViewById(R.id.buttonPartecipa)).setText(R.string.turni_pieno);
						((Button)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
						break;
					case 3:
						((Button)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
						break;
					}
					((Button)row.findViewById(R.id.buttonPartecipa)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							iscrivi_cancella(t.getId());
						}
					}); 
					return row;

				}
			};

			//utilizzo dell'adapter
			listView.setAdapter(adapter);
		}else{
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_turno, R.id.textViewNome,new String[]{"Caricamento.."});
			listView.setAdapter(arrayAdapter);
		}
	}
	
	private Turno getTurno(String id,int position){
		for(int i=0;i<turni.size();i++){
			if(id.equals(turni.get(i).getId())){
				return turni.get(i);
			}
		}
		return turni.get(position);
	}

	private String Number(int num){
		if(num<10)
			return "0"+num;
		else
			return ""+num;
	}

	public void iscrivi_cancella(String id){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", id);
		RichiestaIscrizione asd = new RichiestaIscrizione(data);
		asd.execute();
	}

	class RichiestaIscrizione extends Richiesta {
		public RichiestaIscrizione(HashMap<String, String> data) {
			super(data,DisplayAttivita.this.context);
		}
		public String metodo() { return "turno_partecipa"; }
		protected void onPostExecute(String ris) {

			if(ErrorJson.Controllo(ris,DisplayAttivita.this,risposta)==0){
				//TODO elabora risposta
				//TODO aggiorna tabella turni della view

			}

		}
	}



}
