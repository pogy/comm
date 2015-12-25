package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Channel;

public class ChannelAdapter extends XmlAdapter<Channel, Channel>{

	@Override
	public Channel unmarshal(Channel channel) throws Exception {
		return channel;
	}

	@Override
	public Channel marshal(Channel channel) throws Exception {
		if(channel == null) {
			return null;
		}else {
			Channel simplifiedChannel = new Channel();
			simplifiedChannel.setId(channel.getId());
			simplifiedChannel.setSourceName(channel.getSourceName());
			return simplifiedChannel;
		}	
	}

}
