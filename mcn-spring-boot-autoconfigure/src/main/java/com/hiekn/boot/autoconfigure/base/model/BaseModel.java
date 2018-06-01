package com.hiekn.boot.autoconfigure.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class BaseModel {

    private Integer pageNo;

    private Integer pageSize;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    private Date updateTime;

    public Integer getPageNo() {
        return pageNo;
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