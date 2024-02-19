package com.holland.infrastructure.kit.kit.script;

/**
 * 通用脚本方法
 *
 * @param <SCRIPT>     脚本实体类型
 * @param <RETURN_OBJ> 脚本调用返回对象类型
 */
public interface Script<SCRIPT, RETURN_OBJ> {
    /**
     * 加载脚本
     *
     * @param scriptName    脚本名称
     * @param scriptContent 脚本内容
     * @return 脚本实体类型
     */
    SCRIPT loadScript(String scriptName, String scriptContent);

    /**
     * @param scriptName 脚本名称
     * @param method     方法
     * @param args       参数集
     * @return 返回值
     */
    RETURN_OBJ invoke(String scriptName, String method, Object... args) throws Exception;
}
