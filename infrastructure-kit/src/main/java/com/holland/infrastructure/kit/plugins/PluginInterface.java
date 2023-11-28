package com.holland.infrastructure.kit.plugins;

/**
 * 插件类判断条件
 */
public interface PluginInterface<S> {
    /**
     * Returns if a plugin should be invoked according to the given delimiter.
     *
     * @param delimiter must not be {@literal null}.
     * @return if the plugin should be invoked
     */
    boolean supports(S delimiter);
}
