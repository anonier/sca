package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.dto.CoStructDto;
import com.boredou.user.model.dto.CompanyEditDto;
import com.boredou.user.model.entity.Company;
import com.boredou.user.model.entity.CompanyDept;
import com.boredou.user.model.mapper.CompanyMapper;
import com.boredou.user.service.CompanyDeptService;
import com.boredou.user.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
@Transactional
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    @Resource
    CompanyDeptService companyDeptService;
    @Resource
    CompanyService companyService;

    @Override
    public Company getCoById(String id) {
        return this.getById(id);
    }

    @Override
    public List<CoStructDto> getCoStructById(String id) {
        List<CompanyDept> deptList = companyDeptService.list(new LambdaQueryWrapper<CompanyDept>().eq(CompanyDept::getCompanyId, id));
        List<CoStructDto> structDtoList = new ArrayList<>(Collections.singletonList(CoStructDto.builder()
                .id(id)
                .text(companyService.getCoById(id).getName()).build()));
        deptList.stream().filter(topDept -> topDept.getLevel().equals("1")).forEach(topDept -> structDtoList.retainAll(structRecursion(structDtoList, deptList, topDept, new AtomicReference<>(1), id)));
        return structDtoList;
    }

    /**
     * 公司架构递归
     *
     * @param structDtoList 架构列表
     * @param deptList      部门列表
     * @param topDept       上级部门
     * @param level         部门等级
     * @param id            部门id
     * @return {@link List<CoStructDto>}
     */
    private List<CoStructDto> structRecursion(List<CoStructDto> structDtoList, List<CompanyDept> deptList, CompanyDept topDept, AtomicReference<Integer> level, String id) {
        structDtoList.add(CoStructDto.builder()
                .id(id + "-" + topDept.getId())
                .text(topDept.getName())
                .build());
        if (deptList.stream().anyMatch(dept -> dept.getLevel().equals(String.valueOf(level.get() + 1)))) {
            List<CompanyDept> companyDeptList = deptList.stream().filter(dept -> StringUtils.isNotBlank(dept.getPId())
                    && dept.getPId().equals(topDept.getId())
                    && dept.getLevel().equals(String.valueOf(level.get() + 1))).collect(Collectors.toList());
            for (CompanyDept dept : companyDeptList) {
                level.set(level.get() + 1);
                structDtoList.retainAll(structRecursion(structDtoList, deptList, dept, level, id + "-" + topDept.getId()));
            }
        }
        return structDtoList;
    }

    @Override
    public void edit(CompanyEditDto dto) {
        this.updateById(BeanUtil.copyProperties(dto, Company.class));
    }

    @Override
    @DS("read")
    public List<CompanyDept> getCoDeptById(int id) {
        return companyDeptService.list(new LambdaQueryWrapper<CompanyDept>().eq(CompanyDept::getCompanyId, id));
    }
}
