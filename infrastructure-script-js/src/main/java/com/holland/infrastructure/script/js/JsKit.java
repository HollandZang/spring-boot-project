package com.holland.infrastructure.script.js;

import com.holland.infrastructure.script.AbstractScript;
import com.holland.infrastructure.script.Script;

import javax.script.*;

/**
 * @apiNote Nashorn在 JDK 15 正式移除，所以该类不直接引入NashornScriptEngine、ScriptObjectMirror，因为包名不一致
 */
public class JsKit extends AbstractScript<ScriptEngine, Bindings> implements Script<ScriptEngine, Bindings> {
    /* 单例 */
    private static volatile JsKit instance;

    private JsKit() {
    }

    public static JsKit getInstance() {
        if (instance == null) {
            synchronized (JsKit.class) {
                if (instance == null) {
                    instance = new JsKit();
                }
            }
        }
        return instance;
    }

    /* 核心 */
    private final ScriptEngineManager manager = new ScriptEngineManager();

    @Override
    public ScriptEngine specificLoadScript(String scriptName, String scriptContent) {
        final ScriptEngine engine = manager.getEngineByName("nashorn");

        try {
            engine.eval(scriptContent);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

        return engine;
    }

    @Override
    public Bindings invoke(String scriptName, String method, Object... args) throws Exception {
        final ScriptEngine scriptEngine = cacheScript.get(scriptName);
        return (Bindings) ((Invocable) scriptEngine).invokeFunction(method, args);
    }
}
