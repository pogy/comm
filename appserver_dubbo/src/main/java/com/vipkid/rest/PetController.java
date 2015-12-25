package com.vipkid.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Pet;
import com.vipkid.model.PetType;
import com.vipkid.service.PetService;

@RestController
@RequestMapping("/api/service/private/pets")
public class PetController {
	private Logger logger = LoggerFactory.getLogger(PetController.class.getSimpleName());

	@Resource
	private PetService petService;
	
	@RequestMapping(value = "/findAllTypes", method = RequestMethod.GET)
	public List<PetType> findAllTypes() {
		logger.info("find all petTypes");
		return petService.findAllTypes();
	}
	
	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<Pet> findByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find pets for studentId = {}", studentId);
		return petService.findByStudentId(studentId);
	}
	
	@RequestMapping(value = "/findCurrentByStudentId", method = RequestMethod.GET)
	public Pet findCurrentByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find current pet for studentId = {}", studentId);
		return petService.findCurrentByStudentId(studentId);
	}
	
	@RequestMapping(value = "/findPetByStudentIdAndSequence", method = RequestMethod.GET)
	public List<Pet> findPetByStudentIdAndSequence(@RequestParam("studentId") long studentId,@RequestParam("sequence") int sequence) {
		logger.info("find current pet for studentId = {} sequence = {}", studentId,sequence);
		return petService.findPetByStudentIdAndSequence(studentId,sequence);
	}
	
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Pet create(@RequestBody Pet pet) {
		logger.info("create pet: {}", pet);
		pet.setCreateDateTime(new Date());
		return petService.create(pet);

	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Pet update(@RequestBody Pet pet) {
		logger.info("update pet: {}", pet);
		return petService.update(pet);
	}
}
