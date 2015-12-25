package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Pet;
import com.vipkid.model.PetType;
import com.vipkid.repository.PetRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.store.PetStore;

@Service
public class PetService {
	private Logger logger = LoggerFactory.getLogger(PetService.class.getSimpleName());

	@Resource
	private PetRepository petRepository;
	@Resource
	private StudentRepository studentRepository;
	
	public List<PetType> findAllTypes() {
		logger.debug("find all petTypes");
		return PetStore.getInstance().getPetTypes();
	}
	
	public List<Pet> findByStudentId(long studentId) {
		logger.debug("find pets for studentId = {}", studentId);
		return petRepository.findByStudentId(studentId);
	}
	
	public Pet findCurrentByStudentId(long studentId) {
		logger.debug("find current pet for studentId = {}", studentId);
		return petRepository.findCurrentByStudentId(studentId);
	}
	
	public List<Pet> findPetByStudentIdAndSequence(long studentId, int sequence) {
		logger.debug("find current pet for studentId = {} sequence = {}", studentId,sequence);
		return petRepository.findPetByStudentIdAndSequence(studentId,sequence);
	}
	
	
	public Pet create(Pet pet) {
//		logger.debug("create Pet for student id: {} and PetType name: {}", petAdoption.getStudentId(), petAdoption.getPetType().getName());
		logger.debug("create pet: {}", pet);
//		Student findStudent = studentAccessor.find(petAdoption.getStudentId());
//		Pet pet = null;
//		if(findStudent == null) {
//			throw new UserNotExistServiceException("student not exist.");
//		}else{
//			Pet currentPet = petAccessor.findCurrentByStudentId(petAdoption.getStudentId());
//			if(currentPet != null) {
//				currentPet.setCurrent(false);
//				petAccessor.update(currentPet);
//			}
//			
//			pet = PetStore.create(findStudent, petAdoption.getPetType());
//			petAccessor.create(pet);
//		}
		petRepository.create(pet);
		return pet;
	}
	
	public Pet update(Pet pet) {
//		logger.debug("create Pet for student id: {} and PetType name: {}", petAdoption.getStudentId(), petAdoption.getPetType().getName());
		logger.debug("update pet: {}", pet);
//		Student findStudent = studentAccessor.find(petAdoption.getStudentId());
//		Pet pet = null;
//		if(findStudent == null) {
//			throw new UserNotExistServiceException("student not exist.");
//		}else{
//			Pet currentPet = petAccessor.findCurrentByStudentId(petAdoption.getStudentId());
//			if(currentPet != null) {
//				currentPet.setCurrent(false);
//				petAccessor.update(currentPet);
//			}
//			
//			pet = PetStore.create(findStudent, petAdoption.getPetType());
//			petAccessor.create(pet);
//		}
		petRepository.update(pet);
		return pet;
	}

	public long findStarsByStudentIdAndTimeRange(long studentId, Date startDate, Date endDate) {
		logger.info("find stars for id = {}, startDate = {}, endDate = {}.", studentId, startDate, endDate);
		return petRepository.findStarsByStudentIdAndTimeRange(studentId, startDate, endDate);
	}
}
