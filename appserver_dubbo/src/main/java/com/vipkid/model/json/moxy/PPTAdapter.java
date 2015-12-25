package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.PPT;

public class PPTAdapter extends XmlAdapter<PPT, PPT> {

	@Override
	public PPT unmarshal(PPT ppt) throws Exception {
		return ppt;
	}

	@Override
	public PPT marshal(PPT ppt) throws Exception {
		if(ppt == null) {
			return null;
		}else {
			PPT simplifiedPPT = new PPT();
			simplifiedPPT.setId(ppt.getId());
			simplifiedPPT.setResource(ppt.getResource());
			simplifiedPPT.setSlideInitialized(ppt.isSlideInitialized());
			return simplifiedPPT;
		}	
	}

}
