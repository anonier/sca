package com.boredou.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.boredou.common.entity.ResponseVO;
import com.boredou.common.enums.ResponseMsgEnum;
import com.boredou.user.exception.BusiException;
import com.boredou.user.model.dto.NewDeptDto;
import com.boredou.user.model.dto.UpdateDeptDto;
import com.boredou.user.model.entity.Company;
import com.boredou.user.model.entity.CompanyDept;
import com.boredou.user.model.vo.NewDeptVo;
import com.boredou.user.model.vo.UpdateDeptVo;
import com.boredou.user.service.CompanyDeptService;
import com.boredou.user.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/boss")
@Api(tags = "企业管理模块", description = "企业管理相关模块接口")
public class CompanyCtrl {

    @Resource
    private CompanyService companyService;
    @Resource
    private CompanyDeptService companyDeptService;

    @GetMapping("/{id}")
    public ResponseVO getCoById(@ApiParam(value = "公司id", required = true) @PathVariable("id") int id) {
        Company co = companyService.getCoById(id);
        return ObjectUtil.isEmpty(co) ? new ResponseVO<>(ResponseMsgEnum.USER_ILLEGAL) : ResponseVO.success(co);
    }

    @GetMapping("/dept/{id}")
    public ResponseVO getCoDeptById(@ApiParam(value = "公司id", required = true) @PathVariable("id") int id) {
        List<CompanyDept> dept = companyService.getCoDeptById(id);
        return ObjectUtil.isEmpty(dept) ? new ResponseVO<>(ResponseMsgEnum.USER_ILLEGAL) : ResponseVO.success(dept);
    }

    @PostMapping("/newDept")
    public ResponseVO newDept(@RequestBody @Validated NewDeptVo vo) {
        boolean b = companyDeptService.newCoDept(BeanUtil.copyProperties(vo, NewDeptDto.class));
        return b ? ResponseVO.success() : new ResponseVO<>(ResponseMsgEnum.USER_ILLEGAL);
    }

    @PostMapping("/updateDept")
    public ResponseVO updateDept(@RequestBody @Validated UpdateDeptVo vo) {
        try {
            companyDeptService.updateDept(BeanUtil.copyProperties(vo, UpdateDeptDto.class));
            return ResponseVO.success();
        } catch (BusiException e) {
            return ResponseVO.fail(e.getResultCode().getCode(), e.getMessage());
        }
    }

}
