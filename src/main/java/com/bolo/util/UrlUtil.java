package com.bolo.util;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author wangyue
 * @Date 16:08
 */
public class UrlUtil {
    private static final Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)");

    public static String getCharset(String contentType) {
        return getCharset1(contentType);
    }
    private static String getCharset1(String contentType) {
        Matcher matcher = patternForCharset.matcher(contentType);
        if (matcher.find()) {
            String charset = matcher.group(1);
            if (Charset.isSupported(charset)) {
                return charset;
            }
        }
        return null;
    }

}
