package ch.unibe.ese.shopnote.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.unibe.ese.shopnote.adapters.ItemListAdapter;
import ch.unibe.ese.shopnote.core.BaseActivity;
import ch.unibe.ese.shopnote.core.Item;
import ch.unibe.ese.shopnote.core.ListManager;
import ch.unibe.ese.shopnote.drawer.NavigationDrawer;
import ch.unibe.ese.shopnote.R;

/**
 * 	Displays all Items in the database
 */
public class ItemListActivity extends BaseActivity {
	
	
	private ListManager manager;
	private List<Item> itemList;
	private ArrayAdapter<Item> itemAdapter;
	private DrawerLayout drawMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		//get manager and all items 
		manager = getListManager();
		
		// Create drawer menu
		NavigationDrawer nDrawer = new NavigationDrawer();
		drawMenu = nDrawer.constructNavigationDrawer(drawMenu, this);
	
		updateAdapter();

	}
	
	/**
	 * Updates all adapters 
	 */
	public void updateAdapter() {
		// display items
		itemList = manager.getAllItems();
		itemAdapter = new ItemListAdapter(this, R.layout.shopping_list_item, itemList);
		ListView listView = (ListView) findViewById(R.id.ItemView);
		listView.setAdapter(itemAdapter);
		
		setItemLongClick(listView);
	}

	private void setItemLongClick(ListView listView) {
		//sets clicklistener
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Item selectedItem = itemAdapter.getItem(arg2);
				ItemListActivity.this.startActionMode(new ShoppingListActionMode(
						ItemListActivity.this.manager, selectedItem, ItemListActivity.this.itemAdapter, ItemListActivity.this));
				return true;
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.action_new:
			Intent intent = new Intent(this, CreateItemActivity.class);
			this.startActivityForResult(intent, 1);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Updates the Adapters to get the newest list when returning to activity
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) 
				updateAdapter(); 
	}
	
	@Override
	protected void onPause() {
		super.onPause();
    	drawMenu.closeDrawers();
	}
	
	public void onResume(){
    	super.onResume();
    	updateAdapter();
    }
}
