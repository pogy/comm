package com.vipkid.template;

public class EmailTemplates extends AbstractTemplates {

    public EmailTemplates() {
        super("email");
    }

    public static class NewParentSignupEmailTemplate {
        public static final String SUBJECT = "newParentSignupEmailSubjectTemplate";
        public static final String CONTENT = "newParentSignupEmailContentTemplate";
    }

    public static class ResetTeacherPasswordEmailTemplate {
        public static final String SUBJECT = "resetTeacherPasswordEmailSubjectTemplate";
        public static final String CONTENT = "resetTeacherPasswordEmailContentTemplate";
    }

    public static class ResetStaffPasswordEmailTemplate {
        public static final String SUBJECT = "resetStaffPasswordEmailSubjectTemplate";
        public static final String CONTENT = "resetStaffPasswordEmailContentTemplate";
    }

    public static class NewUserLoginEmailTemplate {
        public static final String SUBJECT = "newUserLoginEmailSubjectTemplate";
        public static final String CONTENT = "newUserLoginEmailContentTemplate";
    }

    public static class cancelOrderEmailTemplate {
        public static final String SUBJECT = "cancelOrderEmailSubjectTemplate";
        public static final String CONTENT = "cancelOrderEmailContentTemplate";
    }

    public static class currentMonthPayrollEmailTemplate {
        public static final String SUBJECT = "currentMonthPayrollEmailSubjectTemplate";
        public static final String CONTENT = "currentMonthPayrollEmailTemplate";
        public static final String TEACHER_NAME = "teacherName";
        public static final String PAYROLL = "payroll";
    }

    public static class OneOnOneOnlineClassIsBookedIn48HoursEmailTemplate {
        public static final String SUBJECT = "oneOnOneOnlineClassIsBookedIn48HoursEmailSubjectTemplate";
        public static final String CONTENT = "oneOnOneOnlineClassIsBookedIn48HoursEmailContentTemplate";
    }

    public static class OneOnOneOnlineClassIsCancelledIn48HoursEmailTemplate {
        public static final String SUBJECT = "oneOnOneOnlineClassIsCancelledIn48HoursEmailSubjectTemplate";
        public static final String CONTENT = "oneOnOneonlineClassIsCancelledIn48HoursEmailContentTemplate";
    }

    public static class DemoOnlineClassIsBookedEmailTemplate {
        public static final String SUBJECT = "demoOnlineClassIsBookedEmailSubjectTemplate";
        public static final String CONTENT = "demoOnlineClassIsBookedEmailContentTemplate";
    }

    public static class DemoOnlineClassIsCancelledEmailTemplate {
        public static final String SUBJECT = "demoOnlineClassIsCancelledEmailSubjectTemplate";
        public static final String CONTENT = "demoOnlineClassIsCancelledEmailContentTemplate";
    }

    public static class NoScheduleOnlineClassNextWeekEmailTemplate {
        public static final String SUBJECT = "noScheduleOnlineClassNextWeekEmailSubjectTemplate";
        public static final String CONTENT = "noScheduleOnlineClassNextWeekEmailContentTemplate";
    }

    public static class SchedulForTheComingWeekEmailTemplate {
        public static final String SUBJECT = "schedulForTheComingWeekEmailSubjectTemplate";
        public static final String CONTENT = "schedulForTheComingWeekEmailContentTemplate";
    }

    public static class SchedulForTodayAndTomorrowEmailTemplate {
        public static final String SUBJECT = "schedulForTodayAndTomorrowEmailSubjectTemplate";
        public static final String CONTENT = "schedulForTodayAndTomorrowEmailContentTemplate";
    }

    public static class NextClassReminderEmailTemplate {
        public static final String SUBJECT = "nextClassReminderEmailSubjectTemplate";
        public static final String CONTENT = "nextClassReminderEmailContentTemplate";
    }

    public static class ArrangeTimeReminderEmailTemplate {
        public static final String SUBJECT = "arrangeTimeReminderEmailSubjectTemplate";
        public static final String CONTENT = "arrangeTimeReminderEmailContentTemplate";
    }

