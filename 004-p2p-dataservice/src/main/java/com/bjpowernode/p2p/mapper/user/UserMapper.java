package com.bjpowernode.p2p.mapper.user;

import com.bjpowernode.p2p.model.user.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 获取平台注册总人数
     * @return
     */
    Long selectAllUserCount();

    /**
     * 根据手机号码查询用户信息
     * @param phone
     * @return
     */
    User selectUserByPhone(String phone);

    /**
     * 根据手机号码和登录密码查询用户信息
     * @param user
     * @return
     */
    User selectUserByPhoneAndLoginPassword(User user);
}