package ch.unibe.ese.shopnote.activities;

import java.util.ArrayList;

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
import ch.unibe.ese.shopnote.core.BaseActivity;
import ch.unibe.ese.shopnote.core.ListManager;
import ch.unibe.ese.shopnote.core.Recipe;
import ch.unibe.ese.shopnote.sidelist.NavigationDrawer;
import ch.unibe.ese.shopnote.R;

public class ManageRecipeActivity extends BaseActivity {
	private ListManager manager; 
	private ArrayAdapter<Recipe> recipeAdapter;
	private ArrayList<Recipe> listOfRecipes;
	private DrawerLayout drawMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_recipe);
		// Show the Up button in the action bar.
		setupActionBar();
		
		manager = getListManager();
		
		// Create drawer menu
		NavigationDrawer nDrawer = new NavigationDrawer();
		drawMenu = nDrawer.constructNavigationDrawer(drawMenu, this);
				
		updateRecipeList();
	}
	
	/**
	 * Updates the Viewlist, which shows all friends and adds itemclicklistener
	 */
	public void updateRecipeList(){
		listOfRecipes = (ArrayList<Recipe>) manager.readRecipes();
		
		recipeAdapter = new ArrayAdapter<Recipe>(this,
				R.layout.shopping_list_item, listOfRecipes );
		ListView listView = (ListView) findViewById(R.id.recipe_list);
		listView.setAdapter(recipeAdapter);
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Recipe selectedRecipe = manager.getRecipeAt(position);
				ManageRecipeActivity.this.startActionMode(new RecipeListActionMode(
						manager, selectedRecipe, recipeAdapter, ManageRecipeActivity.this));
				return true;
			}
		});
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) 
				updateRecipeList();
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
		getMenuInflater().inflate(R.menu.manage_recipe, menu);
		return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
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
				Intent createRecipeIntent = new Intent(this, CreateRecipeActivity.class);
				this.startActivityForResult(createRecipeIntent, 1);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
    	drawMenu.closeDrawers();
	}
}