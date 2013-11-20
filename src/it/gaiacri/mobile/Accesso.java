package it.gaiacri.mobile;

import java.util.HashMap;

import org.json.JSONException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class Accesso extends Activity {

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private Richiesta rLogin = null;
    private Richiesta hello = null;
    
    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    public Context contesto;
    
    public SharedPreferences sharedPref;
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Entra in Gaia");
        setContentView(R.layout.activity_accesso);
        
        contesto = getBaseContext();

        // Set up the login form.
        mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(mEmail);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {
						attemptLogin();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
	                    Log.e("gaia", "Errore di JSON");

					}

                    return true;
                }
                return false;
            }
        });
                
        
        sharedPref = this.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
    	HashMap<String, String> data = new HashMap<String, String>();
		//data.put("sid", sid);
    	RichiestaWelcome hello = new RichiestaWelcome(data);
    	hello.execute();
    	

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
					attemptLogin();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	                Log.e("gaia", "Errore di JSON");

				}
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_accesso, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     * @throws JSONException 
     */
    public void attemptLogin() throws JSONException { 	

     
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            
			HashMap<String, String> data = new HashMap<String, String>();
        	data.put("email", 		mEmail);
        	data.put("password", 	mPassword);
        	RichiestaLogin rLogin = new RichiestaLogin(data);
        	rLogin.execute();

        }
    }

    public class RichiestaLogin extends Richiesta {

        public String metodo() { return "login"; }
    	
    
		public RichiestaLogin(HashMap<String, String> data) {
			super(data);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onPostExecute(String str) {

        	try {
				if ( risposta.getBoolean("login") ) {
				    //showProgress(false);
				    Log.i("Login", "Riuscito");
				    Log.i("Utente", utente.getString("nome"));
			    	SharedPreferences.Editor editor = sharedPref.edit();
			    	editor.putString("sid", getSid());
			    	editor.commit();
			    	showProgress(false);
	                Intent myIntent = new Intent(contesto, MenuPrincipale.class);
	                myIntent.putExtra("sid", getSid());
	                startActivity(myIntent);
	                finish();
	                
				} else {
					Log.i("Login", "Fallito");
					erroreLogin();
					

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    }
    public class RichiestaWelcome extends Richiesta {

        public String metodo() { return "welcome"; }
    	
		public RichiestaWelcome(HashMap<String, String> data) {
			super(data);
			setSid(sharedPref.getString("sid", ""));
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onPostExecute(String str) {
				    	
	    	SharedPreferences.Editor editor = sharedPref.edit();
	    	editor.putString("sid", getSid());
	    	editor.commit();
	    	
	    	if ( utente != null ) {
	            Log.e("Login", "Gia Identificato");

	            Intent myIntent = new Intent(contesto, MenuPrincipale.class);
	            myIntent.putExtra("sid", getSid());
	            startActivity(myIntent);
	            finish();
	    	} else {
	            Log.e("Login", "Non gia Identificato");

	    	}
		}
    	
    }
       
    
    public void erroreLogin() {
    	try {

	    	if ( mPasswordView != null ) {
	    	    mPasswordView.setError(getString(R.string.error_incorrect_password));
	    	    mPasswordView.requestFocus();
	    	}
	    	showProgress(false);
    	} catch (Exception e) {
    		Log.e("Errore", "Generico");
    	}
    }
    
    
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
   
}
