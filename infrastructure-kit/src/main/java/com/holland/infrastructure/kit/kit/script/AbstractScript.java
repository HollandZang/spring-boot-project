package com.holland.infrastructure.kit.kit.script;

import cn.hutool.crypto.digest.MD5;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractScript<SCRIPT, RETURN_OBJ> implements Script<SCRIPT, RETURN_OBJ> {
    /**
     * 缓存 [脚本名, 脚本对象]
     */
    protected final Map<String, SCRIPT> cacheScript = new HashMap<>();
    /**
     * 缓存 [脚本名, 签名]
     */
    protected final Map<String, String> cacheSignature = new HashMap<>();

    /**
     * 对脚本内容签名，用于防止重复加载
     *
     * @param scriptContent 脚本内容
     * @return 签名内容
     */
    protected String signature(String scriptContent) {
        return MD5.create().digestHex(scriptContent, StandardCharsets.UTF_8);
    }

    @Override
    public SCRIPT loadScript(String scriptName, String scriptContent) {
        final String signature = signature(scriptName);
        final String oldSign = cacheSignature.get(scriptName);

        if (null == oldSign) {
            log.debug("初次加载脚本[{}]", scriptName);
        } else if (oldSign.equals(signature)) {
            log.info("相同脚本[{}]，签名一致，无需加载", scriptName);
            return cacheScript.get(scriptName);
        } else {
            log.info("更新脚本[{}]", scriptName);
        }
        final SCRIPT scriptClass = specificLoadScript(scriptName, scriptContent);

        // 缓存
        cacheScript.put(scriptName, scriptClass);
        cacheSignature.put(scriptName, signature);

        return scriptClass;
    }

    protected abstract SCRIPT specificLoadScript(String scriptName, String scriptContent);
}
