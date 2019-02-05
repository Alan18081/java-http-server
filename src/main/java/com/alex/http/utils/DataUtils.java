package com.alex.http.utils;

import java.util.*;

public final class DataUtils {

    private DataUtils() {}

    public static Map<String, Object> buildMap(Object[][] data) {
        Map<String, Object> map = new HashMap<>();
        for(Object[] row : data)  {
            map.put(String.valueOf(row[0]), row[1]);
        }
        return Collections.unmodifiableMap(map);
    }

    public static List<String> convertToLineList(String message) {
        return Arrays.asList(message.split("\r\n"));
    }

}
