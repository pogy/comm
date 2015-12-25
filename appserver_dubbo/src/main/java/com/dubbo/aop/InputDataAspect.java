package com.dubbo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.RpcContext;
import com.dubbo.utils.json.JsonFactory;

/**
 * 
 * @author VIPKID
 *
 */
public class InputDataAspect{
	
	private Logger logger = LoggerFactory.getLogger(InputDataAspect.class.getSimpleName());
	
	/**
	 * 权限拦截
	 * @Author:VIPKID-ZengWeiLong
	 * @param jp 2015年9月19日
	 */
	public void doBefore(JoinPoint jp) {  
		String clientIP = RpcContext.getContext().getRemoteHost();
		logger.info("==================应用,在IP为：" + clientIP + "的客户端发起RPC请求=====================");
		logger.info("前置通知: " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName()); 
        Object[] args=jp.getArgs();
        for (int i = 0; i < args.length; i++) {
        	System.out.println("参数："+args[i]); //输出传入的参数
        }
    }  
    
	/**
	 * 返回数据Jackson转化处理
	 * @Author:VIPKID-ZengWeiLong
	 * @param pjp
	 * @return
	 * @throws Throwable 2015年9月19日
	 */
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {  
    	long t1 = System.currentTimeMillis();  
        Object obj = pjp.proceed();  
        long t2 = System.currentTimeMillis();
        if(obj != null && !(obj instanceof String)){
        	System.out.println("对象为进行Json序列化!");
        	return JsonFactory.getConver(JsonFactory.ConverType.JACKSON).renderObj2Json(obj);
        }
        logger.info("=============服务执行时间："+ (t2-t1) + "，\n结果集：" + obj );
        return obj;
    }  
	  
  	public void afterReturning(JoinPoint jp) {  
  		//System.out.println("后置通知");   
    }  
  
    public void doThrowing(JoinPoint jp, Throwable ex) {  
    	logger.info("method " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName() + " throw exception");  
        System.out.println("异常消息："+ex.getMessage());  
    } 
    
    public void doAfter() {  
        //System.out.println("最终通知");  
    }

}
