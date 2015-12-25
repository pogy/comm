package com.vipkid.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.service.CourseService;

@RestController
@RequestMapping("/api/service")
public class CourseRestController {

	@Autowired
	private CourseService courseService;

	@RequestMapping("/init")
	public void init() {
		
		
	}
}
