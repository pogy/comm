package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Unit;

public class UnitAdapter extends XmlAdapter<Unit, Unit> {

	@Override
	public Unit unmarshal(Unit unit) throws Exception {
		return unit;
	}

	@Override
	public Unit marshal(Unit unit) throws Exception {
		if(unit == null) {
			return null;
		}else {
			Unit simplifiedUnit = new Unit();
			simplifiedUnit.setId(unit.getId());
			simplifiedUnit.setName(unit.getName());
			simplifiedUnit.setSequence(unit.getSequence());
			simplifiedUnit.setCourse(unit.getCourse());
			simplifiedUnit.setSerialNumber(unit.getSerialNumber());
			simplifiedUnit.setLevel(unit.getLevel());
			simplifiedUnit.setCourseLevel(unit.getCourseLevel());
			return simplifiedUnit;
		}	
	}

}
