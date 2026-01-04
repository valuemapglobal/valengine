package com.value.data.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.value.data.common.code.exception.ServiceException;
import com.value.data.common.constant.CacheConstants;
import com.value.data.common.constant.SecurityConstants;
import com.value.data.common.constant.TokenConstants;
import com.value.data.common.model.LoginUser;
import com.value.data.common.model.SysUser;
import com.value.data.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 系统安全工具类
 * @author Raysen
 * @create 2021/12/2 17:03
 */
@Component
public class SecurityUtils {

    //静态初始化当前类
    private static SecurityUtils securityUtils;

    //在方法上加上注解@PostConstruct,这样方法就会在bean初始化之后被spring容器执行
    @PostConstruct
    public void init(){
        //声明的静态类=this
        securityUtils = this;
    }

    @Autowired
    private RedisService redisService;

    /**
     * 获取登录用户身份
     * @author Raysen
     * @create 2021/12/2 17:12
     */
    public static LoginUser getLoginUser(HttpServletRequest request){
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        if(StringUtils.isEmpty(token)){
            String apiTokenDecryptPassword = ReadConfigurationFile.apiTokenDecryptPassword;
            String openApiToken = request.getHeader("open-Api-Token");
            if(StringUtils.isNotEmpty(openApiToken)){
                String userInformationJSON;
                try {
                    userInformationJSON = AESUtil.decryptFromBase64String(openApiToken, apiTokenDecryptPassword);
                } catch (Exception e) {
                    throw new ServiceException(SecurityConstants.ERR_SECURITY_MSG, SecurityConstants.ERR_SECURITY_CODE);
                }
                JSONObject userInformation = JSONObject.parseObject(userInformationJSON);
                String userId = userInformation.get("userId").toString();
                String userName = userInformation.get("userName").toString();
                String deptId = userInformation.get("deptId").toString();
                LoginUser loginUser = new LoginUser();
                SysUser sysUser = new SysUser();
                sysUser.setUserId(Long.parseLong(userId));
                sysUser.setUserName(userName);
                sysUser.setDeptId(Long.parseLong(deptId));
                loginUser.setSysUser(sysUser);
                loginUser.setUserid(Long.parseLong(userId));
                loginUser.setUsername(userName);
                return loginUser;

            }
            throw new ServiceException(SecurityConstants.ERR_SECURITY_MSG,SecurityConstants.ERR_SECURITY_CODE);
        }

        //根据token获取userKey
        String userKey = JwtUtils.getUserKey(token);
        //拼接redisKey
        String redisKey = CacheConstants.LOGIN_TOKEN_KEY+userKey;
        //获取登录用户信息
        com.ruoyi.system.api.model.LoginUser tempLoginUser = securityUtils.redisService.getCacheObject(redisKey);
        LoginUser loginUser = new LoginUser();
        if (tempLoginUser==null){
            throw new ServiceException(SecurityConstants.ERR_SECURITY_MSG,600);
        }
        BeanUtil.copyProperties(tempLoginUser,loginUser);

        return loginUser;
    }
}
