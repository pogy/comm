package com.vipkid.repository;

import javax.persistence.LockModeType;

import org.springframework.stereotype.Repository;

import com.vipkid.model.SeqSeed;

@Repository
public class SeqSeedRepository extends BaseRepository<SeqSeed> {

	public SeqSeedRepository() {
		super(SeqSeed.class);
	}
	
	public SeqSeed findBySeedTypeForUpdate(String seedType) {
		SeqSeed seed = entityManager.find(SeqSeed.class, seedType, LockModeType.PESSIMISTIC_WRITE);
		return seed;
	}
}
