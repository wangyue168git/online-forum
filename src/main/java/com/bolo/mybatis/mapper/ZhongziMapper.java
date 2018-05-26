package com.bolo.mybatis.mapper;

import com.bolo.entity.Zhongzi;
import tk.mybatis.mapper.common.Mapper;

public interface ZhongziMapper extends Mapper<Zhongzi> {

    @Override
    int insert(Zhongzi zhongzi);
}