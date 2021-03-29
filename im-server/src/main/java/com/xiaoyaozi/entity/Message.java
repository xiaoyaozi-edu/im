package com.xiaoyaozi.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * tip: 消息实体
 *
 * @author xiaoyaozi
 * createTime: 2021-03-29 18:22
 */
@Data
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "im_message")
@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String messageId;

    @Column
    private Long fromId;

    @Column
    private Long toId;

    @Column
    private String msg;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;
}
