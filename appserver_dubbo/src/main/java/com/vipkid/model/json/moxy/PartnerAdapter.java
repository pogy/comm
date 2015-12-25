package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Partner;

public class PartnerAdapter extends XmlAdapter<Partner, Partner> {

	@Override
	public Partner unmarshal(Partner partner) throws Exception {
		return partner;
	}

	@Override
	public Partner marshal(Partner partner) throws Exception {
		if(partner == null) {
			return null;
		}else {
			Partner simplifiedPartner = new Partner();
			simplifiedPartner.setId(partner.getId());
			simplifiedPartner.setName(partner.getName());
			simplifiedPartner.setEmail(partner.getEmail());
			//simplifiedPartner.setTeachers(partner.getTeachers());
			return simplifiedPartner;
		}	
	}

}
