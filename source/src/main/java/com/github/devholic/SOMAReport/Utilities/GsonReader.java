package com.github.devholic.SOMAReport.Utilities;

/*
 * Courtesy of
 * http://www.larsmichael.net/?p=140
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;


@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class GsonReader<T> implements MessageBodyReader<T> {
 
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] antns, MediaType mt) {
        return true;
    }
 
    public T readFrom(Class<T> type, Type genericType,
            Annotation[] antns, MediaType mt,
            MultivaluedMap<String, String> mm, InputStream in)
            throws IOException, WebApplicationException {
        Gson g = new Gson();
        return g.fromJson(_convertStreamToString(in), type);
    }
 
    private String _convertStreamToString(InputStream inputStream)
            throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();
 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}