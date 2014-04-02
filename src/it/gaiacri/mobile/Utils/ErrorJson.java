package it.gaiacri.mobile.Utils;

import org.json.JSONObject;

import it.gaiacri.mobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

public class ErrorJson {

	public static int Controllo(String ris,Activity context,JSONObject json){
		if(ris.equals("Errore Internet")){
			//AssenzaInternet(context);
			return 1;
		}else{
			if( ris.equals("Errore")){
				//errore generico
				return 2;
			}else{
				if(ris.startsWith("Errore ")){
					ProblemaApi(ris.substring(7),context,json);
					return 3;
				}
			}
		}
		return 0;
	}
	
	public static AlertDialog.Builder AssenzaInternet(final Activity context){
		AlertDialog.Builder miaAlert = new AlertDialog.Builder(context);
		miaAlert.setMessage(R.string.error_internet);
		miaAlert.setCancelable(false);
		miaAlert.setNegativeButton(R.string.error_internet_no, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
				  context.setResult(100);
				  context.finish();
			  }
			});
		return miaAlert;
	}
	 
	/*public static void AssenzaInternet(final Activity context){
		//Toast.makeText(context, R.string.error_internet,Toast.LENGTH_LONG).show();
		AlertDialog.Builder miaAlert = new AlertDialog.Builder(context);
		miaAlert.setMessage(R.string.error_internet);
		//miaAlert.setTitle("AlertDialog di MrWebMaster");

		miaAlert.setCancelable(false);
		miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int id) {  
		  }
		});
		    	

		AlertDialog alert = miaAlert.create();
		alert.show();
	}*/
	
	
	public static void ProblemaApi(String errore,final Activity context,final JSONObject json){
		AlertDialog.Builder miaAlert = new AlertDialog.Builder(context);
		miaAlert.setTitle(R.string.api_error);
		miaAlert.setMessage(errore);

		miaAlert.setCancelable(false);
		miaAlert.setPositiveButton(R.string.api_email, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int id) {
				  //da aggiungere il codice per la mail
				  Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				            "mailto","android@gaia.cri.it", null));
				  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Errore Android Api");
				  emailIntent.putExtra(Intent.EXTRA_TEXT, "Errore Generato \n"+json);
				  context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
				  context.setResult(100);
				  context.finish();
			  }
			});
		miaAlert.setNegativeButton(R.string.api_close, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int id) {
			  context.setResult(100);
			  context.finish();
		  }
		});
		AlertDialog alert = miaAlert.create();
		alert.show();
	}
}

