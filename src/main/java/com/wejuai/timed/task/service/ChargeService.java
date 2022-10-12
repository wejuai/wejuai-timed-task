package com.wejuai.timed.task.service;

import com.endofmaster.commons.util.DateUtil;
import com.wejuai.entity.mongo.MerchantTransfer;
import com.wejuai.entity.mongo.statistics.ChargeStatisticsByDay;
import com.wejuai.entity.mongo.trade.Charge;
import com.wejuai.entity.mongo.trade.TradeStatus;
import com.wejuai.entity.mysql.ApplyStatus;
import com.wejuai.timed.task.repository.mongo.ChargeStatisticsByDayRepository;
import com.wejuai.timed.task.repository.mysql.WithdrawalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author ZM.Wang
 */
@Service
public class ChargeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalRepository withdrawalRepository;
    private final ChargeStatisticsByDayRepository chargeStatisticsByDayRepository;

    private final MongoBaseService mongoBaseService;

    public ChargeService(ChargeStatisticsByDayRepository chargeStatisticsByDayRepository, WithdrawalRepository withdrawalRepository, MongoBaseService mongoBaseService) {
        this.chargeStatisticsByDayRepository = chargeStatisticsByDayRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.mongoBaseService = mongoBaseService;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void statisticsCharge() {
        logger.info("start---统计每日充值参数任务开始---start");
        LocalDateTime start = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MAX);
        Criteria criteria = new Criteria()
                .and("status").is(TradeStatus.SUCCEEDED)
                .and("startedAt").gte(start).lte(end);
        long rechargeCount = mongoBaseService.getMongoCount(criteria, Charge.class);
        long rechargeAmount = mongoBaseService.getMongoSum(criteria, "amount", Charge.class);
        long merchantTransferCount = mongoBaseService.getMongoCount(criteria, MerchantTransfer.class);
        long merchantTransferAmount = mongoBaseService.getMongoSum(criteria, "amount", MerchantTransfer.class);
        long withdrawalCount = withdrawalRepository.countByStatusAndCreatedAtBetween(ApplyStatus.PASS, DateUtil.localDateTime2Date(start), DateUtil.localDateTime2Date(end));
        long withdrawalAmount = withdrawalRepository.sumIntegralByCreatedAtAndStatus(DateUtil.localDateTime2Date(start), DateUtil.localDateTime2Date(end));
        ChargeStatisticsByDay chargeStatisticsByDay = chargeStatisticsByDayRepository.findByDate(LocalDate.now().plusDays(-1));
        if (chargeStatisticsByDay == null) {
            chargeStatisticsByDay = new ChargeStatisticsByDay(rechargeCount, rechargeAmount, merchantTransferCount, merchantTransferAmount, withdrawalCount, withdrawalAmount);
        } else {
            chargeStatisticsByDay = chargeStatisticsByDay.update(rechargeCount, rechargeAmount, merchantTransferCount, merchantTransferAmount, withdrawalCount, withdrawalAmount);
        }
        chargeStatisticsByDayRepository.save(chargeStatisticsByDay);
        logger.info("end---统计每日充值参数任务结束---end");
    }

}
