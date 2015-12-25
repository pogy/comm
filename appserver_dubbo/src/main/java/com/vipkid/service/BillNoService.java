package com.vipkid.service;

import java.text.DecimalFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.SeqSeed;
import com.vipkid.model.SeqSeed.SeedType;
import com.vipkid.repository.SeqSeedRepository;
import com.vipkid.service.exception.InternalServerErrorServiceException;

@Service
public class BillNoService {

	@Resource
	SeqSeedRepository seqSeedRepository;
	
	private Logger logger = LoggerFactory.getLogger(BillNoService.class.getSimpleName());
	
	public String doGetNextOrderNo() {
		SeqSeed seqSeed = seqSeedRepository.findBySeedTypeForUpdate(SeedType.OrderNo.name());
		if (seqSeed == null) {
			logger.error("exception when get SeqSeed,SeedType = {}",SeedType.OrderNo);
			throw new InternalServerErrorServiceException("exception when get SeqSeed,SeedType = {}",SeedType.OrderNo);
		}
		
		logger.info("get seqSeed: seedType = {},seedDate = {}, seedValue = {},",
				seqSeed.getSeedType(),seqSeed.getSeedDate(),seqSeed.getSeedValue());
		
		String currentDate = DateFormatUtils.format(new Date(), seqSeed.getDateFormat());
		if (currentDate.equals(seqSeed.getSeedDate())) {
			seqSeed.setSeedValue(seqSeed.getSeedValue() + 1);
		} else {
			seqSeed.setSeedDate(currentDate);
			seqSeed.setSeedValue(1);
		}
		
		String maxNo = new DecimalFormat(seqSeed.getValueFormat()).format(seqSeed.getSeedValue());
		seqSeedRepository.update(seqSeed);
		String orderNo = currentDate + maxNo;
		logger.info("return generated orderNo = {}", orderNo);
		return orderNo;
	}

}
