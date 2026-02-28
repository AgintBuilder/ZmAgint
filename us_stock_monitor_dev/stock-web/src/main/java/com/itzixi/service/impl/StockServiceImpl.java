package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itzixi.entity.USStockRss;
import com.itzixi.mapper.USStockWebMapper;
import com.itzixi.service.StockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName StockServiceImpl
 * @Author duqy
 * @Version 1.0
 * @Description StockServiceImpl
 **/
@Service
public class StockServiceImpl implements StockService {

    @Resource
    private USStockWebMapper usStockWebMapper;

    @Override
    public void saveStockNews(USStockRss stockNews) {
        usStockWebMapper.insert(stockNews);
    }

    @Override
    public Boolean isStockNewsExist(String stockCode, String link) {

        QueryWrapper<USStockRss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stock_code", stockCode);
        queryWrapper.eq("link", link);

        return usStockWebMapper.selectCount(queryWrapper) > 0;
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

        return usStockWebMapper.selectCount(queryWrapper);
    }
}
