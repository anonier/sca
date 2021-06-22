package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.dto.NewDeptDto;
import com.boredou.user.model.entity.Company;
import com.boredou.user.model.entity.CompanyDept;

import java.util.List;

public interface CompanyService extends IService<Company> {

    /**
     * 根据id获取公司信息
     *
     * @param id
     * @return {@link Company}
     */
    Company getCoById(int id);

    /**
     * 根据公司id获取公司部门信息
     *
     * @return {@link List<CompanyDept>}
     */
    List<CompanyDept> getCoDeptById(int id);

}
