package commons.support.dubbo;


import commons.support.MixAll;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;

import java.util.Optional;

/**
 * @Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER}, order = Integer.MAX_VALUE)
 */
public class DubboMdcTraceFilter implements Filter {

    private static final String TRACE_MARK = "X-Trace-Id";

    @Override
    public Result invoke(final Invoker<?> invoker, final Invocation invocation) throws RpcException {
        RpcContext context = RpcContext.getContext();

        if(context.isConsumerSide()) {
            context.getObjectAttachments().putIfAbsent(TRACE_MARK, getTraceMark());
        }

        if(context.isProviderSide()) {
            String value = (String) context.getObjectAttachment(TRACE_MARK);
            setTraceMark(value);
        }

        try {
            return invoker.invoke(invocation);
        } finally {
            if(context.isProviderSide()) {
                clearTraceMark();
            }
        }
    }

    private String getTraceMark() {
        return Optional.ofNullable(MDC.get(TRACE_MARK)).orElse(MixAll.uuid());
    }

    private void setTraceMark(String traceMark) {
        MDC.put(TRACE_MARK, Optional.ofNullable(traceMark).orElse(MixAll.uuid()));
    }

    private void clearTraceMark() {
        MDC.remove(TRACE_MARK);
    }



}
