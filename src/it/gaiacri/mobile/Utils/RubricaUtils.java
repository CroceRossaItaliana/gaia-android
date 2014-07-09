package it.gaiacri.mobile.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class RubricaUtils {

	public static void sendMail(String destinatario,Context context){
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto",destinatario, null));
		context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}

	public static void sendCall(String numero,Context context){
		//if(isTelephonyEnabled(context)){
		try{
			String uri = "tel:" + numero.trim() ;
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(uri));
			context.startActivity(intent);
		}catch (Exception e){
			//gestione tablet
			//da aggiungere controllo aggiuntivo prima della chiamata(in modo che l'utente possa visualizzare comunque il numero prima di chiamarlo)
			Intent intent =new Intent(Intent.ACTION_CALL);
			String uri = "tel:" + numero.trim() ;
			intent.setData(Uri.parse(uri));
			context.startActivity(intent);
		}
	}
	/**
	 *
	 * @param context
	 * @return bool, true se c'e il modulo telefonico oppure se la sim e attiva
	 */
	public static boolean isTelephonyEnabled(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY;
	}

}

