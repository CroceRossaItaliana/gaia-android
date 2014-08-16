package it.gaiacri.mobile.Utils;

import it.gaiacri.mobile.MainActivity;
import it.gaiacri.mobile.MainActivity.TrackerName;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Context;

public class GaiaGoogleAnalytics {



	public static void distpach(Context context){
		GoogleAnalytics.getInstance(context).dispatchLocalHits();
	}

	public static void notifyEvent(Context context, String cat,String action, String label){
		if( GaiaGoogleAnalytics.playServiceAvviable(context)){
			Tracker track = MainActivity.getTracker(TrackerName.APP_TRACKER);
			track.send(new HitBuilders.EventBuilder().setCategory(cat)
					.setAction(action).setLabel(label).build());
			distpach(context);
		}
	}

	public static void notifyScreen(Context context,String screen){
		if( GaiaGoogleAnalytics.playServiceAvviable(context)){

			Tracker t =MainActivity.getTracker(TrackerName.APP_TRACKER);
			t.setScreenName(screen);
			t.send(new HitBuilders.AppViewBuilder().build());
			distpach(context);
		}
	}	

	public static boolean playServiceAvviable(Context context){
		if( ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(context))
			return true;
		return false;
	}
}
