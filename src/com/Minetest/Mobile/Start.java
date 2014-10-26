package com.Minetest.Mobile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.minetest.minetest.MtNativeActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.Minetest.Mobile.FREE.R;

public class Start extends Activity {
	Activity activity;
	private ProgressDialog mProgressDialog;
	String unzipLocation = Environment.getExternalStorageDirectory()
			+ "/minetest/";
	String zipFile = Environment.getExternalStorageDirectory() + "/Files.zip";
	final String URLZIP = "http://95.215.44.248/cache/mt/1.5.43/Files.zip";
	private String const_ver = "1.5.43";
	// тут перечисляешь массив плохих версий
	private String[] bad_ver = new String[] { "1.4", "1.3", "1.5" };

	String SDAllPath = "";

	public void deleteFiles(String path) {
		File file = new File(path);
		if (file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cache);
		// Initialization
		String path = "/minetest/";
		File folder = new File(Environment.getExternalStorageDirectory() + path);
		if (!(folder.exists())) {
			folder.mkdirs();
		}

		SDAllPath = folder.getPath() + "/";
		final File version = new File(SDAllPath + "ver.txt");
		final Button next = (Button) findViewById(R.id.nextbtn);
		// Starting game
		Toast.makeText(getBaseContext(), R.string.check, Toast.LENGTH_SHORT)
				.show();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				if (find_ver(version) == 0) {
					next.setVisibility(View.GONE);
					download();
				} else if (find_ver(version) == 1) {
					nnext();
				} else if (find_ver(version) == -1) {
					Toast.makeText(Start.this, R.string.bad, Toast.LENGTH_SHORT)
							.show();
					deleteFiles("/sdcard/minetest/worlds");
					Handler handler1 = new Handler();
					handler1.postDelayed(new Runnable() {
						@Override
						public void run() {
							download();
						}
					}, 3000);
				}

			}
		}, 1000); // * ms

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Toast.makeText(getBaseContext(), R.string.check,
						Toast.LENGTH_SHORT).show();
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						if (find_ver(version) == 0) {
							next.setVisibility(View.GONE);
							download();
						} else {
							if (find_ver(version) == 1) {
								nnext();
							} else if (find_ver(version) == -1) {
								Toast.makeText(Start.this, R.string.bad,
										Toast.LENGTH_SHORT).show();
								deleteFiles("/sdcard/minetest/worlds");
								Handler handler1 = new Handler();
								handler1.postDelayed(new Runnable() {
									@Override
									public void run() {
										download();
									}
								}, 3000);
								nnext();
							}
						}
					}
				}, 1000); // * ms
			}
		});
	}

	private int find_ver(File file) {
		int result = 0;
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			line = br.readLine();
		} catch (IOException e) {
		}
		if (const_ver.equals(line)) {
			result = 1;
		} else if (isBad(line)) {
			result = -1;
		} else
			result = 0;
		return result;
	}

	private boolean isBad(String line) {
		for (String item : bad_ver) {
			if (line.equals(item))
				return true;
		}
		return false;
	}

	private void nnext() {
		Toast.makeText(getBaseContext(), R.string.stgame, Toast.LENGTH_LONG)
				.show();
		Intent intent = new Intent(this, MtNativeActivity.class);
		startActivity(intent);
	}

	class DownloadZip extends AsyncTask<String, String, String> {
		String result = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(Start.this);
			mProgressDialog.setMessage(getString(R.string.download));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();

		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;

			try {
				URL url = new URL(aurl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				int lenghtOfFile = connection.getContentLength();
				int code = ((HttpURLConnection) connection).getResponseCode();
				Log.d("WTF", String.valueOf(code));
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(zipFile);
				byte data[] = new byte[1024];
				long total = 0;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}
				output.close();
				input.close();
				result = "true";

			} catch (FileNotFoundException ex) {
				Log.e("WTF", ex.getLocalizedMessage(), ex.fillInStackTrace());
			} catch (IOException e) {
				Log.e("WTF", e.getLocalizedMessage(), e.fillInStackTrace());
				result = "false";
			}
			return null;

		}

		protected void onProgressUpdate(String... progress) {
			Log.d("WTF", progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			mProgressDialog.dismiss();
			if (result.equalsIgnoreCase("true")) {
				try {
					unzip();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

			}
		}
	}

	public void unzip() throws IOException {
		mProgressDialog = new ProgressDialog(Start.this);
		mProgressDialog.setMessage(getString(R.string.unpack));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		new UnZipTask().execute(zipFile, unzipLocation);
	}

	private class UnZipTask extends AsyncTask<String, Void, Boolean> {
		@SuppressWarnings("rawtypes")
		@Override
		protected Boolean doInBackground(String... params) {
			String filePath = params[0];
			String destinationPath = params[1];

			File archive = new File(filePath);
			try {

				ZipFile zipfile = new ZipFile(archive);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, destinationPath);
				}

				UnzipUtil d = new UnzipUtil(zipFile, unzipLocation);
				d.unzip();

			} catch (Exception e) {

				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mProgressDialog.dismiss();
			File del = new File(zipFile);
			del.delete();
			nnext();
		}

		private void unzipEntry(ZipFile zipfile, ZipEntry entry,
				String outputDir) throws IOException {

			if (entry.isDirectory()) {
				createDir(new File(outputDir, entry.getName()));
				return;
			}

			File outputFile = new File(outputDir, entry.getName());
			if (!outputFile.getParentFile().exists()) {
				createDir(outputFile.getParentFile());
			}

			BufferedInputStream inputStream = new BufferedInputStream(
					zipfile.getInputStream(entry));
			BufferedOutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(outputFile));

			try {

			} finally {
				outputStream.flush();
				outputStream.close();
				inputStream.close();

			}
		}

		private void createDir(File dir) {
			if (dir.exists()) {
				return;
			}
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can not create directory " + dir);
			}
		}
	}

	private void download() {
		final Button next = (Button) findViewById(R.id.nextbtn);
		if (isNetworkAvailable()) {
			Toast.makeText(Start.this, R.string.rmold, Toast.LENGTH_SHORT)
					.show();
			/* DEL older folders */
			deleteFiles("/sdcard/minetest/cache");
			deleteFiles("/sdcard/minetest/builtin");
			deleteFiles("/sdcard/minetest/games");
			deleteFiles("/sdcard/minetest/textures");
			/* END */

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (isFileExist()) {
						File del = new File(zipFile);
						del.delete();
					}
					DownloadZip mew = new DownloadZip();
					mew.execute(URLZIP);
				}
			}, 2000); // * ms

		} else
			Toast.makeText(Start.this, R.string.disconnect, Toast.LENGTH_LONG)
					.show();
		next.setVisibility(View.VISIBLE);

	}

	private boolean isFileExist() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Files.zip");
		if (file.exists()) {
			return true;
		} else
			return false;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork
				.isConnectedOrConnecting());
	}
}