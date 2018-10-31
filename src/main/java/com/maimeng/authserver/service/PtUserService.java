package com.maimeng.authserver.service;

import com.maimeng.authserver.global.bean.BaseData;
import com.maimeng.authserver.global.bean.ResultCode;
import com.maimeng.authserver.global.bean.ResultGenerator;
import com.maimeng.authserver.global.jwt.JWTHelper;
import com.maimeng.authserver.global.jwt.JwtInfo;
import com.maimeng.authserver.global.jwt.KeyConfiguration;
import com.maimeng.authserver.global.util.Common;
import com.maimeng.authserver.manager.PtUserManager;
import com.maimeng.authserver.model.PtUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2018/10/30.
 */
@Service
public class PtUserService {
    @Resource
    private PtUserManager ptUserManager;
    @Resource
    private KeyConfiguration keyConfiguration;
    @Value("${jwt.expire}")
    private int expire;

    public BaseData login(String account, String password) {
        PtUser ptUser = ptUserManager.findByAccount(account);
        if (ptUser == null) {
            //用户不存在
            return ResultGenerator.genFailResult(ResultCode.USER_NO_EXIST, "用户不存在");
        }
        if (!ptUser.getPassword().equals(Common.md5(password))) {
            //密码错误
            //用户不存在
            return ResultGenerator.genFailResult(ResultCode.PASSWORD_ERROR, "密码错误");
        }
        // Create Twt token
        try {
            String token = JWTHelper.generateToken(new JwtInfo(ptUser.getUserId(), ptUser.getName(),
                            System.currentTimeMillis()),
                    keyConfiguration.getUserPriKey(),
                    expire);
            return ResultGenerator.genSuccessResult(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}