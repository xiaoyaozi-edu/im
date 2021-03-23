package com.xiaoyaozi.vo;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * tip: 登录参数
 *
 * @author xiaoyaozi
 * createTime: 2021-03-23 15:22
 */
@Data
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfoReq {

    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;

}
