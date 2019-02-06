package com.alex.http.impl;

import com.alex.http.Constants;
import com.alex.http.config.HttpResponseWriter;
import com.alex.http.config.HttpServerConfig;
import com.alex.http.config.ReadableHttpResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DefaultHttpResponseWriter extends AbstractHttpConfigurableComponent implements HttpResponseWriter {

    DefaultHttpResponseWriter(DefaultHttpServerConfig httpServerConfig) {
        super(httpServerConfig);
    }

    @Override
    public void writeHttpResponse(OutputStream out, ReadableHttpResponse httpResponse) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)));
        addStartingLine(writer, httpResponse);
        addHeaders(writer, httpResponse);
        writer.println();
        writer.flush();
        addMessageBody(out, httpResponse);
    }

    private void addStartingLine(PrintWriter out, ReadableHttpResponse response) {
        String httpVersion = Constants.HTTP_VERSION;
        int statusCode = response.getStatus();
        String statusMessage = httpServerConfig.getStatusMessage(statusCode);
        out.println(String.format("%s %s %s", httpVersion, statusCode, statusMessage));
    }

    private void addHeaders(PrintWriter out, ReadableHttpResponse response) {
        for(Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            out.println(String.format("%s: %s", entry.getKey(), entry.getValue()));
        }
    }

    private void addMessageBody(OutputStream out, ReadableHttpResponse response) throws IOException {
        if(!response.isBodyEmpty()) {
            out.write(response.getBody());
            out.flush();
        }
    }
}
