package com.itzixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzixi.entity.StockCounts;
import com.itzixi.entity.USStockRss;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName USStockRssMapper
 * @Author duqy
 * @Version 1.0
 * @Description 美股RSS数据访问层
 **/
public interface USStockRssMapper extends BaseMapper<USStockRss> {

    /**
     * 查询指定日期段内异动次数超过指定次数的股票
     *
     * @param map 包含 targetCounts, startDate, endDate
     * @return 股票统计列表
     */
    List<StockCounts> queryStockCountsBetweenDate(@Param("paramMap") Map<String, Object> map);

    /**
     * 根据标题关键字查询股票数据
     *
     * @param titleKeywords 标题关键字列表
     * @return 股票新闻列表
     */
    List<USStockRss> queryStockByTitleKeywords(@Param("keywords") List<String> titleKeywords);

}