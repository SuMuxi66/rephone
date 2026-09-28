package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.entity.User;
import com.rephone.service.dto.LoginResult;
import com.rephone.wechat.model.WxSession;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 用户服务：wx 登录即注册。 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final WxSessionCacheService sessionCache;

    public UserService(UserMapper userMapper, WxSessionCacheService sessionCache) {
        this.userMapper = userMapper;
        this.sessionCache = sessionCache;
    }

    /**
     * 按 openid 查找用户，不存在则注册。
     * 登录链路无租户上下文（TenantLineHandler ignore），查询不受租户过滤影响。
     */
    @Transactional
    public LoginResult loginOrRegister(WxSession session) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, session.openid())
                .last("limit 1"));
        boolean newUser = false;
        if (user == null) {
            user = new User();
            user.setTenantId(0L);
            user.setOpenid(session.openid());
            user.setNickname("机友" + ThreadLocalRandom.current().nextInt(100000, 999999));
            user.setAvatarUrl("");
            user.setGender(0);
            user.setStatus(1);
            userMapper.insert(user);
            newUser = true;
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(40301, "账号已被禁用");
        }
        sessionCache.save(user.getOpenid(), session.sessionKey());
        return new LoginResult(user, newUser);
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
