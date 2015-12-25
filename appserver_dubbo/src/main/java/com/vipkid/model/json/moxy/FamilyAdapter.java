package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Family;

public class FamilyAdapter extends XmlAdapter<Family, Family> {

	@Override
	public Family unmarshal(Family family) throws Exception {
		return family;
	}

	@Override
	public Family marshal(Family family) throws Exception {
		if(family == null) {
			return null;
		}else {
			Family simplifiedFamily = new Family();
			simplifiedFamily.setId(family.getId());
			simplifiedFamily.setAddress(family.getAddress());
			simplifiedFamily.setProvince(family.getProvince());
			simplifiedFamily.setCity(family.getCity());
			simplifiedFamily.setDistrict(family.getDistrict());
			simplifiedFamily.setParents(family.getParents());
			simplifiedFamily.setStudents(family.getStudents());
			simplifiedFamily.setStudentNumberIInvented(family.getStudentNumberIInvented());
			simplifiedFamily.setPhone(family.getPhone());
			return simplifiedFamily;
		}	
	}

}
