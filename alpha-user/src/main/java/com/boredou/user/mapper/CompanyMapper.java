package com.boredou.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boredou.user.model.entity.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}
