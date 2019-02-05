package com.alex.http.config;

import java.io.IOException;
import java.io.OutputStream;

public interface HttpResponseWriter {

    void writeHttpResponse(OutputStream out, ReadableHttpResponse httpResponse) throws IOException;

}
