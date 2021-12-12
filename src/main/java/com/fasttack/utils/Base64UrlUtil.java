package com.fasttack.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64UrlUtil {

    public static String encode(String value) {
        if (value==null && "".equals(value)) return null;
        try {
            value = value.replace(".", "");
            return Base64.getUrlEncoder()
                    .encodeToString(value.getBytes(StandardCharsets.UTF_8.toString()));
        } catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String decode(String value) {
        if (value==null && "".equals(value)) return null;
        try {
            value = value.replace(".", "");
            byte[] decodedValue = Base64.getUrlDecoder()
                    .decode(value);
            return new String(decodedValue, StandardCharsets.UTF_8.toString());
        } catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
