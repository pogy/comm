package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.PayrollItem;

public class PayrollItemAdapter extends XmlAdapter<PayrollItem, PayrollItem> {

	@Override
	public PayrollItem unmarshal(PayrollItem payrollItem) throws Exception {
		return payrollItem;
	}

	@Override
	public PayrollItem marshal(PayrollItem payrollItem) throws Exception {
		if(payrollItem == null) {
			return null;
		}else {
			PayrollItem simplifiedPayrollItem = new PayrollItem();
			simplifiedPayrollItem.setId(payrollItem.getId());
			simplifiedPayrollItem.setPayroll(payrollItem.getPayroll());
			simplifiedPayrollItem.setSalary(payrollItem.getSalary());
			simplifiedPayrollItem.setComments(payrollItem.getComments());
			simplifiedPayrollItem.setSalaryPercentage(payrollItem.getSalaryPercentage());
			//simplifiedPayrollItem.setOnlineClass(payrollItem.getOnlineClass());
			return simplifiedPayrollItem;
		}	
	}

}
