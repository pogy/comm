package com.vipkid.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.model.Family;
import com.vipkid.model.FollowUp;
import com.vipkid.model.Leads;
import com.vipkid.model.Leads.OwnerType;
import com.vipkid.model.Leads.Status;
import com.vipkid.model.Parent;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.service.pojo.FollowUpStatus;
import com.vipkid.service.pojo.LeadsAgeRange;
import com.vipkid.service.pojo.leads.FollowUpVo;
import com.vipkid.service.pojo.leads.LeadsVo;
import com.vipkid.service.pojo.leads.ParentVo;
import com.vipkid.util.DaoUtils;
import com.vipkid.util.DateTimeUtils;

@Repository
public class LeadsRepository extends BaseRepository<Leads>{
    
	public LeadsRepository() {
		super(Leads.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<LeadsVo> listLeads(Date tmkAssignTimeFrom, Date tmkAssignTimeTo,Date salesAssignTimeFrom, Date salesAssignTimeTo,
			Date followUpTimeFrom, Date followUpTimeTo, LifeCycle lifeCycle, Long channelId,
			Integer customerStage, Long salesId, Long tmkId, String searchText,Integer status,Integer contact, Boolean locked,String channelLevel, LeadsAgeRange ageRange, Integer start, Integer length) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		
		jpql.append("select stu.id,stu.name,stu.englishName,stu.registerDateTime,stu.lifeCycle,stu.family,channel.sourceName,")
			.append(" leads.salesId,leads.salesName,leads.salesAssignTime,leads.tmkId,leads.tmkName,leads.tmkAssignTime,leads.status,followUp2,leads.id,stu.customerStage,leads.locked,stu.birthday");
		
		jpql.append(" from Student stu join Leads leads on leads.studentId = stu.id");
		
		if (status != null && Integer.compare(status, Status.DEFAULT.getCode()) == 0 ) {
			//status 为 -1时,忽略其他条件,只查询status=-1,salesId=-1,tmkId=-1,isLibrary= false 的leads
			jpql.append(" and leads.status = -1 and leads.salesId = -1 and leads.tmkId = -1 and leads.isLibrary = false ");
		} else {
			//lifeCycle
			if (lifeCycle != null) {
				jpql.append(" and stu.lifeCycle = :lifeCycle");
				params.put("lifeCycle", lifeCycle);
			}
			
			//customerStage
			if (customerStage != null) {
				jpql.append(" and stu.customerStage = :customerStage");
				params.put("customerStage", customerStage);
			}
			
			//channelId
			if (channelId != null) {
				jpql.append(" and stu.channel.id = :channelId");
				params.put("channelId", channelId);
			}
			//channelLevel
			if (StringUtils.isNotBlank(channelLevel)) {
				jpql.append(" and stu.channel.level = :channelLevel");
				params.put("channelLevel", channelLevel);
			}
			
			//ageRange
			if (ageRange != null) {
				if (ageRange == LeadsAgeRange.LessThan5) {
					jpql.append(" and stu.birthday > :birthday");
					params.put("birthday", DateTimeUtils.getYearOffset(new Date(),-5));
				} else if (ageRange == LeadsAgeRange.From5To12) {
					jpql.append(" and stu.birthday >= :birthdayFrom and stu.birthday <:birthdayTo");
					params.put("birthdayFrom", DateTimeUtils.getYearOffset(new Date(),-13));					
					params.put("birthdayTo", DateTimeUtils.getYearOffset(new Date(),-5));					
				} else if (ageRange == LeadsAgeRange.OlderThan12) {
					jpql.append(" and stu.birthday <= :birthday");
					params.put("birthday", DateTimeUtils.getYearOffset(new Date(),-13));					
				}
			}
			
			boolean isLibrarySearch = false;
			if ((salesId != null && salesId == -2) || (tmkId != null && tmkId == -2)) {//如果salesId 或 tmkId 为 -2,则为查询 library
				isLibrarySearch = true;
			}
			if (!isLibrarySearch) {
				// tmkAssignTimeFrom/tmkAssignTimeTo
				if (tmkAssignTimeFrom != null) {
					jpql.append(" and leads.tmkAssignTime >= :tmkAssignTimeFrom");
					params.put("tmkAssignTimeFrom", tmkAssignTimeFrom);
				}
				if (tmkAssignTimeTo != null) {
					tmkAssignTimeTo = DateTimeUtils.getNextDay(tmkAssignTimeTo);
					jpql.append(" and leads.tmkAssignTime < :tmkAssignTimeTo");
					params.put("tmkAssignTimeTo", tmkAssignTimeTo);
				}
				
				// salesAssignTimeFrom/salesAssignTimeTo
				if (salesAssignTimeFrom != null) {
					jpql.append(" and leads.salesAssignTime >= :salesAssignTimeFrom");
					params.put("salesAssignTimeFrom", salesAssignTimeFrom);
				}
				if (salesAssignTimeTo != null) {
					salesAssignTimeTo = DateTimeUtils.getNextDay(salesAssignTimeTo);
					jpql.append(" and leads.salesAssignTime < :salesAssignTimeTo");
					params.put("salesAssignTimeTo", salesAssignTimeTo);
				}
				
				//salesId
				if (salesId != null) {
					jpql.append(" and leads.salesId = :salesId");
					params.put("salesId", salesId);
				}
				
				//tmkId
				if (tmkId != null) {
					jpql.append(" and leads.tmkId = :tmkId");
					params.put("tmkId", tmkId);
				}
				
				
				//status
				if (status != null && status != Status.DEFAULT.getCode()) {
					if (status == -Status.BOOKEDTRIAL.getCode()) {//未约到trial
						jpql.append(" and leads.status in (:ASSIGNED,:CONTACTED)");
						params.put("ASSIGNED",Status.ASSIGNED.getCode());
						params.put("CONTACTED",Status.CONTACTED.getCode());
					} else if (status == -Status.PAYED.getCode()) {//未付费
						jpql.append(" and leads.status != :status");
						params.put("status", Status.PAYED.getCode());
					}else {
						jpql.append(" and leads.status = :status");
						params.put("status", status);	
					}
				}
				
				//isContact
//				if (isContact != null) {
//					jpql.append(" and leads.isContact = :isContact");
//					params.put("isContact", isContact);
//				}
				//contact
				if (contact != null) {
					if (contact == FollowUpStatus.NOT_CONTACT.getCode()) {
						jpql.append(" and leads.isContact = :isContact");
						params.put("isContact", false);
					} else if (contact == FollowUpStatus.CONTACTED.getCode()) {
						jpql.append(" and leads.isContact = :isContact");
						params.put("isContact", true);
					}
				}
				
				//locked
				if (locked != null) {
					jpql.append(" and leads.locked = :locked");
					params.put("locked", locked);
				}
				
				//followUpTimeFrom/followUpTimeTo
				if (followUpTimeFrom != null || followUpTimeTo != null ||
						(contact != null && contact == FollowUpStatus.NEED_CONTACTED_AGAIN.getCode())) {
					jpql.append(" and exists (")
					.append(" select 1 from FollowUp followUp")
					.append(" where followUp.stakeholder = stu");
					
					if (salesId != null && tmkId != null) {
						jpql.append(" and followUp.creater.id in :createrIds");
						List<Long>  createrIds= Lists.newArrayList(salesId,tmkId);
						params.put("createrIds", createrIds);
					} else if (salesId != null) {
						jpql.append(" and followUp.creater.id = :createrId");
						params.put("createrId", salesId);
					} else if (tmkId != null) {
						jpql.append(" and followUp.creater.id = :createrId");
						params.put("createrId", tmkId);
					}
					
					if (followUpTimeFrom != null) {
						jpql.append(" and followUp.targetDateTime >= :followUpTimeFrom ");
						params.put("followUpTimeFrom", followUpTimeFrom);
					}
					if (followUpTimeTo != null) {
						followUpTimeTo = DateTimeUtils.getNextDay(followUpTimeTo);
						jpql.append(" and followUp.targetDateTime < :followUpTimeTo ");
						params.put("followUpTimeTo", followUpTimeTo);
					}
					
					if (contact != null && contact == FollowUpStatus.NEED_CONTACTED_AGAIN.getCode()) {
						if (salesId != null && tmkId != null) {
							jpql.append(" and followUp.id = (select max(f2.id) from FollowUp f2 where f2.stakeholder.id = leads.studentId and f2.creater.id in :followupCreaterIds) ");
							List<Long>  followupCreaterIds= Lists.newArrayList(salesId,tmkId);
							params.put("followupCreaterIds", followupCreaterIds);
						} else if (salesId != null) {
							jpql.append(" and followUp.id = (select max(f2.id) from FollowUp f2 where f2.stakeholder.id = leads.studentId and f2.creater.id = :followupCreaterId) ");
							params.put("followupCreaterId", salesId);
						} else if (tmkId != null) {
							jpql.append(" and followUp.id = (select max(f2.id) from FollowUp f2 where f2.stakeholder.id = leads.studentId and f2.creater.id = :followupCreaterId) ");
							params.put("followupCreaterId", tmkId);
						}
						
					}
					
					jpql.append(")");
				}
				
			} else {
				jpql.append(" and leads.isLibrary = true");
			}
		}
		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and exists (")
			.append(" select 1 from Parent parent")
			.append(" where parent.family = stu.family")
			.append(" and (parent.mobile = :searchText or parent.name like :searchTextLike or stu.name like :searchTextLike or stu.englishName like :searchTextLike)")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		
		//left join channel
		jpql.append(" left join stu.channel channel");
		//left join followup
		jpql.append(" left join FollowUp followUp2 on followUp2.stakeholder = stu and followUp2.current = true");
		
		jpql.append(" order by leads.id desc");
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		if (start != null) {
			query.setFirstResult(start);
		}
		if (length != null) {
			query.setMaxResults(length);
		}
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		List<LeadsVo> leadsList = Lists.newArrayList();
		LeadsVo leads = null;
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (Object[] row : resultList) {
				leads = new LeadsVo();
				Long stuId = (Long) row[0];
				String name = (String) row[1];
				String englishName = (String) row[2];
				Date registerTime = (Date) row[3];
				lifeCycle = (LifeCycle) row[4];
				Family family = (Family) row[5];
				String channel = (String) row[6];
				salesId = (Long) row[7];
				String salesName = (String) row[8];
				Date salesAssignTime = (Date) row[9];
				tmkId = (Long) row[10];
				String tmkName = (String) row[11];
				Date tmkAssignTime = (Date) row[12];
				int leadsStatus = (int) row[13];
				FollowUp followUp = (FollowUp) row[14];
				Long leadId = (Long) row[15];
				customerStage = (Integer) row[16];
				locked = (boolean) row[17];
				Date birthday = (Date) row[18];
				
				//parents
				List<ParentVo> parentsVo = Lists.newArrayList();
				ParentVo parent = null;
				if (family != null && CollectionUtils.isNotEmpty(family.getParents())) {
					for (Parent parent1 : family.getParents()) {
						parent = new ParentVo();
						parent.setId(parent1.getId());
						parent.setName(parent1.getName());
						parent.setMobile(parent1.getMobile());
						parentsVo.add(parent);
					}
				}
				
				//followUp
				FollowUpVo followUpVo = null;
				if (followUp != null) {
					followUpVo = new FollowUpVo();
					followUpVo.setId(followUp.getId());
					followUpVo.setContent(followUp.getContent());
					followUpVo.setAssignee(followUp.getAssignee() != null ? followUp.getAssignee().getName() : null);
					followUpVo.setCreateDateTime(followUp.getCreateDateTime());
					followUpVo.setTargetDateTime(followUp.getTargetDateTime());
				}
				
				leads.setId(leadId);
				leads.setStuId(stuId);
				leads.setName(name);
				leads.setEnglishName(englishName);
				leads.setRegisterTime(registerTime != null ? registerTime.getTime() : null);
				leads.setLifeCycle(lifeCycle);
				leads.setParents(parentsVo);
				leads.setSalesId(salesId);
				leads.setSalesName(salesName);
				leads.setSalesAssignTime(salesAssignTime != null ? salesAssignTime.getTime() : null);
				leads.setTmkId(tmkId);
				leads.setTmkName(tmkName);
				leads.setTmkAssignTime(tmkAssignTime != null ? tmkAssignTime.getTime() : null);
				leads.setLastFollowUp(followUpVo);
				leads.setChannel(channel);
				leads.setStatus(leadsStatus);
				leads.setCustomerStage(customerStage);
				leads.setLocked(locked);
				leads.setBirthday(birthday);
				
				leadsList.add(leads);
			}
		}
			
		return leadsList;
	}
	
	public long countLeads(Date tmkAssignTimeFrom, Date tmkAssignTimeTo,Date salesAssignTimeFrom, Date salesAssignTimeTo,
			Date followUpTimeFrom, Date followUpTimeTo, LifeCycle lifeCycle, Long channelId,
			Integer customerStage, Long salesId, Long tmkId, String searchText, Integer status, Integer contact, Boolean locked,String channelLevel,LeadsAgeRange ageRange) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		long count = 0;
		jpql.append("select count(stu) ");
		jpql.append(" from Student stu join Leads leads on leads.studentId = stu.id");
		
		if (status != null && Integer.compare(status, Status.DEFAULT.getCode()) == 0 ) {
			//status 为 -1时,忽略其他条件,只查询status=-1,salesId=-1,tmkId=-1,isLibrary= false 的leads
			jpql.append(" and leads.status = -1 and leads.salesId = -1 and leads.tmkId = -1 and leads.isLibrary = false ");
		} else {
			//lifeCycle
			if (lifeCycle != null) {
				jpql.append(" and stu.lifeCycle = :lifeCycle");
				params.put("lifeCycle", lifeCycle);
			}
			
			//customerStage
			if (customerStage != null) {
				jpql.append(" and stu.customerStage = :customerStage");
				params.put("customerStage", customerStage);
			}
			
			//channelId
			if (channelId != null) {
				jpql.append(" and stu.channel.id = :channelId");
				params.put("channelId", channelId);
			}
			
			//channelLevel
			if (StringUtils.isNotBlank(channelLevel)) {
				jpql.append(" and stu.channel.level = :channelLevel");
				params.put("channelLevel", channelLevel);
			}
			
			//ageRange
			if (ageRange != null) {
				if (ageRange == LeadsAgeRange.LessThan5) {
					jpql.append(" and stu.birthday > :birthday");
					params.put("birthday", DateTimeUtils.getYearOffset(new Date(),-5));
				} else if (ageRange == LeadsAgeRange.From5To12) {
					jpql.append(" and stu.birthday >= :birthdayFrom and stu.birthday <:birthdayTo");
					params.put("birthdayFrom", DateTimeUtils.getYearOffset(new Date(),-13));					
					params.put("birthdayTo", DateTimeUtils.getYearOffset(new Date(),-5));					
				} else if (ageRange == LeadsAgeRange.OlderThan12) {
					jpql.append(" and stu.birthday <= :birthday");
					params.put("birthday", DateTimeUtils.getYearOffset(new Date(),-13));					
				}
			}
			
			boolean isLibrarySearch = false;
			if ((salesId != null && salesId == -2) || (tmkId != null && tmkId == -2)) {//如果salesId 或 tmkId 为 -2,则为查询 library
				isLibrarySearch = true;
			}
			if (!isLibrarySearch) {
				// tmkAssignTimeFrom/tmkAssignTimeTo
				if (tmkAssignTimeFrom != null) {
					jpql.append(" and leads.tmkAssignTime >= :tmkAssignTimeFrom");
					params.put("tmkAssignTimeFrom", tmkAssignTimeFrom);
				}
				if (tmkAssignTimeTo != null) {
					tmkAssignTimeTo = DateTimeUtils.getNextDay(tmkAssignTimeTo);
					jpql.append(" and leads.tmkAssignTime < :tmkAssignTimeTo");
					params.put("tmkAssignTimeTo", tmkAssignTimeTo);
				}
				
				// salesAssignTimeFrom/salesAssignTimeTo
				if (salesAssignTimeFrom != null) {
					jpql.append(" and leads.salesAssignTime >= :salesAssignTimeFrom");
					params.put("salesAssignTimeFrom", salesAssignTimeFrom);
				}
				if (salesAssignTimeTo != null) {
					salesAssignTimeTo = DateTimeUtils.getNextDay(salesAssignTimeTo);
					jpql.append(" and leads.salesAssignTime < :salesAssignTimeTo");
					params.put("salesAssignTimeTo", salesAssignTimeTo);
				}
				
				//salesId
				if (salesId != null) {
					jpql.append(" and leads.salesId = :salesId");
					params.put("salesId", salesId);
				}
				
				//tmkId
				if (tmkId != null) {
					jpql.append(" and leads.tmkId = :tmkId");
					params.put("tmkId", tmkId);
				}	
			
				//status
				if (status != null && status != Status.DEFAULT.getCode()) {
					if (status == -Status.BOOKEDTRIAL.getCode()) {//未约到trial
						jpql.append(" and leads.status in (:ASSIGNED,:CONTACTED)");
						params.put("ASSIGNED",Status.ASSIGNED.getCode());
						params.put("CONTACTED",Status.CONTACTED.getCode());
					} else if (status == -Status.PAYED.getCode()) {//未付费
						jpql.append(" and leads.status != :status");
						params.put("status", Status.PAYED.getCode());
					}else {
						jpql.append(" and leads.status = :status");
						params.put("status", status);	
					}
				}
				
				//isContact
//				if (isContact != null) {
//					jpql.append(" and leads.isContact = :isContact");
//					params.put("isContact", isContact);
//				}
				//contact
				if (contact != null) {
					if (contact == FollowUpStatus.NOT_CONTACT.getCode()) {
						jpql.append(" and leads.isContact = :isContact");
						params.put("isContact", false);
					} else if (contact == FollowUpStatus.CONTACTED.getCode()) {
						jpql.append(" and leads.isContact = :isContact");
						params.put("isContact", true);
					}
				}
				
				//locked
				if (locked != null) {
					jpql.append(" and leads.locked = :locked");
					params.put("locked", locked);
				}
				
				//followUpTimeFrom/followUpTimeTo
				if (followUpTimeFrom != null || followUpTimeTo != null ||
						(contact != null && contact == FollowUpStatus.NEED_CONTACTED_AGAIN.getCode())) {
					jpql.append(" and exists (")
					.append(" select 1 from FollowUp followUp")
					.append(" where followUp.stakeholder = stu");
					
					if (salesId != null && tmkId != null) {
						jpql.append(" and followUp.creater.id in :createrIds");
						List<Long>  createrIds= Lists.newArrayList(salesId,tmkId);
						params.put("createrIds", createrIds);
					} else if (salesId != null) {
						jpql.append(" and followUp.creater.id = :createrId");
						params.put("createrId", salesId);
					} else if (tmkId != null) {
						jpql.append(" and followUp.creater.id = :createrId");
						params.put("createrId", tmkId);
					}
					
					if (followUpTimeFrom != null) {
						jpql.append(" and followUp.targetDateTime >= :followUpTimeFrom ");
						params.put("followUpTimeFrom", followUpTimeFrom);
					}
					if (followUpTimeTo != null) {
						followUpTimeTo = DateTimeUtils.getNextDay(followUpTimeTo);
						jpql.append(" and followUp.targetDateTime < :followUpTimeTo ");
						params.put("followUpTimeTo", followUpTimeTo);
					}
					
					if (contact != null && contact == FollowUpStatus.NEED_CONTACTED_AGAIN.getCode()) {
						if (salesId != null && tmkId != null) {
							jpql.append(" and followUp.id = (select max(f2.id) from FollowUp f2 where f2.stakeholder.id = leads.studentId and f2.creater.id in :followupCreaterIds) ");
							List<Long>  followupCreaterIds= Lists.newArrayList(salesId,tmkId);
							params.put("followupCreaterIds", followupCreaterIds);
						} else if (salesId != null) {
							jpql.append(" and followUp.id = (select max(f2.id) from FollowUp f2 where f2.stakeholder.id = leads.studentId and f2.creater.id = :followupCreaterId) ");
							params.put("followupCreaterId", salesId);
						} else if (tmkId != null) {
							jpql.append(" and followUp.id = (select max(f2.id) from FollowUp f2 where f2.stakeholder.id = leads.studentId and f2.creater.id = :followupCreaterId) ");
							params.put("followupCreaterId", tmkId);
						}
						
					}
					
					jpql.append(")");
				}
			} else {
				jpql.append(" and leads.isLibrary = true");
			}
		}

		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and exists (")
			.append(" select 1 from Parent parent")
			.append(" where parent.family = stu.family")
			.append(" and (parent.mobile = :searchText or parent.name like :searchTextLike or stu.name like :searchTextLike or stu.englishName like :searchTextLike)")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		
		//left join channel
		jpql.append(" left join stu.channel channel");
		//left join followup
		jpql.append(" left join FollowUp followUp2 on followUp2.stakeholder = stu and followUp2.current = true");
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (long)query.getSingleResult();
		return count;
	}

	public long countLeadsForSales(List<Long> salesIds, Date salesAssignTimeFrom, Date salesAssignTimeTo,List<Integer> statusInclude, List<Integer> statusExclude, boolean isLimitOwnerType, Boolean isContact, Boolean locked) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(leads) from Leads leads where leads.status != :status ");
		params.put("status", Leads.Status.PAYED.getCode());
		
		if (isLimitOwnerType) {
			jpql.append(" and leads.ownerType = :ownerType");
			params.put("ownerType", OwnerType.STAFF_SALES.getCode());
		}
		
		if (CollectionUtils.isNotEmpty(salesIds)) {
			if (salesIds.size() > 1) {
				jpql.append(" and leads.salesId in :salesIds");
				params.put("salesIds", salesIds);
			} else {
				jpql.append(" and leads.salesId = :salesId");
				params.put("salesId", salesIds.get(0));
			}
		}
		
		// salesAssignTimeFrom/salesAssignTimeTo
		if (salesAssignTimeFrom != null) {
			jpql.append(" and leads.salesAssignTime >= :salesAssignTimeFrom");
			params.put("salesAssignTimeFrom", salesAssignTimeFrom);
		}
		if (salesAssignTimeTo != null) {
			salesAssignTimeTo = DateTimeUtils.getNextDay(salesAssignTimeTo);
			jpql.append(" and leads.salesAssignTime < :salesAssignTimeTo");
			params.put("salesAssignTimeTo", salesAssignTimeTo);
		}
		
		if (statusInclude != null) {
			jpql.append(" and leads.status in :statusInclude");
			params.put("statusInclude", statusInclude);
		}
		
		if (statusExclude != null) {
			jpql.append(" and leads.status not in :statusExclude");
			params.put("statusExclude", statusExclude);
		}
		
		//isContact
		if (isContact != null) {
			jpql.append(" and leads.isContact = :isContact");
			params.put("isContact", isContact);
		}
		
		//locked
		if (locked != null) {
			jpql.append(" and leads.locked = :locked");
			params.put("locked", locked);
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		  count = (Long)query.getSingleResult();
		return count;
	}
	
	public long countLeadsForTmk(List<Long> tmkIds, Date tmkAssignTimeFrom, Date tmkAssignTimeTo,List<Integer> statusInclude, List<Integer> statusExclude, boolean isLimitOwnerType,Boolean isContact, Boolean locked) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(leads) from Leads leads where leads.status != :status ");
		params.put("status", Leads.Status.PAYED.getCode());
		
		if (isLimitOwnerType) {
			jpql.append(" and leads.ownerType = :ownerType");
			params.put("ownerType", OwnerType.STAFF_TMK.getCode());
		}
		
		if (CollectionUtils.isNotEmpty(tmkIds)) {
			if (tmkIds.size() > 1) {
				jpql.append(" and leads.tmkId in :tmkIds");
				params.put("tmkIds", tmkIds);
			} else {
				jpql.append(" and leads.tmkId = :tmkId");
				params.put("tmkId", tmkIds.get(0));
				
			}
		}
		// tmkAssignTimeFrom/tmkAssignTimeTo
		if (tmkAssignTimeFrom != null) {
			jpql.append(" and leads.tmkAssignTime >= :tmkAssignTimeFrom");
			params.put("tmkAssignTimeFrom", tmkAssignTimeFrom);
		}
		if (tmkAssignTimeTo != null) {
			tmkAssignTimeTo = DateTimeUtils.getNextDay(tmkAssignTimeTo);
			jpql.append(" and leads.tmkAssignTime < :tmkAssignTimeTo");
			params.put("tmkAssignTimeTo", tmkAssignTimeTo);
		}
		
		if (statusInclude != null) {
			jpql.append(" and leads.status in :statusInclude");
			params.put("statusInclude", statusInclude);
		}
		
		if (statusExclude != null) {
			jpql.append(" and leads.status not in :statusExclude");
			params.put("statusExclude", statusExclude);
		}
		
		//isContact
		if (isContact != null) {
			jpql.append(" and leads.isContact = :isContact");
			params.put("isContact", isContact);
		}
		
		//locked
		if (locked != null) {
			jpql.append(" and leads.locked = :locked");
			params.put("locked", locked);
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		  count = (Long)query.getSingleResult();
		return count;
	}
	
	public long countLeadsForSales(Long salesId) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(leads) from Leads leads where leads.salesId = :salesId and leads.ownerType=:ownerType and leads.status != :status ");
		params.put("ownerType", OwnerType.STAFF_SALES.getCode());
		params.put("salesId", salesId);
		params.put("status", Leads.Status.PAYED.getCode());
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (Long)query.getSingleResult();
		return count;
	}
	
