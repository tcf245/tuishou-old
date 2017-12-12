package com.bfd.crawler;

import jdk.nashorn.internal.runtime.ParserException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String getRegexGroup(String regex, String str, int id) throws ParserException {
        String resultStr = "";
        if (hasValue(str)) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(str.trim());
            if (m.find()) {
                resultStr = m.group(id);
            }
        }

        if (resultStr.equals("")) {
            throw new ParserException("str:" + str.trim().substring(0, str.trim().length() < 100 ? str.trim().length() : 100) + ",regex:" + regex + " parser error!");
        } else {
            resultStr = resultStr.trim();
            return resultStr;
        }
    }

    public static boolean hasValue(String str) {
        return str != null && !str.trim().equals("");
    }
}
