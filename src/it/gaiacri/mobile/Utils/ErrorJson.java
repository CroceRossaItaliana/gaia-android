package it.gaiacri.mobile.Utils;

import it.gaiacri.mobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ErrorJson {

	public static int Controllo(String ris,Activity context){
		if(ris.equals("Errore Internet")){
			AssenzaInternet(context);
			return 1;
		}else{
			if( ris.equals("Errore")){
				//errore generico
				return 2;
			}else{
				if(ris.startsWith("Errore ")){
					ProblemaApi(ris.substring(7),context);
					return 3;
				}
			}
		}
		return 0;
	}

	public static void AssenzaInternet(final Activity context){
		//Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
		AlertDialog.Builder miaAlert = new AlertDialog.Builder(context);
		miaAlert.setMessage(R.string.error_internet);
		//miaAlert.setTitle("AlertDialog di MrWebMaster");

		miaAlert.setCancelable(false);
		miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int id) {
		    
		  }
		});
		    	
		miaAlert.setNegativeButton(R.string.error_internet_no, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int id) {
			  context.setResult(100);
			  context.finish();
		  }
		});
		AlertDialog alert = miaAlert.create();
		alert.show();
	}
	
	public static void ProblemaApi(String errore,final Activity context){
		//Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
		AlertDialog.Builder miaAlert = new AlertDialog.Builder(context);
		miaAlert.setTitle(R.string.api_error);
		miaAlert.setMessage(errore);

		miaAlert.setCancelable(false);
		    	
		miaAlert.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int id) {
			  context.setResult(100);
			  context.finish();
		  }
		});
		AlertDialog alert = miaAlert.create();
		alert.show();
	}
	
	
}

