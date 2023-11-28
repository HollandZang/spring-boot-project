package com.holland.infrastructure.kit.plugins;

import java.util.*;

/**
 * 有序插件
 */
public abstract class SortedPlugin<PLUGIN extends PluginInterface<S>, S> implements Plugin<PLUGIN, S> {
    private List<PLUGIN> plugins;

    /**
     * 排序规则定义
     *
     * @return 排序规则
     */
    protected abstract Comparator<PLUGIN> sort();

    @Override
    public List<PLUGIN> getPlugins() {
        return plugins;
    }

    @Override
    public Optional<PLUGIN> getPluginFor(S delimiter) {
        return plugins.stream()
                .filter(translatePlugin -> translatePlugin.supports(delimiter))
                .findFirst();
    }

    @Override
    public void registerPlugin(PLUGIN plugin) {
        if (null == plugin) {
            return;
        }
        if (null == this.plugins) {
            this.plugins = new ArrayList<>();
        }
        this.plugins.add(plugin);
        this.plugins.sort(sort());
    }

    @Override
    public void registerPlugin(Collection<PLUGIN> plugins) {
        if (null == plugins || plugins.isEmpty()) {
            return;
        }
        if (null == this.plugins) {
            this.plugins = new ArrayList<>(plugins.size());
        }
        this.plugins.addAll(plugins);
        this.plugins.sort(sort());
    }

    @Override
    public void removePlugin(PLUGIN plugin) {
        if (null == plugin) {
            return;
        }
        if (null == this.plugins) {
            this.plugins = new ArrayList<>();
        }
        this.plugins.remove(plugin);
    }

}
