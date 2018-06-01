package com.hiekn.boot.autoconfigure.base.service;

import com.hiekn.boot.autoconfigure.base.model.result.RestData;

public interface BaseService<T,PK> {
    T save(T pojo);
    T saveSelective(T pojo);
    void deleteByPrimaryKey(PK id);
    T getByPrimaryKey(PK id);
    void updateByPrimaryKeySelective(T pojo);
    RestData<T> listByPage(T pojo);
    T selectByCondition(T pojo);

}
