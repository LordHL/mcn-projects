package com.hiekn.boot.autoconfigure.web.service;

import com.hiekn.boot.autoconfigure.base.mapper.BaseMapper;
import com.hiekn.boot.autoconfigure.base.model.result.RestData;
import com.hiekn.boot.autoconfigure.base.service.BaseService;
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
        return new RestData<>(pageSelect(pojo),pageCount(pojo));
    }

    @Override
    public List<T> pageSelect(T pojo) {
        return baseMapper.pageSelect(pojo);
    }

    @Override
    public int pageCount(T pojo) {
        return baseMapper.pageCount(pojo);
    }

    @Override
    public List<T> selectByCondition(T pojo) {
        return baseMapper.selectByCondition(pojo);
    }
}
