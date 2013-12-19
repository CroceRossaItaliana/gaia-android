package it.gaiacri.mobile;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * This Fragment manages a single background task and retains
 * itself across configuration changes.
 */
public class TaskFragment extends Fragment {

	/**
	 * Callback interface through which the fragment will report the
	 * task's progress and results back to the Activity.
	 */
	static interface TaskCallbacks {
		void onPostExecuteW(String ret);
		void onPostUpdate();
		void onPostExecuteC_1();
		void onPostExecuteC_2(String ris,JSONObject risposta);
	}

	private TaskCallbacks mCallbacks;
	private RichiestaChiSono richiesta;
	private RichiestaWelcome welcome;
	private Context context;
	public SharedPreferences sharedPref;


	/**
	 * Hold a reference to the parent Activity so we can report the
	 * task's current progress and results. The Android framework
	 * will pass us a reference to the newly created Activity after
	 * each configuration change.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	/**
	 * This method will only be called once when the retained
	 * Fragment is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);
		this.context = this.getActivity().getApplicationContext();
		sharedPref = this.getActivity().getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
		// Create and execute the background task.
		HashMap<String, String> data = new HashMap<String, String>();
		welcome = new RichiestaWelcome(data);
		welcome.execute();
	}

	/**
	 * Set the callback to null so we don't accidentally leak the
	 * Activity instance.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}


	public class RichiestaWelcome extends Richiesta {

		public String metodo() { return "welcome"; }

		public RichiestaWelcome(HashMap<String, String> data) {
			super(data,TaskFragment.this.context);
			setSid(sharedPref.getString("sid", ""));
		}

		@Override
		protected void onPostExecute(String str) {
			if(mCallbacks!=null){
				if(str.equals("Errore Internet")){
					mCallbacks.onPostExecuteW("Errore Internet");
				}
				else{
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString("sid", getSid());
					editor.commit();

					if ( utente != null ) {
						Log.e("Login", "Gia Identificato");
						mCallbacks.onPostUpdate();
						richiestaDati();


						//mHandler.sendEmptyMessage(FINISH_LOAD);
					} else {
						Log.e("Login", "Non gia Identificato");
						mCallbacks.onPostExecuteW("Sessione non valida");
					}
				}
			}
		}

	}

	public void richiestaDati(){
		if(mCallbacks!=null){
			if(MenuPrincipale.user_nome == null && MenuPrincipale.user_comitato==null){
				//caso in cui l'utente abbia appena effettuato il login
				HashMap<String, String> data = new HashMap<String, String>();
				richiesta = new RichiestaChiSono(data);
				richiesta.execute();
			}else{//in questo caso l'utente ha ruotato lo schemo oppure e arrivato da un activity successiva e io ricarico i dati scaricati precedentemente
				mCallbacks.onPostExecuteC_1();
			}
		}
	}

	class RichiestaChiSono extends Richiesta {
		public RichiestaChiSono(HashMap<String, String> data) {
			super(data,TaskFragment.this.context);
		}

		public String metodo() { return "me"; }

		protected void onPostExecute(String ris) {
			if(mCallbacks!=null){
				mCallbacks.onPostExecuteC_2(ris,risposta);
			}
		}
	}




}