package com.dubbo.utils.json.support;

import java.text.DateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dubbo.utils.json.JsonConver;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * Jackson 转换工具
 * 
 * @author VIPKID 基于JackJson对象转化类
 */
public class JacksonImpl implements JsonConver {

	private Logger logger = LoggerFactory.getLogger(JacksonImpl.class.getSimpleName());

	private ObjectMapper objectMapper;
	
	@Override
	public JacksonImpl init() {
		objectMapper = new ObjectMapper();
		// json --> pojo
		// 1.设置输入时忽略在JSON字符串中存在，但Java对象实际没有的属性
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 2.设置禁止将空对象转化到
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 3.按字母顺序排序属性
		// objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY,true);
		// 4.Json美化
		// objectMapper.configure(SerializationFeature.INDENT_OUTPUT,true);
		// 仅仅包好不为Null的属性
		// objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 注册JaxbAnnotation
		// AnnotationIntrospector jaxbIntropsector = new JaxbAnnotationIntrospector();
		// objectMapper.setAnnotationIntrospector(jaxbIntropsector);
		objectMapper.registerModule(new JaxbAnnotationModule());
		// 默认转化时间格式
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// 有属性不能映射的时候不报错
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 不序列化为NULL值的Map
		objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		return this;
	}

	@Override
	public JacksonImpl setDateFormate(DateFormat dateFormat) {
		objectMapper.setDateFormat(dateFormat);
		return this;
	}

	@Override
	public String renderObj2Json(Object obj) {
		try {
			logger.info("obj -> json,data:" + obj);
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("解析对象错误");
		}
	}

	@Override
	public <T> T renderJson2Obj(String json, Class<T> clazz) {
		try {
			logger.info("json -> " +  clazz + ",data:" + json);
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> renderJson2List(String json) {
		try {
			logger.info("json -> List,data:" + json);
			return objectMapper.readValue(json, List.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}
}
