package com.bfd.crawler;

import com.bfd.crawler.api.*;
import com.bfd.crawler.utils.OkHttpUtils;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final String proxyurl = "http://123.56.17.130:8080/proxy?id=crawler";


    public static void main(String[] args) {

        List<Map<String,Object>> proxies = new ArrayList<>();
        try {
            String result = OkHttpUtils.doGet(proxyurl,null,null,0);
            proxies = new Gson().fromJson(result,List.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (Map<String,Object> p : proxies){

        }

        String mid = "4183539555347568";

        WeiboAdd add = new WeiboAdd();
        WeiboComment comment = new WeiboComment();
        WeiboForward forward = new WeiboForward();
        WeiboLike like = new WeiboLike();
        WeiboLogin login = new WeiboLogin();
        WeiboObjectlike objectlike = new WeiboObjectlike();

        try {
            List<String> lines = FileUtils.readLines(new File("etc/account.txt"),"utf-8");
            for (String l : lines) {
                String[] strs = l.split("----");
                String user = strs[0];
                String passwd = strs[1];
                String uid = strs[3];

                String cookie = login.getCookie(user,passwd);
                if (cookie.length() > 10){
                    String string = user + "----" + passwd + "----" + cookie + "\n";
                    FileUtils.writeStringToFile(new File("etc/cookie.txt"),string,"utf-8",true);
                }

                add.add("",cookie);
                forward.forward("超级推荐",uid,cookie,mid);
                like.like(mid,"",cookie);
                String content = "真心非常不错，推荐!";
                comment.comment(mid,uid,content,cookie);

                Thread.sleep(3000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
