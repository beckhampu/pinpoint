package com.navercorp.pinpoint.plugin.shardingsphere;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;

import java.security.ProtectionDomain;

/**
 * @author chenqingyang
 */
public class ShardingSpherePlugin implements ProfilerPlugin, TransformTemplateAware {
    
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    
    private static final String SHARDINGSPHERE_SCOPE = "SHARDINGSPHERE_SCOPE";
    
    private TransformTemplate transformTemplate;
    
    @Override
    public void setup(ProfilerPluginSetupContext context) {
        ShardingSphereConfig config = new ShardingSphereConfig(context.getConfig());
        logger.debug("[ShardingSphere] pluginEnable={}", config.isPluginEnable());
        if (config.isPluginEnable()) {
            addSqlRouteTransformer();
            addSqlExecutorTransformer();
            addResultMergeTransformer();
        }
    }
    
    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
    
    private void addSqlRouteTransformer() {
        
        transformTemplate.transform("io.shardingsphere.core.routing.PreparedStatementRoutingEngine", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                InstrumentMethod method = target.getDeclaredMethod("route", "java.util.List");
                method.addScopedInterceptor("com.navercorp.pinpoint.plugin.shardingsphere.interceptor.SqlRouteInterceptor", SHARDINGSPHERE_SCOPE);
                return target.toBytecode();
            }
        });
        
        transformTemplate.transform("io.shardingsphere.core.routing.StatementRoutingEngine", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                InstrumentMethod method = target.getDeclaredMethod("route", "java.lang.String");
                method.addScopedInterceptor("com.navercorp.pinpoint.plugin.shardingsphere.interceptor.SqlRouteInterceptor", SHARDINGSPHERE_SCOPE);
                return target.toBytecode();
            }
        });
    }
    
    private void addSqlExecutorTransformer() {
        transformTemplate.transform("io.shardingsphere.core.executor.ExecutorEngine", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                InstrumentMethod method = target.getDeclaredMethod("execute", 
                        "io.shardingsphere.core.constant.SQLType",
                        "java.util.Collection", 
                        "io.shardingsphere.core.executor.ExecuteCallback");
                method.addScopedInterceptor("com.navercorp.pinpoint.plugin.shardingsphere.interceptor.SqlExecutorInterceptor", SHARDINGSPHERE_SCOPE);
                return target.toBytecode();
            }
        });
    }
    
    private void addResultMergeTransformer() {
        TransformCallback transformCallback = new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
                InstrumentMethod method = target.getDeclaredMethod("merge", "io.shardingsphere.core.merger.MergeEngine");
                method.addScopedInterceptor("com.navercorp.pinpoint.plugin.shardingsphere.interceptor.ResultSetMergeInterceptor", SHARDINGSPHERE_SCOPE);
                return target.toBytecode();
            }
        };
        transformTemplate.transform("io.shardingsphere.core.jdbc.core.statement.ShardingPreparedStatement", transformCallback);
        transformTemplate.transform("io.shardingsphere.core.jdbc.core.statement.ShardingStatement", transformCallback);
    }
    
}
