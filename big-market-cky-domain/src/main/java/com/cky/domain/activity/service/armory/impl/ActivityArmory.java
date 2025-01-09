package com.cky.domain.activity.service.armory.impl;

import com.cky.domain.activity.model.entity.ActivitySkuEntity;
import com.cky.domain.activity.repository.IActivityRepository;
import com.cky.domain.activity.service.armory.IActivityArmory;
import com.cky.domain.activity.service.armory.IActivityDispatch;
import com.cky.types.common.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ActivityArmory
 * @Description
 * @Author lukcy
 * @Date 2025/1/2 16:18
 * @Version 1.0
 */
@Service
public class ActivityArmory implements IActivityArmory, IActivityDispatch {
    @Resource
    private IActivityRepository repository;
    @Override
    public boolean assembleActivitySku(Long sku) {
        //预热sku活动库存
        ActivitySkuEntity activitySkuEntity = repository.queryActivitySku(sku);
        cacheActivitySkuStockCount(sku,activitySkuEntity.getStockCount());
        //缓存活动  查询活动时会加到缓存里
        repository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //预热活动次数【查询时预热到缓存】
        repository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        return true;
    }

    @Override
    public void assembleActivitySkuByActivityId(Long activityId) {
        List<ActivitySkuEntity> activitySkuEntities =repository.queryActivitySkuListByActivityId(activityId);
        for (ActivitySkuEntity activitySkuEntity:activitySkuEntities){
            cacheActivitySkuStockCount(activitySkuEntity.getSku(),activitySkuEntity.getStockCount());
            //预热sku活动库存
            cacheActivitySkuStockCount(activitySkuEntity.getSku(), activitySkuEntity.getStockCountSurplus());
            //预热sku对应的活动次数【查询时预热到缓存】
            repository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        }
        //缓存活动  查询活动时会加到缓存里
        repository.queryRaffleActivityByActivityId(activityId);
    }

    private void cacheActivitySkuStockCount(Long sku, Integer stockCount) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        repository.cacheActivitySkuStockCount(cacheKey, stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        return repository.subtractionActivitySkuStock(sku, cacheKey, endDateTime);
    }
}
