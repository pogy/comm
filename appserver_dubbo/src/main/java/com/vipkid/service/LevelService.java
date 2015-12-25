package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.CourseLevel;
import com.vipkid.repository.LevelRepository;

@Service
public class LevelService {
	private Logger logger = LoggerFactory.getLogger(LevelService.class.getSimpleName());

	@Resource
	private LevelRepository levelRepository;

	public CourseLevel find(long id) {
		return levelRepository.find(id);
	}

	public List<CourseLevel> findByCourseId(long courseId) {
		return levelRepository.findByCourseId(courseId);
	}

	public CourseLevel update(CourseLevel level) {
		levelRepository.update(level);
		return level;
	}
}
