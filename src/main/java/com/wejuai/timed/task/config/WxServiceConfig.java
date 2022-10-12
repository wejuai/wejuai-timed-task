package com.wejuai.timed.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author ZM.Wang
 */
@Configuration
@EnableConfigurationProperties({WxServiceConfig.Properties.class, WxServiceConfig.Msg.class, WxServiceConfig.Page.class})
public class WxServiceConfig {

    private final Properties weixin;
    private final Msg msg;
    private final Page page;

    public WxServiceConfig(Properties weixin, Msg msg, Page page) {
        this.weixin = weixin;
        this.msg = msg;
        this.page = page;
    }

    @Bean
    WxServiceClient wxServiceClient() {
        return new WxServiceClient(weixin.getGateway());
    }

    @Validated
    @ConfigurationProperties(prefix = "weixin.page")
    public static class Page {
        @NotBlank
        private String article;
        @NotBlank
        private String reward;
        @NotBlank
        private String orders;

        public String getArticle() {
            return article;
        }

        public Page setArticle(String article) {
            this.article = article;
            return this;
        }

        public String getReward() {
            return reward;
        }

        public Page setReward(String reward) {
            this.reward = reward;
            return this;
        }

        public String getOrders() {
            return orders;
        }

        public Page setOrders(String orders) {
            this.orders = orders;
            return this;
        }
    }

    @Validated
    @ConfigurationProperties(prefix = "weixin.msg")
    public static class Msg {

        /** 悬赏到期通知 */
        @NotBlank
        private String appRewardDemandExpire;

        public String getAppRewardDemandExpire() {
            return appRewardDemandExpire;
        }

        public Msg setAppRewardDemandExpire(String appRewardDemandExpire) {
            this.appRewardDemandExpire = appRewardDemandExpire;
            return this;
        }
    }

    @Validated
    @ConfigurationProperties(prefix = "weixin")
    public static class Properties {
        @NotBlank
        private String gateway;

        public String getGateway() {
            return gateway;
        }

        public Properties setGateway(String gateway) {
            this.gateway = gateway;
            return this;
        }
    }


    public Properties getWeixin() {
        return weixin;
    }

    public Msg getMsg() {
        return msg;
    }

    public Page getPage() {
        return page;
    }


}
