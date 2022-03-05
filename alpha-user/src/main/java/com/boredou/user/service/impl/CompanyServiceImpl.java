package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.dto.CoStructDto;
import com.boredou.user.model.dto.CompanyEditDto;
import com.boredou.user.model.entity.Company;
import com.boredou.user.model.entity.CompanyDept;
import com.boredou.user.model.mapper.CompanyMapper;
import com.boredou.user.model.vo.RechargeVo;
import com.boredou.user.service.CompanyDeptService;
import com.boredou.user.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    @Resource
    private CompanyDeptService companyDeptService;

    @Override
    public Company getCoById(String id) {
        return this.getById(id);
    }

    @Override
    public List<CoStructDto> getCoStructById(String id) {
        List<CompanyDept> deptList = companyDeptService.list(new LambdaQueryWrapper<CompanyDept>().eq(CompanyDept::getCompanyId, id));
        List<CoStructDto> structDtoList = new ArrayList<>(Collections.singletonList(new CoStructDto(id, this.getCoById(id).getName())));
        return structRecursion(structDtoList, deptList, null, 1, id);
    }

    /**
     * 公司架构递归
     *
     * @param deptList 部门列表
     * @param topDept  上级部门
     * @param level    部门等级
     * @param id       部门id
     * @return {@link List<CoStructDto>}
     */
    private List<CoStructDto> structRecursion(List<CoStructDto> structDtoList, List<CompanyDept> deptList, CompanyDept topDept, int level, String id) {
        if (hasDept(deptList, topDept, level)) {
            return deptList.stream().filter(dept -> dept.getLevel().equals(String.valueOf(level))
                            && (level == 1 ? StringUtils.isBlank(dept.getPId()) : dept.getPId().equals(topDept.getId())))
                    .map(dept -> {
                        structDtoList.add(new CoStructDto(id + "-" + dept.getId(), dept.getName()));
                        return new ArrayList<>(structRecursion(structDtoList, deptList, dept, level + 1, id + "-" + dept.getId()));
                    }).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        } else return structDtoList;
    }

    /**
     * 是否有当级部门
     *
     * @param deptList {@link List<CompanyDept>}
     * @param topDept  {@link CompanyDept} 上级部门
     * @param level    部门级别
     */
    private boolean hasDept(List<CompanyDept> deptList, CompanyDept topDept, int level) {
        return deptList.stream().anyMatch(dept -> dept.getLevel().equals(String.valueOf(level))
                && (level == 1 ? StringUtils.isBlank(dept.getPId()) : dept.getPId().equals(topDept.getId())));
    }

    @Override
    @DS("write")
    public void edit(CompanyEditDto dto) {
        this.updateById(BeanUtil.copyProperties(dto, Company.class));
    }

    @Override
    public List<CompanyDept> getCoDeptById(int id) {
        return companyDeptService.list(new LambdaQueryWrapper<CompanyDept>().eq(CompanyDept::getCompanyId, id));
    }

    @Override
    public BigDecimal getBalance(String id) {
        return this.getCoById(id).getBalance();
    }

    @Override
    public void recharge(RechargeVo vo) {
        this.updateById(Company.builder().id(vo.getId()).balance(vo.getAmount()).build());
    }
}
