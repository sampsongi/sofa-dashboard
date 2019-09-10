package me.izhong.dashboard.manage.dao;

import me.izhong.dashboard.manage.entity.SysUserPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostDao extends MongoRepository<SysUserPost, Long> {

    void deleteAllByUserId(Long userId);

}
