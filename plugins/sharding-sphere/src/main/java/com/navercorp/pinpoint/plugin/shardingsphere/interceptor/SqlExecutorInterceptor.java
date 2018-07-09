package com.navercorp.pinpoint.plugin.shardingsphere.interceptor;

import com.google.common.collect.Lists;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor3;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.plugin.shardingsphere.ShardingSphereConstants;
import io.shardingsphere.core.executor.BaseStatementUnit;

import java.util.Collection;
import java.util.List;

/**
 * @author chenqingyang
 */
public class SqlExecutorInterceptor implements AroundInterceptor3 {
    
    private final TraceContext traceContext;
    
    private final MethodDescriptor descriptor;
    
    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    
    
    public SqlExecutorInterceptor(TraceContext traceContext, MethodDescriptor descriptor) {
        this.traceContext = traceContext;
        this.descriptor = descriptor;
    }
    
    @Override
    public void before(Object target, Object arg0, Object arg1, Object arg2) {
        if (logger.isDebugEnabled()) {
            logger.beforeInterceptor(target, new Object[]{arg0, arg1, arg2});
        }
        
        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        
        trace.traceBlockBegin();
    }
    
    @Override
    public void after(Object target, Object arg0, Object arg1, Object arg2, Object result, Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.afterInterceptor(target, new Object[]{arg0, arg1, arg2}, result, throwable);
        }
        
        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        
        try {
            SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            recorder.recordServiceType(ShardingSphereConstants.SHARDING_SPHERE_EXECUTOR);
            recorder.recordApi(descriptor);
            recorder.recordException(throwable);
            
            if (arg1 != null && arg1 instanceof Collection) {
                List<String> dataSources = Lists.newArrayList();
                List<List<List<Object>>> parameterLists = Lists.newArrayList();
                List<BaseStatementUnit> baseStatementUnits = Lists.newArrayList(((Collection) arg1).iterator());
                if (!CollectionUtils.isEmpty(baseStatementUnits)) {
                    for (BaseStatementUnit baseStatementUnit : baseStatementUnits) {
                        dataSources.add(baseStatementUnit.getSqlExecutionUnit().getDataSource());
                        parameterLists.add(baseStatementUnit.getSqlExecutionUnit().getSqlUnit().getParameterSets());
                    }
                    recorder.recordAttribute(ShardingSphereConstants.DB_INSTANCE, dataSources);
                    recorder.recordAttribute(AnnotationKey.SQL, baseStatementUnits.get(0).getSqlExecutionUnit().getSqlUnit().getSql());
                    recorder.recordAttribute(AnnotationKey.SQL_BINDVALUE, parameterLists);
                }
                
                
            }
            
            
        } finally {
            trace.traceBlockEnd();
        }
    }
}
