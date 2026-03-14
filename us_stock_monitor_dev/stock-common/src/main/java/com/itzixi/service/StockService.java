package com.itzixi.service;

import com.itzixi.entity.StockCounts;
import com.itzixi.entity.USStockRss;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName StockService
 * @Author duqy
 * @Version 1.0
 * @Description 股票服务接口（统一接口，合并 stock-web 和 stock-mcp 的方法）
 **/
public interface StockService {

    // ==================== stock-web 原有方法 ====================

    /**
     * 保存股票新闻
     *
     * @param stockNews 股票新闻实体
     */
    void saveStockNews(USStockRss stockNews);

    /**
     * 判断股票新闻是否已存在
     *
     * @param stockCode 股票代码
     * @param link      新闻链接
     * @return true-已存在，false-不存在
     */
    Boolean isStockNewsExist(String stockCode, String link);

    /**
     * 获取股票在指定时间段内的异动次数
     *
     * @param stockNews 股票新闻（用于获取股票代码）
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 异动次数
     */
    Long getStockUnusualCounts(USStockRss stockNews, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== stock-mcp 原有方法 ====================

    /**
     * 根据股票代码查询股票数据
     *
     * @param stockCode 股票代码
     * @return 股票新闻列表
     */
    List<USStockRss> queryStock(String stockCode);

    /**
     * 查询时间段内的股票数据
     *
     * @param stockCode 股票代码
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate   结束日期（格式：yyyy-MM-dd）
     * @return 股票新闻列表
     */
    List<USStockRss> queryStockBetweenDate(String stockCode, String startDate, String endDate);

    /**
     * 查询指定日期段内异动次数超过指定次数的股票
     *
     * @param targetCounts 目标次数
     * @param startDate    开始日期
     * @param endDate      结束日期
     * @return 股票统计列表
     */
    List<StockCounts> queryStockCountsBetweenDate(Integer targetCounts, String startDate, String endDate);

    /**
     * 根据标题关键字查询股票数据
     *
     * @param titleKeywords 标题关键字列表
     * @return 股票新闻列表
     */
    List<USStockRss> queryStockByTitleKeywords(List<String> titleKeywords);

}