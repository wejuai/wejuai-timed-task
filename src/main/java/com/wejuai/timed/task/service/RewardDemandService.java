package com.wejuai.timed.task.service;

import com.wejuai.entity.mongo.SystemMessage;
import com.wejuai.entity.mysql.*;
import com.wejuai.timed.task.config.WxServiceClient;
import com.wejuai.timed.task.config.WxServiceConfig;
import com.wejuai.timed.task.repository.mongo.SystemMessageRepository;
import com.wejuai.timed.task.repository.mysql.OrdersRepository;
import com.wejuai.timed.task.repository.mysql.RewardDemandRepository;
import com.wejuai.timed.task.repository.mysql.RewardSubmissionRepository;
import com.wejuai.timed.task.repository.mysql.UserRepository;
import com.wejuai.timed.task.service.dto.WxTemplateMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.wejuai.entity.mysql.RewardDemandStatus.END;

/**
 * @author ZM.Wang
 */
@Service
public class RewardDemandService {

    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final RewardDemandRepository rewardDemandRepository;
    private final SystemMessageRepository systemMessageRepository;
    private final RewardSubmissionRepository rewardSubmissionRepository;

    private final WxServiceClient wxServiceClient;
    private final WxServiceConfig.Page page;
    private final WxServiceConfig.Msg msg;

    public RewardDemandService(OrdersRepository ordersRepository, UserRepository userRepository, RewardDemandRepository rewardDemandRepository, SystemMessageRepository systemMessageRepository, RewardSubmissionRepository rewardSubmissionRepository, WxServiceClient wxServiceClient, WxServiceConfig.Page page, WxServiceConfig.Msg msg) {
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
        this.rewardDemandRepository = rewardDemandRepository;
        this.systemMessageRepository = systemMessageRepository;
        this.rewardSubmissionRepository = rewardSubmissionRepository;
        this.wxServiceClient = wxServiceClient;
        this.page = page;
        this.msg = msg;
    }

    /** ???????????????????????? */
    @Scheduled(cron = "0 10 0 * * ?")
    public void rewardDemandDeadlineHandle() {
        List<RewardDemand> rewardDemands = rewardDemandRepository.findByDeadlineAndStatus(LocalDate.now().plusDays(-1), RewardDemandStatus.NORMAL)
                .stream().map(rewardDemand -> {
                    long submission = rewardSubmissionRepository.countByRewardDemand(rewardDemand);
                    long integral = rewardDemand.getIntegral();
                    if (submission > 0) {
                        long sendIntegral = integral / submission;
                        rewardSubmissionRepository.findByRewardDemand(rewardDemand).forEach(rewardSubmission ->
                                rewardDemandExpire(sendIntegral, rewardDemand, rewardSubmission.getUser()));
                        systemMessageRepository.save(new SystemMessage("??????????????????????????????????????????????????????", rewardDemand.getUser().getId(), false));
                    } else {
                        ordersRepository.save(new Orders(OrdersType.REWARD_DEMAND_RETURN, true, integral, rewardDemand, rewardDemand.getUser()));
                        systemMessageRepository.save(new SystemMessage("??????????????????????????????????????????", rewardDemand.getUser().getId(), false));
                    }
                    return rewardDemand.setStatus(END);
                }).collect(Collectors.toList());
        rewardDemandRepository.saveAll(rewardDemands);
    }

    /** ?????????????????????????????? */
    @Scheduled(cron = "0 0 10 * * ?")
    public void rewardDemandDeadline2Days() {
        rewardDemandRepository.findByDeadlineAndStatus(LocalDate.now().plusDays(-3), RewardDemandStatus.NORMAL).forEach(rewardDemand -> {
            WeixinUser weixinUser = rewardDemand.getUser().getAccounts().getWeixinUser();
            if (weixinUser == null || StringUtils.isBlank(weixinUser.getAppOpenId())) {
                return;
            }
            WxTemplateMsg wxTemplateMsg = new WxTemplateMsg().setTemplateId(msg.getAppRewardDemandExpire())
                    .setOpenId(weixinUser.getAppOpenId()).setUrl(page.getReward() + "?id=" + rewardDemand.getId())
                    .addDataItem("thing1", rewardDemand.getTitle())
                    .addDataItem("time2", rewardDemand.getDeadline().toString())
                    .addDataItem("number3", rewardDemand.getRewardSubmissionCount() + "")
                    .addDataItem("thing4", "????????????2???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
            wxServiceClient.sendWxTemplateMsg(wxTemplateMsg);
        });
    }

    private void rewardDemandExpire(long sendIntegral, RewardDemand rewardDemand, User recipient) {
        Orders orders = new Orders(OrdersType.REWARD_DEMAND_COMPENSATE, true, sendIntegral, rewardDemand, recipient);
        ordersRepository.save(orders);
        userRepository.save(recipient.addIntegral(sendIntegral).addMsg());
        systemMessageRepository.save(new SystemMessage("????????????????????????????????????????????????: " + sendIntegral + ", ?????????", recipient.getId(), false));
    }
}
