package org.thewheatfield.psi_brunei;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;



import android.os.AsyncTask;
import android.util.Log;

class DownloadWebPageTask extends AsyncTask<String, Void, String> {
	String mUrl = "";
	IDownloadPageCallback callback = null;
	
	public void setCallback(IDownloadPageCallback c){
		callback = c;
	}
	// data return is the result in onPostExecute()
	@Override
	protected String doInBackground(String... arg) {
		try {
			mUrl = arg[0];
		} catch (Exception e) {
		}
        StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(mUrl);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            response.append(inputLine);
			in.close();
		} catch (MalformedURLException e) {
			Log.d("Malformed", e.getMessage() + "\n" + e.getStackTrace());
		} catch (IOException e) {
			Log.d("IOException", e.getMessage() + "\n" + e.getStackTrace());
		} catch (Exception e) {
			Log.d("Exception", e.getMessage() + "\n" + e.getStackTrace());
		}		
		return response.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		if(callback != null)
			callback.process(result);
	}
}
