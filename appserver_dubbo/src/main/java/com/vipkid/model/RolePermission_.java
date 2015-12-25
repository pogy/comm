package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(RolePermission.class)
public class RolePermission_ {
	public static volatile SingularAttribute<RolePermission, String> role;
	public static volatile SingularAttribute<RolePermission, String> permissions;
}
