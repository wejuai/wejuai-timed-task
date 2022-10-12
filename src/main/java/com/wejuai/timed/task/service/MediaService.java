package com.wejuai.timed.task.service;

import com.endofmaster.commons.aliyun.oss.AliyunOss;
import com.endofmaster.commons.util.DateUtil;
import com.wejuai.entity.mysql.Article;
import com.wejuai.entity.mysql.Image;
import com.wejuai.entity.mysql.ImageUploadType;
import com.wejuai.entity.mysql.User;
import com.wejuai.timed.task.repository.mysql.*;
import com.wejuai.util.MediaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZM.Wang
 */
@Service
public class MediaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleDraftRepository articleDraftRepository;
    private final RewardDemandRepository rewardDemandRepository;
    private final RewardSubmissionRepository rewardSubmissionRepository;
    private final RewardSubmissionDraftRepository rewardSubmissionDraftRepository;

    private final AliyunOss aliyunOss;

    public MediaService(ImageRepository imageRepository, UserRepository userRepository, ArticleRepository articleRepository, ArticleDraftRepository articleDraftRepository, RewardDemandRepository rewardDemandRepository, RewardSubmissionRepository rewardSubmissionRepository, RewardSubmissionDraftRepository rewardSubmissionDraftRepository, AliyunOss aliyunOss) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.articleDraftRepository = articleDraftRepository;
        this.rewardDemandRepository = rewardDemandRepository;
        this.rewardSubmissionRepository = rewardSubmissionRepository;
        this.rewardSubmissionDraftRepository = rewardSubmissionDraftRepository;
        this.aliyunOss = aliyunOss;
    }

    /** 一天前的图片清理 */
    @Scheduled(cron = "0 0 3 * * ?")
    public void userImageClear() {
        logger.info("start------新生代图片清理开始-----start");
        Date before = DateUtil.getAnyDayEnd(LocalDate.now().plusDays(-1));
        Set<Image> delImages = new HashSet<>();
        for (User user : userRepository.findAll()) {
            //头像处理
            if (user.getHeadImage() != null) {
                List<Image> images = imageRepository.findByTypeAndUserAndCreatedAtBeforeAndIdNot(
                        ImageUploadType.HEAD_IMAGE, user, before, user.getHeadImage().getId());
                delImages.addAll(images);
            }
            //封面图处理
            if (user.getCover() != null) {
                List<Image> images = imageRepository.findByTypeAndUserAndCreatedAtBeforeAndIdNot(
                        ImageUploadType.COVER, user, before, user.getCover().getId());
                delImages.addAll(images);
            }
        }

        //文章内容中图片
        List<Image> articleDelImages = imageRepository.findByTypeAndCreatedAtBefore(ImageUploadType.ARTICLE, before)
                .stream().map(image -> {
                    String useUrl = MediaUtils.buildUrl(image);
                    boolean use = articleRepository.existsByTextLike("%" + useUrl + "%");
                    boolean use2 = articleDraftRepository.existsByTextLike("%" + useUrl + "%");
                    return (use || use2) ? null : image;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        delImages.addAll(articleDelImages);
        //文章封面图的id集合
        List<String> articles = articleRepository.findByCreatedAtBefore(before).stream()
                .map(Article::getCover).filter(Objects::nonNull).map(Image::getId).collect(Collectors.toList());
        if (articles.size() > 0) {
            List<Image> articleCovers = imageRepository.findByTypeAndCreatedAtBeforeAndIdNotIn(ImageUploadType.ARTICLE_HEAD, before, articles);
            delImages.addAll(articleCovers);
        }
        //悬赏需求中的图片
        List<Image> rewardDemand = imageRepository.findByTypeAndCreatedAtBefore(ImageUploadType.REWARD_DEMAND, before)
                .stream().map(image -> {
                    boolean use = rewardDemandRepository.existsByTextLike("%" + MediaUtils.buildUrl(image) + "%");
                    return use ? null : image;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        delImages.addAll(rewardDemand);
        //悬赏回答中的图
        List<Image> rewardSubmissionDraft = imageRepository.findByTypeAndCreatedAtBefore(ImageUploadType.REWARD_DEMAND_SUBMISSION, before)
                .stream().map(image -> {
                    String useUrl = MediaUtils.buildUrl(image);
                    boolean use = rewardSubmissionRepository.existsByTextLike("%" + useUrl + "%");
                    boolean use2 = rewardSubmissionDraftRepository.existsByTextLike("%" + useUrl + "%");
                    return (use || use2) ? null : image;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        delImages.addAll(rewardSubmissionDraft);

        if (delImages.size() > 0) {
            logger.info("需要清理的图片有: [{}]张", delImages.size());
            List<String> ossKeys = delImages.stream().map(Image::getOssKey).collect(Collectors.toList());
            imageRepository.deleteAll(delImages);
            aliyunOss.delete(new ArrayList<>(ossKeys));
        }
        logger.info("end------新生代图片清理结束-----end");
    }

}
