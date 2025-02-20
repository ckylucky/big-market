package com.cky.tigger.api;

import com.cky.tigger.api.dao.*;

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

    /**
     * 查询抽奖策略权重规则，给用户展示出抽奖N次后必中奖奖品范围
     *
     * @param request 请求对象
     * @return 权重奖品配置列表「这里会返回全部，前端可按需展示一条已达标的，或者一条要达标的」
     */
    Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO request);
}
