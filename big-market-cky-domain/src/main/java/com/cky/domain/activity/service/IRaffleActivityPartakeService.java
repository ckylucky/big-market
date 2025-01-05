package com.cky.domain.activity.service;

import com.cky.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.cky.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * @ClassName IRaffleActivityPartakeService
 * @Description  参与抽奖活动接口
 * @Author lukcy
 * @Date 2025/1/3 20:15
 * @Version 1.0
 */
public interface IRaffleActivityPartakeService {
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);
}
