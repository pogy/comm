package com.vipkid.service.pojo;

import java.io.Serializable;

public class OnlineClassFinishCountView implements Serializable {
	
		private static final long serialVersionUID = 1L;

		private long asScheduledCount;

		private long teacherNoShowCount;

		private long teacherItProblemCount;
		
		private long studentNoShowCount;
		
		private long studentItProblemCount;
		

		public long getAsScheduledCount() {
			return asScheduledCount;
		}

		public void setAsScheduledCount(long asScheduledCount) {
			this.asScheduledCount = asScheduledCount;
		}

		public long getTeacherNoShowCount() {
			return teacherNoShowCount;
		}

		public void setTeacherNoShowCount(long teacherNoShowCount) {
			this.teacherNoShowCount = teacherNoShowCount;
		}

		public long getTeacherItProblemCount() {
			return teacherItProblemCount;
		}

		public void setTeacherItProblemCount(long teacherItProblemCount) {
			this.teacherItProblemCount = teacherItProblemCount;
		}

		public long getStudentNoShowCount() {
			return studentNoShowCount;
		}

		public void setStudentNoShowCount(long studentNoShowCount) {
			this.studentNoShowCount = studentNoShowCount;
		}

		public long getStudentItProblemCount() {
			return studentItProblemCount;
		}

		public void setStudentItProblemCount(long studentItProblemCount) {
			this.studentItProblemCount = studentItProblemCount;
		}		
}
