package com.vipkid.repository;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vipkid.model.Leads;
import com.vipkid.model.QueueNodeBean;
import com.vipkid.model.Role;
import com.vipkid.model.SalesTeam;
import com.vipkid.model.SalesTeam.Type;
import com.vipkid.model.Staff;
import com.vipkid.model.User;
import com.vipkid.rest.vo.query.SalesVO;

@Repository
public class SalesTeamRepository extends BaseRepository<SalesTeam> {

	public SalesTeamRepository() {
		super(SalesTeam.class);
	}
	
	public List<SalesTeam> findAll() {
		String sql = "SELECT st FROM SalesTeam st";
		TypedQuery<SalesTeam> typedQuery = entityManager.createQuery(sql, SalesTeam.class);
		return typedQuery.getResultList();
	}
	
	public List<SalesTeam> findByType(Type type) {
		String sql = "SELECT st FROM SalesTeam st WHERE st.type = :type";
		TypedQuery<SalesTeam> typedQuery = entityManager.createQuery(sql, SalesTeam.class);
		typedQuery.setParameter("type", type);
		return typedQuery.getResultList();
	}
	
	public SalesTeam findByManagerId(long managerId) {
		String sql = "SELECT st FROM SalesTeam st WHERE st.managerId = :managerId";
		TypedQuery<SalesTeam> typedQuery = entityManager.createQuery(sql, SalesTeam.class);
		typedQuery.setParameter("managerId", managerId);
		List<SalesTeam> salesTeams = typedQuery.getResultList();
		if (salesTeams.isEmpty()) {
			return null;
		} else {
			return salesTeams.get(0);
		}
	}
	
	public List<SalesVO> listForSalesTeam(String role, Long salesTeamId, Boolean autoAssignLeads,String searchText,Boolean isInTeam, Integer start, Integer length) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT distinct s,n.autoAssign FROM User u, Staff s LEFT JOIN QueueNodeBean n ON s.id = n.userId WHERE s.id = u.id");
		
		buffer.append(" and s.accountType = :accountType and s.status = :staffStatus ");
//		if (role != null) {
//			buffer.append(" AND u.roles LIKE '%").append(role).append("%'");
//		} else {
//			buffer.append(" AND (u.roles LIKE '%STAFF_SALES%' OR u.roles LIKE '%STAFF_TMK%')");
//		}
//		
//		buffer.append(" AND u.roles not like '%_DIRECTOR' ");
		
		if (StringUtils.isNotBlank(role)) {
			buffer.append(" AND u.roles REGEXP '[[:<:]]").append(role).append("[[:>:]]'");
		} else {
			buffer.append(" AND (u.roles REGEXP '[[:<:]]").append(Role.STAFF_SALES.name())
				.append("[[:>:]]|[[:<:]]").append(Role.STAFF_TMK.name()).append("[[:>:]]')");
		}
		
		if (salesTeamId != null) {
			buffer.append(" AND s.salesTeamId = " + salesTeamId);
		}
		
		if (isInTeam != null) {
			if (isInTeam == true) {
				buffer.append(" AND (s.salesTeamId is not null and s.salesTeamId != -1) ");
			} else {
				buffer.append(" AND (s.salesTeamId is null or s.salesTeamId = -1) ");
			}
		}
		
		if (autoAssignLeads != null) {
			buffer.append(" AND n.autoAssign = " + autoAssignLeads.toString());
		}
		
		if (searchText != null) {
			buffer.append(" AND u.name like '%" + searchText + "%' or s.englishName like '%" + searchText + "%'" );
		}
		
		Query query = entityManager.createQuery(buffer.toString());
		query.setParameter("accountType", User.AccountType.NORMAL);
		query.setParameter("staffStatus", User.Status.NORMAL);
		
		if(start != null) {
			query.setFirstResult(start);
		}
		if(length != null) {
			query.setMaxResults(length);
		}
		List<Object> rows = query.getResultList();
		List<SalesVO> salesVOs = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return salesVOs;
        }
		for (Object row : rows) {
			Object[] cells = (Object[])row;
			Staff staff = (Staff)cells[0];
			SalesVO salesVO = new SalesVO();
			salesVO.setId(staff.getId());
			salesVO.setName(staff.getName());
			salesVO.setEnglishName(staff.getEnglishName());
			salesVO.setEmail(staff.getEmail());
			salesVO.setMobile(staff.getMobile());
			salesVO.setRoles(staff.getRoles());
			salesVO.setSalesTeamId(staff.getSalesTeamId());
			
			Boolean autoAssign = (Boolean)cells[1];
			if(autoAssign != null) {
				salesVO.setAutoAssignLeads(autoAssign);
			}else {
				salesVO.setAutoAssignLeads(null);
			}
			
			salesVOs.add(salesVO);
		}
		return salesVOs;
	}
	
	public long countForSalesTeam(String role, Long salesTeamId, Boolean autoAssignLeads,String searchText,Boolean isInTeam) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT count(distinct s) FROM User u, Staff s LEFT JOIN QueueNodeBean n ON s.id = n.userId WHERE s.id = u.id");
		
		buffer.append(" and s.accountType = :accountType and s.status = :staffStatus ");

//		if (role != null) {
//			buffer.append(" AND u.roles LIKE '%").append(role).append("%'");
//		} else {
//			buffer.append(" AND (u.roles LIKE '%STAFF_SALES%' OR u.roles LIKE '%STAFF_TMK%')");
//		}
//		
//		buffer.append(" AND u.roles not like '%_DIRECTOR' ");
		
		if (StringUtils.isNotBlank(role)) {
			buffer.append(" AND u.roles REGEXP '[[:<:]]").append(role).append("[[:>:]]'");
		} else {
			buffer.append(" AND (u.roles REGEXP '[[:<:]]").append(Role.STAFF_SALES.name())
				.append("[[:>:]]|[[:<:]]").append(Role.STAFF_TMK.name()).append("[[:>:]]')");
		}
		
		if (salesTeamId != null) {
			buffer.append(" AND s.salesTeamId = " + salesTeamId);
		}
		
		if (isInTeam != null) {
			if (isInTeam == true) {
				buffer.append(" AND (s.salesTeamId is not null and s.salesTeamId != -1) ");
			} else {
				buffer.append(" AND (s.salesTeamId is null or s.salesTeamId = -1) ");
			}
		}
		
		if (autoAssignLeads != null) {
			buffer.append(" AND n.autoAssign = " + autoAssignLeads.toString());
		}
		
		if (searchText != null) {
			buffer.append(" AND u.name like '%" + searchText + "%' or s.englishName like '%" + searchText + "%'" );
		}
		
		Query query = entityManager.createQuery(buffer.toString());
		query.setParameter("accountType", User.AccountType.NORMAL);
		query.setParameter("staffStatus", User.Status.NORMAL);
		return (long)query.getSingleResult();
	}
	
	public List<QueueNodeBean> findQueueNodeByUserId(Long userId) {
		String sql = "SELECT n FROM QueueNodeBean n WHERE n.userId = :userId";
		TypedQuery<QueueNodeBean> typedQuery = entityManager.createQuery(sql, QueueNodeBean.class);
		typedQuery.setParameter("userId", userId);
		return typedQuery.getResultList();
	}
	
	public void removeTeamByManagerId(Long managerId) {
		
		String sql = "delete FROM SalesTeam st WHERE st.managerId = :managerId";
		TypedQuery<SalesTeam> typedQuery = entityManager.createQuery(sql, SalesTeam.class);
		typedQuery.setParameter("managerId", managerId);
		typedQuery.executeUpdate();
	}
}
