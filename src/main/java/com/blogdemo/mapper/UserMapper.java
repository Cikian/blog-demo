package com.blogdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blogdemo.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @Title: UserMapper
 * @Author Cikian
 * @Package com.blogdemo.mapper
 * @Date 2024/7/18 上午3:42
 * @description: blog-demo:
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Map<String, Object> getUserById(String userId);
    User getUserByUserName(String userName);
    int updateUser(User user);
}
