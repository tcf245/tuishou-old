package com.bfd.crawler.api;

import com.bfd.crawler.StringUtil;
import com.bfd.crawler.utils.OkHttpUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeiboForward {
    private static final Logger LOG = LoggerFactory.getLogger(WeiboForward.class);
    private static Gson gson = new Gson();


    /**
     * 微博转发
     * @param content
     * @param uid
     * @param cookie
     * @param mid
     */
    public void forward(String content,String uid, String cookie,String mid){
        String forwardurl = "https://weibo.com/aj/v6/mblog/forward?ajwvr=6";//&domain=" + uid + "&__rnd=" + System.currentTimeMillis();

        Map<String, String> params = getFormData(content,mid);
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
            String html = OkHttpUtils.doPost(forwardurl,headers,params,null,0);
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

    public Map<String,String> getFormData(String content,String mid){
        if (!StringUtil.hasValue(content)){
            content = "转发微博";
        }

        Map<String,String> params = new HashMap<>();
        params.put("pic_src","");
        params.put("pic_id","");
        params.put("appkey","");
        params.put("mid",mid);
        params.put("style_type","1");
        params.put("mark","");
        params.put("reason",content);
        params.put("location","v6_content_home");
        params.put("pdetail","");
        params.put("module","");
        params.put("page_module_id","");
        params.put("refer_sort","");
        params.put("rank","0");
        params.put("rankid","");
        params.put("group_source","group_all");
        params.put("rid","");
//        params.put("rid","0_0_8_3080961486612290786");
        params.put("_t","0");
        return params;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        WeiboForward forward = new WeiboForward();
        WeiboLogin login = new WeiboLogin();
        String cookie = login.getCookie("15620699507","chaofan85.20");

        System.out.println(cookie);

        String mid = "4183447508063080";


    }
}
