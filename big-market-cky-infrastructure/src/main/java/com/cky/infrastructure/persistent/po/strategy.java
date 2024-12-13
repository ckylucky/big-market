package com.cky.infrastructure.persistent.po;

import lombok.Data;

/**
 * @ClassName strategy
 * @Description TODO
 * @Author lukcy
 * @Date 2024/12/13 10:32
 * @Version 1.0
 */

@Data
public class strategy {

/**自增ID*/
private Long id;
/**抽奖策略id*/
private Long strategyId;
/**抽奖策略描述*/
private String strategyDesc;
/**抽奖规则类型*/
private String ruleModels;
/**创建时间*/
private String createTime;
/**更新时间*/
private String updateTime;
}
