package com.cky.domain.activity.service.armory;

/**
 * @ClassName IActivityArmory
 * @Description  活动装配接口
 * @Author lukcy
 * @Date 2025/1/2 16:17
 * @Version 1.0
 */
public interface IActivityArmory {
    boolean assembleActivitySku(Long sku);

    void assembleActivitySkuByActivityId(Long activity);
}
