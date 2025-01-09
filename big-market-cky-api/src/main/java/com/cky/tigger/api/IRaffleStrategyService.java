package com.cky.tigger.api;

import com.cky.tigger.api.dao.RaffleAwardListRequestDTO;
import com.cky.tigger.api.dao.RaffleAwardListResponseDTO;
import com.cky.tigger.api.dao.RaffleStrategyRequestDTO;
import com.cky.tigger.api.dao.RaffleStrategyResponseDTO;

import com.cky.types.model.Response;
import java.util.List;

/**
 * @ClassName IRaffleService
 * @Description  抽奖服务接口
 * @Author lukcy
 * @Date 2024/12/22 9:36
 * @Version 1.0
 */
public interface IRaffleStrategyService {
    /**
     *   策略装配
     * @param strategyId  策略id
     * @return
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询抽奖奖品列表
     * @param raffleAwardListRequestDTO  定义为一个对象，好增加查询内容
     * @return
     */
    Response<List<RaffleAwardListResponseDTO>> qureyRaffleAwardList(RaffleAwardListRequestDTO raffleAwardListRequestDTO);

    /**
     * 随机抽奖接口
     *
     * @param requestDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO requestDTO);
}
