package com.vipkid.repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Sets;
import com.vipkid.model.RolePermission;
import com.vipkid.model.RolePermission_;
import com.vipkid.util.TextUtils;


@Repository
public class RolePermissionRepository extends BaseRepository<RolePermission> {
	
	public boolean hasPermission(String permission, Set<String> roles){
		//String sql = "SELECT rp FROM RolePermission rp WHERE rp.role IN :roles AND rp.permissions like :permission";
		String sql = "SELECT rp FROM RolePermission rp WHERE rp.role IN :roles";
		TypedQuery<RolePermission> typedQuery = entityManager.createQuery(sql, RolePermission.class);
		typedQuery.setParameter("roles", roles);	
		//typedQuery.setParameter("permission", "%" + permission + "%");
        List<RolePermission> rolePermissionList = typedQuery.getResultList();
		if (CollectionUtils.isNotEmpty(rolePermissionList)){
            boolean hasPermission;
            for (RolePermission rolePermission : rolePermissionList) {
                hasPermission = rolePermission.getPermissionList().contains(permission);
                if (hasPermission) {
                    return true;
                }
            }
		}
		return false;
	}
	
	public String findPermissions(Set<String> roles){
		String sql = "SELECT rp FROM RolePermission rp WHERE rp.role IN :roles";
		TypedQuery<RolePermission> typedQuery = entityManager.createQuery(sql, RolePermission.class);
		typedQuery.setParameter("roles", roles);	
		StringBuilder sb = new StringBuilder();
        Set<String> permissionSet = Sets.newHashSet();
        for (RolePermission rolePermission : typedQuery.getResultList()){
            rolePermission.getPermissionList().stream().filter(permission -> !permissionSet.contains(permission)).forEach(permissionSet::add);
		}
        for (String permission : permissionSet) {
            sb.append(permission);
            sb.append(TextUtils.SPACE);
        }
		return sb.toString().trim();
	}
	
	public List<String> findPermissionCodes(Set<String> roles){
		String sql = "SELECT rp FROM RolePermission rp WHERE rp.role IN :roles";
		TypedQuery<RolePermission> typedQuery = entityManager.createQuery(sql, RolePermission.class);
		typedQuery.setParameter("roles", roles);
		List<String> permissionCodes = new ArrayList<String>();
		for (RolePermission rolePermission : typedQuery.getResultList()){
			for (String permissionCode : rolePermission.getPermissionCodeList()){
				if (permissionCodes.indexOf(permissionCode) < 0){
					permissionCodes.add(permissionCode);
				}
			}
		}
		return permissionCodes;
	}
	
	public List<String> findAllRoles(){
		String sql = "SELECT rp.role FROM RolePermission rp";
		TypedQuery<String> typedQuery = entityManager.createQuery(sql, String.class);
		return typedQuery.getResultList();
	}
	
	public List<RolePermission> list(String search, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RolePermission> criteriaQuery = criteriaBuilder.createQuery(RolePermission.class);
		Root<RolePermission> role = criteriaQuery.from(RolePermission.class);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(role.get(RolePermission_.role), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

	
		// compose final predicate
		if(!orPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate);
		}
		
		TypedQuery<RolePermission> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	
	public long count(String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<RolePermission> role = criteriaQuery.from(RolePermission.class);
		criteriaQuery.select(criteriaBuilder.count(role));


		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(role.get(RolePermission_.role), "%" + search + "%"));

		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

	
		// compose final predicate
		if(!orPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate);
		}
		
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

}
