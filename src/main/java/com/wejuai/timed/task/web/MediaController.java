package com.wejuai.timed.task.web;

import com.wejuai.timed.task.service.MediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZM.Wang
 */
@Api(tags = "媒体相关")
@RestController
@RequestMapping("/task/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @ApiOperation("用户图片清理")
    @GetMapping("/image/clear")
    public void userImageClear() {
        mediaService.userImageClear();
    }
}
