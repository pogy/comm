package com.vipkid.service.pojo;

import java.io.Serializable;

public class TeacherView implements Serializable {
	
		private static final long serialVersionUID = 1L;

		private long id;

		private String avatar;

		private String introduction;
		
		private boolean isNoAvailable;
		
		private String name;
		
		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public String getIntroduction() {
			return introduction;
		}

		public void setIntroduction(String introduction) {
			this.introduction = introduction;
		}

		public boolean isNoAvailable() {
			return isNoAvailable;
		}

		public void setNoAvailable(boolean isNoAvailable) {
			this.isNoAvailable = isNoAvailable;
		}
		
}
