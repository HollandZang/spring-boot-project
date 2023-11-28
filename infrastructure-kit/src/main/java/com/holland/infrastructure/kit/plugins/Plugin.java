package com.holland.infrastructure.kit.plugins;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 插件类接口
 */
public interface Plugin<PLUGIN extends PluginInterface<S>, S> {
    /**
     * Returns all {@link org.springframework.plugin.core.Plugin}s contained in this registry. Will return an immutable {@link List} to prevent outside
     * modifications of the {@link org.springframework.plugin.core.PluginRegistry} content.
     *
     * @return will never be {@literal null}.
     */
    List<PLUGIN> getPlugins();

    /**
     * Returns the first {@link org.springframework.plugin.core.Plugin} found for the given delimiter. Thus, further configured {@link org.springframework.plugin.core.Plugin}s are
     * ignored.
     *
     * @param delimiter must not be {@literal null}.
     * @return a plugin for the given delimiter or {@link Optional#empty()} if none found.
     */
    Optional<PLUGIN> getPluginFor(S delimiter);

    /**
     * 往插件类注册一个插件
     *
     * @param plugin 需要注册的插件
     */
    void registerPlugin(PLUGIN plugin);

    /**
     * 往插件类注册多个插件
     *
     * @param plugins 需要注册的插件
     */
    void registerPlugin(Collection<PLUGIN> plugins);

    /**
     * 插件类移除其中的插件
     *
     * @param plugin 需要移除的插件
     */
    void removePlugin(PLUGIN plugin);
}
