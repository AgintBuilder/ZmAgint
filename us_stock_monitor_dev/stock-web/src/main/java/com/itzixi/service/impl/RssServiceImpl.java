package com.itzixi.service.impl;

import cn.hutool.json.JSONUtil;
import com.itzixi.entity.BaiduTransEntity;
import com.itzixi.entity.USStockMsg;
import com.itzixi.entity.USStockRss;
import com.itzixi.enums.StockTag;
import com.itzixi.service.RssService;
import com.itzixi.service.StockService;
import com.itzixi.utils.DingTalkApi;
import com.itzixi.utils.GMTDateConverter;
import com.itzixi.utils.StockTitanCrawler;
import com.itzixi.utils.TransApi;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RssServiceImpl
 * @Author duqy
 * @Version 1.0
 * @Description RssServiceImpl
 **/
@Service
public class RssServiceImpl implements RssService {

    @Resource
    private StockService stockService;

    @Resource
    private TransApi transApi;

    @Resource
    private DingTalkApi dingTalkApi;

    public static final String RSS_URL = "https://www.stocktitan.net/rss";

    @Override
    public void displayRss() throws Exception {

        List<USStockMsg> stockMsgList = new ArrayList<>();

        List<SyndEntry> rssList = this.fetchRssReed(RSS_URL);
        //        System.out.println(rssList);

        if (rssList == null || rssList.isEmpty()) {
            System.out.println("RSS 列表为空");
            return;
        }

        for (SyndEntry entry : rssList) {
            USStockRss stockNews = new USStockRss();

            // 获得股票异动信息的标题
            String title = entry.getTitle();
            String titleEn = getStockTitle(title);
            stockNews.setTitle(titleEn);

            // 获得股票异动信息的链接地址
            stockNews.setLink(entry.getLink());

            // 获得股票异动信息的发布时间（GMT时间和北京时间）
            Date gmtDateTemp = entry.getPublishedDate();
            LocalDateTime gmtDate = GMTDateConverter.convertGmt(gmtDateTemp);
            stockNews.setPubDateGmt(gmtDate);
            stockNews.setPubDateBj(GMTDateConverter.convertGmtToBeijing(gmtDateTemp));

            // 获得股票异动信息的股票代码
            String stockCode = getStockCode(title);
            stockNews.setStockCode(stockCode);

            transApi.testEnv();

            // 判断股票异动信息是否已存在，如果存在则不进行保存的操作
            if (stockService.isStockNewsExist(stockCode, stockNews.getLink())) {
//                System.out.println("股票代码为【" + stockCode + "】的已存在，跳过。。。");
                continue;
            }


            // 使用百度翻译SDK调用其API对英文标题进行翻译（翻译为中文）
            String finalTitleZh = "";

//            String result = transApi.getTransResult(titleEn, "en", "zh");
////            System.out.println("百度翻译调用的结果 result为： " +  result);
//            BaiduTransEntity transEntity = JSONUtil.toBean(result, BaiduTransEntity.class);
//            if (transEntity != null) {
//                List<BaiduTransEntity.TransResult> transResultList = transEntity.getTrans_result();
//                if (transResultList == null || transResultList.isEmpty() || transResultList.size() <= 0) {
//                    continue;
//                }
//                finalTitleZh = transResultList.get(0).getDst();
//            }

            // 处理股票标题，翻译为中文
            stockNews.setTitleZh(finalTitleZh);


            // 获得股票异动信息的标签
            try {
                List<String> tagsList = StockTitanCrawler.getTags(titleEn);
                stockNews.setTags(getTagsZh(tagsList));
            } catch (Exception e) {
//                throw new RuntimeException(e);
                // 此处因为频繁访问页面抓取数据，可能导致429的反爬虫异常，无需处理，tags无所谓，可以直接设为空值
                stockNews.setTags("");
            }

//            System.out.println(stockNews.toString());
            stockService.saveStockNews(stockNews);


            USStockMsg stockMsg = new USStockMsg();
            BeanUtils.copyProperties(stockNews, stockMsg);

            stockMsg.setPubDateBj(GMTDateConverter.getBeijingTime(gmtDateTemp));

            Long counts24Hour = stockService.getStockUnusualCounts(stockNews,
                                                                    GMTDateConverter.minus24Hour(gmtDate),
                                                                    GMTDateConverter.plus1Minute(gmtDate));
            Long counts3Day = stockService.getStockUnusualCounts(stockNews,
                                                                    GMTDateConverter.minus3Day(gmtDate),
                                                                    GMTDateConverter.plus1Minute(gmtDate));
            Long counts1Week = stockService.getStockUnusualCounts(stockNews,
                                                                    GMTDateConverter.minus1Week(gmtDate),
                                                                    GMTDateConverter.plus1Minute(gmtDate));
            stockMsg.setCounts24Hour(counts24Hour.intValue());
            stockMsg.setCounts3Day(counts3Day.intValue());
            stockMsg.setCounts1Week(counts1Week.intValue());

            System.out.println(stockMsg.toString());

            stockMsgList.add(stockMsg);
        }

        if (!stockMsgList.isEmpty()) {
            // 将消息列表按每10个一组拆分，避免单次发送消息过长
            List<List<USStockMsg>> batchList = RssServiceImpl.splitList(stockMsgList);
            for (List<USStockMsg> batch : batchList) {
                dingTalkApi.sendTextMessage(dingTalkApi.formatStockInfoFromList(batch));
            }
        }

    }

    @Override
    public List<SyndEntry> fetchRssReed(String rssUrl) throws Exception {
        URL url = new URL(rssUrl);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        return feed.getEntries();
    }

    /**
     * @Description: 处理股票标题
     * @Author duqy
     * @param title
     * @return String
     */
    private String getStockTitle(String title) {
        String[] titleArr = title.split("\\|");
        return titleArr[0].trim();
    }

    /**
     * @Description: 处理股票代码
     * @Author duqy
     * @param title
     * @return String
     */
    private String getStockCode(String title) {
        String[] titleArr = title.split("\\|");
        String stockStr = titleArr[titleArr.length - 1];

        String[] stockCodeArr = stockStr.split("Stock News");

        return stockCodeArr[0].trim();
    }

    /**
     * @Description: 从股票标签枚举中获取映射的中文标签（带有emoji）
     * @Author duqy
     * @param list
     * @return String
     */
    private String getTagsZh(List<String> list) {

        String tagStr = "";

        for (int i = 0; i < list.size(); i++) {
            String tag = list.get(i);
            tagStr += StockTag.getTagValue(tag);

            if (i < list.size() - 1) {
                tagStr += ", ";
            }
        }

        return tagStr;
    }

    /**
     * @Description: 将列表按指定大小拆分
     * @Author duqy
     * @param input 输入列表
     * @return 拆分后的列表集合
     */
    public static <T> List<List<T>> splitList(List<T> input) {
        List<List<T>> result = new ArrayList<>();
        int size = input.size();

        if (size <= 10) {
            // 直接整体放进去
            result.add(new ArrayList<>(input));
        } else {
            // 超过 10，就每 10 个拆分
            for (int i = 0; i < size; i += 10) {
                int end = Math.min(i + 10, size);
                result.add(new ArrayList<>(input.subList(i, end)));
            }
        }
        return result;
    }

}
