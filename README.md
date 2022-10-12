# wejuai-time-task

定时任务服务

### 时间表

1. 0:10 悬赏到时间未选定结果的积分处理
2. 1:00 charge统计
3. 1:10 用户信息统计
4. 1:20 订单统计
5. 1:30 用户点数重新计算
6. 1:40 爱好热度统计空处理开始
7. 3:00 两天前的图片清理
8. 4:00 用户积分重新计算
9. 10:00 悬赏还有2天到期发送微信消息提醒用户

### 外部关联
- aliyun oss
- wejuai-weixin-service

### 配置项
- bootstrap.yml中config-server的地址配置
- config-server中对于该项目的详细配置
- `SecurityConfig.java`中对于唯一api的账号密码配置