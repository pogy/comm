package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.InventionCode;


public class InventionCodeAdapter extends XmlAdapter<InventionCode, InventionCode> {

	@Override
	public InventionCode unmarshal(InventionCode inventionCode) throws Exception {
		return inventionCode;
	}

	@Override
	public InventionCode marshal(InventionCode inventionCode) throws Exception {
		if(inventionCode == null) {
			return null;
		}else {
			InventionCode simplifiedInventionCode = new InventionCode();
			simplifiedInventionCode.setId(inventionCode.getId());
			simplifiedInventionCode.setCode(inventionCode.getCode());
			return simplifiedInventionCode;
		}	
	}

}
