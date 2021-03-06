package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.dto.CoStructDto;
import com.boredou.user.model.dto.CompanyEditDto;
import com.boredou.user.model.entity.Company;
import com.boredou.user.model.entity.CompanyDept;
import com.boredou.user.model.entity.SysBalance;
import com.boredou.user.model.vo.RechargeVo;

import java.math.BigDecimal;
import java.util.List;

public interface CompanyService extends IService<Company> {

    /**
     * 根据id获取公司信息
     *
     * @param id
     * @return {@link Company}
     */
    Company getCoById(String id);

    /**
     * 根据id获取公司信息
     *
     * @param id
     * @return {@link List<CoStructDto>}
     */
    List<CoStructDto> getCoStructById(String id);

    /**
     * 编辑公司信息
     *
     * @param companyEditVo {@link CompanyEditDto}
     */
    void edit(CompanyEditDto companyEditVo);

    /**
     * 根据公司id获取公司部门信息
     *
     * @return {@link List<CompanyDept>}
     */
    List<CompanyDept> getCoDeptById(int id);

    /**
     * 根据公司id获取公司余额
     *
     * @return {@link BigDecimal}
     */
    BigDecimal getBalance(String id);

    /**
     * 根据公司id充值
     *
     * @param rechargeVo {@link RechargeVo}
     */
    void recharge(RechargeVo rechargeVo);
}
