package com.vipkid.model.json.moxy;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.OnlineClass;

public class OnlineClassesAdapter  extends XmlAdapter<List<OnlineClass>, List<OnlineClass>> {
	
	@Override
	public List<OnlineClass> unmarshal(List<OnlineClass> onlineClasses) throws Exception {
		return null;
	}

	@Override
	public List<OnlineClass> marshal(List<OnlineClass> onlineClasses) throws Exception {
		return null;
	}
}
