package com.itzixi.service;

import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

/**
 * @ClassName RssService
 * @Author duqy
 * @Version 1.0
 * @Description RssService
 **/
public interface RssService {

    public void displayRss() throws Exception;

    public List<SyndEntry> fetchRssReed(String rssUrl) throws Exception;

}
