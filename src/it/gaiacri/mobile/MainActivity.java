package it.gaiacri.mobile;

import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.HashMap;

import NavigationDrawer.NsMenuAdapter;
import NavigationDrawer.NsMenuItemModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private ListView mDrawerList;
	private static DrawerLayout mDrawer;
	private static CustomActionBarDrawerToggle mDrawerToggle;
	private String[] menuItems;
	private Context context;
	private static String title;
	private static ActionBar actionbar;
	private NsMenuAdapter mAdapter;
	private String[] menuItemsIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.context=this.getApplicationContext();

		// enable ActionBar app icon to behave as action to toggle nav drawer
		actionbar=getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setHomeButtonEnabled(false);

		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		_initMenu();
		mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
		mDrawer.setDrawerListener(mDrawerToggle);

		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerToggle.setDrawerIndicatorEnabled(false);

		Intent i=this.getIntent();
		Bundle b=i.getExtras();
		Fragment t=new MenuPrincipale();
		t.setArguments(b);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.content_frame, t).commit();
		title=getString(R.string.title_activity_menu_principale);
	}

	private int addGroup(int header,int menuitems,int i){
		// Add Header
		mAdapter.addHeader(header);
		// Add first bloc
		menuItems = getResources().getStringArray(
				menuitems);
		//int res = 0;
		for (String item : menuItems) {

			int id_title = getResources().getIdentifier(item, "string",
					this.getPackageName());
			int id_icon = getResources().getIdentifier(menuItemsIcon[i],
					"string", this.getPackageName());
			NsMenuItemModel mItem = new NsMenuItemModel(id_title, id_icon);
			mAdapter.addItem(mItem);
			i++;
		}
		return i;	
	}

	private void _initMenu() {
		mAdapter = new NsMenuAdapter(this);
		//add Home Page
		int i=0;
		menuItemsIcon = getResources().getStringArray(R.array.ns_menu_items_icon);
		NsMenuItemModel homeitem = new NsMenuItemModel(R.string.ns_menu_home_page, getResources().getIdentifier(menuItemsIcon[i],
				"string", this.getPackageName()));
		mAdapter.addItem(homeitem);
		i++;
		//Add Attivita
		i=addGroup(R.string.ns_menu_main_header_attivita,R.array.ns_menu_items_attivita,i);
		//Add Rubrica
		i=addGroup(R.string.ns_menu_main_header_rubrica,R.array.ns_menu_items_rubrica,i);
		//Add Feedback
		i=addGroup(R.string.ns_menu_main_header_feedback,R.array.ns_menu_items_feedback,i);
		//Add Impostazioni
		i=addGroup(R.string.ns_menu_main_header_setting,R.array.ns_menu_items_setting,i);

		mDrawerList = (ListView) findViewById(R.id.drawer);
		if (mDrawerList != null)
			mDrawerList.setAdapter(mAdapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.main, menu);
		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * The action bar home/up should open or close the drawer.
		 * ActionBarDrawerToggle will take care of this.
		 */
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle your other action bar items...
		return super.onOptionsItemSelected(item);
	}

	private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {

		public CustomActionBarDrawerToggle(Activity mActivity,DrawerLayout mDrawerLayout){
			super(
					mActivity,
					mDrawerLayout,
					R.drawable.ic_drawer,
					R.string.drawer_open,
					R.string.drawer_close);
		}

		@Override
		public void onDrawerClosed(View view) {
			getSupportActionBar().setTitle(title);
			supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		}

		@Override
		public void onDrawerOpened(View drawerView) {
			getSupportActionBar().setTitle(getString(R.string.drawer_open));
			supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// Highlight the selected item, update the title, and close the drawer
			// update selected item and title, then close the drawer			
			Fragment test=null;
			mDrawerList.setItemChecked(position, true);
			Bundle b=new Bundle();
			b.putString("sid", "test");
			//Home Page	
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_home_page))){
				title=getString(R.string.ns_menu_home_page);
				test = new MenuPrincipale();
				test.setArguments(b);
			}
			//Rubrica Delegati
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_rubrica_delegati))){
				title=getString(R.string.title_activity_rubrica_delegati);
				test = new RubricaDelegati();

			}
			//Rubrica Volontari
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_rubrica_volontari))){
				title=getString(R.string.title_activity_rubrica_volontari);
				test = new RubricaVolontari();

			}
			//Elenco Attivita
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_attivita_elenco))){
				title=getString(R.string.title_activity_elenco_attivita);
				test = new ElencoAttivita();
			}			
			//Miei Turni
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_attivita_miei))){
				title=getString(R.string.title_activity_miei_turni);
				test = new PartecipazioniAttivita();
			}
			//About
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_setting_about))){
				title=getString(R.string.title_activity_setting_about);
				test = new AboutAttivita();
			}


			//Logout
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_setting_logout))){
				richiestaLogout();
			}
			//Supporto
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_feedback_supporto))){
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto","android@gaia.cri.it", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Supporto Gaia Android");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
			}
			//Giudizi
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_feedback_giudizi))){
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto","giudizi.android@gaia.cri.it", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Giudizi Gaia Android");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
			}





			if (test != null) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.content_frame, test).commit(); //

			}

			//You should reset item counter
			mDrawer.closeDrawer(mDrawerList);

		}

	}
	class RichiestaLogout extends Richiesta {
		public RichiestaLogout(HashMap<String, String> data) {
			super(data,MainActivity.this.context);
		}
		public String metodo() { return "logout"; }
		protected void onPostExecute(String ris) {
			if(ErrorJson.Controllo(ris,MainActivity.this,risposta)==0){
				setResult(Activity.RESULT_OK);
				//annulla();
				Intent myIntent = new Intent(MainActivity.this, Accesso.class);
				startActivity(myIntent);
				finish();
			}
		}
		@Override
		public void restore(){
			AlertDialog.Builder miaAlert=ErrorJson.AssenzaInternet(MainActivity.this);
			miaAlert.setPositiveButton(R.string.error_internet_si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					richiestaLogout();
				}
			});
			AlertDialog alert = miaAlert.create();
			alert.show();		
		}
	}

	public void richiestaLogout(){
		HashMap<String, String> data = new HashMap<String, String>();
		RichiestaLogout asd = new RichiestaLogout(data);
		asd.execute();
	}
	public static void enable(){
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == 100)
		{
			setResult(100);
			this.finish();
		}
	}

}