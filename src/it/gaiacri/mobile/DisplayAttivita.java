package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Turno;

import java.util.ArrayList;
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
	private static double lat=0,lon=0;
	private static String att_luogo,att_title;
	private static ArrayList<Turno> turni;



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
		}else{

			((TextView) findViewById(R.id.NomeAttivita)).setText(att_title);
			((TextView) findViewById(R.id.LuogoAttivita)).setText(att_luogo);
			aggiornalist();
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
		getMenuInflater().inflate(R.menu.activity_display_attivita, menu);
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
		default:
			return super.onOptionsItemSelected(item);
		}
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
						Turno t=new Turno("desc ", tur_id, tur_start, tur_end, "",
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

				data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
			}


			String[] from={"turno_title","turno_data","turno_iscritti"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.textViewNome,R.id.textViewData,R.id.textViewIscritti};//agli id delle view

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
					((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.parseColor("#000000"));
					((TextView)row.findViewById(R.id.textViewData)).setTextColor(Color.parseColor("#000000"));
					((TextView)row.findViewById(R.id.textViewIscritti)).setTextColor(Color.parseColor("#000000"));

					final int pos=position;
					Log.d("posizione",position + " ");
					//Log.d("num elementi",turni.size() + " tot");
					int num=turni.get(position).getPartecipa();
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
							iscrivi_cancella(pos);
						}
					}); 
					return row;

				}
			};

			//utilizzo dell'adapter
			DisplayAttivita.listView.setAdapter(adapter);
			
			
			/*for(int i=0;i<turni.size();i++){
				int wantedPosition = i; // Whatever position you're looking for
				int firstPosition = DisplayAttivita.listView.getFirstVisiblePosition() - DisplayAttivita.listView.getHeaderViewsCount(); // This is the same as child #0
				int wantedChild = wantedPosition - firstPosition;
				// Say, first visible position is 8, you want position 10, wantedChild will now be 2
				// So that means your view is child #2 in the ViewGroup:
				Log.d("ciao", DisplayAttivita.listView.getChildCount()+" ");
				if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
					//Log.w(TAG, "Unable to get view for desired position, because it's not being displayed on screen.");
					return;
				}
				// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
				View row = DisplayAttivita.listView.getChildAt(wantedChild);
				final int pos=i;
				Log.d("posizione",i + " ");
				//Log.d("num elementi",turni.size() + " tot");
				int num=turni.get(i).getPartecipa();
				switch(num){
				case 0://caso in cui l'utente si puo iscrivere
					((Button)row.findViewById(R.id.buttonPartecipa)).setText(R.string.turni_partecipa);
					break;
				case 1://caso in cui l'utente risulta gia essere iscritto
					((Button)row.findViewById(R.id.buttonPartecipa)).setText(R.string.turni_cancella);
					break;
				case 2://caso in cui l'utente non si possa iscrivere
					((Button)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
				}
				((Button)row.findViewById(R.id.buttonPartecipa)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						iscrivi_cancella(pos);
					}
				}); 
			}*/
			//listView.invalidate();

			
		}else{
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_turno, R.id.textViewNome,new String[]{"Caricamento.."});
			DisplayAttivita.listView.setAdapter(arrayAdapter);
		}
	}

	public void iscrivi_cancella(int posizione){
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaIscrizione asd = new RichiestaIscrizione(data);
		asd.execute();
	}

	class RichiestaIscrizione extends Richiesta {
		public RichiestaIscrizione(HashMap<String, String> data) {
			super(data,DisplayAttivita.this.context);
		}
		public String metodo() { return "iscrivi"; }
		protected void onPostExecute(String ris) {

			if(ris.equals("Errore Internet"))
				Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
			else{
				//TODO elabora risposta
				//TODO aggiorna tabella turni della view

			}

		}
	}



}
