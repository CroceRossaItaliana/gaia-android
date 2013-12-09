package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Attivita;
import it.gaiacri.mobile.Object.Turno;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayAttivita extends Activity {

	public String sid = "";
	public DisplayAttivita attivita;

	public TextView nome;
	public TextView comitato;

	public String id;
	
	private static ListView listView ;

	private RichiestaDettagli richiesta;
	private Context context;
	private double lat,lon;
	private ArrayList<Turno> turni;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_attivita);
		attivita = this;

		id= (String) this.getIntent().getExtras().get("id");
		nome = (TextView) findViewById(R.id.textView1);
		comitato = (TextView) findViewById(R.id.textView2);

		context=this.getApplicationContext();
		listView = (ListView)findViewById(R.id.listElenco);
		//sid = getIntent().getExtras().getString("sid");

		richiesta=(RichiestaDettagli)getLastNonConfigurationInstance();
		if(richiesta==null){
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("id", id);
			richiesta=new RichiestaDettagli(data);
			richiesta.execute();
		}

		//Log.i("sid", sid);
		Log.i("id",id);



	}

	@Override
	public Object onRetainNonConfigurationInstance() {	    
		return(richiesta);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu_principale, menu);
		return true;
	}

	class RichiestaDettagli extends Richiesta {
		public RichiestaDettagli(HashMap<String, String> data) {
			super(data,DisplayAttivita.this.context);
		}

		public String metodo() { return "dettagliAttivita"; }

		protected void onPostExecute(String ris) {
			if(ris.equals("Errore Internet"))
				Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
			else{
				try {
					String TAG="Risposta: ";
					Log.d(TAG+"nome",risposta.getString("nome"));
					Log.d(TAG+"luogo",risposta.getString("luogo"));
					((TextView) findViewById(R.id.NomeAttivita)).setText(risposta.getString("nome"));
					((TextView) findViewById(R.id.LuogoAttivita)).setText(risposta.getString("luogo"));
					Object obj=risposta.get("coordinate");
					Log.d(TAG+"coordinate", obj+"");
					String [] a=obj.toString().split("\"");
					lat=Double.parseDouble(a[1]);
					lon=Double.parseDouble(a[3]);
					TextView tv=(TextView) findViewById(R.id.CordinateAttivita);
					tv.setText(lat + " " +lon);
					tv.setOnClickListener(new View.OnClickListener() {
					    public void onClick(View v) {
					    	
					    	String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", lat, lon);
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
							startActivity(intent);
					    }
					});
					
					turni=new ArrayList<Turno>();
					JSONArray js=risposta.getJSONArray("turni");
					for(int i=0;i<js.length();i++){
						risposta=js.getJSONObject(i);
						//manca descrizione e url
						//descrizione mi interessa
						String tur_id=risposta.getString("id");
						String tur_start=(risposta.getJSONObject("inizio")).getString("date");
						String tur_end=(risposta.getJSONObject("fine")).getString("date");
						//TODO bisogna inserire la politica per la scelta del colore
						//String tur_color=risposta.getString("color");
						boolean tur_pieno=risposta.getBoolean("pieno");
						boolean tur_futuro=risposta.getBoolean("futuro");
						boolean tur_scoperto=risposta.getBoolean("scoperto");
						boolean tur_puoRichiedere=risposta.getBoolean("puoRichiedere");
						boolean tur_partecipa=risposta.getBoolean("partecipa");
						String tur_partecipazione=risposta.getString("partecipazione");
						//Log.d(TAG+"pieno",risposta.getBoolean("pieno")+"");
						//Log.d(TAG+"futuro",risposta.getBoolean("futuro")+"");
						//Log.d(TAG+"scoperto",risposta.getBoolean("scoperto")+"");
						//Log.d(TAG+"puoRichiedere",risposta.getBoolean("puoRichiedere")+"");
						//Log.d(TAG+"partecipa",risposta.getBoolean("partecipa")+"");
						//Log.d(TAG+"partecipazione",risposta.getString("partecipazione")+"");
						Turno t=new Turno("desc ", tur_id, tur_start, tur_end, "",
								"#3135B0", tur_pieno, tur_futuro,tur_scoperto,
								tur_puoRichiedere, tur_partecipa, tur_partecipazione);		
						turni.add(t);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
				ServiceMap.put("attivita_title", tur.getId() + tur.getDesc());
				ServiceMap.put("attivita_url", tur.getStart() +" - " +tur.getEnd());
				data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
			}


			String[] from={"attivita_title","attivita_url"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.textViewList,R.id.textViewListUrl};//agli id delle view

			//costruzione dell adapter
			SimpleAdapter adapter=new SimpleAdapter(
					getApplicationContext(),
					data,//sorgente dati
					R.layout.riga_attivita, //layout contenente gli id di "to"
					from,
					to){
				//con questo metodo riesco a inserire il colore nel testo delle attivita
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (convertView==null){
						LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView=inflater.inflate(R.layout.riga_attivita, null);
					}
					View row = super.getView(position, convertView, parent);
					if(turni!=null && turni.size()!=0){
						String col=turni.get(position).getColor();
						Log.d("Colore Elab:", col);
						((TextView)row.findViewById(R.id.textViewList)).setTextColor(Color.parseColor(col));
					}
					return row;

				}
			};

			//utilizzo dell'adapter
			DisplayAttivita.listView.setAdapter(adapter);
			//ElencoAttivita.tv.setText(textView);
		}else{
			//ElencoAttivita.tv.setText(textView);
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_attivita, R.id.textViewList,new String[]{"Caricamento.."});
			DisplayAttivita.listView.setAdapter(arrayAdapter);
		}
	}

	
	
	
	
	
}
