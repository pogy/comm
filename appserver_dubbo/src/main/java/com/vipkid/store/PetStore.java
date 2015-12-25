package com.vipkid.store;

import java.util.LinkedList;
import java.util.List;

import com.vipkid.model.Pet;
import com.vipkid.model.PetType;
import com.vipkid.model.Student;

public class PetStore {
	private static PetStore instance;
	private List<PetType> petTypes = new LinkedList<PetType>(); 
	
	private PetStore() {
		PetType petType1 = new PetType();
		petType1.setName("Cat");
		petType1.setPrice(1);
		petType1.setUrl("http://baidu.com");
		petTypes.add(petType1);
		
		//TODO 从文件读取petType列表
//		petTypes = GsonManager.getInstance().getGson().fromJson(json, PetType.class);
	}
	
	public synchronized static PetStore getInstance() {
		if(instance == null) {
			instance = new PetStore();
		}
		
		return instance;
	}
	
	public static Pet create(Student student, PetType petType) {
		Pet pet = new Pet();
		pet.setName(student.getEnglishName() + "'s " + petType.getName());
		pet.setUrl(petType.getUrl());
		pet.setPrice(petType.getPrice());
		pet.setStudent(student);
		pet.setCurrent(false);
		
		return pet;
	}

	public List<PetType> getPetTypes() {
		return petTypes;
	}
	
	
}
