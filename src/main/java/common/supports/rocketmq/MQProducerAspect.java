package common.supports.rocketmq;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MQProducerAspect {

    @Pointcut("execution(* org.apache.rocketmq.client.producer.MQProducer.send*(..))")
    public void mqProducerSendMethodPointcut() {}

    @Pointcut("execution(* org.apache.rocketmq.client.producer.MQProducer.request*(..))")
    public void mqProducerRequestMethodPointcut() {}

    @Around("mqProducerSendMethodPointcut() || mqProducerRequestMethodPointcut()")
    public Object methodsProcessingJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }


}
