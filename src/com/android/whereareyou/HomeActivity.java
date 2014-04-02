package com.android.whereareyou;

import android.app.ActionBar;
import android.os.Bundle;

import android.content.Intent;

import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class HomeActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	String switchVar = "false";
	
	public final static String EDIT_TEXT1 = "com.android.whereareyou.EDIT_TEXT1";
	public final static String SWITCH1 = "com.android.whereareyou.SWITCH1";
	public final static String EDIT_TEXT3 = "com.android.whereareyou.EDIT_TEXT3";
	public final static String EDIT_TEXT4 = "com.android.whereareyou.EDIT_TEXT4";
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current drop down position.
	 */
	//private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// Set up the action bar to show a drop down list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		// Set up the drop down list navigation in the action bar.
		//actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the drop down list.
			/*	new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2),
								getString(R.string.title_section3), }), this); */
		
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current drop down position.
		//if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
		//	getActionBar().setSelectedNavigationItem(
		//			savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		//}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		//outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
			//	.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given drop down item is selected, show its contents in the
		// container view.
		/*Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();*/
		
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	/*public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}*/
	
	public void onCheckChanged(View view) {
		boolean isOn = ((Switch) view).isChecked();
		if(isOn) {
			switchVar = "true";
		}
		else {
			switchVar = "false";
		}
	}
	
	/**
	 * Build the message up to start MapActivity, send over the data in the text fields and switch.
	 * 
	 * @param view
	 */
	public void sendMessage(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		
		EditText editText1 = (EditText) findViewById(R.id.editText1);
		EditText editText3 = (EditText) findViewById(R.id.editText3);
		EditText editText4 = (EditText) findViewById(R.id.editText4);
		
		String message1 = editText1.getText().toString();
		String switchString = switchVar;
		String message3 = editText3.getText().toString();
		String message4 = editText4.getText().toString();
		
		intent.putExtra(EDIT_TEXT1, message1);
		intent.putExtra(SWITCH1, switchString);
		intent.putExtra(EDIT_TEXT3, message3);
		intent.putExtra(EDIT_TEXT4, message4);
		
		startActivity(intent);
	}

}
