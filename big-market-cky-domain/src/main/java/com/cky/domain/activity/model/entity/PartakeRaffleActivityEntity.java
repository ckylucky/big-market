package com.cky.domain.activity.model.entity;

import lombok.Data;

/**
 * @ClassName PartakeRaffleActivityEntity
 * @Description 参与抽奖活动实体对象
 * @Author lukcy
 * @Date 2025/1/3 20:16
 * @Version 1.0
 */
@Data
public class PartakeRaffleActivityEntity {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;
}
