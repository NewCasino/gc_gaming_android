package com.jdapplications.gcgaming.tasks;

import android.os.AsyncTask;

import com.jdapplications.gcgaming.listener.OnAsyncResultListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by danielhartwich on 1/6/15.
 */
public class BlizzRaceTask extends AsyncTask<String, String, String> {
    private OnAsyncResultListener listener;

    Exception error = null;

    public BlizzRaceTask(OnAsyncResultListener listener) {
        this.listener = listener;
    }


    @Override
    protected String doInBackground(String... params) {
        String responseString = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            HttpGet getRequest = new HttpGet("http://eu.battle.net/api/wow/data/character/races");

            httpResponse = httpClient.execute(getRequest);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
                httpResponse.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            error = e1;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            error = e1;
        } catch (IOException e1) {
            e1.printStackTrace();
            error = e1;
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        if (error == null) {
            super.onPostExecute(s);
            listener.onResult(s);
        } else {
            listener.onError(error);
        }
    }
}