package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Activity;

public class ActivityAdapter extends XmlAdapter<Activity, Activity> {

	@Override
	public Activity unmarshal(Activity activity) throws Exception {
		return activity;
	}

	@Override
	public Activity marshal(Activity activity) throws Exception {
		if(activity == null) {
			return null;
		}else {
			Activity simplifiedActivity = new Activity();
			simplifiedActivity.setId(activity.getId());
			return simplifiedActivity;
		}	
	}

}
