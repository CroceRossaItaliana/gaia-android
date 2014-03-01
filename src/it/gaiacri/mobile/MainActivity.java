package it.gaiacri.mobile;

import it.gaiacri.mobile.Utils.ErrorJson;

import java.util.HashMap;

import NavigationDrawer.NsMenuAdapter;
import NavigationDrawer.NsMenuItemModel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.context=this.getApplicationContext();

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		_initMenu();
		mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
		mDrawer.setDrawerListener(mDrawerToggle);

		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerToggle.setDrawerIndicatorEnabled(false);


		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.content_frame, new MenuPrincipale()).commit();
		title=getString(R.string.title_activity_menu_principale);

	}

	private void _initMenu() {
		NsMenuAdapter mAdapter = new NsMenuAdapter(this);

		//add Home Page
		NsMenuItemModel homeitem = new NsMenuItemModel(R.string.ns_menu_home_page, R.drawable.ic_action_settings);
		mAdapter.addItem(homeitem);
		//Add Attivita
		// Add Header
		mAdapter.addHeader(R.string.ns_menu_main_header_attivita);
		// Add first bloc
		menuItems = getResources().getStringArray(
				R.array.ns_menu_items_attivita);
		String[] menuItemsIcon = getResources().getStringArray(
				R.array.ns_menu_items_icon);
		//int res = 0;
		for (String item : menuItems) {

			int id_title = getResources().getIdentifier(item, "string",
					this.getPackageName());
			int id_icon = getResources().getIdentifier(menuItemsIcon[0],
					"drawable", this.getPackageName());

			NsMenuItemModel mItem = new NsMenuItemModel(id_title, id_icon);
			//	if (res==1) mItem.counter=12; //it is just an example...
			//	if (res==3) mItem.counter=3; //it is just an example...
			mAdapter.addItem(mItem);
			//	res++;
		}

		//Add Rubrica
		mAdapter.addHeader(R.string.ns_menu_main_header_rubrica);
		// Add first bloc
		menuItems = getResources().getStringArray(
				R.array.ns_menu_items_rubrica);
		menuItemsIcon = getResources().getStringArray(
				R.array.ns_menu_items_icon);
		//res = 0;
		for (String item : menuItems) {

			int id_title = getResources().getIdentifier(item, "string",
					this.getPackageName());
			int id_icon = getResources().getIdentifier(menuItemsIcon[0],
					"drawable", this.getPackageName());

			NsMenuItemModel mItem = new NsMenuItemModel(id_title, id_icon);
			mAdapter.addItem(mItem);
			//	res++;
		}

		//Add Feedback
		mAdapter.addHeader(R.string.ns_menu_main_header_feedback);
		// Add first bloc
		menuItems = getResources().getStringArray(
				R.array.ns_menu_items_feedback);
		menuItemsIcon = getResources().getStringArray(
				R.array.ns_menu_items_icon);
		//res = 0;
		for (String item : menuItems) {

			int id_title = getResources().getIdentifier(item, "string",
					this.getPackageName());
			int id_icon = getResources().getIdentifier(menuItemsIcon[0],
					"drawable", this.getPackageName());

			NsMenuItemModel mItem = new NsMenuItemModel(id_title, id_icon);
			mAdapter.addItem(mItem);
			//	res++;
		}


		//Add Impostazioni
		mAdapter.addHeader(R.string.ns_menu_main_header_setting);
		// Add first bloc
		menuItems = getResources().getStringArray(
				R.array.ns_menu_items_setting);
		menuItemsIcon = getResources().getStringArray(
				R.array.ns_menu_items_icon);
		//res = 0;
		for (String item : menuItems) {

			int id_title = getResources().getIdentifier(item, "string",
					this.getPackageName());
			int id_icon = getResources().getIdentifier(menuItemsIcon[0],
					"drawable", this.getPackageName());

			NsMenuItemModel mItem = new NsMenuItemModel(id_title, id_icon);
			mAdapter.addItem(mItem);
			//	res++;
		}

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
			//Home Page	
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_home_page))){
				title=getString(R.string.ns_menu_home_page);
				test = new PostaIngresso();
			}
			//Rubrica Delegati
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_rubrica_delegati))){
				title=getString(R.string.title_activity_rubrica_delegati);
				test = new RubricaDelegati();

			}
			//Elenco Attivita
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_attivita_elenco))){
				title=getString(R.string.title_activity_elenco_attivita);
				test = new ElencoAttivita();
			}

			//Logout
			if(((TextView)view.findViewById(R.id.menurow_title)).getText().toString().equals(getString(R.string.ns_menu_setting_logout))){
				HashMap<String, String> data = new HashMap<String, String>();
				RichiestaLogout asd = new RichiestaLogout(data);
				asd.execute();
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
	}

	public static void enable(){
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		mDrawerToggle.setDrawerIndicatorEnabled(true);
	}


}