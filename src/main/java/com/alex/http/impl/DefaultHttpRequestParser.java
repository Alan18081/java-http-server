package com.alex.http.impl;

import com.alex.http.Constants;
import com.alex.http.HttpRequest;
import com.alex.http.config.HttpRequestParser;
import com.alex.http.exception.BadRequestException;
import com.alex.http.exception.HttpServerException;
import com.alex.http.exception.HttpVersionNotSupportedException;
import com.alex.http.exception.MethodNotAllowedException;
import com.alex.http.utils.DataUtils;
import com.alex.http.utils.HttpUtils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DefaultHttpRequestParser implements HttpRequestParser {
    @Override
    public HttpRequest parseHttpRequest(InputStream in, String remoteAddress) throws IOException, HttpServerException {
        String startingLine = null;
        try {
            System.out.println(in);
            ParsedRequest request = parseInputStream(in);
            return convertParsedRequestToHttpRequest(request, remoteAddress);
        } catch (RuntimeException e) {
            if(e instanceof HttpServerException) {
                throw e;
            } else {
                throw new BadRequestException("Can't parse http request: " + e.getMessage(), e, startingLine);
            }
        }
    }

    protected ParsedRequest parseInputStream(InputStream inputStream) throws IOException {
        String startingLineAndHeaders = HttpUtils.readStartingLinesAndHeaders(inputStream);
        int contentLengthIndex = HttpUtils.getContentLengthIndex(startingLineAndHeaders);
        if(contentLengthIndex != -1) {
            int contentLength = HttpUtils.getContentLengthValue(startingLineAndHeaders, contentLengthIndex);
            String messageBody = HttpUtils.readMessageBody(inputStream, contentLength);
            return new ParsedRequest(startingLineAndHeaders, messageBody);
        } else {
            return new ParsedRequest(startingLineAndHeaders, null);
        }
    }

    protected HttpRequest convertParsedRequestToHttpRequest(ParsedRequest request, String remoteAddress) throws IOException {
        String[] startingLineData = request.getStartingLine().split(" ");
        String method = startingLineData[0];
        String uri = startingLineData[1];
        String httpVersion = startingLineData[2];
        validateHttpVersion(request.getStartingLine(), httpVersion);

        Map<String, String> headers = parseHeaders(request.getHeadersLine());
        ProcessedUri processedUri = extractParametersIfPresent(method, uri, httpVersion, request.getMessageBody());
        return new DefaultHttpRequest(method, processedUri.uri, httpVersion, remoteAddress, headers, processedUri.parameters);
    }

    protected Map<String, String> parseHeaders(List<String> list) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        String prevName = null;
        for(String headerItem : list) {
            prevName = putHeader(prevName, map, headerItem);
        }
        return map;
    }

    protected String putHeader(String prevName, Map<String, String> map, String header) {
        if(header.charAt(0) == ' ') {
            String value = map.get(prevName) + header.trim();
            map.put(prevName, value);
            return prevName;
        } else {
            String[] pair = header.split(":");
            String name = HttpUtils.normalizeHeaderName(pair[0]);
            map.put(name, pair[1]);
            return name;
        }
    }

    protected ProcessedUri extractParametersIfPresent(String method, String uri, String httpVersion, String messageBody) throws IOException {
        Map<String, String> parameters = Collections.emptyMap();
        if(Constants.GET.equalsIgnoreCase(method) || Constants.HEAD.equalsIgnoreCase(method)) {
            int indexOfDelim = uri.indexOf('?');
            if(indexOfDelim != -1) {
                return extractParametersFromUri(uri, indexOfDelim);
            }
        } else if(Constants.POST.equalsIgnoreCase(method)) {
            if(messageBody != null && !"".equals(messageBody)) {
                parameters = getParameters(messageBody);
            }
        } else {
            throw new MethodNotAllowedException(method, String.format("%s %s %s", method, uri, httpVersion));
        }
        return new ProcessedUri(uri, parameters);
    }

    protected ProcessedUri extractParametersFromUri(String uri, int indexOfDelim) throws UnsupportedEncodingException {
        String paramString = uri.substring(indexOfDelim);
        Map<String, String> parameters = getParameters(paramString);
        return new ProcessedUri(uri.substring(0 ,indexOfDelim), parameters);
    }

    protected Map<String, String> getParameters(String paramString) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] params = paramString.split("&");
        for(String param : params) {
            String[] items = param.split("=");
            if(items.length == 1) {
                items = new String[] { items[0], "" };
            }
            String name = items[0];
            String value = map.get(name);
            if(value != null) {
                value += "," + URLDecoder.decode(items[1], StandardCharsets.UTF_8.name());
            } else {
                value = URLDecoder.decode(items[1], StandardCharsets.UTF_8.name());
            }
            map.put(name, value);
        }
        return map;
    }


    protected void validateHttpVersion(String startingLine, String httpVersion) {
        if(!Constants.HTTP_VERSION.equals(httpVersion)) {
            throw new HttpVersionNotSupportedException("Current server supports only " + Constants.HTTP_VERSION + " protocol", startingLine);
        }
    }

    private static class ParsedRequest {
        private final String startingLine;
        private final List<String> headersLine;
        private final String messageBody;

        ParsedRequest(String startingLineAndHeaders, String messageBody) {
            super();
            List<String> list = DataUtils.convertToLineList(startingLineAndHeaders);
            System.out.println("Some list" + list);
            this.startingLine = list.remove(0);
            if(list.isEmpty()) {
                this.headersLine = Collections.emptyList();
            } else {
                this.headersLine = Collections.unmodifiableList(list);
            }
            this.messageBody = messageBody;
        }

        String getStartingLine() {
            return startingLine;
        }

        List<String> getHeadersLine() {
            return headersLine;
        }

        String getMessageBody() {
            return messageBody;
        }
    }

    private static class ProcessedUri {
        final String uri;
        final Map<String, String> parameters;

        ProcessedUri(String uri, Map<String, String> parameters) {
            super();
            this.uri = uri;
            this.parameters = parameters;
        }
    }
}
