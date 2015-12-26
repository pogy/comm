package com.zwl.eu.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

public class FilesUtils {

	private static Logger logger = Logger.getLogger(FilesUtils.class.getSimpleName());

	/**
	 * 读取文件内容
	 * 
	 * @Author:ALong
	 * @param templeteName
	 *            文件名称
	 * @return 2015年11月5日
	 */
	public static String readContent(InputStream inputStream, Charset charSet) {
		StringBuilder result = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charSet));// 构造一个BufferedReader类来读取文件

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(line);
			}

			bufferedReader.close();
			inputStream.close();
		} catch (Exception e) {
			logger.info("Read file stream error.");
		} finally {
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (Exception ex) {
				logger.info("Close file stream error.");
			}
		}

		return result.toString();
	}
	
	
	
	/**
	 * 模板操作
	 * @param inputStream
	 * @param parmMap
	 * @return String
	 * @date 2015年12月26日
	 */
	public static String readLogTemplete(InputStream inputStream,Map<String,Object> parmMap) {
		String content = FilesUtils.readContent(inputStream,StandardCharsets.UTF_8);
		for(String key:parmMap.keySet()){
			content = content.replace("%"+key+"%",parmMap.get(key).toString());
		}
		return content;
	}

}
