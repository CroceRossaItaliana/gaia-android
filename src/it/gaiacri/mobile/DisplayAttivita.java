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

import com.beardedhen.androidbootstrap.BootstrapButton;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DisplayAttivita extends ActionBarActivity {

	public String sid = "";

	public TextView nome;
	public TextView comitato;

	public String id_dettagli;
	public String id_turno;
	public String id_partecipazione;

	private static ListView listView ;
	private Context context;
	private double lat=0,lon=0;
	private String att_luogo,att_title;
	private ArrayList<Turno> turni;
	private boolean passati;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_attivita);
		passati=false;

		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		id_dettagli= (String) this.getIntent().getExtras().get("id");
		nome = (TextView) findViewById(R.id.textView1);
		comitato = (TextView) findViewById(R.id.textView2);

		context=this.getApplicationContext();
		listView = (ListView)findViewById(R.id.listElenco);

		richiestaDettagli();
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
		case android.R.id.home:
			super.onBackPressed();
	        return true;
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

		@SuppressLint("SetJavaScriptEnabled")
		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,DisplayAttivita.this,risposta)==0){
				try {
					//String TAG="Risposta: ";
					att_title=risposta.getString("nome");
					att_luogo=risposta.getString("luogo");
					String att_info=risposta.getString("descrizione");
					JSONObject ref=risposta.getJSONObject("referente");
					String att_referente=ref.getString("nome");
					String att_referentenum=ref.getString("numero");
					String att_referenteemail=ref.getString("email");

					String html = "<html><body><b>Referente:</b> "+att_referente+"<br><b>Num:</b> <a href=\"tel:"+att_referentenum+"\">"+att_referentenum+"</a><br><b>Email:</b> <a href=\"mailto:"+att_referenteemail+"\">"+att_referenteemail+"</a><br>"+att_info+"</body></html>";
					//String html = "<html><body><b>Referente:</b> Mario Rossi<br>Telefono: <a href=\"tel:3474975206\">3414477852</a><br>Email: <A HREF=\"mailto:ciopper90@gmail.com\">email@example.it</a><br>desc</body></html>";
					String mime = "text/html";
					String encoding = "utf-8";
					//Log.d(TAG+"nome",att_title);
					//Log.d(TAG+"luogo",risposta.getString("luogo"));
					//Log.d(TAG+"luogo",risposta.getString("luogo"));
					((TextView) findViewById(R.id.NomeAttivita)).setText(att_title);
					((TextView) findViewById(R.id.LuogoAttivita)).setText(att_luogo);
					((TextView)findViewById(R.id.LuogoAttivita)).setTextColor(Color.parseColor(getString(R.color.gaia_maps)));
					((WebView) findViewById(R.id.InfoAttivita)).getSettings().setJavaScriptEnabled(true);
					((WebView) findViewById(R.id.InfoAttivita)).loadDataWithBaseURL(null, "<style type='text/css'>img {max-width: 100%;height:initial;}</style>"+html, mime, encoding, null);
					((TextView) findViewById(R.id.UlterioriAttivita)).setCompoundDrawablesWithIntrinsicBounds( 0 , 0, R.drawable.ic_btn_round_more_disabled_down, 0);
					((TextView) findViewById(R.id.UlterioriAttivita)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							//va cambiata la scritta di ulteriori info :D
							if(((WebView) findViewById(R.id.InfoAttivita)).getVisibility()==View.GONE){
								((WebView) findViewById(R.id.InfoAttivita)).setVisibility(View.VISIBLE);
								((TextView) findViewById(R.id.UlterioriAttivita)).setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_btn_round_more_disabled_up, 0);
							}else{
								((WebView) findViewById(R.id.InfoAttivita)).setVisibility(View.GONE);
								((TextView) findViewById(R.id.UlterioriAttivita)).setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_btn_round_more_disabled_down, 0);
							}
						}
					});
					Object obj=risposta.get("coordinate");
					//Log.d(TAG+"coordinate", obj+"");
					String [] a=obj.toString().split("\"");
					lat=Double.parseDouble(a[1]);
					lon=Double.parseDouble(a[3]);

					turni=new ArrayList<Turno>();
					JSONArray js=risposta.getJSONArray("turni");
					for(int i=0;i<js.length();i++){
						turni.add(Turno.create(js.getJSONObject(i)));
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
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(DisplayAttivita.this);
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaDettagli();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
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
				String date=tur.getDate()+" (" +tur.getDurata()+")";
				ServiceMap.put("turno_data",date);
				//TODO da aggiungere estrazione partecipanti
				ServiceMap.put("turno_iscritti","");
				ServiceMap.put("turno_id", tur.getId());
				if(passati==true){
					data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
				}else{
					//tur.getStart()
					Calendar c=Calendar.getInstance();
					String date2=c.get(Calendar.YEAR)+"-"+Number(c.get(Calendar.MONTH)+ 1)+"-"+Number(c.get(Calendar.DAY_OF_MONTH));
					//Log.d("Data", date2);
					//Log.d("Data","" + date2.compareTo(tur.getStart()));

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
					//((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.parseColor(t.getColor()));
					((TextView)row.findViewById(R.id.textViewData)).setTextColor(Color.parseColor("#000000"));
					((TextView)row.findViewById(R.id.textViewIscritti)).setTextColor(Color.parseColor("#000000"));
					((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setTag(id);

					//Log.d("num elementi",turni.size() + " tot");
					int num=t.getPartecipa();
					//Log.d("posizione",position + " " +num);
					switch(num){
					case 0://caso in cui l'utente si puo iscrivere
						((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.parseColor(getString(R.color.turno_vuoto)));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setText(getString(R.string.turni_partecipa));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setEnabled(true);
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setBootstrapType("success");
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								String id=(String)((BootstrapButton)v.findViewById(R.id.buttonPartecipa)).getTag();
								((BootstrapButton)v.findViewById(R.id.buttonPartecipa)).setOnClickListener(null);
								iscrivi(id);
							}
						}); 
						break;
					case 1://caso in cui l'utente risulta gia essere iscritto
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								String id_tur=(String)((BootstrapButton)v.findViewById(R.id.buttonPartecipa)).getTag();
								int pos=getTurno(id_tur);
								String id_part=turni.get(pos).getPartecipazione();
								((BootstrapButton)v.findViewById(R.id.buttonPartecipa)).setOnClickListener(null);
								cancella(id_tur,id_part);
							}
						}); 
						((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.parseColor(getString(R.color.turno_pieno)));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setText(getString(R.string.turni_cancella));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setBootstrapType("danger");
						//((Button)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
						break;
					case 2://caso in cui il turno e pieno
						((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.BLACK);
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setText(getString(R.string.turni_pieno));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setBootstrapType("info");
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
						break;
					case 3:
						((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.BLACK);
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setText(getString(R.string.turni_timeout));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setBootstrapType("default");
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
						break;
					case 4:
						((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.parseColor(getString(R.color.turno_pieno)));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setText(getString(R.string.turni_concessa));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setBootstrapType("info");
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
						break;
					case 5:
						((TextView)row.findViewById(R.id.textViewNome)).setTextColor(Color.BLACK);
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setText(getString(R.string.turni_negata));
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setBootstrapType("info");
						((BootstrapButton)row.findViewById(R.id.buttonPartecipa)).setEnabled(false);
						break;												
					}
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

	private int getTurno(String id){
		for(int i=0;i<turni.size();i++){
			if(id.equals(turni.get(i).getId())){
				return i;
			}
		}
		return 0;
	}

	private String Number(int num){
		if(num<10)
			return "0"+num;
		else
			return ""+num;
	}

	public void iscrivi(String id){
		pd=new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setMessage("Iscrizione in corso");
		pd.show();
		this.id_turno=id;
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", id_turno);
		RichiestaIscrizione asd = new RichiestaIscrizione(data);
		asd.execute();
	}
	public void cancella(String id_turno,String id_partecipazione){
		pd=new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setMessage("Cancellazione in corso");
		pd.show();
		this.id_turno=id_turno;
		this.id_partecipazione=id_partecipazione;		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", id_partecipazione);
		data.put("id_tur", id_turno);
		RichiestaCancellazione asd = new RichiestaCancellazione(data);
		asd.execute();
	}

	class RichiestaIscrizione extends Richiesta {
		public RichiestaIscrizione(HashMap<String, String> data) {
			super(data,DisplayAttivita.this.context);
		}
		public String metodo() { return "turno_partecipa"; }
		protected void onPostExecute(String ris) {
			pd.dismiss();
			pd.cancel();
			if(ErrorJson.Controllo(ris,DisplayAttivita.this,risposta)==0){
				//modifica turni(con il tuo id) in base all'esito della chiamata
				//controllo se iscritto cancella altrimnti iscrivi
				try {
					String id=richiesta.getJSONObject("parametri").getString("id");
					String result=risposta.getString("ok");
					if(Boolean.parseBoolean(result)){
						String partecipazione_id =risposta.getString("id");
						turni.get(getTurno(id)).setPart(Boolean.parseBoolean(result));
						turni.get(getTurno(id)).ritirabile(true);
						turni.get(getTurno(id)).setPartecipazione(partecipazione_id);
					}
				} catch (JSONException e) {
					//Auto-generated catch block
					e.printStackTrace();
				}
				//aggiorna tabella turni della view
				aggiornalist();
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(DisplayAttivita.this);
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaIscrizione();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}
	
	class RichiestaCancellazione extends Richiesta {
		public RichiestaCancellazione(HashMap<String, String> data) {
			super(data,DisplayAttivita.this.context);
		}
		public String metodo() { return "partecipazione_ritirati"; }
		protected void onPostExecute(String ris) {
			pd.dismiss();
			pd.cancel();
			if(ErrorJson.Controllo(ris,DisplayAttivita.this,risposta)==0){
				//modifica turni(con il tuo id) in base all'esito della chiamata
				//controllo se iscritto cancella altrimnti iscrivi
				try {
					String result=risposta.getString("ok");
					if(!Boolean.parseBoolean(result))
						Crouton.makeText(DisplayAttivita.this, R.string.error_turn_confirmed, Style.ALERT ).show();
						//Toast.makeText(DisplayAttivita.this, R.string.error_turn_confirmed, Toast.LENGTH_LONG).show();
					richiestaDettagli();
				} catch (JSONException e) {
					//Auto-generated catch block
					//e.printStackTrace();
					Log.d("tag",e.getMessage());
				}
				//aggiorna tabella turni della view
				//aggiornalist();
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(DisplayAttivita.this);
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaCancellazione();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}
	
	public void richiestaDettagli(){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", id_dettagli);
		RichiestaDettagli richiesta=new RichiestaDettagli(data);
		richiesta.execute();
	}
	public void richiestaIscrizione(){
		iscrivi(id_turno);
	}
	public void richiestaCancellazione(){
		cancella(id_turno,id_partecipazione);
		
	}
}