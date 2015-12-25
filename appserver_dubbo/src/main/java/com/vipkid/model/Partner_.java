package com.vipkid.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

//import com.vipkid.model.User.Status;
import com.vipkid.model.Partner.Type;;

@StaticMetamodel(Partner.class)
public class Partner_ {
    public static volatile SingularAttribute<Partner, String> email;
    public static volatile SingularAttribute<Partner, Type> type;
}
