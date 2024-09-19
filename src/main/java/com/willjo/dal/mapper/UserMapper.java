package com.willjo.dal.mapper;

import com.willjo.dal.entity.UserEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 用户数据访问接口，继承自通用的基础数据访问接口
 * 该接口定义了对用户实体数据进行操作的标准方法
 *
 * @author lizhuo
 * @since 2019-02-23
 */
public interface UserMapper extends BaseMapper<UserEntity> {
    // 由于BaseMapper<UserEntity>已经包含了CRUD等基本方法，这里不需要定义具体的操作方法
    // 实际应用中可能会根据用户业务需求添加一些特定的查询方法
}