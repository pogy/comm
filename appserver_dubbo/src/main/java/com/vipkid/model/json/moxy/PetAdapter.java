package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.Pet;

public class PetAdapter extends XmlAdapter<Pet, Pet> {

	@Override
	public Pet unmarshal(Pet pet) throws Exception {
		return pet;
	}

	@Override
	public Pet marshal(Pet pet) throws Exception {
		if(pet == null) {
			return null;
		}else {
			Pet simplifiedPet = new Pet();
			simplifiedPet.setId(pet.getId());
			simplifiedPet.setName(pet.getName());
			return simplifiedPet;
		}	
	}

}
