package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.AirCraft;

public class AirCraftAdapter extends XmlAdapter<AirCraft, AirCraft> {

	@Override
	public AirCraft unmarshal(AirCraft airCraft) throws Exception {
		return airCraft;
	}

	@Override
	public AirCraft marshal(AirCraft airCraft) throws Exception {
		if(airCraft == null) {
			return null;
		}else {
			AirCraft simplifiedAirCraft = new AirCraft();
			simplifiedAirCraft.setId(airCraft.getId());
			simplifiedAirCraft.setSequence(airCraft.getSequence());
			return simplifiedAirCraft;
		}	
	}

}
