package com.navercorp.pinpoint.plugin.shardingsphere;

import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;

/**
 * @author chenqingyang
 */
public class ShardingSphereTypeProvider implements TraceMetadataProvider {
    @Override
    public void setup(TraceMetadataSetupContext context) {
        context.addServiceType(ShardingSphereConstants.SHARDING_SPHERE_ROUTE);
        context.addServiceType(ShardingSphereConstants.SHARDING_SPHERE_EXECUTOR);
        context.addServiceType(ShardingSphereConstants.SHARDING_SPHERE_MERGE);
        context.addAnnotationKey(ShardingSphereConstants.DB_INSTANCE);
    }
}
