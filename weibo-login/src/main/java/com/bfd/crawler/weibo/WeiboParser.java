package com.bfd.crawler.weibo;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public interface WeiboParser {
    Logger LOG = LoggerFactory.getLogger(WeiboParser.class);
    Gson gson = new Gson();


    Collection<?> parse(String pagedata);
}
