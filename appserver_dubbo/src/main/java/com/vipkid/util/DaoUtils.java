package com.vipkid.util;

import java.util.Iterator;
import java.util.Map;

import javax.persistence.Query;

public class DaoUtils {

	public static Query setQueryParameters(Query query, Map<String, Object> params) {
		if (!params.isEmpty()) {
			Iterator<Map.Entry<String,Object>> iterator = params.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<String,Object> entry = iterator.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query;
	}
}
