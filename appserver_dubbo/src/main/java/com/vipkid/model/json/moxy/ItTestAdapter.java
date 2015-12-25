package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.ItTest;

public class ItTestAdapter extends XmlAdapter<ItTest, ItTest> {

	@Override
	public ItTest unmarshal(ItTest itTest) throws Exception {
		return itTest;
	}

	@Override
	public ItTest marshal(ItTest itTest) throws Exception {
		if(itTest == null) {
			return null;
		}else {
			ItTest simplifiedItTest = new ItTest();
			simplifiedItTest.setId(itTest.getId());
			simplifiedItTest.setSystem(itTest.getSystem());
			simplifiedItTest.setSystemResult(itTest.getSystemResult());
			simplifiedItTest.setBrowser(itTest.getBrowser());
			simplifiedItTest.setBrowserResult(itTest.getBrowserResult());
			simplifiedItTest.setFlash(itTest.getFlash());
			simplifiedItTest.setFlashResult(itTest.getFlashResult());
			simplifiedItTest.setConnect(itTest.getConnect());
			simplifiedItTest.setConnectResult(itTest.getConnectResult());
			simplifiedItTest.setDelay(itTest.getDelay());
			simplifiedItTest.setDelayResult(itTest.getDelayResult());
			simplifiedItTest.setBandWidth(itTest.getBandWidth());
			simplifiedItTest.setBandWidthResult(itTest.getBandWidthResult());
			simplifiedItTest.setSound(itTest.getSound());
			simplifiedItTest.setSoundResult(itTest.getSoundResult());
			simplifiedItTest.setMic(itTest.getMic());
			simplifiedItTest.setMicResult(itTest.getMicResult());
			simplifiedItTest.setCamera(itTest.getCamera());
			simplifiedItTest.setCameraResult(itTest.getCameraResult());
			return simplifiedItTest;
		}	
	}

}
