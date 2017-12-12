package com.bfd.crawler;

import com.bfd.crawler.utils.OkHttpUtils;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import jdk.nashorn.internal.runtime.ParserException;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeiboLogin {
    private static Logger LOG = LoggerFactory.getLogger(WeiboLogin.class);
    private static String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36";
    private static String loginUrl = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)";

    public String getCookie(String user, String passwd){
        String su = null;
        try {
            su = Base64.encode(URLEncoder.encode(user,"utf-8").getBytes());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //输入账号之后发送这个请求， su 是 user的 URL编码后 base64   有些会直接出来验证码，有些不会
        String preLoginUrl = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su="
                + su + "&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.5)";

        String sp = "";
        String servertime = null;
        String nonce = null;
        String rasPubkey = null;
        String rsakv = null;
        String preLogin = null;
        String showpin = null;
        String pcid = null;
        String Prefix = null;

        Map<String,String> headers = new HashMap<>();
        headers.put("User-Agent", ua);
        headers.put("Accept-Language", "zh-cn,en-us;q=0.8,zh-tw;q=0.5,en;q=0.3");
        headers.put("Accept-Charset", "utf-8, GBK, GB2312;q=0.7,*;q=0.7");

        try {
            preLogin = OkHttpUtils.doGet(preLoginUrl,headers,null,0);
            System.out.println(preLogin);
            servertime = getRegexGroup("\"servertime\":(\\d+)", preLogin, 1);
            nonce = getRegexGroup("\"nonce\":\"(\\w+?)\"", preLogin, 1);
            rasPubkey = getRegexGroup("\"pubkey\":\"(\\w+?)\"", preLogin, 1);
            rsakv = getRegexGroup("\"rsakv\":\"(\\d+)\"", preLogin, 1);

            // 获取sp值   从第一个请求的返回结果中获取一些参数   其中  showpin  参数可直接判断是否需要验证码
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            FileReader reader = new FileReader("etc/login.js");
            engine.put("servertime", servertime);
            engine.put("nonce", nonce);
            engine.put("rasPubkey", rasPubkey);
            engine.put("rsakv", rsakv);
            engine.put("password", passwd);
            engine.eval(reader);
            sp = engine.get("sp").toString();
            reader.close();
            //如果 showpin = 0  则表示 该账号不需要验证码  如果  showpin=1  则表示该账号需要验证码
            showpin = getRegexGroup("\"showpin\":(\\d+)", preLogin, 1);
            Prefix = getRegexGroup("\"pcid\":\"(\\w+?)-(\\w+?)\"", preLogin, 1);
            pcid = Prefix+"-"+getRegexGroup("\"pcid\":\"(\\w+?)-(\\w+?)\"", preLogin, 2);
            LOG.info("showpin = "+ showpin);
            LOG.info("pcid = " + pcid);
            if(("1").equals(showpin)){
                System.out.println("该账号需要验证码");
                LOG.debug("该账号登录需要输入验证码！ 账号："+ user);

            }else {
                System.out.println("该账号不需要验证码，可直接登录");
                LOG.debug("该账号不需要验证码！ 账号："+ user);
            }
            Map<String, String> postParams = getLoginFormParams(su, servertime, nonce, rsakv, sp,"0");

            //发送post请求，根据返回的参数可知是否登录成功拿到cookie
            String result = OkHttpUtils.doPost(loginUrl,null,postParams,null,0);

            String location = getRegexGroup("location\\.replace\\(['\"]([\\s\\S]+?)['\"]\\)",result, 1);
            LOG.debug("location:" + location);

            if (location.contains("retcode=0")) {
                System.out.println("登录成功");

                headers.clear();
                headers.put("Referer",loginUrl);
                Response response = OkHttpUtils.doGet(location,headers);
                LOG.debug("login ok!");
                String cookie = OkHttpUtils.getCookie(response);

                return cookie;
            } else if (location.contains("retcode=4049")) {
                //次账号需要手机验证，不可用  将 needCode 改为0  status 改为 99
                System.out.println("此账号需要手机验证，不可用");
                LOG.info("cid: sina" + ", user: " + user + " need validate! == 需要手机验证");
            } else if(location.contains("retcode=101")){
                System.out.println("用户名或密码错误");
            }else{
                System.out.println("登录失败");
                LOG.warn("login failed, ");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return "";
    }

    //拼otherData
    private static String getOtherData(String ip, String ua) {
        String otherData = "";
        StringBuffer sb = new StringBuffer();
        sb.append("{\"ipflag\":\"" + ip + "\"");
        sb.append(",\"ua\":\"" + ua + "\"}");
        otherData = sb.toString();
        return otherData;
    }

    public static Map<String, String> getLoginFormParams(String su, String servertime, String nonce, String rsakv, String sp,String code) {
        Map<String, String> params = new HashMap<>();
        if(!code.equals("")) {
            params.put("door", code);
        }
        params.put("entry", "weibo");
        params.put("gateway", "1");
        params.put("from", "");
        params.put("savestate", "7");
        params.put("userticket", "1");
        params.put("pagerefer", "http://login.sina.com.cn/sso/logout.php?entry=miniblog&r=http%3A%2F%2Fweibo.com%2Flogout.php%3Fbackurl%3D%2F");
        params.put("vsnf", "1");
        params.put("su", su);
        params.put("service", "miniblog");
        params.put("servertime", servertime);
        params.put("nonce", nonce);
        params.put("pwencode", "rsa2");
        params.put("rsakv", rsakv);
        params.put("sp", sp);
        params.put("encoding", "UTF-8");
        params.put("prelt", "21");
        params.put("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
        params.put("return type", "META");
        params.put("ssosimplelogin", "1");

        return params;

    }

    public static boolean hasValue(String str) {
        return str != null && !str.trim().equals("");
    }

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


    public static void main(String[] args) {
        WeiboLogin login = new WeiboLogin();
        String cookie = login.getCookie("15620699507","chaofan85.20");

        System.out.println(cookie);
    }
}
