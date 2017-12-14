package com.bfd.crawler.api;

import com.bfd.crawler.StringUtil;
import com.bfd.crawler.utils.OkHttpUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WeiboAdd {
    private static final Logger LOG = LoggerFactory.getLogger(WeiboAdd.class);
    private static final Gson gson = new Gson();

    /**
     * 撰写微博
     * @param content
     * @param cookie
     * @return
     */
    public String add(String content,String cookie){
        String commenturl = "https://weibo.com/aj/mblog/add?ajwvr=6&__rnd=" + System.currentTimeMillis();

        Map<String, String> params = getFormData(content);
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
                LOG.info("user : {} has forward weibo {} success! ");
                return StringUtil.getRegexGroup("mid=(\\d+)",html,1);

            }else{
                LOG.error("user : {} has forward weibo {} fail! code is {} ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "4183539555347568";
    }

    public Map<String,String> getFormData(String content){
        if (!StringUtil.hasValue(content)){
            content = "只要200元就能买一个又轻，颜值又高的行李箱，真的太值了！比一般的行李箱更加的轻薄，容量也更大，短途旅行出差都没问题！万向轮顺滑，推起来也方便，还是新秀丽的制造商，大牌品质，亲民价格，果然只有严选才能做到了，厉害！链接请戳：http\",\"//m.you.163.com/item/detail?id=1113019&ts_dealer=1&ts_sharerId=00be84f9a057ee2a&channel_type=1";
        }

        Map<String,String> params = new HashMap<>();
        params.put("location","v6_content_home");
        params.put("text",content);
        params.put("appkey","");
        params.put("style_type","1");
        params.put("pic_id","");
        params.put("tid","");
        params.put("pdetail","");
        params.put("gif_ids","");
        params.put("rank","0");
        params.put("rankid","");
        params.put("module","stissue");
        params.put("pub_source","main_");
        params.put("updata_img_num","1");
        params.put("pub_type","dialog");
        params.put("isPri","undefined");
        params.put("_t","0");
        return params;
    }

}
