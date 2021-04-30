package com.jiayaxing.securityweb.service;


import com.alibaba.fastjson.JSONObject;
import com.jiayaxing.securityweb.dao.RoleDao;
import com.jiayaxing.securityweb.dao.UserDao;
import com.jiayaxing.securityweb.entity.RoleEntity;
import com.jiayaxing.securityweb.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    public UserEntity findByUsername(String username){

        UserEntity userEntity = null;
        try {
            userEntity = userDao.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userEntity;
    }

    public List findByRole(String role){
        String roleAll = null;
        if(role!=null){
            roleAll="ROLE_"+role;
        }
        List<UserEntity>  users = null;
        if(roleAll!=null){
            users = userDao.findByRoleAndDeleteFlag1(roleAll,0);
        }else {
            users = userDao.findAllByDeleteFlag(0);
        }
        ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserEntity userEntity = users.get(i);
            HashMap<String, String> map = new HashMap<>();
            map.put("role",userEntity.getRole());
            map.put("username",userEntity.getUsername());
            map.put("uuid",userEntity.getUuid());
            String groupName = userEntity.getGroupName();
            map.put("groupName",groupName==null?"":groupName);
            hashMaps.add(map);
        }
        return hashMaps;
    }

    public List getUserListAll() {
        List<UserEntity> all = userDao.findAll();
        return all;
    }
    public List getUserListByUsername(String username) {
        List<UserEntity> all = null;
        if(username.equals("")){
            all = userDao.findAll();
        }else {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            ExampleMatcher matching  = ExampleMatcher.matching();
            matching  = matching .withMatcher("username", ExampleMatcher.GenericPropertyMatcher::contains);
            all = userDao.findAll(Example.of(userEntity,matching));
        }
        return all;
    }

    @Transactional
    public JSONObject saveUserInfo(String username, String password, String role)  throws Exception {

        JSONObject jsonObject = new JSONObject();

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        List<UserEntity> all = userDao.findAll(Example.of(userEntity));
        if(all!=null&&all.size()!=0){
            UserEntity userEntity1 = all.get(0);
            userEntity1.setEncodePassword(passwordEncoder.encode(password));
            userEntity1.setPassword(password);
            userEntity1.setRole("ROLE_"+role);
            userDao.save(userEntity1);
            jsonObject.put("msg", username+"修改成功");

        }else {
            userEntity.setEncodePassword(passwordEncoder.encode(password));
            userEntity.setPassword(password);
            userEntity.setRole("ROLE_"+role);
            userEntity.setUuid(UUID.randomUUID().toString().replaceAll("-",""));
            userEntity.setDeleteFlag(0);
            userEntity.setGroupName("备用组");
            userDao.save(userEntity);
            jsonObject.put("msg", username+"新增成功");
        }

        jsonObject.put("state", 0);
        return jsonObject;
    }


    @Transactional
    public Object editUserInfo(Long id,String username, String password, String role, Integer deleteFlag) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        UserEntity userEntity = userDao.getOne(id);
        if(userEntity!= null){
            if(username!=null&&!username.equals("")){
                userEntity.setUsername(username);
            }
            if(password!=null&&!password.equals("")){
                userEntity.setEncodePassword(passwordEncoder.encode(password));
                userEntity.setPassword(password);
            }
            if(role!=null&&!role.equals("")){
                userEntity.setRole("ROLE_"+role);
            }
            if(deleteFlag!=null){
                userEntity.setDeleteFlag(deleteFlag);
            }
            userEntity.setGroupName("测试员组");
            userDao.save(userEntity);
            stringObjectHashMap.put("code",0);
            if(deleteFlag!=null){
                if(deleteFlag==0){
                    stringObjectHashMap.put("msg","启用用户信息成功");
                }else {
                    stringObjectHashMap.put("msg","停用用户信息成功");
                }
            }else {
                stringObjectHashMap.put("msg","保存用户信息成功");
            }

        }else {
            stringObjectHashMap.put("code",1);
            stringObjectHashMap.put("msg","操作失败");
        }
        return  stringObjectHashMap;
    }

    @Transactional
    public Object insertUserInfo(String username, String password, String role) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        if(username.equals("")){
            stringObjectHashMap.put("code",1);
            stringObjectHashMap.put("msg","新增用户失败，账号不能为空！");
            return  stringObjectHashMap;
        }
        if(password.equals("")){
            stringObjectHashMap.put("code",1);
            stringObjectHashMap.put("msg","新增用户失败，密码不能为空！");
            return  stringObjectHashMap;
        }
        if(role.equals("")){
            stringObjectHashMap.put("code",1);
            stringObjectHashMap.put("msg","新增用户失败，请选择权限！");
            return  stringObjectHashMap;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        List<UserEntity> all = userDao.findAll(Example.of(userEntity));
        if(all!=null&&all.size()!=0){
            stringObjectHashMap.put("code",1);
            stringObjectHashMap.put("msg","新增用户失败，用户已存在！");
        }else {
            userEntity.setEncodePassword(passwordEncoder.encode(password));
            userEntity.setPassword(password);
            userEntity.setRole("ROLE_"+role);
            userEntity.setUuid(UUID.randomUUID().toString().replaceAll("-",""));
            userEntity.setDeleteFlag(0);
            userEntity.setGroupName("测试员组");
            userDao.save(userEntity);
            stringObjectHashMap.put("code",0);
            stringObjectHashMap.put("msg","新增用户成功");
        }
        return  stringObjectHashMap;
    }

    public List getRoleList(String state) {
        List<RoleEntity> all = null;
        if(state!=null){
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setState(state);
            all = roleDao.findAll(Example.of(roleEntity));
        }else {
            all = roleDao.findAll();
        }
        return all;
    }

    @Transactional
    public Object saveRole(String role) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(role);
        roleEntity.setState("打开");
        RoleEntity save = roleDao.save(roleEntity);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("code",0);
        stringObjectHashMap.put("msg","保存权限成功");
        return stringObjectHashMap;
    }

    @Transactional
    public Object editRole(Long id, String state) {
        RoleEntity one = roleDao.getOne(id);
        one.setState(state);
        roleDao.save(one);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("code",0);
        stringObjectHashMap.put("msg",state+"权限成功");
        return stringObjectHashMap;
    }
}
