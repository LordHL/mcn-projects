package com.hiekn.boot.autoconfigure.base.service;

import com.hiekn.boot.autoconfigure.base.mapper.BaseMapper;
import com.hiekn.boot.autoconfigure.base.model.result.RestData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseServiceImpl<T,PK> implements BaseService<T,PK> {

    @Autowired
    private BaseMapper<T,PK> baseMapper;

    @Override
    public T save(T pojo) {
        baseMapper.insert(pojo);
        return pojo;
    }

    @Override
    public T saveSelective(T pojo) {
        baseMapper.insertSelective(pojo);
        return pojo;
    }

    @Override
    public void deleteByPrimaryKey(PK id) {
        baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public T getByPrimaryKey(PK id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateByPrimaryKeySelective(T pojo) {
        baseMapper.updateByPrimaryKeySelective(pojo);
    }


    @Override
    public RestData<T> listByPage(T pojo) {
        return new RestData<>(baseMapper.pageSelect(pojo),baseMapper.pageCount(pojo));
    }

    @Override
    public List<T> selectByCondition(T pojo) {
        return baseMapper.selectByCondition(pojo);
    }
}
