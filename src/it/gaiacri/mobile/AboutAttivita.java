package it.gaiacri.mobile;

import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutAttivita extends Fragment {

	private Context context;
	public SharedPreferences sharedPref;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout v= (RelativeLayout)inflater.inflate(R.layout.activity_about, container, false);
		context=super.getActivity().getApplicationContext();
		Log.d("screen", super.getActivity().getRequestedOrientation() +"");		
		context=super.getActivity();
		((TextView)v.findViewById(R.id.about_info)).setText(Html.fromHtml(getString(R.string.prova)));
		return v;
	}	
}
