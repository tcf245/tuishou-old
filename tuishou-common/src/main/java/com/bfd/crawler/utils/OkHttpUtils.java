package com.bfd.crawler.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by BFD_303 on 2017/7/7.
 */
public class OkHttpUtils {
    private static final Logger LOG = LoggerFactory.getLogger(OkHttpUtils.class);

    private static final CookieJar cookieJar = new MyCookieJar();
    private static final OkHttpClient dClient = new OkHttpClient().newBuilder()
            .cookieJar(cookieJar)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * execute request and return html(String) with guess encode.
     * @param request
     * @param client
     * @return
     * @throws Exception
     */
    private static String doExecute(Request request, OkHttpClient client) throws Exception{
        if (client == null)
            client = dClient;

        Response response = null;
        try{
            response = client.newCall(request).execute();
            LOG.info(request.url() + " => get status code is " + response.code());

            byte[] bytes = response.body().bytes();
            String encode = EncodeUtil.getHtmlEncode(bytes);
            LOG.info("get encode : " + encode);
            return new String(bytes,encode);
        }finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * use okhttpclient to send a get request
     *
     * @param url
     * @param headers
     * @param ip
     * @param port
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String,String> headers,String ip,int port) throws Exception {
        OkHttpClient client ;
        if (ip == null)
            client = dClient;
        else
        client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip,port)))
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doExecute(request,client);
    }

    public static String doPost(String url, Map<String,String> headers,Map<String,String> params, String ip, int port) throws Exception {
        OkHttpClient client;
        if (ip == null)
            client = dClient;
        else
            client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip,port)))
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        FormBody.Builder bodyBuilder= new FormBody.Builder();
        if (params != null){
            params.forEach((k,v) -> bodyBuilder.add(k,v));
        }
        RequestBody body = bodyBuilder.build();

        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return doExecute(request,client);
    }

    public static byte[] doPostByte(String url,Map<String,String> headers,Map<String,String> params,String ip, int port) throws IOException {
        OkHttpClient client;
        if (ip == null)
            client = dClient;
        else
            client = dClient.newBuilder()
                .cookieJar(cookieJar)
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip,port)))
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        FormBody.Builder bodyBuilder= new FormBody.Builder();
        if (params != null){
            params.forEach((k,v) -> bodyBuilder.add(k,v));
        }
        RequestBody body = bodyBuilder.build();

        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();

        Response response = null;
        try{
            response = client.newCall(request).execute();
            System.out.println(request.url() + " => get status code is " + response.code());
            return response.isSuccessful() ? response.body().bytes() : null;
        }finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * do post request and return Response
     * @param url
     * @param headers
     * @param params
     * @return
     * @throws Exception
     */
    public static Response doPost(String url, Map<String,String> headers,Map<String,String> params) throws Exception {
        OkHttpClient client = dClient;

        FormBody.Builder bodyBuilder= new FormBody.Builder();
        if (params != null){
            params.forEach((k,v) -> bodyBuilder.add(k,v));
        }
        RequestBody body = bodyBuilder.build();

        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null && headers.size() > 0){
            headers.forEach((k,v) -> builder.addHeader(k,v));
        }
        Request request = builder.build();
        return client.newCall(request).execute();
    }

    /**
     * execute get request
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    public static Response doGet(String url,Map<String,String> headers) throws IOException {
        Request.Builder rBuilder = new Request.Builder().url(url);

        if (headers != null){
            headers.forEach((k,v) -> rBuilder.addHeader(k,v));
        }

        Request request = rBuilder.build();
        Response response = null;

        return dClient.newCall(request).execute();
    }

    /**
     * get Set-Cookie header from respinse.
     * @param response
     * @return
     */
    public static String getCookie(Response response){
        try {
            List<Cookie> cookies = new ArrayList<>();
            Headers headers = response.headers();
            List<String> cookieStrings = headers.values("Set-Cookie");
            for (String s : cookieStrings){
                Cookie c = Cookie.parse(response.request().url(),s);
                cookies.add(c);
            }
            return cookies.stream().map(Cookie::toString).collect(Collectors.joining(";"));
        }finally {
            response.close();
        }
    }


    public static void main(String[] args) throws Exception {
        String content = doGet("http://www.weibo.com",null,null,0);
        System.out.println(content);
    }
}
