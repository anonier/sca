package com.boredou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.DingTalkInfo;
import com.boredou.user.model.mapper.DingTalkInfoMapper;
import com.boredou.user.service.DingTalkInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RefreshScope
@Transactional
public class DingTalkInfoServiceImpl extends ServiceImpl<DingTalkInfoMapper, DingTalkInfo> implements DingTalkInfoService {
}
