package com.bfd.crawler.api;

import com.bfd.crawler.utils.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

public class WeiboHot {

    String hoturl= "https://d.weibo.com/";

    /**
     * 获得hot页面
     * @param cookie
     * @return
     */
    public String getHot(String cookie){
        String commenturl = "https://d.weibo.com/aj/v6/comment/add?ajwvr=6&__rnd=" + System.currentTimeMillis();

        Map<String,String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        headers.put("accept-encoding", "gzip, deflate, br");
        headers.put("accept-language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        headers.put("connection", "keep-alive");
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("cookie", cookie);
        headers.put("host", "weibo.com");
        headers.put("origin", "https://weibo.com");
        headers.put("referer", "https://d.weibo.com/");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        headers.put("x-requested-with", "XMLHttpRequest");

        try {
            return OkHttpUtils.doGet(commenturl,headers,null,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
