package ch.unibe.ese.core.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ch.unibe.ese.core.Friend;
import ch.unibe.ese.core.Item;
import ch.unibe.ese.core.PersistenceManager;
import ch.unibe.ese.core.ShoppingList;

/**
 * This class provides function to save all lists / items / shops to a SQLite
 * database
 * 
 * @author Stephan
 * 
 */

public class SQLitePersistenceManager implements PersistenceManager {

	private final Context context;
	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private SQLiteUpdateHelper updateHelper;
	private SQLiteReadHelper readHelper;

	public void close() {
		dbHelper.close();
	}

	public SQLitePersistenceManager(Context applicationContext) {
		if (applicationContext == null)
			throw new IllegalArgumentException("null is not allowed");

		this.context = applicationContext;
		this.dbHelper = new SQLiteHelper(this.context);
		this.database = dbHelper.getWritableDatabase();
		this.readHelper = new SQLiteReadHelper(this.database);
		this.updateHelper = new SQLiteUpdateHelper(this.database,
				this.readHelper);
	}

	/**
	 * Everything for lists
	 */

	@Override
	public List<ShoppingList> getLists() {
		List<ShoppingList> lists = new ArrayList<ShoppingList>();

		Cursor cursor = readHelper.getListCursor();
		while (!cursor.isAfterLast()) {
			ShoppingList list = readHelper.cursorToShoppingList(cursor);
			lists.add(list);
			cursor.moveToNext();
		}
		cursor.close();
		return lists;
	}

	@Override
	public void save(ShoppingList list) {
		// Convert the list to a ContentValue
		// Automatically creates a new shop in the database if it doesn't exist
		ContentValues values = updateHelper.toValue(list);
		// If this is a new list
		if (list.getId() == null) {
			long id = database.insert(SQLiteHelper.TABLE_LISTS, null, values);
			list.setId(id);
		} else { // Else if it is an old list
			database.update(SQLiteHelper.TABLE_LISTS, values,
					SQLiteHelper.COLUMN_LIST_ID + " = ?", new String[] { ""
							+ list.getId() });
		}
	}

	@Override
	public void remove(ShoppingList list) {
		Long listId = list.getId();
		if (listId != null) {
			database.delete(SQLiteHelper.TABLE_ITEMTOLIST,
					SQLiteHelper.COLUMN_LIST_ID + " = ?", new String[] { ""
							+ listId });
			database.delete(SQLiteHelper.TABLE_LISTS,
					SQLiteHelper.COLUMN_LIST_ID + "= ?", new String[] { ""
							+ listId });
		}
	}

	/**
	 * Everything for Items
	 */

	@Override
	public List<Item> getItems(ShoppingList list) {
		List<Item> itemList = new ArrayList<Item>();
		Cursor cursor = readHelper.getItemCursor(list);
		while (!cursor.isAfterLast()) {
			Item item = readHelper.cursorToItem(cursor);
			itemList.add(item);
			cursor.moveToNext();
		}
		cursor.close();
		return itemList;
	}

	public ArrayList<Item> getAllItems() {
		ArrayList<Item> itemList = new ArrayList<Item>();
		Cursor cursor = readHelper.getItemCursor();
		while (!cursor.isAfterLast()) {
			Item item = readHelper.cursorToItem(cursor);
			itemList.add(item);
			cursor.moveToNext();
		}
		// TODO: Restructer database so that items can be loaded just with the
		// code above.
		cursor = readHelper.getItemTableCursor();
		while (!cursor.isAfterLast()) {
			Item item = readHelper.cursorToItemLite(cursor);
			for (Item compare : itemList)
				if (item != null)
					if (compare.getName().equals(item.getName()))
						item = null;
			if (item != null)
				itemList.add(item);
			cursor.moveToNext();
		}

		cursor.close();

		return itemList;
	}

	@Override
	public void save(Item item, ShoppingList list) {
		// Add the item to the Items Table
		updateHelper.addItemIfNotExistent(item);
		// Add the item to the ItemtoList Table
		ContentValues values = updateHelper.toValue(item, list);
		if (readHelper.isInList(item, list)) {
			Long listId = list.getId();
			database.update(SQLiteHelper.TABLE_ITEMTOLIST, values,
					SQLiteHelper.COLUMN_ITEM_ID + "= ? AND "
							+ SQLiteHelper.COLUMN_LIST_ID + "=?", new String[] {
							"" + item.getId(), "" + listId });
		} else {
			database.insert(SQLiteHelper.TABLE_ITEMTOLIST, null, values);
		}

	}

	@Override
	public void remove(Item item, ShoppingList list) {
		Long listId = list.getId();
		if (readHelper.isInList(item, list)) {
			database.delete(SQLiteHelper.TABLE_ITEMTOLIST,
					SQLiteHelper.COLUMN_ITEM_ID + "=? AND "
							+ SQLiteHelper.COLUMN_LIST_ID + "=?", new String[] {
							"" + item.getId(), "" + listId });
		}
	}

	@Override
	public void save(Item item) {
		ContentValues values = updateHelper.toValue(item);
		long id = 0;
		if (readHelper.isInList(item)) {
			id = item.getId();
			database.update(SQLiteHelper.TABLE_ITEMS, values,
					SQLiteHelper.COLUMN_ITEM_ID + "=? ",
					new String[] { "" + id });
		} else {
			id = database.insert(SQLiteHelper.TABLE_ITEMS, null, values);
			item.setId(id);
		}
	}

	@Override
	public void remove(Item item) {
		if (readHelper.isInList(item)) {
			database.delete(SQLiteHelper.TABLE_ITEMTOLIST,
					SQLiteHelper.COLUMN_ITEM_ID + "=? ", new String[] { ""
							+ item.getId() });

			database.delete(SQLiteHelper.TABLE_ITEMS,
					SQLiteHelper.COLUMN_ITEM_ID + "=? ", new String[] { ""
							+ item.getId() });
		}
	}

	/**
	 * Everything for friends
	 */

	@Override
	public ArrayList<Friend> getFriends() {
		ArrayList<Friend> list = new ArrayList<Friend>();

		Cursor cursor = readHelper.getFriendCursor();
		while (!cursor.isAfterLast()) {
			Friend friend = readHelper.cursorToFriend(cursor);
			list.add(friend);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

	public void save(Friend friend) {
		// Convert the friend to a ContentValue
		ContentValues values = updateHelper.toValue(friend);
		// If this is a new friend
		if (readHelper.getFriendName(friend.getPhoneNr()) == null) {
			database.insert(SQLiteHelper.TABLE_FRIENDS, null, values);
		} else { // Else if it is an old friend
			database.update(
					SQLiteHelper.TABLE_FRIENDS,
					values,
					SQLiteHelper.COLUMN_FRIEND_PHONENR + "="
							+ friend.getPhoneNr(), null);
		}
	}

	@Override
	public void removeFriend(Friend friend) {
		int friendNr = readHelper.getFriendNr(friend.getName());
		if (friendNr != -1) {
			database.delete(SQLiteHelper.TABLE_FRIENDS,
					SQLiteHelper.COLUMN_FRIEND_PHONENR + "= ?",
					new String[] { "" + friendNr });
		}

	}
}