	public long countLeadsForTmk(Long tmkId) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(leads) from Leads leads where leads.tmkId = :tmkId and leads.ownerType=:ownerType and leads.status != :status");
		params.put("ownerType", OwnerType.STAFF_TMK.getCode());
		params.put("tmkId", tmkId);
		params.put("status", Leads.Status.PAYED.getCode());
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (Long)query.getSingleResult();
		return count;
	}
	
	public Leads findByStudentId(Long studentId) {
			String sql = "SELECT l FROM Leads l WHERE l.studentId = :studentId";
			TypedQuery<Leads> typedQuery = entityManager.createQuery(sql, Leads.class);
			typedQuery.setParameter("studentId", studentId);
			List<Leads> leadss = typedQuery.getResultList();
			if(!leadss.isEmpty()) {
				return leadss.get(0);
			}else {
				return null;
			}
	}
	
	public void updateLeadsStatusIfNeed(Long studentId,int status) {
		String sql = "update Leads l set l.status = :status,l.isContact = :isContact where l.studentId = :studentId and l.status < :status";
		TypedQuery<Leads> typedQuery = entityManager.createQuery(sql, Leads.class);
		typedQuery.setParameter("status", status);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("isContact", true);
		typedQuery.executeUpdate();
	}
	
	public void updateLeadsContactField(Long studentId, boolean isContact) {
		String sql = "update Leads l set l.isContact = :isContact where l.studentId = :studentId";
		TypedQuery<Leads> typedQuery = entityManager.createQuery(sql, Leads.class);
		typedQuery.setParameter("isContact", isContact);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.executeUpdate();
	}
	
	public long countLockedLeadsForSales(Long salesId) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(leads) from Leads leads where leads.salesId = :salesId and leads.ownerType=:ownerType and leads.locked = :locked and leads.status != :status ");
		params.put("ownerType", OwnerType.STAFF_SALES.getCode());
		params.put("locked", true);
		params.put("salesId", salesId);
		params.put("status", Leads.Status.PAYED.getCode());
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (Long)query.getSingleResult();
		return count;
		
	}
}
