package com.sloy.adda.contactmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.common.collect.Lists;

import model.MyDiary;
import quickactions.ActionItem;
import quickactions.QuickAction;

import java.util.List;

public class MobileContactsAndroidActivity extends Activity {

	private MyDiary mDiary;
	private ListView mListView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mListView = (ListView)findViewById(R.id.contacts);

		((ImageButton)findViewById(R.id.ic_add)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getApplicationContext(), AddContact.class), 666);
			}
		});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
				String name = ((TextView)v.findViewById(android.R.id.text1)).getText().toString();
				String contact = mDiary.getContact(name);
				new AlertDialog.Builder(MobileContactsAndroidActivity.this).setTitle(name).setMessage(contact)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create().show();
			}
		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View view, int pos, long arg3) {
				final QuickAction qa = new QuickAction(view);
				ActionItem a1 = new ActionItem().setTitle("Add field").setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						editContact(((TextView)view.findViewById(android.R.id.text1)).getText().toString());
						qa.dismiss();
					}
				});
				ActionItem a2 = new ActionItem().setTitle("Remove").setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						removeContact(((TextView)view.findViewById(android.R.id.text1)).getText().toString());
						qa.dismiss();
					}
				});
				qa.addActionItem(a1);
				qa.addActionItem(a2);
				qa.show();
				return true;
			}
		});

		mDiary = new MyDiary();

		// fills de Diary with the data from the DB
		DataFramework db = null;
		try{
			// opens db
			db = DataFramework.getInstance();
			db.open(this, getPackageName());

			List<Entity> contacts = db.getEntityList("contacts");
			for(Entity con : contacts){
				List<Entity> infos = db.getEntityList("info", "contact_id = " + con.getId());
				for(Entity inf : infos){
					mDiary.addContact(con.getString("name"), inf.getString("type"), inf.getString("value"));
				}
			}
		}catch(Exception e){
			Log.e("ADDA", e.getMessage(), e);
		}finally{
			if(db != null){
				db.close();
			}
		}
		showMessage();
		loadContacts();
	}

	private void addContact(String name, String field, String value) {
		mDiary.addContact(name, field, value);
		// also to de DB
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			Entity con = db.getTopEntity("contacts", "name = '" + name + "'", null);
			if(con == null){
				con = new Entity("contacts");
				con.setValue("name", name);
				con.save();
			}
			Entity inf = new Entity("info");
			inf.setValue("contact_id", con.getId());
			inf.setValue("type", field);
			inf.setValue("value", value);
			inf.save();
		}catch(Exception e){
			Log.e("ADDA", e.getMessage(), e);
		}finally{
			db.close();
		}

		loadContacts();
	}

	private void editContact(String name) {
		Intent i = new Intent(this, AddContact.class).putExtra("name", name);
		startActivityForResult(i, 666);

	}

	private void removeContact(String name) {
		mDiary.deleteContact(name);
		// DB
		DataFramework db = null;
		try{
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			Entity con = db.getTopEntity("contacts", "name = '" + name + "'", null);
			if(con != null){
				con.delete();;
			}

		}catch(Exception e){
			Log.e("ADDA", e.getMessage(), e);
		}finally{
			db.close();
		}
		loadContacts();
	}

	private void loadContacts() {
		mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Lists.newArrayList(mDiary.getAgenda().keySet())));
	}

	private void showMessage() {
		SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		boolean first_start = preferences.getBoolean("firststart", true);
		if(!first_start){
			return;
		}
		new AlertDialog.Builder(this).setMessage("Welcome to the demonstration of an exercise for the ADDA League.\n\nGUI Team")
				.setPositiveButton("Let me see", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					};
				}).create().show();
		preferences.edit().putBoolean("firststart", false).commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 666 && resultCode == 1){
			addContact(data.getStringExtra("name"), data.getStringExtra("field"), data.getStringExtra("value"));
		}
	}
}