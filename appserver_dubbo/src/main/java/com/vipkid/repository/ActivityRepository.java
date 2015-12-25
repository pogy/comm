package com.vipkid.repository;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Activity;

@Repository
public class ActivityRepository extends BaseRepository<Activity> {

	//private Logger logger = LoggerFactory.getLogger(ActivityRepository.class);

	public ActivityRepository() {
		super(Activity.class);
	}
}
