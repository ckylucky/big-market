package com.cky.domain.activity.service.partake;

import com.cky.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.cky.domain.activity.model.entity.*;
import com.cky.domain.activity.model.valobj.UserRaffleOrderStateVO;
import com.cky.domain.activity.repository.IActivityRepository;
import com.cky.types.enums.ResponseCode;
import com.cky.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName RaffleActivityPartakeService
 * @Description
 * @Author lukcy
 * @Date 2025/1/3 20:23
 * @Version 1.0
 */
@Service
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake{
    public RaffleActivityPartakeService(IActivityRepository activityRepository) {
        super(activityRepository);
    }
    private final SimpleDateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM");
    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 创建用户抽奖订单
     * @param userId
     * @param activityId
     * @param currentDate
     * @return
     */
    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate) {
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        // 构建订单
        UserRaffleOrderEntity userRaffleOrder = new UserRaffleOrderEntity();
        userRaffleOrder.setUserId(userId);
        userRaffleOrder.setActivityId(activityId);
        userRaffleOrder.setActivityName(activityEntity.getActivityName());
        userRaffleOrder.setStrategyId(activityEntity.getStrategyId());
        userRaffleOrder.setOrderId(RandomStringUtils.randomNumeric(12));
        userRaffleOrder.setOrderTime(currentDate);
        userRaffleOrder.setOrderState(UserRaffleOrderStateVO.create);
        return userRaffleOrder;
    }
    /***
     *   账户过滤 并且构建聚合对象
     * @param userId
     * @param activityId
     * @param currentDate
     * @return
     */
    @Override
    protected CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate) {
        // 查询总账户额度
        ActivityAccountEntity activityAccountEntity =  activityRepository.queryActivityAccountByUserId(userId,activityId);
        //总额度不足
        if(activityAccountEntity==null||activityAccountEntity.getTotalCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }
        String month = dateFormatMonth.format(currentDate);
        String day = dateFormatDay.format(currentDate);
        //查询月额度
        ActivityAccountMonthEntity activityAccountMonthEntity = activityRepository.queryActivityAccountMonthByUserId(userId,activityId,month);
        if(activityAccountMonthEntity!=null&&activityAccountMonthEntity.getMonthCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
        }
        // 创建月账户额度；true = 存在月账户、false = 不存在月账户
        boolean isExistAccountMonth=null!=activityAccountMonthEntity;
        if(null==activityAccountMonthEntity){
            activityAccountMonthEntity=new ActivityAccountMonthEntity();
            activityAccountMonthEntity.setUserId(userId);
            activityAccountMonthEntity.setActivityId(activityId);
            activityAccountMonthEntity.setMonth(month);
            activityAccountMonthEntity.setMonthCount(activityAccountEntity.getMonthCount());
            activityAccountMonthEntity.setMonthCountSurplus(activityAccountEntity.getMonthCountSurplus());
        }
        //查询日额度
        ActivityAccountDayEntity activityAccountDayEntity = activityRepository.queryActivityAccountDayByUserId(userId,activityId,day);
        if(activityAccountDayEntity!=null&&activityAccountDayEntity.getDayCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
        }
        // 创建日账户额度；true = 存在日账户、false = 不存在日账户
        boolean isExistAccountDay=null!=activityAccountDayEntity;
        if(null==activityAccountDayEntity){
            activityAccountDayEntity=new ActivityAccountDayEntity();
            activityAccountDayEntity.setUserId(userId);
            activityAccountDayEntity.setActivityId(activityId);
            activityAccountDayEntity.setDay(day);
            activityAccountDayEntity.setDayCount(activityAccountEntity.getDayCount());
            activityAccountDayEntity.setDayCountSurplus(activityAccountEntity.getDayCountSurplus());
        }
        // 构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate=new CreatePartakeOrderAggregate();
        createPartakeOrderAggregate.setUserId(userId);
        createPartakeOrderAggregate.setActivityId(activityId);
        createPartakeOrderAggregate.setActivityAccountEntity(activityAccountEntity);
        createPartakeOrderAggregate.setActivityAccountDayEntity(activityAccountDayEntity);
        createPartakeOrderAggregate.setActivityAccountMonthEntity(activityAccountMonthEntity);
        createPartakeOrderAggregate.setExistAccountDay(isExistAccountDay);
        createPartakeOrderAggregate.setExistAccountMonth(isExistAccountMonth);

        return createPartakeOrderAggregate;
    }
}
