package com.dubbo.utils.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.service.GenericException;

/**
 * Dubbo对象方法调用类
 * @author VIPKID
 *
 */
public class ReflectionHandle {
	
	private static Logger logger = LoggerFactory.getLogger(ReflectionHandle.class.getSimpleName());
	/**
	 * 执行定义
	 * @Author:VIPKID-ZengWeiLong
	 * @param obj
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @return
	 * @throws GenericException 2015年9月19日
	 */
	public static Object $invoke(Object obj, String method, String[] parameterTypes, Object[] args)
			throws GenericException {
		// TODO Auto-generated method stub
		Object o = "Date Exception !!!";
		Class<?>[] clazzs = new Class[parameterTypes.length];
		try {
			for (int i = 0; i < parameterTypes.length; i++) {
				if (parameterTypes[i].indexOf(".") > -1) {
					Class<?> c = Class.forName(parameterTypes[i]);
					clazzs[i] = c;
				} else {
					if ("int".equals(parameterTypes[i])) {
						clazzs[i] = int.class;
					} else if ("boolean".equals(parameterTypes[i])) {
						clazzs[i] = boolean.class;
					} else if ("float".equals(parameterTypes[i])) {
						clazzs[i] = float.class;
					} else if ("double".equals(parameterTypes[i])) {
						clazzs[i] = double.class;
					} else if ("char".equals(parameterTypes[i])) {
						clazzs[i] = char.class;
					} else if ("byte".equals(parameterTypes[i])) {
						clazzs[i] = byte.class;
					} else if ("long".equals(parameterTypes[i])) {
						clazzs[i] = long.class;
					} else if ("short".equals(parameterTypes[i])) {
						clazzs[i] = short.class;
					}
				}
			}
			Method m = obj.getClass().getMethod(method, clazzs);
			o = m.invoke(obj, args);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			logger.error("调用方法未定义:"+e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			logger.error("违反系统的安全约束:"+e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("未加载到对应的Class："+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error("安全权限异常："+e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error("传入参数异常："+e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			Throwable ex = e.getTargetException();
			logger.error("程序内部异常："+ex.getMessage());
			e.printStackTrace();
		}
		return o;
	}
}
