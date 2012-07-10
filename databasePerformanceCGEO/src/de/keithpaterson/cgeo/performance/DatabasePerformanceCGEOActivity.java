package de.keithpaterson.cgeo.performance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DatabasePerformanceCGEOActivity extends Activity implements OnClickListener {


    private SQLiteDatabase databaseRO = null;
	private ArrayAdapter<String> adapter;
	private ProgressDialog waitDialog;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		waitDialog = ProgressDialog.show(this, "Running Perf Test", "Run", true);
		waitDialog.setCancelable(true);

		new AsyncTask<Void, Void, List<String>>() {

			@Override
			protected List<String> doInBackground(Void... params) {
				int tick = 0;
				while (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (tick++ > 20) {
						break;
					}
					setMessage("Wait " + tick);
				}
				ArrayList<String> log = new ArrayList<String>();
				setMessage("Open Database");
				try {
					File file = new File(Environment.getExternalStorageDirectory(), ".cgeo/data");
					boolean exists = file.exists();
					// list(file);
					databaseRO = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
							SQLiteDatabase.OPEN_READONLY);
					log.add("Opened Database");
				} catch (Exception e) {
					log.add("Error opening Database : " + e.toString());
					e.printStackTrace();
				}
				return log;
			}

			@Override
			protected void onPostExecute(final List<String> addresses) {
				waitDialog.dismiss();
				for (String string : addresses) {
					adapter.add(string);
				}
			}
		}.execute();
		ListView v = (ListView) findViewById(R.id.listView1);
		v.setAdapter(adapter);
    }

	@Override
	protected void onDestroy() {
		databaseRO.close();
		super.onDestroy();
	}

	protected void setMessage(final String message) {
		Runnable changeMessage = new Runnable() {
			@Override
			public void run() {
				// Log.v(TAG, strCharacters);
				waitDialog.setMessage(message);
			}
		};
	}

	private void list(File file) {
		String[] s = file.list();
		for (int i = 0; i < s.length; i++) {
			String string = s[i];
			File f = new File(file, string);
			if (f.isDirectory())
				list(f);
			else {
				System.out.println(f.getAbsolutePath());
			}
		}
	}

	@Override
	public void onClick(View v) {
	}
    
}