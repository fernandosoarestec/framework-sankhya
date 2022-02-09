package br.com.sankhya.ibc.utils;

import javax.activation.DataSource;
import java.io.*;

public class ByteArrayDataSource implements DataSource {

    private byte[] data;
    private String mimetype;
    private String name;

    public ByteArrayDataSource(final String name, final byte[] data, final String mimetype) {
        this.data = data;
        this.mimetype = mimetype;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public String getContentType() {
        return mimetype;
    }

    @Override
    public String getName() {
        return name;
    }
}
