package com.xiaoyaozi.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * tip: 登录响应参数
 *
 * @author xiaoyaozi
 * createTime: 2021-03-23 15:24
 */
@Data
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfoResp implements Serializable {
    private static final long serialVersionUID = -6219102698660983147L;

    /**
     * 账号
     */
    private String account;
    /**
     * userId
     */
    private Long userId;
    /**
     * im服务端地址
     */
    private String serverIp;
}
