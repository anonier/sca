package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.Company;
import com.boredou.user.model.entity.CompanyDept;
import com.boredou.user.model.mapper.CompanyMapper;
import com.boredou.user.service.CompanyDeptService;
import com.boredou.user.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@RefreshScope
@Transactional
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    @Resource
    CompanyDeptService companyDeptService;

    @Override
    public Company getCoById(int id) {
        return this.getById(id);
    }

    @Override
    @DS("read")
    public List<CompanyDept> getCoDeptById(int id) {
        return companyDeptService.list(new LambdaQueryWrapper<CompanyDept>().eq(CompanyDept::getCompanyId, id));
    }

}
