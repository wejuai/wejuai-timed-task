package com.wejuai.timed.task.repository.mysql;

import com.wejuai.entity.mysql.Image;
import com.wejuai.entity.mysql.ImageUploadType;
import com.wejuai.entity.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {

    List<Image> findByTypeAndUserAndCreatedAtBeforeAndIdNot(ImageUploadType type, User user, Date createdAt, String id);

    List<Image> findByTypeAndCreatedAtBeforeAndIdNotIn(ImageUploadType type, Date createdAt, List<String> ids);

    List<Image> findByTypeAndCreatedAtBefore(ImageUploadType type, Date createdAt);

}