    public static class AlertTeacherContractEndDateEmailTemplate {
        public static final String SUBJECT = "AlertTeacherContractEndDateEmailSubjectTemplate";
        public static final String CONTENT = "AlertTeacherContractEndDateEmailContentTemplate";
    }

    public static class ItOnlineClassIsBookedEmailTemplate {
        public static final String SUBJECT = "itOnlineClassIsBookedEmailSubjectTemplate";
        public static final String CONTENT = "itOnlineClassIsBookedEmailContentTemplate";
    }

    public static class ItOnlineClassIsCancelldEmailTemplate {
        public static final String SUBJECT = "itOnlineClassIsCancelledEmailSubjectTemplate";
        public static final String CONTENT = "itOnlineClassIsCancelledEmailContentTemplate";
    }

    public static class NextMonthTeachersNotSetEnoughTimeslotReminderEmailTemplate {
        public static final String SUBJECT = "nextMonthTeachersNotSetEnoughTimeslotReminderEmailSubjectTemplate";
        public static final String CONTENT = "nextMonthTeachersNotSetEnoughTimeslotReminderEmailContentTemplate";
    }

    public static class EducationServiceOnlineClassIsBookedEmailTemplate {
        public static final String SUBJECT = "educationServiceOnlineClassIsBookedEmailSubjectTemplate";
        public static final String CONTENT = "educationServiceOnlineClassIsBookedEmailContentTemplate";
    }

    public static class EducationServiceOnlineClassIsCancelledEmailTemplate {
        public static final String SUBJECT = "educationServiceOnlineClassIsCancelledEmailSubjectTemplate";
        public static final String CONTENT = "educationServiceOnlineClassIsCancelledEmailContentTemplate";
    }

    public static class RemindParentsBookOnlineClassEmailTemplate {
        public static final String SUBJECT = "remindParentsBookOnlineClassEmailSubjectTemplate";
        public static final String CONTENT = "remindParentsBookOnlineClassEmailContentTemplate";
    }

    public static class NextWeekItOnlineClassesReminderEmailTemplate {
        public static final String SUBJECT = "nextWeekItOnlineClassesReminderEmailSubjectTemplate";
        public static final String CONTENT = "nextWeekItOnlineClassesReminderEmailContentTemplate";
    }

    public static class TodayAndTomorrowItOnlineClassesReminderEmailTemplate {
        public static final String SUBJECT = "todayAndTomorrowItOnlineClassesReminderEmailSubjectTemplate";
        public static final String CONTENT = "todayAndTomorrowItOnlineClassesReminderEmailContentTemplate";
    }

    public static class TeacherNoShowEmailTemplate {
        public static final String SUBJECT = "teacherNoShowEmailSubjectTemplate";
        public static final String CONTENT = "teacherNoShowEmailContentTemplate";
    }

    public static class StudentNoShowEmailTemplate {
        public static final String SUBJECT = "studentNoShowEmailSubjectTemplate";
        public static final String CONTENT = "studentNoShowEmailContentTemplate";
    }

    public static class DemoOnlineClassIsFinishedEmailTemplate {
        public static final String SUBJECT = "demoOnlineClassIsFinishedEmailSubjectTemplate";
        public static final String CONTENT = "demoOnlineClassIsFinishedEmailContentTemplate";
    }

    public static class ItOnlineClassIsFinishedEmailTemplate {
        public static final String SUBJECT = "itOnlineClassIsFinishedEmailSubjectTemplate";
        public static final String CONTENT = "itOnlineClassIsFinishedEmailContentTemplate";
    }

    public static class LearningProgressIsComingToFinishEmailTemplate {
        public static final String SUBJECT = "learningProgressIsComingToFinishEmailSubjectTemplate";
        public static final String CONTENT = "learningProgressIsComingToFinishEmailContentTemplate";
    }

    public static class LearningProgressIsFinishedEmailTemplate {
        public static final String SUBJECT = "learningProgressIsFinishedEmailSubjectTemplate";
        public static final String CONTENT = "learningProgressIsFinishedEmailContentTemplate";
    }

