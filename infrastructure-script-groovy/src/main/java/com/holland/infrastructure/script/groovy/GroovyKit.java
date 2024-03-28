package com.holland.infrastructure.script.groovy;

import com.holland.infrastructure.script.AbstractScript;
import com.holland.infrastructure.script.Script;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 * @apiNote 建议缓存 Groovy类，而不是缓存 Groovy对象示例
 */
public class GroovyKit extends AbstractScript<Class<?>, Object> implements Script<Class<?>, Object> {
    /* 单例 */
    private static volatile GroovyKit instance;

    private GroovyKit() {
    }

    public static GroovyKit getInstance() {
        if (instance == null) {
            synchronized (GroovyKit.class) {
                if (instance == null) {
                    instance = new GroovyKit();
                }
            }
        }
        return instance;
    }

    /* 核心 */
    private final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    @Override
    public Class<?> specificLoadScript(String scriptName, String scriptContent) {
        return (Class<?>) groovyClassLoader.parseClass(scriptContent);
    }

    @Override
    public Object invoke(String scriptName, String method, Object... args) throws Exception {
        final Class<?> scriptClass = cacheScript.get(scriptName);

        final GroovyObject scriptInstance = (GroovyObject) scriptClass.getConstructor().newInstance();
        return scriptInstance.invokeMethod(method, args);
    }
}
