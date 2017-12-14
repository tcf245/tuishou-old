package com.bfd.crawler.api;

import com.bfd.crawler.utils.OkHttpUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WeiboComment {
    private static final Logger LOG = LoggerFactory.getLogger(WeiboComment.class);
    private static final Gson gson = new Gson();

    /**
     * 微博评论
     * @param mid
     * @param uid
     * @param content
     * @param cookie
     */
    public void comment(String mid,String uid,String content,String cookie){
        String commenturl = "https://d.weibo.com/aj/v6/comment/add?ajwvr=6&__rnd=" + System.currentTimeMillis();

        Map<String, String> params = getFormData(mid,uid,content);
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
            String html = OkHttpUtils.doPost(commenturl,headers,params,null,0);
            Map<String,Object> result = gson.fromJson(html,Map.class);
            String code = (String) result.get("code");
            if ("100000".equals(code)){
                LOG.info("user : {} has forward weibo {} success! ", uid, mid);
            }else{
                LOG.error("user : {} has forward weibo {} fail! code is {} ", uid, mid, code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String,String> getFormData(String mid,String uid,String content){
        Map<String,String> params = new HashMap<>();
        params.put("act","post");
        params.put("mid",mid);
        params.put("uid",uid);
        params.put("forward","0");
        params.put("isroot","0");
        params.put("content",content);
        params.put("location","page_102803_ctg1_1760_-_ctg1_1760_home");
        params.put("module","scommlist");
        params.put("group_source","");
        params.put("tranandcomm","1");
        params.put("filter_actionlog","");
        params.put("pdetail","102803_ctg1_1760_-_ctg1_1760");
        params.put("_t","0");
        return params;
    }
}
