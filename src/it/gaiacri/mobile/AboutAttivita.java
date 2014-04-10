package it.gaiacri.mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutAttivita extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout v= (LinearLayout)inflater.inflate(R.layout.activity_about, container, false);

		((TextView)v.findViewById(R.id.about_title)).setText("Gaia Mobile v "+getString(R.string.app_version));

		//licenza
		((TextView)v.findViewById(R.id.about_licenza)).setText(Html.fromHtml("Questa applicazione è rilasciata con licenza <a href=\"\">GPL v3</a>"));
		((TextView)v.findViewById(R.id.about_licenza)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(AboutAttivita.this.getActivity()); 
				WebView wv = new WebView(AboutAttivita.this.getActivity());
				wv.loadUrl("https://raw.githubusercontent.com/CroceRossaItaliana/gaia-android/master/LICENSE.txt");
				wv.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						view.loadUrl(url);

						return true;
					}
				});
				alert.setView(wv);
				alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
		//portale gaia
		((TextView)v.findViewById(R.id.about_portale_gaia)).setText(Html.fromHtml("<a href=\"\">Portale Gaia</a>"));
		((TextView)v.findViewById(R.id.about_portale_gaia)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Internet("https://gaia.cri.it");
			}
		});
		//informazioni gaia
		((TextView)v.findViewById(R.id.about_gaia)).setText(Html.fromHtml("<a href=\"\">Informazioni GAIA</a>"));
		((TextView)v.findViewById(R.id.about_gaia)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Internet("https://gaia.cri.it/?p=public.about");
			}
		});
		//portale sviluppo mobile
		((TextView)v.findViewById(R.id.about_gaia_mobile)).setText(Html.fromHtml("<a href=\"\">Codice Sorgente Gaia Mobile</a>"));
		((TextView)v.findViewById(R.id.about_gaia_mobile)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Internet("https://github.com/CroceRossaItaliana/gaia-android");
			}
		});
		//infomrazione dati
		((TextView)v.findViewById(R.id.about_gaia_privacy)).setText(Html.fromHtml("<a href=\"\">Informazioni Gestione Dati</a>"));
		((TextView)v.findViewById(R.id.about_gaia_privacy)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Internet("https://gaia.cri.it/?p=public.privacy");
			}
		});



		//((WebView)v.findViewById(R.id.about_info))//.setClickable(true);;	
		return v;
	}	

	private void Internet(String url){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
}
