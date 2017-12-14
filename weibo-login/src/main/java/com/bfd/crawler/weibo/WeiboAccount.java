package com.bfd.crawler.weibo;

public class WeiboAccount {
    private String user;
    private String passwd;
    private String cookie;

    private String ip;
    private int port;

    public WeiboAccount(String user, String passwd, String cookie, String ip, int port) {
        this.user = user;
        this.passwd = passwd;
        this.cookie = cookie;
        this.ip = ip;
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
