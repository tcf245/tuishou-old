package com.bfd.crawler.utils;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyCookieJar implements CookieJar {
    private final Map<String, Map<String, Cookie>> cookieStore = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        Map<String, Cookie> cookies = cookieStore.get(httpUrl.host());
        if (cookies == null) {
            cookies = new HashMap<>();
        }
        for (Cookie c : list) {
            cookies.put(c.name(), c);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        Map<String,Cookie> cookies = cookieStore.get(httpUrl.host());

        List<Cookie> list = new ArrayList<>();
        if (cookies == null)
            return list;

        cookies.forEach((k,v) -> list.add(v));
        return list;
    }
}
