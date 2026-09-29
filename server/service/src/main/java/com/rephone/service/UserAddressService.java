package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.context.TenantContextHolder;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.UserAddressMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.dto.AdminAddressItem;
import com.rephone.pojo.entity.User;
import com.rephone.pojo.entity.UserAddress;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户地址簿：用户端 CRUD + 管理端查看/删除（隐私合规删除）。
 * 服务端校验字段长度与手机号格式；默认地址唯一（设置默认时清掉其余）。
 */
@Service
public class UserAddressService {

    private final UserAddressMapper addressMapper;
    private final UserMapper userMapper;

    public UserAddressService(UserAddressMapper addressMapper, UserMapper userMapper) {
        this.addressMapper = addressMapper;
        this.userMapper = userMapper;
    }

    // ===== 用户端 =====

    public List<UserAddress> listMine() {
        return addressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, UserContextHolder.require().userId())
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getId));
    }

    public Long create(String name, String phone, String region, String detail, boolean isDefault) {
        validate(name, phone, region, detail);
        UserAddress addr = new UserAddress();
        addr.setTenantId(TenantContextHolder.getOrDefault());
        addr.setUserId(UserContextHolder.require().userId());
        addr.setName(name.trim());
        addr.setPhone(phone.trim());
        addr.setRegion(region.trim());
        addr.setDetail(detail.trim());
        if (isDefault) {
            addressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                            .eq(UserAddress::getUserId, addr.getUserId())
                            .eq(UserAddress::getIsDefault, 1))
                    .forEach(a -> {
                        a.setIsDefault(0);
                        addressMapper.updateById(a);
                    });
            addr.setIsDefault(1);
        }
        addressMapper.insert(addr);
        return addr.getId();
    }

    public void update(Long id, String name, String phone, String region, String detail) {
        validate(name, phone, region, detail);
        UserAddress addr = requireOwned(id);
        addr.setName(name.trim());
        addr.setPhone(phone.trim());
        addr.setRegion(region.trim());
        addr.setDetail(detail.trim());
        addressMapper.updateById(addr);
    }

    public void delete(Long id) {
        addressMapper.deleteById(requireOwned(id).getId());
    }

    public void setDefault(Long id) {
        UserAddress addr = requireOwned(id);
        addressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, addr.getUserId())
                        .eq(UserAddress::getIsDefault, 1))
                .forEach(a -> {
                    a.setIsDefault(0);
                    addressMapper.updateById(a);
                });
        addr.setIsDefault(1);
        addressMapper.updateById(addr);
    }

    // ===== 管理端 =====

    public Page<AdminAddressItem> adminPage(Long userId, String keyword, long pageNum, long pageSize) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<UserAddress>()
                .orderByDesc(UserAddress::getId);
        if (userId != null && userId > 0) {
            wrapper.eq(UserAddress::getUserId, userId);
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(UserAddress::getName, kw)
                    .or().like(UserAddress::getPhone, kw)
                    .or().like(UserAddress::getRegion, kw));
        }
        Page<UserAddress> page = addressMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Map<Long, User> users = page.getRecords().isEmpty() ? Map.of()
                : userMapper.selectList(new LambdaQueryWrapper<User>()
                        .in(User::getId, page.getRecords().stream().map(UserAddress::getUserId).toList()))
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        Page<AdminAddressItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream()
                .map(a -> new AdminAddressItem(a.getId(), a.getUserId(),
                        users.containsKey(a.getUserId()) ? users.get(a.getUserId()).getNickname() : "",
                        a.getName(), a.getPhone(), a.getRegion(), a.getDetail(), a.getIsDefault(),
                        a.getCreateTime() == null ? "" : a.getCreateTime().toString()))
                .toList());
        return result;
    }

    /** 管理端删除：隐私合规（用户要求删除个人信息的场景）。 */
    public void adminDelete(Long id) {
        if (id == null || addressMapper.selectById(id) == null) {
            throw new BizException(40406, "地址不存在");
        }
        addressMapper.deleteById(id);
    }

    private UserAddress requireOwned(Long id) {
        UserAddress addr = id == null ? null : addressMapper.selectById(id);
        if (addr == null) {
            throw new BizException(40406, "地址不存在");
        }
        if (!addr.getUserId().equals(UserContextHolder.require().userId())) {
            throw new BizException(40302, "无权操作该地址");
        }
        return addr;
    }

    private void validate(String name, String phone, String region, String detail) {
        if (!StringUtils.hasText(name) || name.length() > 32) {
            throw new BizException(40061, "收件人不能为空且不超过 32 字");
        }
        if (!StringUtils.hasText(phone) || !phone.matches("1\\d{10}")) {
            throw new BizException(40062, "手机号格式不正确");
        }
        if (!StringUtils.hasText(region) || region.length() > 128) {
            throw new BizException(40063, "所在地区不能为空");
        }
        if (!StringUtils.hasText(detail) || detail.length() > 255) {
            throw new BizException(40064, "详细地址不能为空");
        }
    }
}
