package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.DemoReport;

public class DemoReportAdapter extends XmlAdapter<DemoReport, DemoReport> {

	@Override
	public DemoReport unmarshal(DemoReport demoReport) throws Exception {
		return demoReport;
	}

	@Override
	public DemoReport marshal(DemoReport demoReport) throws Exception {
		if(demoReport == null) {
			return null;
		}else {
			DemoReport simplifiedDemoReport = new DemoReport();
			simplifiedDemoReport.setId(demoReport.getId());
			simplifiedDemoReport.setStudent(demoReport.getStudent());
			simplifiedDemoReport.setLifeCycle(demoReport.getLifeCycle());
			simplifiedDemoReport.setConfirmDateTime(demoReport.getConfirmDateTime());
			simplifiedDemoReport.setSubmitDateTime(demoReport.getSubmitDateTime());
			simplifiedDemoReport.setComment(demoReport.getComment());
			simplifiedDemoReport.setLevel(demoReport.getLevel());
			simplifiedDemoReport.setCreateDateTime(demoReport.getCreateDateTime());
			simplifiedDemoReport.setAttention(demoReport.getAttention());
			simplifiedDemoReport.setConfidence(demoReport.getConfidence());
			simplifiedDemoReport.setIndependent(demoReport.getIndependent());
			simplifiedDemoReport.setInteraction(demoReport.getInteraction());
			simplifiedDemoReport.setMouse(demoReport.getMouse());
			//simplifiedDemoReport.setOnlineClass(demoReport.getOnlineClass());
			
			simplifiedDemoReport.setL1(demoReport.getL1());
			simplifiedDemoReport.setL2(demoReport.getL2());
			simplifiedDemoReport.setL3(demoReport.getL3());
			simplifiedDemoReport.setL4(demoReport.getL4());
			simplifiedDemoReport.setL5(demoReport.getL5());
			simplifiedDemoReport.setL6(demoReport.getL6());
			simplifiedDemoReport.setL7(demoReport.getL7());
			
			simplifiedDemoReport.setS1(demoReport.getS1());
			simplifiedDemoReport.setS2(demoReport.getS2());
			simplifiedDemoReport.setS3(demoReport.getS3());
			simplifiedDemoReport.setS4(demoReport.getS4());
			simplifiedDemoReport.setS5(demoReport.getS5());
			simplifiedDemoReport.setS6(demoReport.getS6());
			simplifiedDemoReport.setS7(demoReport.getS7());
			simplifiedDemoReport.setS8(demoReport.getS8());
			simplifiedDemoReport.setS9(demoReport.getS9());
			simplifiedDemoReport.setS10(demoReport.getS10());
			simplifiedDemoReport.setS11(demoReport.getS11());
			simplifiedDemoReport.setS12(demoReport.getS12());
			simplifiedDemoReport.setS13(demoReport.getS13());
			simplifiedDemoReport.setS14(demoReport.getS14());
			simplifiedDemoReport.setS15(demoReport.getS15());
			
			simplifiedDemoReport.setR1(demoReport.getR1());
			simplifiedDemoReport.setR2(demoReport.getR2());
			simplifiedDemoReport.setR3(demoReport.getR3());
			simplifiedDemoReport.setR4(demoReport.getR4());
			simplifiedDemoReport.setR5(demoReport.getR5());
			simplifiedDemoReport.setR6(demoReport.getR6());
			simplifiedDemoReport.setR7(demoReport.getR7());
			simplifiedDemoReport.setR8(demoReport.getR8());
			simplifiedDemoReport.setR9(demoReport.getR9());
			simplifiedDemoReport.setR10(demoReport.getR10());
			simplifiedDemoReport.setR11(demoReport.getR11());
			simplifiedDemoReport.setR12(demoReport.getR12());
			simplifiedDemoReport.setR13(demoReport.getR13());
			simplifiedDemoReport.setR14(demoReport.getR14());
			simplifiedDemoReport.setR15(demoReport.getR15());
			simplifiedDemoReport.setR16(demoReport.getR16());
			simplifiedDemoReport.setR17(demoReport.getR17());
			
			simplifiedDemoReport.setM1(demoReport.getR1());
			simplifiedDemoReport.setM2(demoReport.getR2());
			simplifiedDemoReport.setM3(demoReport.getR3());
			simplifiedDemoReport.setM4(demoReport.getR4());
			simplifiedDemoReport.setM5(demoReport.getR5());
			simplifiedDemoReport.setM6(demoReport.getR6());
			simplifiedDemoReport.setM7(demoReport.getR7());
			simplifiedDemoReport.setM8(demoReport.getR8());
			simplifiedDemoReport.setM9(demoReport.getR9());
			simplifiedDemoReport.setM10(demoReport.getR10());
						
			
			return simplifiedDemoReport;
		}	
	}

}