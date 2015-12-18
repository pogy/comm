/**
 * 
 */
package com.dubbo.service;

import com.dubbo.bean.TestBeans;

/**
 * 
 * @author:VIPKID ZengWeiLong
 * @date:2015-9-7
 */
public interface DemoService {
	/**
	 * 
	 * @author:VIPKID ZengWeiLong 2015-9-7
	 * @param str 字符串
	 * @param bean 对象
	 * @return
	 * String
	 */
	public String sayHello(String str,TestBeans bean);
 
}
