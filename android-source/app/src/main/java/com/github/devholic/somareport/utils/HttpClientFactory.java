package com.github.devholic.somareport.utils;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by JaeyeonLee on 2015. 8. 24..
 */
public class HttpClientFactory {

    private static DefaultHttpClient client;

    public synchronized static DefaultHttpClient getThreadSafeClient() {
        if (client != null) return client;

        else {
            client = new DefaultHttpClient();
            ClientConnectionManager manager = client.getConnectionManager();
            HttpParams params = client.getParams();
            client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, manager.getSchemeRegistry()), params);
            return client;
        }
    }

    public synchronized static void closeClient() {

    }

    public static String getEntityFromResponse(HttpResponse httpResponse) throws IOException{
        InputStream is = httpResponse.getEntity().getContent();
        StringBuilder stringBuilder = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = is.read(b)) != -1;) {
            stringBuilder.append(new String(b, 0, n));
        }
        return stringBuilder.toString();
    }
}
