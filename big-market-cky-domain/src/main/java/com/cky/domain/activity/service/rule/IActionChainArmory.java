package com.cky.domain.activity.service.rule;

/**
 * @description
 */
public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);

}
