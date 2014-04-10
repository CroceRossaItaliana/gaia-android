package it.gaiacri.mobile;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

public class AboutAttivita extends Fragment {

	private Context context;
	private ListView listview;
	private ArrayList<String> info,url;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout v= (RelativeLayout)inflater.inflate(R.layout.activity_about, container, false);
		context=super.getActivity().getApplicationContext();
		Log.d("screen", super.getActivity().getRequestedOrientation() +"");		
		context=super.getActivity();
		
		info = new ArrayList<String>();
		url = new ArrayList<String>();
		
		//informazioni su gaia
		info.add("Versione GAIA Mobile");
		url.add("v "+getString(R.string.app_version));
				
		//link gaiaweb
		info.add("Sito Web");
		url.add("https://gaia.cri.it");
				
		//informazioni su gaia
		info.add("Informazioni GAIA");
		url.add("https://gaia.cri.it/?p=public.about");
		
		//link github di gaia Android
		info.add("Codice Sorgente Gaia Android");
		url.add("https://github.com/CroceRossaItaliana/gaia-android");
		
		//Informazioni Gestione Dati
		info.add("Informazioni Gestione Dati");
		url.add("https://gaia.cri.it/?p=public.privacy");
		
		listview =(ListView)v.findViewById(R.id.listInfo);//).setText(Html.fromHtml(getString(R.string.prova)));
		
		aggiornalist();
		
		return v;
	}	
	
	private void aggiornalist() {

		if(info!=null){
			//Questa è la lista che rappresenta la sorgente dei dati della listview
			//ogni elemento è una mappa(chiave->valore)
			ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

			HashMap<String,Object> ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
			for(int i=0;i<info.size();i++){
				ServiceMap=new HashMap<String, Object>();//creiamo una mappa di valori
				ServiceMap.put("about_title", info.get(i));
				ServiceMap.put("about_url",url.get(i));
				data.add(ServiceMap);  //aggiungiamo la mappa di valori alla sorgente dati
			}
			String[] from={"about_title","about_url"}; //dai valori contenuti in queste chiavi
			int[] to={R.id.about_title,R.id.about_url};//agli id delle view

			//costruzione dell adapter
			SimpleAdapter adapter=new SimpleAdapter(
					context,
					data,//sorgente dati
					R.layout.riga_about, //layout contenente gli id di "to"
					from,
					to);
			//utilizzo* dell'adapter
			listview.setAdapter(adapter);
		}else{
			ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(context, R.layout.riga_about, R.id.textViewList,new String[]{"Caricamento.."});
			listview.setAdapter(arrayAdapter);
		}
	}
	
	
	
}
