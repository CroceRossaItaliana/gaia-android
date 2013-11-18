package it.gaiacri.mobile;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ElencoAttivita extends Activity {
	private ListView listView ;
	private Context context;
	private static Calendar c;
	private TextView tv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_elenco_attivita);
		setTitle("Prossime attività");


		context=this;
		tv=(TextView) this.findViewById(R.id.attivita_data);

		Loading(Calendar.getInstance());
		
		Button prev= (Button)this.findViewById(R.id.button1);
		prev.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Loading(-7);
			}
		});      

		Button next= (Button)this.findViewById(R.id.button2);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Loading(7);
			}
		});

		listView = (ListView)findViewById(R.id.listElenco);
		String [] array = {"Caricamento.."};
		ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(this, R.layout.riga_attivita, R.id.textViewList, array);
		listView.setAdapter(arrayAdapter);

	}

	private void Loading(Calendar instance) {
		if(c==null)
			c=instance;
		Loading(0);
	}

	private void Loading(int offset){
		
		HashMap<String, String> data1 = new HashMap<String, String>();
		c.add(Calendar.DATE, -c.get(Calendar.DAY_OF_WEEK));
		c.add(Calendar.DATE, offset);

		String date1=c.get(Calendar.YEAR)+"-"+(Number(c.get(Calendar.MONTH)+1))+"-"+Number(c.get(Calendar.DAY_OF_MONTH))+"T"+"00:00:00.000Z";
		Log.d("Date inizio:",date1);

		c.add(Calendar.DATE, 6);
		String date2=c.get(Calendar.YEAR)+"-"+(Number(c.get(Calendar.MONTH)+1))+"-"+Number(c.get(Calendar.DAY_OF_MONTH))+"T"+"23:59:00.000Z";
		Log.d("Date fine:",date2);
		c.add(Calendar.DATE, 1);
		
		data1.put("inizio", date1);
		data1.put("fine", date2);
		RichiestaAttivita hello1 = new RichiestaAttivita(data1);
		hello1.execute();
		tv.setText(date1.substring(0,10) +"\n" +date2.substring(0,10));
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

		public String metodo() { return "attivita"; }

		public RichiestaAttivita(HashMap<String, String> data) {
			super(data);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPostExecute(String str) {
			//TODO
			try{
				ArrayList<String> a=new ArrayList<String>();
				for(int i=0;i<attivita.length();i++){	
					a.add(((JSONObject)attivita.get(i)).getString("title"));
				}
				ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_attivita, R.id.textViewList, a);
				listView.setAdapter(arrayAdapter);
			}catch(Exception e){}

		}

	}



}
