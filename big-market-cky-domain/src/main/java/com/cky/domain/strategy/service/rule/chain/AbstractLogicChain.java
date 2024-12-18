package com.cky.domain.strategy.service.rule.chain;

/**
 * @ClassName AbstractLogicChain
 * @Description
 * @Author lukcy
 * @Date 2024/12/17 20:07
 * @Version 1.0
 */
public abstract class AbstractLogicChain implements ILogicChain{

    private ILogicChain next;


    @Override
    public ILogicChain appendNext(ILogicChain next) {
        return this.next=next;
    }

    @Override
    public ILogicChain next() {
        return next;
    }

    protected abstract String ruleModel();
}
