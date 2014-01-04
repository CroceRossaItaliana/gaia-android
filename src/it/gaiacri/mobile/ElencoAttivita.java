package it.gaiacri.mobile;


import it.gaiacri.mobile.Object.Attivita;
import it.gaiacri.mobile.Object.Turno;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ElencoAttivita extends Activity {
	private static ListView listView ;
	private Context context;
	private static Calendar c;
	private static TextView tv;
	private static ArrayList<Attivita> a;
	private static String textView;

	//barra di caricamento
	private static ProgressDialog pd;
	//ripristino barra dopo rotazione
	private static String alert;

	private static RichiestaAttivita hello1;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elenco_attivita);
		((Button) this.findViewById(R.id.button1)).setText("<");
		((Button) this.findViewById(R.id.button2)).setText(">");

		tv=(TextView) this.findViewById(R.id.attivita_data);
		listView = (ListView)findViewById(R.id.listElenco);

		context=this;

		if(textView!=null && !textView.equals(""))
			ElencoAttivita.tv.setText(textView);
		
		hello1=(RichiestaAttivita)getLastNonConfigurationInstance();	

		
		listView.setOnItemClickListener(new OnItemClickListener() {

	        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
	                long arg3) {
	             //Toast.makeText(getApplicationContext(),"hiihih" + pos,Toast.LENGTH_SHORT).show();
	             String id=a.get(pos).getId();
	             if(!id.equals("no")){
	            	 Intent i= new Intent(ElencoAttivita.this,DisplayAttivita.class);
	            	 i.putExtra("id", id);
	            	 startActivity(i);
	             }else{
	            	 Toast.makeText(context, R.string.attivita_no_turni, Toast.LENGTH_SHORT).show();
	             }
	             
	             
	        }
	    });
		
		Button prev= (Button)this.findViewById(R.id.button1);
		prev.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				a=null;
				aggiornalist();
				Loading(-7);
			}
		});      

		Button next= (Button)this.findViewById(R.id.button2);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				a=null;
				aggiornalist();
				Loading(7);
			}
		});

		if(alert==null){
			alert="";
			aggiornalist();
			Loading(Calendar.getInstance());
		}else{
			if(alert.equals("download")){
				hello1.attach(this);
				ProgressDialogShow();
			}else{
				aggiornalist();
				Loading(Calendar.getInstance());
			}
		}
	}

	@Override
	protected void onDestroy() {
		if(pd!=null){
			pd.dismiss();
			pd.cancel();
		}
		pd=null;
		super.onDestroy();
	}

	  @Override
	  public Object onRetainNonConfigurationInstance() {
	    hello1.detach();
	    
	    return(hello1);
	  }

	private void Loading(Calendar instance) {
		if(c==null){
			c=instance;
			Loading(0);
		}else
			aggiornalist();
	}

	private void Loading(int offset){

		ProgressDialogShow();

		HashMap<String, String> data1 = new HashMap<String, String>();
		c.add(Calendar.DATE, (-c.get(Calendar.DAY_OF_WEEK))+2);
		c.add(Calendar.DATE, offset);

		String date1=c.get(Calendar.YEAR)+"-"+(Number(c.get(Calendar.MONTH)+1))+"-"+Number(c.get(Calendar.DAY_OF_MONTH))+"T"+"00:00:00.000Z";
		Log.d("Date inizio:",date1);

		c.add(Calendar.DATE, 6);
		String date2=c.get(Calendar.YEAR)+"-"+(Number(c.get(Calendar.MONTH)+1))+"-"+Number(c.get(Calendar.DAY_OF_MONTH))+"T"+"23:59:00.000Z";
		Log.d("Date fine:",date2);
		c.add(Calendar.DATE, -6);
		data1.put("inizio", date1);
		data1.put("fine", date2);
		hello1 = new RichiestaAttivita(this,data1);
		hello1.execute();
		ElencoAttivita.tv.setText(date1.substring(0,10) +"\n" +date2.substring(0,10));
		textView=date1.substring(0,10) +"\n" +date2.substring(0,10);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_elenco_attivita, menu);
		return true;
	}
	private String Number(int num){
		if(num<10)
			return "0"+num;
		else
			return ""+num;
	}

	public class RichiestaAttivita extends Richiesta {
		ElencoAttivita activity;
		
		public String metodo() { return "attivita"; }

		public RichiestaAttivita(ElencoAttivita activity,HashMap<String, String> data) {
			super(data,ElencoAttivita.this.context);
			attach(activity);
			// TODO Auto-generated constructor stub
		}

	    void detach() {
	        activity=null;
	      }
	      
	      void attach(ElencoAttivita activity) {
	        this.activity=activity;
	      }
	      
		@Override
		protected void onPostExecute(String str) {
			//TODO
			Log.d("Log","Ciao");
			try{
				JSONObject js=null;
				a=new ArrayList<Attivita>();
				for(int i=0;i<attivita.length();i++){
					js=(JSONObject)attivita.get(i);
					String att_title=js.getString("title");
					String tur_desc=att_title.substring(att_title.indexOf(',')+1);
					att_title=att_title.substring(0,att_title.indexOf(','));
					String att_id=js.getString("attivita");
					String tur_url=js.getString("url");
					String tur_id=js.getString("id");
					String tur_start=js.getString("start");
					String tur_end=js.getString("end");
					String tur_color=js.getString("color");
					//se a contiene l'attivita allora aggiunto un turno
					//altrimenti creo una nuova attivita e gli aggiunto il turno
					int indice=contiene(att_id);
					if(indice==-1){
						a.add(new Attivita(att_title,att_id));
						indice=a.size()-1;
					}
					a.get(indice).addTurno(new Turno(tur_desc,tur_id,tur_start,tur_end,tur_url,tur_color));
				}
				aggiornalist();
				ElencoAttivita.pd.dismiss();
				ElencoAttivita.alert="";
			}catch(Exception e){}

		}

	}

	public int contiene(String id){
		for(int i=0;i<a.size();i++){
			if(a.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	@Override
	public void onBackPressed()
	{
		//nel caso in cui l'utente prema il tasto back viene cancellata la data
		c=null;
		a=null;
		super.onBackPressed();  // optional depending on your needs
	}

	private void aggiornalist() {

		if(a!=null){
			//Questa è la lista che rappresenta la sorgente dei dati della listview
			//ogni elemento è una mappa(chiave->valore)
			ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

			HashMap<String,Object> ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
			Attivita att=null;
			for(int i=0;i<a.size();i++){
				ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
				att=a.get(i);
				ServiceMap.put("attivita_title", att.getTitle());
				ServiceMap.put("attivita_url", this.getString(R.string.attivita_comitato)+"");
				data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
			}


			String[] from={"attivita_title","attivita_url"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.textViewList,R.id.textViewListUrl};//agli id delle view

			//costruzione dell adapter
			SimpleAdapter adapter=new SimpleAdapter(
					context,
					data,//sorgente dati
					R.layout.riga_attivita, //layout contenente gli id di "to"
					from,
					to);/*{
				//con questo metodo riesco a inserire il colore nel testo delle attivita
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (convertView==null){
						LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView=inflater.inflate(R.layout.riga_attivita, null);
					}
					View row = super.getView(position, convertView, parent);
					if(a!=null && a.size()!=0){
						String col=a.get(position).getTurni().get(0).getColor();
						Log.d("Colore Elab:", col);
						((TextView)row.findViewById(R.id.textViewList)).setTextColor(Color.BLACK);
					}
					return row;

				}
			};*/

			//utilizzo dell'adapter
			ElencoAttivita.listView.setAdapter(adapter);
			ElencoAttivita.tv.setText(textView);
		}else{
			ElencoAttivita.tv.setText(textView);
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_attivita, R.id.textViewList,new String[]{"Caricamento.."});
			ElencoAttivita.listView.setAdapter(arrayAdapter);
		}
	}

	private void ProgressDialogShow(){
		pd=new ProgressDialog(this);
		pd.setMessage(getString(R.string.attivita_download));
		pd.setCancelable(false);
		pd.show();
		alert="download";
	}
}
