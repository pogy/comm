package com.vipkid.repository;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Star;

@Repository
public class StarRepository extends BaseRepository<Star> {
	//private Logger logger = LoggerFactory.getLogger(StarRepository.class);
	
	public StarRepository() {
		super(Star.class);
	}
	
}
