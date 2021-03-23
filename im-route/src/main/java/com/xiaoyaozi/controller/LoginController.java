package com.xiaoyaozi.controller;

import com.xiaoyaozi.base.R;
import com.xiaoyaozi.route.RouteStrategy;
import com.xiaoyaozi.vo.LoginInfoReq;
import com.xiaoyaozi.vo.LoginInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * tip: 登录端
 *
 * @author xiaoyaozi
 * createTime: 2021-03-23 15:12
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private RouteStrategy routeStrategy;

    @PostMapping()
    public R<LoginInfoResp> login(@RequestBody LoginInfoReq loginInfoReq) {
        return R.success(LoginInfoResp.builder().account(loginInfoReq.getAccount())
                .userId(System.currentTimeMillis()).serverIp(routeStrategy.routeServerIp(null)).build());
    }

}

