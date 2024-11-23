package com.willjo.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.willjo.dal.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<UserEntity> {
    // 由于BaseMapper<UserEntity>已经包含了CRUD等基本方法，这里不需要定义具体的操作方法
    // 实际应用中可能会根据用户业务需求添加一些特定的查询方法

    @Insert("insert into sys_user(username,age) values( #{username}, #{age})")
    int userBatchInsert(UserEntity user);

    void userBatchInsert2(@Param("users") List<UserEntity> users);
}