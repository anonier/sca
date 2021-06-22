package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.exception.BusiException;
import com.boredou.user.mapper.CompanyDeptMapper;
import com.boredou.user.model.dto.NewDeptDto;
import com.boredou.user.model.dto.UpdateDeptDto;
import com.boredou.user.model.entity.CompanyDept;
import com.boredou.user.service.CompanyDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RefreshScope
@Transactional
public class CompanyDeptServiceImpl extends ServiceImpl<CompanyDeptMapper, CompanyDept> implements CompanyDeptService {

    @Override
    public boolean newCoDept(NewDeptDto dto) {
        CompanyDept dept = BeanUtil.copyProperties(dto, CompanyDept.class);
        dept.setGmtModified(new Date());
        return this.save(dept);
    }

    @Override
    public void updateDept(UpdateDeptDto dto) {
        CompanyDept dept = BeanUtil.copyProperties(dto, CompanyDept.class);
        try {
            this.baseMapper.updateById(dept);
        } catch (BusiException e) {
            throw new BusiException("更新部门失败");
        }
    }

}
