package ch.unibe.ese.shopnote.adapters;

import java.util.ArrayList;
import java.util.List;
import ch.unibe.ese.shopnote.R;
import ch.unibe.ese.shopnote.core.ShoppingList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *	Modified ArrayAdapter to display the shopping lists including the (bought) item count
 */
public class ShoppingListAdapter extends ArrayAdapter<ShoppingList> {
	
	private List<ShoppingList> shoppingLists;
	private ArrayList<Integer> boughtItemCount = new ArrayList<Integer>();
	private ArrayList<Integer> totalItemCount = new ArrayList<Integer>();
	private Context context;

	public ShoppingListAdapter(Context context, int resource, List<ShoppingList> shoppingLists) {
		super(context, resource, shoppingLists);
		this.context = context;
		this.shoppingLists = shoppingLists;
	}
	
	private class ListHolder {
		TextView name;
		ImageView isShared;
		TextView count;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ListHolder holder;
		ShoppingList list = shoppingLists.get(position);
		
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.shopping_list_item_count, null);
			holder = new ListHolder();
			holder.name = (TextView) convertView.findViewById(R.id.shopping_list_name);
			holder.isShared = (ImageView) convertView.findViewById(R.id.shopping_list_isShared);
			holder.count = (TextView) convertView.findViewById(R.id.shopping_list_count);
			convertView.setTag(holder);
		} else {
			holder = (ListHolder) convertView.getTag();
		}
		
		// set name
		holder.name.setText(list.toString());
		
		// set shared icon
		if (list.isShared())
			holder.isShared.setImageResource(R.drawable.ic_action_share);

		//set count
		if (totalItemCount.get(position) != 0)
			holder.count.setText(
					boughtItemCount.get(position) + " / " + 
					totalItemCount.get(position));
		
		return convertView;
	}
	
	/**
	 * Used to display the bought items and total number of items in a shopping
	 * list on the overview screen.
	 * 
	 * @param boughtItems
	 * @param totalItems
	 */
	public void setCount (int boughtItems, int totalItems) {
		boughtItemCount.add(boughtItems);
		totalItemCount.add(totalItems);
	}
}