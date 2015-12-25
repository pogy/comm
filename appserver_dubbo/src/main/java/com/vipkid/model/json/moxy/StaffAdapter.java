package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Staff;

public class StaffAdapter extends XmlAdapter<Staff, Staff> {

	@Override
	public Staff unmarshal(Staff staff) throws Exception {
		return staff;
	}

	@Override
	public Staff marshal(Staff staff) throws Exception {
		if(staff == null) {
			return null;
		}else {
			Staff simplifiedStaff = new Staff();
			simplifiedStaff.setId(staff.getId());
			simplifiedStaff.setName(staff.getName());
			return simplifiedStaff;
		}	
	}

}
