package it.gaiacri.mobile;

import it.gaiacri.mobile.Object.Rubrica;
import it.gaiacri.mobile.Utils.Cache;
import it.gaiacri.mobile.Utils.ErrorJson;
import it.gaiacri.mobile.Utils.RubricaUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beardedhen.androidbootstrap.BootstrapButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RubricaDelegati extends Fragment{

	private ArrayList<Rubrica> rubrica;
	private static ListView listView;
	private Cache cache;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v=inflater.inflate(R.layout.activity_rubrica_delegati, container, false);
		richiestaDelegati();
		context= this.getActivity();
		cache=new Cache(this.getActivity());
		listView = (ListView)v.findViewById(R.id.listRubrica);
		aggiornalist();
		return v;
	}

	class RichiestaDelegati extends Richiesta {

		public RichiestaDelegati(HashMap<String, String> data) {
			super(data,RubricaDelegati.this.getActivity().getApplicationContext());
		}

		public String metodo() { return "rubrica_delegati"; }

		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,RubricaDelegati.this.getActivity(),risposta)==0){
				Log.d("Json",risposta.toString());

				if(rubrica==null)
					rubrica= new ArrayList<Rubrica>();

				try {
					//	prin
					//String TAG="Risposta: ";
					//att_title=risposta.getString("nome");
					//att_luogo=risposta.getString("luogo");
					//int info_totale=risposta.getInt("totale");
					JSONArray res=risposta.getJSONArray("risultati");

					//recupera anche mittente da mostrare e salvare
					for(int i=0;i<res.length();i++){
						JSONObject obj=res.getJSONObject(i);
						JSONObject avatar=obj.getJSONObject("avatar");
						String rubrica_avatar=avatar.getString("10");
						String rubrica_nome=obj.getString("nome");
						String rubrica_cognome=obj.getString("cognome");
						String rubrica_numero=obj.getString("numero");
						String rubrica_email=obj.getString("email");
						//JSONObject mittente=obj.optJSONObject("deleghe");
						/*String posta_mittente="";
						if(!(mittente == null)){
							posta_mittente=obj.getJSONObject("mittente").getString("id");
							if(!mitt.contains(posta_mittente))
								mitt.add(posta_mittente);
						}*/						
						rubrica.add(new Rubrica(rubrica_avatar,rubrica_nome,rubrica_cognome,rubrica_numero,rubrica_email,new ArrayList<String>()));//Log.d("ciao", );
					}

					orderArray();
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
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(RubricaDelegati.this.getActivity());
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaDelegati();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}	

	private void orderArray(){
		Collections.sort(rubrica, new Comparator<Rubrica>() {
			@Override
			public int compare(Rubrica arg0, Rubrica arg1) {
				// TODO Auto-generated method stub
				return (arg0.getCognome() + " "+ arg0.getNome()).compareTo(arg1.getCognome() + " "+ arg1.getNome());
			}
		});

		aggiornalist();
		downloadImg();
	}
	private void aggiornalist() {

		if(rubrica!=null){
			//Questa è la lista che rappresenta la sorgente dei dati della listview
			//ogni elemento è una mappa(chiave->valore)
			ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

			HashMap<String,Object> ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
			Rubrica pos=null;
			for(int i=0;i<rubrica.size();i++){
				ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
				pos=rubrica.get(i);
				ServiceMap.put("rubrica_nome", pos.getNome());
				ServiceMap.put("rubrica_cognome",pos.getCognome());
				data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
			}
			String[] from={"rubrica_cognome","rubrica_nome"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.rubrica_nome,R.id.rubrica_ruoli};//agli id delle view

			//costruzione dell adapter
			SimpleAdapter adapter=new SimpleAdapter(
					context,
					data,//sorgente dati
					R.layout.riga_delegati, //layout contenente gli id di "to"
					from,
					to){
				//con questo metodo riesco a inserire il colore nel testo delle attivita
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (convertView==null){
						LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView=inflater.inflate(R.layout.riga_delegati, null);
					}
					View row = super.getView(position, convertView, parent);

					//set Avatar
					//Log.d("avatar",""+rubrica.get(position).getAvatar());
					ImageView iw=(ImageView) row.findViewById(R.id.rubrica_avatar);
					iw.setImageBitmap(rubrica.get(position).getBitmap(context));

					//((BootstrapButton)v.findViewById(R.id.buttonPartecipa)).setTag(tag);
					((BootstrapButton)row.findViewById(R.id.rubrica_email)).setTag(rubrica.get(position).getEmail());
					((BootstrapButton)row.findViewById(R.id.rubrica_chiama)).setTag(rubrica.get(position).getNumero());
					if(!RubricaUtils.isTelephonyEnabled(context))
						((BootstrapButton)row.findViewById(R.id.rubrica_chiama)).setVisibility(View.GONE);;

						//settare azioni
						((BootstrapButton)row.findViewById(R.id.rubrica_email)).setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								RubricaUtils.sendMail((String)((BootstrapButton)v.findViewById(R.id.rubrica_email)).getTag(),context);
								//String id=(String)((BootstrapButton)v.findViewById(R.id.buttonPartecipa)).getTag();
							}
						});
						((BootstrapButton)row.findViewById(R.id.rubrica_chiama)).setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								RubricaUtils.sendCall((String)((BootstrapButton)v.findViewById(R.id.rubrica_chiama)).getTag(),context);
								//String id=(String)((BootstrapButton)v.findViewById(R.id.buttonPartecipa)).getTag();
							}
						});

						return row;
				}
			};
			//utilizzo dell'adapter
			listView.setAdapter(adapter);
		}else{
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_attivita, R.id.textViewList,new String[]{"Caricamento.."});
			listView.setAdapter(arrayAdapter);
		}
	}

	public void downloadImg(){
		ImageDownloaderProva im=new ImageDownloaderProva();
		im.execute();
	}

	public void richiestaDelegati(){
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaDelegati richiesta=new RichiestaDelegati(data);
		richiesta.execute();
	}

	private class ImageDownloaderProva extends AsyncTask<Object,Object,Object> {
		@Override
		protected void onProgressUpdate (Object... values){
			Log.d("update", "via");
			aggiornalist();
		}
		
		@Override
		protected Object doInBackground(Object... param) {
			// TODO Auto-generated method stub
			String url="";
			for(int i=0;i<rubrica.size();i++){
				url=rubrica.get(i).getAvatar();
				if(!url.equals("https://gaia.cri.it/./upload/avatar/placeholder/10.jpg")){
					//e in cache??
					if(cache.contains(url)){
						Log.i("cache", "image"+rubrica.get(i).getAvatar());
						rubrica.get(i).setBitmap(cache.get(url));
						publishProgress("");
					}else{
						Log.i("download", "image"+rubrica.get(i).getAvatar());
						ImageDownloader im=new ImageDownloader();
						im.execute(url,i);
					}
				}
			}
			return null;
		}      
	}

	private class ImageDownloader extends AsyncTask<Object,Bitmap,Bitmap> {
		private int i;
		@Override
		protected Bitmap doInBackground(Object... param) {
			// TODO Auto-generated method stub
			i = (Integer) param[1];
			return downloadBitmap((String)param[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			Log.i("Async-Example", "onPostExecute Called");
			rubrica.get(i).setBitmap(result);
			aggiornalist();
			//image.setImageBitmap(result);
			//downloadedImg.setImageBitmap(result);

		}

		private Bitmap downloadBitmap(String url) {
			// initilize the default HTTP client object
			final DefaultHttpClient client = new DefaultHttpClient();

			//forming a HttoGet request
			final HttpGet getRequest = new HttpGet(url);
			try {

				HttpResponse response = client.execute(getRequest);

				//check 200 OK for success
				final int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					Log.w("ImageDownloader", "Error " + statusCode +
							" while retrieving bitmap from " + url);
					return null;
				}
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						// getting contents from the stream
						inputStream = entity.getContent();

						// decoding stream data back into image Bitmap that android understands
						final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						cache.put(url,bitmap);
						return bitmap;
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				// You Could provide a more explicit error message for IOException
				getRequest.abort();
				Log.e("ImageDownloader", "Something went wrong while" +
						" retrieving bitmap from " + url + e.toString());
			}
			return null;
		}
	}


}
