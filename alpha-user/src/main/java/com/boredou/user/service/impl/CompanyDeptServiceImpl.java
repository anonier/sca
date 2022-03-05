package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.common.enums.BizException;
import com.boredou.user.model.dto.NewDeptDto;
import com.boredou.user.model.dto.UpdateDeptDto;
import com.boredou.user.model.entity.CompanyDept;
import com.boredou.user.model.mapper.CompanyDeptMapper;
import com.boredou.user.service.CompanyDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class CompanyDeptServiceImpl extends ServiceImpl<CompanyDeptMapper, CompanyDept> implements CompanyDeptService {

    @Override
    @DS("write")
    public void newCoDept(NewDeptDto dto) {
        CompanyDept dept = BeanUtil.copyProperties(dto, CompanyDept.class);
        dept.setGmtModified(new Date());
        try {
            this.save(dept);
        } catch (Exception e) {
            throw new BizException("新建部门失败");
        }
    }

    @Override
    @DS("write")
    public void updateDept(UpdateDeptDto dto) {
        CompanyDept dept = BeanUtil.copyProperties(dto, CompanyDept.class);
        try {
            this.updateById(dept);
        } catch (BizException e) {
            throw new BizException("更新部门失败");
        }
    }
}
