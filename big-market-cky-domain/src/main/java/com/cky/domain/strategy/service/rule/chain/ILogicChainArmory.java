package com.cky.domain.strategy.service.rule.chain;

/**
 * @ClassName ILogicChainArmory
 * @Description
 * @Author lukcy
 * @Date 2024/12/18 10:10
 * @Version 1.0
 */
public interface ILogicChainArmory {
    /**
     * 追加下一个处理逻辑
     * @param next
     * @return
     */
    ILogicChain appendNext(ILogicChain next);

    /**
     * 返回当前逻辑的下一个逻辑链
     * @return
     */
    ILogicChain next();
}
