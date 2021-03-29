package com.xiaoyaozi.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * tip: 用户实体
 *
 * @author xiaoyaozi
 * createTime: 2021-03-29 17:35
 */
@Data
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "im_user")
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = -8470055253149456953L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String account;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String mobile;

    @Column
    private LocalDateTime lastLoginTime;

    @Column
    private LocalDateTime createTime;

    @Column
    private Long createUser;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Long updateUser;

}
