package com.vipkid.model.json.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vipkid.model.User;

public class UserAdapter extends XmlAdapter<User, User> {

	@Override
	public User unmarshal(User user) throws Exception {
		return user;
	}

	@Override
	public User marshal(User user) throws Exception {
		if(user == null) {
			return null;
		}else {
			User simplifiedUser = new User();
			simplifiedUser.setId(user.getId());
			simplifiedUser.setName(user.getName());
			return simplifiedUser;
		}	
	}

}
