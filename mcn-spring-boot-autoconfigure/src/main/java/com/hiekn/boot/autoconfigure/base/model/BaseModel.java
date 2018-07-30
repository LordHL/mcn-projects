package com.hiekn.boot.autoconfigure.base.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.Objects;

public class BaseModel {

    @ApiParam("当前页，默认1")
    @DefaultValue("1")
    @Min(1)
    @QueryParam("pageNo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageNo;

    @ApiParam("每页数，默认10")
    @DefaultValue("10")
    @Max(50)
    @QueryParam("pageSize")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;

    private transient Date createTime;

    private transient Date updateTime;

    public Integer getPageNo() {
        if(Objects.isNull(pageNo)){//此处不能用三目表达式，猜测利用反射获取的值不能带逻辑判断？
            return pageNo;
        }
        return (pageNo - 1) * pageSize;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}