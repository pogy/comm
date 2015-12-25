package com.vipkid.service.pojo;

import java.io.Serializable;

public class ServerDateTime implements Serializable {
	
		private static final long serialVersionUID = 1L;

		private long dateTime;
		
		public ServerDateTime() {
			
		}

		public ServerDateTime(long dateTime) {
			super();
			this.dateTime = dateTime;
		}

		public long getDateTime() {
			return dateTime;
		}

		public void setDateTime(long dateTime) {
			this.dateTime = dateTime;
		}

}
