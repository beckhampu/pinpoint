package com.navercorp.pinpoint.plugin.shardingsphere.interceptor;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor1;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.shardingsphere.ShardingSphereConstants;

/**
 * @author chenqingyang
 */
public class ResultSetMergeInterceptor implements AroundInterceptor1 {
    
    private final TraceContext traceContext;
    
    private final MethodDescriptor descriptor;
    
    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    
    
    public ResultSetMergeInterceptor(TraceContext traceContext, MethodDescriptor descriptor) {
        this.traceContext = traceContext;
        this.descriptor = descriptor;
    }
    
    @Override
    public void before(Object target, Object arg0) {
        if (logger.isDebugEnabled()) {
            logger.beforeInterceptor(target, new Object[]{arg0});
        }
        
        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        
        trace.traceBlockBegin();
    }
    
    @Override
    public void after(Object target, Object arg0, Object result, Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.afterInterceptor(target, new Object[]{arg0}, result, throwable);
        }
        
        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        
        try {
            SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            recorder.recordServiceType(ShardingSphereConstants.SHARDING_SPHERE_MERGE);
            recorder.recordApi(descriptor);
            recorder.recordException(throwable);
        } finally {
            trace.traceBlockEnd();
        }
    }
}
