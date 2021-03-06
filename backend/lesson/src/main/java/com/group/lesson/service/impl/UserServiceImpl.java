package com.group.lesson.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.group.lesson.entity.User;
import com.group.lesson.mapper.UserMapper;
import com.group.lesson.service.UserService;
import com.group.lesson.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: hwj
 * @Date: 2021/9/8 13:19
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private HttpServletRequest request;
    @Override
    public List<UserVo> getAllUser(Integer pageNum) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.select("username","registryDate","email","mobile");
        queryWrapper.last("limit "+(pageNum-1)*10+",10");
        List<User> users = userMapper.selectList(queryWrapper);
        ArrayList<UserVo> userVos = new ArrayList<>();
        for (User user:users){
            UserVo userVo = new UserVo();
            userVo.setEmail(user.getEmail());
            userVo.setMobile(user.getMobile());
            userVo.setRegistryDate(user.getRegistryDate());
            userVo.setUsername(user.getUsername());
            userVos.add(userVo);
        }
        return userVos;
    }

    @Override
    public Integer getUserNum() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        return userMapper.selectCount(userQueryWrapper);
    }

    @Override
    public Boolean distinctUsername(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username",username);
        List<User> users = userMapper.selectList(userQueryWrapper);
        if (users.isEmpty()){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    @Override
    public Boolean registry(User user) {
        int insert = userMapper.insert(user);
        return insert==1?Boolean.TRUE:Boolean.FALSE;
    }

    @Override
    public String login(String username, String password) {
        String tok = request.getHeader("u-token");
        if (tok!=null){
            redisTemplate.opsForValue().set(tok,username,60*30);
            return tok;
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username).eq("password",password);
        Integer integer = userMapper.selectCount(wrapper);
        if (integer==1){
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(token,username,60*30);
            return token;
        }
        return null;

    }
}
