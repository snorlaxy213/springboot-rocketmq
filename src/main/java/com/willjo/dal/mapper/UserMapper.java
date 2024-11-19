package com.willjo.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.willjo.dal.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;

/**
 * 用户数据访问接口，继承自通用的基础数据访问接口
 * 该接口定义了对用户实体数据进行操作的标准方法
 *
 * @author Grio Vino
 * @since 2019-02-23
 */
public interface UserMapper extends BaseMapper<UserEntity> {
    // 由于BaseMapper<UserEntity>已经包含了CRUD等基本方法，这里不需要定义具体的操作方法
    // 实际应用中可能会根据用户业务需求添加一些特定的查询方法

    @Insert("insert into user(username,age) values( #{username}, #{age})")
    int userBatchInsert(UserEntity user1);

}