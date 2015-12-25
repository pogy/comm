package com.vipkid.template;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STRawGroupDir;

import com.google.common.base.Preconditions;

public abstract class AbstractTemplates {
	protected Logger logger = LoggerFactory.getLogger(AbstractTemplates.class.getSimpleName());
	protected STGroup stGroup;
	protected ST st;

	public AbstractTemplates(final String templateDir) {
        Preconditions.checkArgument(StringUtils.isNotBlank(templateDir),"templateDir should not null");

		if (stGroup == null){
            //stGroup = new STRawGroupDir(this.getClass().getClassLoader().getResource("/").getPath() + "../template/" + templateDir, '$', '$');
            logger.info("########################################################");
            logger.info("resource dir is {}",this.getClass().getClassLoader().getResource("/template").getPath() +  templateDir);
            logger.info("########################################################");
            //stGroup = new STRawGroupDir(this.getClass().getResource("/").getPath() + "template/" + templateDir, '$', '$');
            stGroup = new STRawGroupDir(this.getClass().getClassLoader().getResource("/template").getPath() +  templateDir, '$', '$');
		}
	}

	public String render(String templateFileName, Map<String, Object> paramsMap) {
		st = stGroup.getInstanceOf(templateFileName);

		for (String key : paramsMap.keySet()) {
			st.add(key, paramsMap.get(key));
		}
		
		return st.render();
	}

}
