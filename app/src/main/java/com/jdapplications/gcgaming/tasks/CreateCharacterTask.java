package com.jdapplications.gcgaming.tasks;

import android.os.AsyncTask;

import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.models.Character;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielhartwich on 1/7/15.
 */
public class CreateCharacterTask extends AsyncTask<Void, String, String> {
    private OnAsyncResultListener listener;
    private Character myNewChar;
    Exception error = null;


    public CreateCharacterTask(OnAsyncResultListener listener, Character character) {
        this.listener = listener;
        this.myNewChar = character;
    }

    @Override
    protected String doInBackground(Void... params) {
        String responseString = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse;
            HttpPost postRequest = new HttpPost("http://api.dotards.net:3000/api/v1/characters/create");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("lastModified", String.valueOf(myNewChar.lastModified)));
            nvps.add(new BasicNameValuePair("name", myNewChar.name));
            nvps.add(new BasicNameValuePair("realm", myNewChar.realm));
            nvps.add(new BasicNameValuePair("battlegroup", myNewChar.battlegroup));
            nvps.add(new BasicNameValuePair("class", String.valueOf(myNewChar.charClass)));
            nvps.add(new BasicNameValuePair("race", String.valueOf(myNewChar.race)));
            nvps.add(new BasicNameValuePair("gender", String.valueOf(myNewChar.gender)));
            nvps.add(new BasicNameValuePair("level", String.valueOf(myNewChar.level)));
            nvps.add(new BasicNameValuePair("achievementPoints", String.valueOf(myNewChar.achievementPoints)));
            nvps.add(new BasicNameValuePair("thumbnailurl", myNewChar.thumbNailUrl));
            nvps.add(new BasicNameValuePair("itemleveltotal", String.valueOf(myNewChar.itemLevelTotal)));
            nvps.add(new BasicNameValuePair("itemlevelequipped", String.valueOf(myNewChar.itemLevelEquipped)));
            nvps.add(new BasicNameValuePair("userid", String.valueOf(myNewChar.userid)));
            postRequest.setEntity(new UrlEncodedFormEntity(nvps));

            httpResponse = httpClient.execute(postRequest);
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
