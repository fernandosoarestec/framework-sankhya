package br.com.ssa.agent.ctf.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Map;

/**
 * Created by Henrique Eichler on 02/06/2017.
 */
public class Http {

    private URL url;

    public Http(final String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public static void setProxy(final String host, final int port, final String user, final String pass) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", "" + port);
        System.setProperty("http.proxyUser", user);
        System.setProperty("http.proxyPassword", pass);
    }

    public byte[] post(final Map<String, String> properties, final byte[] post) throws IOException {
        final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        httpURLConnection.setRequestMethod("POST");
        for (final Map.Entry<String, String> entry : properties.entrySet()) {
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        httpURLConnection.setRequestProperty("Content-Length", Integer.toString(post.length));

        httpURLConnection.getOutputStream().write(post);
        final int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == 200) {
            return read(httpURLConnection.getInputStream());
        } else {
            throw new IOException(responseCode + " Response Code");
        }
    }

    private byte[] read(final InputStream inputStream) throws IOException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];

            int read = 0;
            while ((read = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }
}