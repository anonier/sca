package com.boredou.user.filter.RsaFilter;


import com.alibaba.fastjson.JSONObject;
import com.boredou.common.enums.BizException;
import com.boredou.common.util.AESUtil;
import com.boredou.common.util.RsaUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Optional;

/**
 * request改变入参
 *
 * @author yb
 * @since 2021-8-31
 */
public class RsaHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final String body;

    public RsaHttpServletRequestWrapper(HttpServletRequest request, String rsaPriKey) {
        super(request);
        //创建字符缓冲区
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            if (Optional.ofNullable(inputStream).isPresent()) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                //将输入流里面的参数读取到字符缓冲区
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            throw new BizException("RsaHttpServletRequestWrapper流处理失败");
        } finally {
            if (Optional.ofNullable(inputStream).isPresent()) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String s = stringBuilder.toString();
        if (StringUtils.isNotBlank(s)) {
            try {
                JSONObject json = JSONObject.parseObject(stringBuilder.toString());
                String plaintext = AESUtil.decrypt(json.get("data").toString(), new String(RsaUtil.decryptByPrivateKey(Base64.decodeBase64(json.get("aesKey").toString()), rsaPriKey)));
                JSONObject jsonObject = JSONObject.parseObject(plaintext);
                body = jsonObject.toString();
            } catch (Exception e) {
                throw new BizException("解密失败");
            }
        } else {
            body = s;
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
