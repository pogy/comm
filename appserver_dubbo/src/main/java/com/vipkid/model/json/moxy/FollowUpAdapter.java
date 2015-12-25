package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.FollowUp;

public class FollowUpAdapter  extends XmlAdapter<FollowUp, FollowUp> {

	@Override
	public FollowUp unmarshal(FollowUp followUp) throws Exception {
		return followUp;
	}

	@Override
	public FollowUp marshal(FollowUp followUp) throws Exception {
		if(followUp == null) {
			return null;
		}else {
			FollowUp simplifiedFollowUp = new FollowUp();
			simplifiedFollowUp.setId(followUp.getId());
			return simplifiedFollowUp;
		}
	}
}
