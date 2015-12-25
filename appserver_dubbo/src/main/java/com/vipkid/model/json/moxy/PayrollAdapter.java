package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Payroll;

public class PayrollAdapter extends XmlAdapter<Payroll, Payroll> {

	@Override
	public Payroll unmarshal(Payroll payroll) throws Exception {
		return payroll;
	}

	@Override
	public Payroll marshal(Payroll payroll) throws Exception {
		if(payroll == null) {
			return null;
		}else {
			Payroll simplifiedPayroll = new Payroll();
			simplifiedPayroll.setId(payroll.getId());
			return simplifiedPayroll;
		}	
	}

}
