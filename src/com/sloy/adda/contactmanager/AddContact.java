package com.sloy.adda.contactmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class AddContact extends Activity {

	private TextView title;
	private EditText txtName, txtValue, txtNewField;
	private Spinner spField;
	private View newfield;
	private String[] fieldArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newcontact);

		fieldArray = getResources().getStringArray(R.array.fields_array);

		title = (TextView)findViewById(R.id.title);
		txtName = (EditText)findViewById(R.id.name_txt);
		txtValue = (EditText)findViewById(R.id.value_txt);
		txtNewField = (EditText)findViewById(R.id.fieldname_txt);
		newfield = findViewById(R.id.newfield);
		spField = (Spinner)findViewById(R.id.field_sp);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fields_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spField.setAdapter(adapter);
		
		spField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int pos, long arg3) {
				if(pos == spField.getCount() - 1){
					// other
					newfield.setVisibility(View.VISIBLE);
				}else{
					newfield.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		String name = getIntent().getStringExtra("name");
		if(name != null){
			// we edit
			title.setText("Add field");
			txtName.setText(name);
			txtName.setEnabled(false);
		}

		((ImageButton)findViewById(R.id.ic_ok)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = txtName.getText().toString();
				if(name.endsWith(" ")){
					name = name.substring(0, name.length() - 1);
				}
				String field = null;
				if(newfield.getVisibility() == View.VISIBLE){
					// newfield
					field = txtNewField.getText().toString();
				}else{
					field = fieldArray[spField.getSelectedItemPosition()];
				}
				String value = txtValue.getText().toString();

				Intent i = new Intent();
				i.putExtra("name", name);
				i.putExtra("field", field);
				i.putExtra("value", value);
				setResult(1, i);
				finish();
			}
		});
		((ImageButton)findViewById(R.id.ic_discard)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});
	}

}
