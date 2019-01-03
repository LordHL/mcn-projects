package com.hiekn.boot.autoconfigure.base.service;

import com.hiekn.boot.autoconfigure.base.model.result.RestData;

import java.util.List;

public interface BaseService<T,PK> {
    T save(T pojo);
    T saveSelective(T pojo);
    void deleteByPrimaryKey(PK id);
    T getByPrimaryKey(PK id);
    void updateByPrimaryKeySelective(T pojo);
    RestData<T> listByPage(T pojo);
    List<T> pageSelect(T pojo);
    int pageCount(T pojo);
    List<T> selectByCondition(T pojo);

}
