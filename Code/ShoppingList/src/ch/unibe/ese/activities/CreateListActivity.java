package ch.unibe.ese.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.unibe.ese.core.BaseActivity;
import ch.unibe.ese.core.ListManager;
import ch.unibe.ese.core.ShoppingList;
import ch.unibe.ese.core.sqlite.SQLiteShopAdapter;
import ch.unibe.ese.shoppinglist.R;

public class CreateListActivity extends BaseActivity {

	private ListManager manager;
	private ShoppingList list;
	private TextView textViewTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_list);

		// hide the action bar on this activity
		getActionBar().hide();
		manager = getListManager();

		// edit shopping list
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			// Get list
			int listIndex = extras.getInt("selectedList");
			list = manager.getShoppingLists().get(listIndex);
			textViewTitle = (TextView) findViewById(R.id.textViewTitle);
			textViewTitle.setText(this.getString(R.string.edit_list_title));
			setList();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_list, menu);
		return true;
	}

	private void setList() {
		// set name
		EditText textName = (EditText) findViewById(R.id.editTextName);
		textName.setText(list.getName());

		// set due date
		EditText textDate = (EditText) findViewById(R.id.editTextDate);
		Date date = list.getDueDate();
		if (date != null) {
			DateFormat dateFormat = SimpleDateFormat.getDateInstance();
			textDate.setText(dateFormat.format(date));
		}

		// set autocompletion and former shop name
		AutoCompleteTextView textShop = (AutoCompleteTextView) findViewById(R.id.editTextShop);
		SQLiteShopAdapter sqliteAdapter = new SQLiteShopAdapter(this,
				android.R.layout.simple_list_item_1);
		textShop.setAdapter(sqliteAdapter);
		textShop.setText(list.getShop());
	}

	/** Called when the user touches the abort button */
	public void goBack(View view) {
		finish();
	}

	/** Called when the user touches the save button */
	public void saveList(View view) {
		// get name
		EditText textName = (EditText) findViewById(R.id.editTextName);
		String name = textName.getText().toString();
		if (name == null || name.trim().isEmpty()) {
			Toast.makeText(this, this.getString(R.string.error_name), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (list == null)
			list = new ShoppingList(name);
		else
			list.setName(name);

		// get shop
		EditText textShop = (EditText) findViewById(R.id.editTextShop);
		String shop = textShop.getText().toString();
		list.setShop(shop);

		// get due date
		EditText textDate = (EditText) findViewById(R.id.editTextDate);
		Date date;
		try {
			date = SimpleDateFormat.getDateInstance().parse(
					textDate.getText().toString());
			list.setDueDate(date);
		} catch (ParseException e) {
			// throw new IllegalStateException(e);
		}
		
		// save the shopping list
		manager.addShoppingList(list);
		
		// TODO: maybe allow user to choose in settings if new list should open after creating
		// go back to home activity after editing the list
		//NavUtils.navigateUpFromSameTask(this);
		
		// open the created list
		Intent intent = new Intent(this, ViewListActivity.class);
		int position = manager.getShoppingLists().indexOf(list);
		intent.putExtra("selectedList", position);
		this.startActivity(intent);
	}

	/**
	 * Called when the user touches the duedate-field.
	 */
	public void showDatePickerDialog(View view) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	@SuppressLint("ValidFragment")
	private class DatePickerFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker

			final Calendar c = Calendar.getInstance();
			if (list != null && list.getDueDate() != null)
				c.setTime(list.getDueDate());
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			DatePickerDialog dialog = new DatePickerDialog(getActivity(), null,
					year, month, day);
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources()
					.getText(R.string.btn_done),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							onDateSet();
						}
					});
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources()
					.getText(R.string.btn_cancel),
					(DialogInterface.OnClickListener) null);

			return dialog;
		}

		public void onDateSet() {
			DatePicker datePicker = ((DatePickerDialog) this.getDialog())
					.getDatePicker();
			Date date = new GregorianCalendar(datePicker.getYear(),
					datePicker.getMonth(), datePicker.getDayOfMonth())
					.getTime();
			DateFormat dateFormat = SimpleDateFormat.getDateInstance();
			EditText textDate = (EditText) findViewById(R.id.editTextDate);
			textDate.setText(dateFormat.format(date));
		}
	}
}
