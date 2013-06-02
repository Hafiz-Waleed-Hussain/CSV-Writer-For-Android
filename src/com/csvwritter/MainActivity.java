package com.csvwritter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	// Views
	private EditText mFirstNameEditText = null;
	private EditText mLastNameEditText = null;
	private EditText mEmailEditText = null;
	private Button mSubmitButton = null;
	private Button mSendEmail = null;

	// Fields
	private HashMap<String, Object> ANSWER;
	boolean isAlreadyExist = false;
	private File desFile = null;
	// Constants
	private static final String CSV_HEADER[] = { "FirstName", "LastName",
			"Emmail" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.submit:
			onSubmitClick();
			break;
		case R.id.send_email:
			onSendEmmail();
			break;

		default:
			break;
		}
	}

	// Initialization

	private void init() {
		ANSWER = new HashMap<String, Object>();
		initViews();
		setUpViews();
	}

	private void initViews() {
		mFirstNameEditText = (EditText) findViewById(R.id.firstName);
		mLastNameEditText = (EditText) findViewById(R.id.lastName);
		mEmailEditText = (EditText) findViewById(R.id.email);
		mSubmitButton = (Button) findViewById(R.id.submit);
		mSendEmail = (Button) findViewById(R.id.send_email);
	}

	private void setUpViews() {
		mSubmitButton.setOnClickListener(this);
		mSendEmail.setOnClickListener(this);
	}

	// Utility Functions

	private String getText(EditText et) {
		return et.getText().toString();
	}

	private void onSendEmmail() {
		try {
			String strFile = Environment.getExternalStorageDirectory() + "/"
					+ "Android/data/" + getPackageName().toString()
					+ "/CSV.csv";

			File file = new File(strFile);
			if (!file.exists()) {
				Toast.makeText(this, "No data available", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			//
			final Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			//
			emailIntent.setType("plain/text");

			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Title");
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Content");

			emailIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://" + strFile));

			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Temp");

			startActivity(Intent.createChooser(emailIntent, "Send mail..."));

		} catch (Throwable t) {
			Toast.makeText(this, "Request failed: " + t.toString(),
					Toast.LENGTH_LONG).show();
		}

	}

	// CSV related functions

	// This function tell to CSV Writer about cell constraints. Like here we say
	// CSV Writer the FirstName is compulsory, Email is optional.

	private CellProcessor[] getProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // Cell
																				// 1
																				// constraint
																				// (Firstname)
				new NotNull(), // Cell 2 constraint (LastName)
				new Optional() // Cell 3 constraint (Email)
		};
		return processors;
	}

	// When button click
	private void onSubmitClick() {

		addData();
		sdCardHandler();
		wrietDataOnCSV();

	}

	private void wrietDataOnCSV() {
		ICsvMapWriter mapWriter = null;
		try {
			mapWriter = new CsvMapWriter(new FileWriter(desFile, true),
					CsvPreference.STANDARD_PREFERENCE);

			final CellProcessor[] processors = getProcessors();

			// write the header
			if (isAlreadyExist)
				mapWriter.writeHeader(CSV_HEADER);

			mapWriter.write(ANSWER, CSV_HEADER, processors);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (mapWriter != null) {
				try {
					mapWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void sdCardHandler() {
		// SD Card path
		File mainDirect = new File(Environment.getExternalStorageDirectory()
				+ "/" + "Android/data/" + getPackageName().toString());

		// If Directory not exist then create
		if (!mainDirect.exists())
			if (mainDirect.mkdir())
				;

		// Here we are creating CSV file on SD Card
		desFile = new File(mainDirect + "/" + "CSV.csv");

		if (!desFile.exists()) {
			// Here only i check if the file is already exist than we not write
			// header of CSV vice versa we write CSV Header
			isAlreadyExist = true;
		}

	}

	private void addData() {
		String firstName = getText(mFirstNameEditText);
		String lastName = getText(mLastNameEditText);
		String email = getText(mEmailEditText);
		ANSWER.put(CSV_HEADER[0], firstName);
		ANSWER.put(CSV_HEADER[1], lastName);
		ANSWER.put(CSV_HEADER[2], email);

	}

}
