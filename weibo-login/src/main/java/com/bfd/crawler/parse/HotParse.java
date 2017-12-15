package com.bfd.crawler.parse;

import com.bfd.crawler.api.WeiboComment;
import com.bfd.crawler.api.WeiboHot;
import com.bfd.crawler.api.WeiboLogin;
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
        LOG.info("Get html length : " + html.length());

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

    public static void main(String[] args) throws InterruptedException {
        WeiboHot hot = new WeiboHot();
        HotParse parse = new HotParse();

        WeiboLogin login = new WeiboLogin();
        WeiboComment comment = new WeiboComment();

//        String user = "17854325640";
//        String passwd = "zhverl7024s";
//        String cookie = login.getCookie(user,passwd);


        String cookie = "SINAGLOBAL=1353369883399.7266.1509197060403; UOR=,,www.google.com.hk; un=15620699507; wvr=6; SSOLoginState=1513262175; _s_tentry=-; Apache=7366691354857.087.1513343810921; ULV=1513343811066:37:12:3:7366691354857.087.1513343810921:1513172708983; YF-Page-G0=b35da6f93109faa87e8c89e98abf1260; SCF=Aq8wtjUvTd53UB4hiSATA15QHEI4aI_Hi06OlAMa704em8FtMYKnyau0BK1Nz9lx-lt94b6QjT99EjjmsmHIQmo.; SUB=_2A253N5EkDeRhGeVN61QY8y3IwjiIHXVURIXsrDV8PUNbmtBeLRaskW9NTGyiQl0PoyDdacUJon3UHQZNaoumiiOI; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW0xNGV_NHP3MZBPqe5ocIw5JpX5KMhUgL.Foe0ehq4e0eX1KB2dJLoI0YLxK-LBK-L1KBLxK-L1K5L1K2LxKqL1-eLB-2LxKqL1-BLBK-LxKqL1KMLBK-LxK-LB-BL1K5LxKBLBonLBoqt; SUHB=0FQrEhHOFmmLmY; ALF=1544885491";
        String pagedata = hot.getHot(cookie);
        List<Weibo> weibos = parse.parse(pagedata);
        LOG.info("Get weibos size : " + weibos.size());

        String content = "男款立领轻薄羽绒服 请戳：https://m.kaola.com/product/1809668.html?ts_dealer=1&ts_sharerId=00be84f9a057ee2a&ts_share=1";
        for (Weibo weibo : weibos){
            String mid = weibo.getMid();
            String uid = weibo.getUid();
            comment.comment(mid,uid,content,cookie);
            Thread.sleep(5000 );
        }

    }

}