    public static class InterviewResultEmailTemplate {
        public static final String SUBJECT = "interviewResultEmailSubjectTemplate";
        public static final String CONTENT = "interviewResultEmailContentTemplate";
    }

    public static class TeacherContractWillRunOutTemplate {
        public static final String SUBJECT = "teacherContractWillRunOutEmailSubjectTemplate";
        public static final String CONTENT = "teacherContractWillRunOutEmailContentTemplate";
    }

    public static class DemoReportIsConfirmedEmailTemplate {
        public static final String SUBJECT = "demoReportIsConfirmedEmailSubjectTemplate";
        public static final String CONTENT = "demoReportIsConfirmedEmailContentTemplate";
    }

    /**
     * apply thanks email
     */
    public static class TeacherRecruitmentApplyThxEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentApplyThxEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentApplyThxEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase1PassResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase1ResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase1PassResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase1FailResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentEmailSubjectTemplate4PartTimeTeacherInVipkid";
        public static final String CONTENT = "teacherRecruitmentPhase1FailResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase1ReapplyResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentEmailSubjectTemplate4PartTimeTeacherInVipkid";
        public static final String CONTENT = "teacherRecruitmentPhase1ReapplyResultEmailContentTemplate";
    }

    // reminder email
    public static class ReminderApplicantBefore24HoursEmailTemplate {
        public static final String SUBJECT = "reminderApplicantBefore24HoursEmailSubjectTemplate";
        public static final String CONTENT = "reminderApplicantBefore24HoursEmailContentTemplate";
    }

    // thanks demo class email
    public static class ReminderApplicantThanksForDemoClassEmailTemplate {
        public static final String SUBJECT = "reminderApplicantThanksForDemoClassEmailSubjectTemplate";
        public static final String CONTENT = "reminderApplicantThanksForDemoClassEmailContentTemplate";
    }

    // absent demo class email
    public static class ReminderApplicantAbsentDemoClassEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase2AbsentEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase2AbsentEmailContentTemplate";
    }

    // phase 2
    public static class TeacherRecruitmentPhase2PassResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase2PassEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase2PassResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase2FailResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentEmailSubjectTemplate4PartTimeTeacherInVipkid";
        public static final String CONTENT = "teacherRecruitmentPhase2FailResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase2ReapplyResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase2ResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase2ReapplyResultEmailContentTemplate";
    }


    // phase 3
    public static class TeacherRecruitmentPhase3PassResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase3ResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase3PassResultEmailContentTemplate";
    }

    // phase 3截止
    public static class TeacherRecruitmentPhase3FailResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentEmailSubjectTemplate4PartTimeTeacherInVipkid";
        public static final String CONTENT = "teacherRecruitmentPhase3FailResultEmailContentTemplate";
    }

    // 3审文档要求
    public static class TeacherRecruitmentPhase3ReapplyResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase3UpdateEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase3ReapplyResultEmailContentTemplate";
    }

    // phase 4 - training
    public static class TeacherRecruitmentPhase4PassResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase4ResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase4PassResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase4FailResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase4ResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase4FailResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase4ReapplyResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase4ResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase4ReapplyResultEmailContentTemplate";
    }

    // phase 5
    public static class TeacherRecruitmentPhase5PassResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase5PassResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase5PassResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase5FailResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase5FailResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase5FailResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase5ReapplyResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase5ReapplyResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase5ReapplyResultEmailContentTemplate";
    }

    public static class TeacherRecruitmentPhase5Practicum2ResultEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentPhase5Practicum2ResultEmailSubjectTemplate";
        public static final String CONTENT = "teacherRecruitmentPhase5Practicum2ResultEmailContentTemplate";
    }

    // contract-sign reminder邮件  116 hours --> 2 days
    public static class TeacherRecruitmentReminderContractSign116HoursEmailTemplate {
        public static final String SUBJECT = "reminderApplicantContractSign116HoursEmailSubjectTemplate";
        public static final String CONTENT = "reminderApplicantContractSign116HoursEmailContentTemplate";
    }

    // contract-sign 过期邮件
    public static class TeacherRecruitmentReminderContractSignTerminateEmailTemplate {
        public static final String SUBJECT = "teacherRecruitmentEmailSubjectTemplate4PartTimeTeacherInVipkid";
        public static final String CONTENT = "reminderApplicantContractSignTerminateEmailContentTemplate";
    }

    //
    public static class reminderFiremanAttatchDocumentFailEmailTemplate {
        public static final String SUBJECT = "reminderFiremanAttatchDocumentFailSubjectEmailTemplate";
        public static final String CONTENT = "reminderFiremanAttatchDocumentFailContentEmailTemplate";
    }
    
    //自动创建教室失败
    public static class reminderForAutoCreateClassRoomFailed{
    	 public static final String SUBJECT = "reminderForAutoCreateClassRoomFailSubjectEmailTemplate";
         public static final String CONTENT = "reminderForAutoCreateClassRoomFailContentEmailTemplate";
    }
    

    // family it test normal
    public static class FamilyItTestNormalEmailTemplate {
        public static final String SUBJECT = "familyItTestNormalSubjectEmailTemplate";
        public static final String CONTENT = "familyItTestNormalContentEmailTemplate";
    }

    // family it test abnormal
    public static class FamilyItTestAbnormalEmailTemplate {
        public static final String SUBJECT = "familyItTestAbnormalSubjectEmailTemplate";
        public static final String CONTENT = "familyItTestAbnormalContentEmailTemplate";
    }

    // teacher it test
    public static class TeacherItTestEmailTemplate {
        public static final String SUBJECT = "teacherItTestSubjectEmailTemplate";
        public static final String CONTENT = "teacherItTestContentEmailTemplate";
    }
    
    // 学生水平自测
 	public static class StudentLevelExamEmailTemplate {
 		public static final String SUBJECT = "studentLevelExamResultEmailSubjectTemplate";
 		public static final String CONTENT = "studentLevelExamResultEmailContentTemplate";
 	}
 	
 	// 2015-08-31 学生Trial水平测试
  	public static class StudentTrialLevelExamEmailTemplate {
  		public static final String SUBJECT = "studentTrialExamResultEmailSubjectTemplate";
  		public static final String CONTENT = "studentTrialExamResultEmailContentTemplate";
  	}
  	
 	 // 按照course统计当天的onlineClass 模板
 	public static class CountOnlineClassGroupByCourseTemplate {
 		public static final String SUBJECT = "countOnlineClassGroupByCourseEmailSubjectTemplate";
 		public static final String CONTENT = "countOnlineClassGroupByCourseEmailContentTemplate";
 	}
 	
 	 // opencalss 发送trial邮件
 	public static class TrialOnlineClassFromOpenClassTemplate {
 		public static final String SUBJECT = "TrialOnlineClassFromOpenClassEmailSubjectTemplate";
 		public static final String CONTENT = "TrialOnlineClassFromOpenClassEmailContentTemplate";
 	}

    
    public static class JMSExceptionToRDTeamEmailTemplate {
        public static final String SUBJECT = "JMSExceptionToRDTeamSubjectEmailTemplate";
        public static final String CONTENT = "JMSExceptionToRDTeamContentEmailTemplate";
    }
    
    // 每天统计新增的需要续费学生 模板
  	public static class StatisticsRenewStudentTemplate {
  		public static final String SUBJECT = "statisticsRenewStudentTemplateEmailSubjectTemplate";
  		public static final String CONTENT = "statisticsRenewStudentTemplateEmailContentTemplate";
  	}

  	//ITTest统计 邮件模板
  	public static class InfoITTestOfTomorrowTemplate{
  		public static final String SUBJECT = "InfoITTestOfTomorrowEmailSubjectTemplate";
  		public static final String CONTENT = "InfoITTestOfTomorrowEmailContentTemplate";
  	}
}
