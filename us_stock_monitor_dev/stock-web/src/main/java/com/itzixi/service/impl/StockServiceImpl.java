package com.itzixi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itzixi.entity.StockCounts;
import com.itzixi.entity.USStockRss;
import com.itzixi.mapper.USStockRssMapper;
import com.itzixi.service.StockService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Redis key 前缀：股票新闻存在性缓存
     */
    private static final String STOCK_NEWS_EXIST_KEY_PREFIX = "stock:news:exist:";

    /**
     * Redis 缓存过期时间：7天
     */
    private static final long CACHE_EXPIRE_DAYS = 7;

    @Override
    public void saveStockNews(USStockRss stockNews) {
        usStockRssMapper.insert(stockNews);

        // 同步写入 Redis Set，过期时间 7 天
        String redisKey = STOCK_NEWS_EXIST_KEY_PREFIX + stockNews.getStockCode();
        stringRedisTemplate.opsForSet().add(redisKey, stockNews.getLink());
        stringRedisTemplate.expire(redisKey, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    @Override
    public Boolean isStockNewsExist(String stockCode, String link) {
        String redisKey = STOCK_NEWS_EXIST_KEY_PREFIX + stockCode;

        // 1. 先查 Redis
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(redisKey, link);
        if (Boolean.TRUE.equals(isMember)) {
            // Redis 中存在，直接返回 true
            return true;
        }

        // 2. Redis 未命中，查数据库
        QueryWrapper<USStockRss> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stock_code", stockCode);
        queryWrapper.eq("link", link);
        boolean exists = usStockRssMapper.selectCount(queryWrapper) > 0;

        // 3. 如果数据库存在，同步到 Redis（设置过期时间 7 天）
        if (exists) {
            stringRedisTemplate.opsForSet().add(redisKey, link);
            stringRedisTemplate.expire(redisKey, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
        }

        return exists;
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
