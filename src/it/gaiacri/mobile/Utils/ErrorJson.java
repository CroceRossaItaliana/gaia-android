package it.gaiacri.mobile.Utils;

import org.json.JSONObject;

import it.gaiacri.mobile.Accesso;
import it.gaiacri.mobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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
					Log.d("Errore",json.optJSONObject("errore").optString("codice"));
					Log.d("Errore", ""+ ("1010".equals(json.optJSONObject("errore").optString("codice"))));
					if("1010".equals(json.optJSONObject("errore").optString("codice"))){
						if(context == null)
							return 2;
						Intent myIntent = new Intent(context, Accesso.class);
						context.startActivity(myIntent);
						context.finish(); 
					}else{
						ProblemaApi(ris.substring(7),context,json);
						return 3;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 
	 * @param context Utilizzato per generare il Dialog
	 * @return AlertDialog.Builder Oggetto che poi viene adattato alle esigenze
	 */
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

	/**
	 * 
	 * @param errore Stringa che indica il tipo di errore che poi verra mostrato all'utente
	 * @param context Utilizzato per creare il Dialog
	 * @param json Contiene la risposta ritornata all'utente e viene eventualmente usata per segnalare tramite email il problema allo sviluppatore
	 */

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

