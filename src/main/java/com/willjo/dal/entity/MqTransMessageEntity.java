package com.willjo.dal.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Accessors(chain = true)
 * 该注解用于简化方法链的编写，使得可以连续调用多个setter方法或其他返回对象自身的方法。具体功能如下：
 * 使setter方法自动返回当前对象，便于进行方法链式调用。
 * 可应用于getter、setter和其他返回对象自身的方法上。
 * 简化对象属性操作，提高代码可读性和编写效率。
 *
 * @EqualsAndHashCode(callSuper = false)
 * 该注解用于简化生成 equals 和 hashCode 方法。具体功能如下：
 * 避免继承父类方法：callSuper = false 表示生成的方法不会调用父类的 equals 和 hashCode 方法。
 * 自动生成方法：基于类中的字段自动生成 equals 和 hashCode 的实现。
 *
 * @author Grio Vino
 * @since 2019-02-23
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("mq_trans_message")
public class MqTransMessageEntity extends Model<MqTransMessageEntity> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String topic;

    private String tag;

    private String message;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
    
    /**
     * 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
