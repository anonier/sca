package com.boredou.user.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(value = "alpha-oauth")
public interface ApplyTokenService {

    @PostMapping(value = "/oauth/token", headers = {"Content-Type: multipart/form-data"})
    Map<String, String> applyToken(@RequestBody MultiValueMap<String, String> map, @RequestHeader("Authorization") String Authorization);

}
