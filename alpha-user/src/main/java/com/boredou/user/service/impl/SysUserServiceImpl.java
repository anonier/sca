package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.common.enums.BizException;
import com.boredou.common.enums.enums.*;
import com.boredou.common.enums.exception.ExceptionFeign;
import com.boredou.common.enums.exception.ExceptionRedis;
import com.boredou.common.enums.exception.Exceptions;
import com.boredou.common.mapper.SysUserMapper;
import com.boredou.common.module.entity.SysUser;
import com.boredou.common.util.CookieUtil;
import com.boredou.common.util.MD5Util;
import com.boredou.user.model.dto.AuthorityDto;
import com.boredou.user.model.dto.AuthorityMetaDto;
import com.boredou.user.model.dto.DingTalkBindDto;
import com.boredou.user.model.dto.UserDto;
import com.boredou.user.model.entity.*;
import com.boredou.user.model.result.LoginResult;
import com.boredou.user.service.*;
import com.boredou.user.service.feign.ApplyTokenService;
import com.boredou.user.util.DingTalkUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiUserGetbyunionidRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiUserGetbyunionidResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    @Value("${auth.cookieDomain}")
    private String cookieDomain;
    @Value("${dingTalk.appSecret}")
    private String appSecret;
    @Value("${dingTalk.appKey}")
    private String appKey;
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;
    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;
    @Value("${dingTalk.codeValidSeconds}")
    private int codeValidSeconds;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ApplyTokenService applyTokenService;
    @Resource
    private DingTalkService dingTalkService;
    @Resource
    private DingTalkUtil dingTalkUtil;
    @Resource
    private CompanyService companyService;
    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private SysRoleService sysRoleService;

    @Override
    public LoginResult login(String type, String username, String password, String code) {
        LoginResult loginResult;
        switch (type) {
            case "dingTalk_code":
                loginResult = this.getToken(EnumAuth.DINGTALK_CODE.getDesc(), null, null, this.getUserByName(username).getPhone(), code, clientId, clientSecret);
                break;
            case "sms_code":
                loginResult = this.getToken(EnumAuth.SMS_CODE.getDesc(), null, null, this.getUserByName(username).getPhone(), code, clientId, clientSecret);
                break;
            case "dingTalk_qrcode":
                OapiV2UserGetResponse rspGetResponse = getDingTalkUserInfo(code);
                stringRedisTemplate.boundValueOps(rspGetResponse.getResult().getMobile() + "_" + "qrcode").set(code, codeValidSeconds, TimeUnit.SECONDS);
                loginResult = this.getToken(EnumAuth.DINGTALK_QRCODE.getDesc(), null, null, rspGetResponse.getResult().getMobile(), code, clientId, clientSecret);
                break;
            default:
                loginResult = this.getToken(EnumAuth.PASSWD.getDesc(), username, password, null, null, clientId, clientSecret);
        }
        //将令牌存储到redis
        boolean bl = saveToken(Objects.requireNonNull(loginResult).getJti(), JSON.toJSONString(loginResult), tokenValiditySeconds);
        if (!bl) {
            throw new BizException(ExceptionRedis.SAVE_FAIL);
        }
        //保存cookie
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        CookieUtil.addCookie(Objects.requireNonNull(response), cookieDomain, EnumPunctuation.SLASH.getDesc(), EnumCookie.UID.getDesc(), loginResult.getJti(), cookieMaxAge, false);
        return loginResult;
    }

    @Override
    public List<AuthorityDto> getPermissions(String username) {
        //查询所有用户所有菜单和权限
        SysUser user = this.getUserByName(username);
        List<SysMenu> menus = sysMenuService.getMenus(user.getId());
        //递归组装菜单和权限集
        return authorityRecursion(menus, null, 1);
    }

    /**
     * 用户权限递归
     *
     * @param menus   所有权限
     * @param topMenu 父节点
     * @param level   等级
     * @return {@link List<AuthorityDto>}
     */
    private List<AuthorityDto> authorityRecursion(List<SysMenu> menus, SysMenu topMenu, int level) {
        return menus.stream().filter(menu -> StringUtils.isNotBlank(menu.getLevel())
                        && menu.getLevel().equals(String.valueOf(level)) && menu.getStatus().equals("1")
                        && (level == 1 ? StringUtils.isBlank(menu.getPId()) && menu.getIsMenu().equals("1")
                        : menu.getPId().equals(topMenu.getId()) && menu.getIsMenu().equals("1")))
                .map(menu -> new ArrayList<>(Collections.singletonList(AuthorityDto.builder()
                        .path(menu.getUrl())
                        .name(menu.getName())
                        .hidden(level > 2)
                        .component(menu.getComponent())
                        .children(hasLowMenu(menus, menu.getId(), level) ? authorityRecursion(menus, menu, level + 1) : null)
                        .meta(AuthorityMetaDto.builder()
                                .title(menu.getTitle())
                                .icon(menu.getIcon())
                                .authorities(hasPermission(menus, menu.getId()) ? menus.stream().filter(authority -> authority.getIsMenu().equals("0")
                                        && StringUtils.isNotBlank(authority.getPId())
                                        && authority.getPId().equals(menu.getId())
                                        && authority.getStatus().equals("1")).collect(Collectors.toList()) : null)
                                .build())
                        .build()))
                ).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    /**
     * 是否有下级菜单
     *
     * @param menus 所有权限
     * @param topId 该节点id
     * @return boolean
     */
    private boolean hasLowMenu(List<SysMenu> menus, String topId, int level) {
        return menus.stream().anyMatch(i -> StringUtils.isNotBlank(i.getLevel())
                && StringUtils.isNotBlank(i.getPId())
                && i.getLevel().equals(String.valueOf(level + 1))
                && i.getPId().equals(topId)
                && i.getStatus().equals("1"));
    }

    /**
     * 该菜单是否有权限
     *
     * @param menus 所有权限
     * @param topId 该节点id
     * @return boolean
     */
    private boolean hasPermission(List<SysMenu> menus, String topId) {
        return menus.stream().anyMatch(authority -> authority.getIsMenu().equals("0")
                && StringUtils.isNotBlank(authority.getPId())
                && authority.getPId().equals(topId)
                && authority.getStatus().equals("1"));
    }

    @Override
    public void bindDingTalkByPhone(String username) {
        SysUser user = this.getUserByName(username);
        if (user.getDingTalkBindStatus().equals("1")) {
            throw new BizException("账号已绑定");
        } else {
            this.update(user.setDingTalkBindStatus("1"));
            dingTalkService.saveDingTalkMessage(user, null);
        }
    }

    @Override
    public void bindDingTalkByQrcode(String code) {
        OapiV2UserGetResponse rspGetResponse = getDingTalkUserInfo(code);
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, rspGetResponse.getResult().getMobile()));
        if (user.getDingTalkBindStatus().equals("1")) {
            throw new BizException("账号已绑定");
        } else {
            this.update(user.setDingTalkBindStatus("1"));
            dingTalkService.saveDingTalkMessage(user, BeanUtil.copyProperties(rspGetResponse.getResult(), DingTalkBindDto.class));
        }
    }

    /**
     * 返回钉钉用户信息
     *
     * @param code 二维码Code
     * @return 钉钉用户信息对象
     */
    private OapiV2UserGetResponse getDingTalkUserInfo(String code) {
        try {
            String access_token = dingTalkUtil.getToken();
            // 通过临时授权码获取授权用户的个人信息
            DefaultDingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
            OapiSnsGetuserinfoBycodeRequest reqByCodeRequest = new OapiSnsGetuserinfoBycodeRequest();
            // 通过扫描二维码，跳转指定的redirect_uri后，向url中追加的code临时授权码
            reqByCodeRequest.setTmpAuthCode(code);
            OapiSnsGetuserinfoBycodeResponse byCodeResponse = client2.execute(reqByCodeRequest, appKey, appSecret);
            // 根据unionId获取userid
            String unionId = byCodeResponse.getUserInfo().getUnionid();
            DingTalkClient clientDingTalkClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/getbyunionid");
            OapiUserGetbyunionidRequest reqGetByUnionIdRequest = new OapiUserGetbyunionidRequest();
            reqGetByUnionIdRequest.setUnionid(unionId);
            OapiUserGetbyunionidResponse oapiUserGetbyunionidResponse = clientDingTalkClient.execute(reqGetByUnionIdRequest, access_token);
            // 根据userId获取用户信息
            String userid = oapiUserGetbyunionidResponse.getResult().getUserid();
            DingTalkClient clientDingTalkClient2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetRequest reqGetRequest = new OapiV2UserGetRequest();
            reqGetRequest.setUserid(userid);
            reqGetRequest.setLanguage("zh_CN");
            return clientDingTalkClient2.execute(reqGetRequest, access_token);
        } catch (Exception e) {
            throw new BizException("获取钉钉用户信息失败");
        }
    }

    @Override
    public int sendDingTalkCode(String username) {
        DingTalkUser user = dingTalkService.getDingTalkByUserName(username);
        int code = sendVerCode(user);
        stringRedisTemplate.boundValueOps(user.getPhone() + "_" + "dingTalkCode").set(String.valueOf(code), codeValidSeconds, TimeUnit.SECONDS);
        return code;
    }

    /**
     * 发送钉钉验证码
     *
     * @param user {@link DingTalkUser}
     * @return int 验证码
     */
    private int sendVerCode(DingTalkUser user) {
        try {
            DingTalkInfo info = dingTalkService.getDingTalkInfo(user.getId());
            // 生成六位随机数作为验证码
            int number = new Random().nextInt(900000) + 100000;
            // 0表示禁用验证，1表示启用验证
            if (ObjectUtil.isNotEmpty(info) && "1".equals(user.getDingTalkState()) && StringUtils.isNotBlank(info.getUserId())) {
                dingTalkUtil.sendUserMsg(DingTalkUtil.getVerCodeTemplateMsg("V39平台通知", "登录验证码", number + ""), info.getUserId());
            }
            return number;
        } catch (Exception e) {
            throw new BizException("发送验证码失败");
        }
    }

    @Override
    public void modifyPasswd(String password) {
        SysUser user = this.getUserByName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        user.setPassword(MD5Util.encode(password));
        this.update(user);
    }

    @Override
    public void resetPasswd(String username) {
        SysUser user = StringUtils.isBlank(username) ? this.getUserByName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()) : this.getUserByName(username);
        user.setPassword(MD5Util.encode(ResponseBaseUser.USER_DEFALTE_PASSWD.getMessage()));
        user.setUpdateTime(new Date());
        this.update(user);
    }

    @Override
    public void logout() {
        String uid = this.getTokenFormCookie();
        if (StringUtils.isBlank(uid) || Boolean.FALSE.equals(stringRedisTemplate.hasKey("user_token:" + uid)))
            throw new BizException(Exceptions.FAILURE);
        this.delToken(uid);
        //清除cookie
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        CookieUtil.addCookie(Objects.requireNonNull(response), cookieDomain, EnumPunctuation.SLASH.getDesc(), EnumCookie.UID.getDesc(), uid, 0, false);
    }

    @Override
    @DS("write")
    public void newUser(UserDto dto) {
        dto.setPassword(MD5Util.encode(ResponseBaseUser.USER_DEFALTE_PASSWD.getMessage()));
        SysUser user = BeanUtil.copyProperties(dto, SysUser.class).setStatus("1").setDingTalkBindStatus("0");
        try {
            this.save(user);
            if (dto.getRoleIds().contains(",")) {
                List<UserRole> userRoles = Arrays.stream(dto.getRoleIds().split(",")).map(id -> UserRole.builder()
                        .roleId(id)
                        .userId(user.getId())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build()).collect(Collectors.toList());
                userRoleService.saveBatch(userRoles);
            } else {
                userRoleService.save(UserRole.builder()
                        .roleId(dto.getRoleIds())
                        .userId(user.getId())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            }
        } catch (Exception e) {
            throw new BizException(Exceptions.ADD_USER_FAIL);
        }
    }

    /**
     * 获取token
     *
     * @param type         登入类型
     * @param username     账号
     * @param password     密码
     * @param phone        手机
     * @param code         验证码
     * @param clientId     clientId
     * @param clientSecret clientSecret
     * @return {@link LoginResult} 登入结果
     */
    private LoginResult getToken(String type, String username, String password, String phone, String code, String clientId, String clientSecret) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        switch (type) {
            case "dingTalk_code":
                SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
                if (ObjectUtil.isEmpty(user)) {
                    throw new BizException("用户不存在");
                } else if (ObjectUtil.isNotEmpty(user) && user.getStatus().equals("0")) {
                    throw new BizException("用户被禁用");
                }
                Company company = companyService.getById(user.getCompany());
                if (ObjectUtil.isEmpty(company) || company.getDingTalkCodeLogin().equals("0")) {
                    throw new BizException("该公司未开通钉钉验证码登入");
                }
                if (ObjectUtil.isNotEmpty(user)) {
                    body.add(EnumAuth.GRANT_TYPE.getDesc(), EnumAuth.DINGTALK_CODE.getDesc());
                    body.add(EnumAuth.PHONE.getDesc(), phone);
                    body.add(EnumAuth.CODE.getDesc(), code);
                } else {
                    throw new BizException("用户不存在");
                }
                break;
            case "sms_code":
                user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
                if (ObjectUtil.isEmpty(user)) {
                    throw new BizException("用户不存在");
                } else if (ObjectUtil.isNotEmpty(user) && user.getStatus().equals("0")) {
                    throw new BizException("用户被禁用");
                }
                company = companyService.getById(user.getCompany());
                if (ObjectUtil.isEmpty(company) || company.getDingTalkQrcodeLogin().equals("0")) {
                    throw new BizException("该公司未开通手机验证码登入");
                }
                if (ObjectUtil.isNotEmpty(user)) {
                    body.add(EnumAuth.GRANT_TYPE.getDesc(), EnumAuth.SMS_CODE.getDesc());
                    body.add(EnumAuth.PHONE.getDesc(), phone);
                    body.add(EnumAuth.CODE.getDesc(), code);
                } else {
                    throw new BizException("用户不存在");
                }
                break;
            case "dingTalk_qrcode":
                user = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
                if (ObjectUtil.isEmpty(user)) {
                    throw new BizException("用户不存在");
                } else if (ObjectUtil.isNotEmpty(user) && user.getStatus().equals("0")) {
                    throw new BizException("用户被禁用");
                }
                company = companyService.getById(user.getCompany());
                if (ObjectUtil.isEmpty(company) || company.getDingTalkQrcodeLogin().equals("0")) {
                    throw new BizException("该公司未开通钉钉二维码登入");
                }
                if (ObjectUtil.isNotEmpty(user)) {
                    body.add(EnumAuth.GRANT_TYPE.getDesc(), EnumAuth.DINGTALK_QRCODE.getDesc());
                    body.add(EnumAuth.PHONE.getDesc(), phone);
                    body.add(EnumAuth.CODE.getDesc(), code);
                } else {
                    throw new BizException("用户不存在");
                }
                break;
            default:
                user = this.getUserByName(username);
                if (ObjectUtil.isEmpty(user)) {
                    throw new BizException("用户不存在");
                } else if (ObjectUtil.isNotEmpty(user) && user.getStatus().equals("0")) {
                    throw new BizException("用户被禁用");
                }
                body.add(EnumAuth.GRANT_TYPE.getDesc(), EnumAuth.PASSWD.getDesc());
                body.add(EnumAuth.USERNAME.getDesc(), username);
                body.add(EnumAuth.PASSWD.getDesc(), password);
        }
        String Authorization = CookieUtil.getHttpBasic(clientId, clientSecret);
        try {
            Map<String, String> oauth2 = applyTokenService.applyToken(body, Authorization);
            return BeanUtil.fillBeanWithMap(oauth2, new LoginResult(), false);
        } catch (FeignException e) {
            throw new BizException(ExceptionFeign.FEIGN_FAIL);
        }
    }

    /**
     * 保存token至redis
     *
     * @param access_token key
     * @param content      token
     * @param ttl          token存在redis的时间
     * @return boolean
     */
    public boolean saveToken(String access_token, String content, long ttl) {
        String key = EnumRedis.USER_TOKEN.getDesc() + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire != null && expire > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public SysUser getUserByName(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username).eq(SysUser::getStatus, "1"));
    }

    @Override
    @DS("write")
    public void update(SysUser sysUser) {
        this.updateById(sysUser);
    }

    @Override
    public void editUser(UserDto dto) {
        this.update(BeanUtil.copyProperties(dto, SysUser.class));
    }

    @Override
    public String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, EnumCookie.UID.getDesc());
        if (StrUtil.isNotBlank(map.get(EnumCookie.UID.getDesc()))) {
            return map.get(EnumCookie.UID.getDesc());
        }
        return null;
    }

    @Override
    public void delToken(String accessToken) {
        String key = EnumRedis.USER_TOKEN.getDesc() + accessToken;
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            throw new BizException(ExceptionRedis.DEL_FAIL);
        }
    }

    @Override
    public void banUser(String id) {
        SysUser user = this.getById(id);
        user.setStatus("0");
        this.update(user);
    }

    @Override
    public List authorityManage(String id) {
        List<SysRole> sysRoles = sysRoleService.list(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCompany, id));
        List<JSONObject> list = new ArrayList<>();
        for (SysRole sysRole : sysRoles) {
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(sysRole));
            jsonObject.put("users", sysRoleService.getUsers(sysRole.getId(), id));
            list.add(jsonObject);
        }
        return list;
    }

    @Override
    public List<SysUser> memberManage(String id) {
        return this.baseMapper.getUsers(id);
    }
}
