package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.MarketingActivity;

public class MarketingActivityAdapter extends XmlAdapter<MarketingActivity, MarketingActivity> {

	@Override
	public MarketingActivity unmarshal(MarketingActivity marketingActivity) throws Exception {
		return marketingActivity;
	}

	@Override
	public MarketingActivity marshal(MarketingActivity marketingActivity) throws Exception {
		if(marketingActivity == null) {
			return null;
		}else {
			MarketingActivity simplifiedMarketingActivity = new MarketingActivity();
			simplifiedMarketingActivity.setId(marketingActivity.getId());
			simplifiedMarketingActivity.setName(marketingActivity.getName());
			simplifiedMarketingActivity.setChannel(marketingActivity.getChannel());
			return simplifiedMarketingActivity;
		}	
	}

}
