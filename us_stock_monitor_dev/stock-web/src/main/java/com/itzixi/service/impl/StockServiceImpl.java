package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itzixi.entity.StockCounts;
import com.itzixi.entity.USStockRss;
import com.itzixi.mapper.USStockRssMapper;
import com.itzixi.service.StockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @ClassName StockServiceImpl
 * @Author duqy
 * @Version 1.0
 * @Description StockServiceImpl
 **/
@Service
public class StockServiceImpl implements StockService {

    @Resource
    private USStockRssMapper usStockRssMapper;

    @Override
    public void saveStockNews(USStockRss stockNews) {
        usStockRssMapper.insert(stockNews);
    }

    @Override
    public Boolean isStockNewsExist(String stockCode, String link) {

        QueryWrapper<USStockRss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stock_code", stockCode);
        queryWrapper.eq("link", link);

        return usStockRssMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Long getStockUnusualCounts(USStockRss stockNews, LocalDateTime startDate, LocalDateTime endDate) {

        String stockCode = stockNews.getStockCode();

        QueryWrapper<USStockRss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stock_code", stockCode);

//        数据库中的日期时间格式： yyyy-MM-dd HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDateStr = formatter.format(startDate);
        String endDateStr = formatter.format(endDate);

        queryWrapper.ge("pub_date_gmt", startDateStr);
        queryWrapper.le("pub_date_gmt", endDateStr);

        return usStockRssMapper.selectCount(queryWrapper);
    }

    // ==================== stock-mcp 的方法（在 stock-web 中不支持） ====================

    @Override
    public List<USStockRss> queryStock(String stockCode) {
        throw new UnsupportedOperationException("stock-web 模块不支持此方法，请使用 stock-mcp 模块");
    }

    @Override
    public List<USStockRss> queryStockBetweenDate(String stockCode, String startDate, String endDate) {
        throw new UnsupportedOperationException("stock-web 模块不支持此方法，请使用 stock-mcp 模块");
    }

    @Override
    public List<StockCounts> queryStockCountsBetweenDate(Integer targetCounts, String startDate, String endDate) {
        throw new UnsupportedOperationException("stock-web 模块不支持此方法，请使用 stock-mcp 模块");
    }

    @Override
    public List<USStockRss> queryStockByTitleKeywords(List<String> titleKeywords) {
        throw new UnsupportedOperationException("stock-web 模块不支持此方法，请使用 stock-mcp 模块");
    }
}
