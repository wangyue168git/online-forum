package com.bolo.mybatis.mapper;

import com.bolo.entity.Zhongzi;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ZhongziMapper extends Mapper<Zhongzi> {

    @Override
    int insert(Zhongzi zhongzi);

    int insertBatch(List<Zhongzi> list);

}