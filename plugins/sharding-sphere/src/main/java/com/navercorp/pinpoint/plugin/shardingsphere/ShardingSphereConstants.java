package com.navercorp.pinpoint.plugin.shardingsphere;

import com.navercorp.pinpoint.common.trace.*;

/**
 * @author chenqingyang
 */
public final class ShardingSphereConstants {
    
    private ShardingSphereConstants() {
    }
    
    public static final ServiceType SHARDING_SPHERE_ROUTE = ServiceTypeFactory.of(2900, "SHARDING-SPHERE/ROUTE");
    public static final ServiceType SHARDING_SPHERE_EXECUTOR = ServiceTypeFactory.of(2901, "SHARDING-SPHERE/EXECUTE");
    public static final ServiceType SHARDING_SPHERE_MERGE = ServiceTypeFactory.of(2902, "SHARDING-SPHERE/MERGE");
    
    public static final AnnotationKey DB_INSTANCE = AnnotationKeyFactory.of(901, "db.instance", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
}
