package com.cky.domain.strategy.service.rule.chain;

/**
 * @ClassName ILoginChain
 * @Description 抽奖策略规则责任链接口
 * @Author lukcy
 * @Date 2024/12/17 20:03
 * @Version 1.0
 */
public interface ILogicChain extends ILogicChainArmory{
    /**
     * 具体的逻辑过滤实现
     * @param userId  用户id
     * @param strategyId  策略id
     * @return  返回奖品id
     */
    Integer logic(String userId, Long strategyId);


}
