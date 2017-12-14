package com.bfd.crawler.parse;

import com.bfd.crawler.weibo.Weibo;
import com.bfd.crawler.weibo.WeiboParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotParse implements WeiboParser{

    public List<Weibo> parse(String pagedata){
        String html = getHtml(pagedata);
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("div.WB_cardwrap");

        List<Weibo> weibos = new ArrayList<>();
        for (Element e : elements){
            String mid = e.attr("mid");
            String uid = e.attr("tbinfo").replace("ouid=","");

            LOG.info("get weibo mid:{} uid:{}",mid,uid);
            weibos.add(new Weibo(mid,uid));
        }

        return weibos;
    }

    /**
     * 提取html代码
     * @param pageData
     * @return
     */
    public String getHtml(String pageData){
        if(pageData == null || pageData.length() < 100)
            return "";
        String REG = "<script>FM.view\\((\\{\"ns\":\"pl.content.homeFeed.index\".+\\})\\)";
        Matcher m = Pattern.compile(REG).matcher(pageData);
        if (m.find()){
           String json = m.group(1);
            Map<String,Object> map = gson.fromJson(json,Map.class);
            return (String) map.get("html");
        }
        return "";
    }
}
