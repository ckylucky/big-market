package com.cky.domain.activity.service.partake;

import com.alibaba.fastjson.JSON;
import com.cky.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.cky.domain.activity.model.entity.ActivityEntity;
import com.cky.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.cky.domain.activity.model.entity.UserRaffleOrderEntity;
import com.cky.domain.activity.repository.IActivityRepository;
import com.cky.domain.activity.service.IRaffleActivityPartakeService;
import com.cky.types.enums.ResponseCode;
import com.cky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.Date;

/**
 * @ClassName AbstractRaffleActivityPartake
 * @Description
 * @Author lukcy
 * @Date 2025/1/3 20:19
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {
    protected final IActivityRepository activityRepository;

    public AbstractRaffleActivityPartake(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(String userId, Long activityId) {
        return createOrder(PartakeRaffleActivityEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
    }

    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        //1、参数校验
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        String userId = partakeRaffleActivityEntity.getUserId();
        Date currentDate = new Date();
        if(StringUtils.isBlank(userId)||activityId==null){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //2、活动查询
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        //3、活动信息校验
        Date beginDateTime = activityEntity.getBeginDateTime();
        Date endDateTime = activityEntity.getEndDateTime();

        //当前日期如果在开始时间之前 或者在结束时间之后
        if(beginDateTime.after(currentDate)||endDateTime.before(currentDate)){
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        //4、查询未使用的活动参与订单记录
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryNoUsedRaffleOrder(partakeRaffleActivityEntity);
        //4.1 有 直接返回
        if(userRaffleOrderEntity!= null){
            log.info("创建参与活动订单 userId:{} activityId:{} userRaffleOrderEntity:{}", userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            return userRaffleOrderEntity;
        }
        //5、额度账户过滤&返回账户构建聚合对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate =this.doFilterAccount(userId,activityId,currentDate);
        //6、补充构建订单
        UserRaffleOrderEntity userRaffleOrder =  this.buildUserRaffleOrder(userId,activityId,currentDate);
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);

        //7、保存聚合对象  一个领域内的聚合对象是一个事务 一起执行 这里包括更新账户信息 更新月 更新日 保存订单操作
        activityRepository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);
        //8、返回订单信息
        return userRaffleOrder;
    }

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate);

    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate);
}
