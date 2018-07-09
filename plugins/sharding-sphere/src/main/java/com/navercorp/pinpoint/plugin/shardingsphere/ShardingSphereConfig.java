package com.navercorp.pinpoint.plugin.shardingsphere;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

/**
 * @author chenqingyang
 */
public class ShardingSphereConfig {
    
    private final boolean pluginEnable;
    
    public ShardingSphereConfig(ProfilerConfig config) {
        this.pluginEnable = config.readBoolean("profiler.shardingsphere", false);
    }
    
    public boolean isPluginEnable() {
        return pluginEnable;
    }
}
