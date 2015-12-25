package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.model.Parent;

public class ParentAdapter extends XmlAdapter<Parent, Parent> {
	private Logger logger = LoggerFactory.getLogger(ParentAdapter.class);
	
	@Override
	public Parent unmarshal(Parent parent) throws Exception {
		return parent;
	}

	@Override
	public Parent marshal(Parent parent) throws Exception {
		if(parent == null) {
			return null;
		}else {
			if (parent != null && parent instanceof Parent){
				Parent simplifiedParent = new Parent();
				simplifiedParent.setId(parent.getId());
				simplifiedParent.setName(parent.getName());
				simplifiedParent.setRelation(parent.getRelation());
				simplifiedParent.setMobile(parent.getMobile());
				return simplifiedParent;
			} else{
				logger.error("Mashal Parent failed because parent={}", parent);
				return null;
			}
		}	
	}

}
