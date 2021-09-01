package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.dto.NewDeptDto;
import com.boredou.user.model.dto.UpdateDeptDto;
import com.boredou.user.model.entity.CompanyDept;

public interface CompanyDeptService extends IService<CompanyDept> {

    /**
     * 新建部门
     *
     * @param dto {@link NewDeptDto}
     */
    void newCoDept(NewDeptDto dto);

    /**
     * 新建部门
     *
     * @param dto {@link UpdateDeptDto}
     */
    void updateDept(UpdateDeptDto dto);
}
