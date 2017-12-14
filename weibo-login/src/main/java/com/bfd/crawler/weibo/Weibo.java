package com.bfd.crawler.weibo;

public class Weibo {
    private String mid;
    private String uid;

    public Weibo(String mid, String uid) {
        this.mid = mid;
        this.uid = uid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
