package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Attivita;
import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ElencoAttivita extends ActionBarActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	private static int giorni=120;
	private static Context context;
	ArrayList<String> array;
	static String IUrl;
	static String URL;
	static String time;
	static Calendar c;

	static ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		// Create the adapter that will return a fragment for each of the three primary sections
		// of the apps
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		context=this.getApplicationContext();


		// Set up the ViewPager with the sections adapter.
		c=Calendar.getInstance();

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);		
		mViewPager.setCurrentItem(giorni/2);


	}
	
	

	@Override
	protected void onResume() {
		Log.d("pause", "restore");
		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_elenco, menu);*/
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*switch (item.getItemId()) {
		case R.id.item2:
			if (item.isChecked()) 
				item.setChecked(false);
            else 
            	item.setChecked(true);
            return true;
		 */
		/*switch (item.getItemId()) {
		case R.id.cpy:
			// Toast.makeText(getApplicationContext(), "Copy",Toast.LENGTH_SHORT).show();
			//URL=(String) mViewPager.
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
				android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
				ClipData clip = ClipData.newPlainText("label", IUrl);
				clipboard.setPrimaryClip(clip); 
			} else{
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE); 
				clipboard.setText(IUrl);
			}
			Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.save:
			Toast.makeText(getApplicationContext(), "Save",Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}*/
		return true;
	}



	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();			

			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return giorni;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			c.add(Calendar.DAY_OF_MONTH, position-giorni/2);
			//"Ven 17 Gen"
			String date1=DayWeek(c.get(Calendar.DAY_OF_WEEK))+" "+c.get(Calendar.DAY_OF_MONTH)+ " "+ Month(c.get(Calendar.MONTH));
			c.add(Calendar.DAY_OF_MONTH, -(position-giorni/2));
			return date1;
		}

		private String Month(int i) {
			switch(i){
			case 0: return getString(R.string.month_jan);
			case 1: return getString(R.string.month_feb);
			case 2: return getString(R.string.month_mar);
			case 3: return getString(R.string.month_apr);
			case 4: return getString(R.string.month_may);
			case 5: return getString(R.string.month_jun);
			case 6: return getString(R.string.month_jul);
			case 7: return getString(R.string.month_aug);
			case 8: return getString(R.string.month_sep);
			case 9: return getString(R.string.month_opt);
			case 10: return getString(R.string.month_nov);
			case 11: return getString(R.string.month_dec);
			}
			return "";
		}

		private String DayWeek(int i) {
			switch(i){
			case 1: return getString(R.string.day_sunday);
			case 2: return getString(R.string.day_monday);
			case 3: return getString(R.string.day_tuesday);
			case 4: return getString(R.string.day_wednesday);
			case 5: return getString(R.string.day_thursday);
			case 6: return getString(R.string.day_friday);
			case 7: return getString(R.string.day_saturday);
			}
			return "";
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		public DummySectionFragment() {
		}

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Bundle args = getArguments();  
			ListView lv=new ListView(getActivity());
			//download turni e visualizzazione

			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_attivita, R.id.textViewList,new String[]{"Caricamento.."});
			lv.setAdapter(arrayAdapter);


			HashMap<String, String> data1 = new HashMap<String, String>();
			c.add(Calendar.DAY_OF_MONTH, args.getInt(ARG_SECTION_NUMBER)-giorni/2-1);

			String date1=c.get(Calendar.YEAR)+"-"+(Number(c.get(Calendar.MONTH)+1))+"-"+Number(c.get(Calendar.DAY_OF_MONTH))+"T"+"00:00:00.000Z";
			Log.d("Date inizio:",date1);
			String date2=c.get(Calendar.YEAR)+"-"+(Number(c.get(Calendar.MONTH)+1))+"-"+Number(c.get(Calendar.DAY_OF_MONTH))+"T"+"23:59:00.000Z";
			Log.d("Date fine:",date2);
			c.add(Calendar.DAY_OF_MONTH, -(args.getInt(ARG_SECTION_NUMBER)-giorni/2-1));
			data1.put("inizio", date1);
			data1.put("fine", date2);
			RichiestaAttivita hello1 = new RichiestaAttivita(lv,data1);
			hello1.execute();
			return lv;
		}

		private String Number(int num){
			if(num<10)
				return "0"+num;
			else
				return ""+num;
		}



		public class RichiestaAttivita extends Richiesta {
			ArrayList<Attivita> a;
			ListView lv;

			public String metodo() { return "attivita"; }

			public RichiestaAttivita(ListView lv,HashMap<String, String> data) {
				super(data,ElencoAttivita.context);
				this.lv=lv;
			}

			@Override
			protected void onPostExecute(String str) {
				//Log.d("Log","Ciao");
				
				if(ErrorJson.Controllo(str,getActivity(),risposta)==0){
					try{
						JSONObject js=null;
						a=new ArrayList<Attivita>();
						for(int i=0;i<attivita.length();i++){
							js=(JSONObject)attivita.get(i);

							String att_title=js.getJSONObject("attivita").getString("nome");//js.getString("title");
							String tur_title=js.getJSONObject("turno").getString("nome");
							//att_title=att_title.substring(0,att_title.indexOf(','));
							String att_id=js.getJSONObject("attivita").getString("id");//js.getString("attivita");
							//String tur_url=js.getString("url");
							//String tur_id=js.getString("id");
							String att_organizzatore=js.getJSONObject("organizzatore").getString("nome");
							//String tur_start=js.getString("start");
							//String tur_end=js.getString("end");
							String tur_color=js.getString("colore");
							//se a contiene l'attivita allora aggiunto un turno
							//altrimenti creo una nuova attivita e gli aggiunto il turno
							//int indice=contiene(att_id);
							//if(indice==-1){
							a.add(new Attivita(att_title+ ", "+tur_title,att_id,att_organizzatore,tur_color));
							//indice=a.size()-1;
							//}
							//a.get(indice).addTurno(new Turno(tur_desc,tur_id,tur_start,tur_end,tur_url,tur_color));
						}
						aggiornalist();
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
						ServiceMap.put("attivita_url", att.getOrganizzatore());
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
							to){
						//con questo metodo riesco a inserire il colore nel testo delle attivita
						@Override
						public View getView(int position, View convertView, ViewGroup parent) {
							if (convertView==null){
								LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								convertView=inflater.inflate(R.layout.riga_attivita, null);
							}
							View row = super.getView(position, convertView, parent);
							if(a!=null && a.size()!=0){
								//String col=a.get(position).getTurni().get(0).getColor();
								//Log.d("Colore Elab:", col);
								((TextView)row.findViewById(R.id.textViewList)).setTextColor(Color.parseColor(a.get(position).getColor()));
							}
							return row;

						}
					};

					//utilizzo dell'adapter
					lv.setAdapter(adapter);
				}else{
					ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_attivita, R.id.textViewList,new String[]{"Caricamento.."});
					lv.setAdapter(arrayAdapter);
				}
				lv.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
							long arg3) {
						//Toast.makeText(getApplicationContext(),"hiihih" + pos,Toast.LENGTH_SHORT).show();
						String id=a.get(pos).getId();
						if(!id.equals("no")){
							Intent i= new Intent(ElencoAttivita.context,DisplayAttivita.class);
							i.putExtra("id", id);
							startActivityForResult(i, 0);
						}else{
							Toast.makeText(context, R.string.attivita_no_turni, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == 100)
		{
			setResult(100);
			this.finish();
		}
	}
	
}
