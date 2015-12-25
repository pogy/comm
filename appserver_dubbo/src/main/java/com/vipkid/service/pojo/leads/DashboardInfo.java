package com.vipkid.service.pojo.leads;

public class DashboardInfo {
	private long leadsToday;
	private long leadsTodayNotFollow;
	private long leadsPreviousNotContact;
	private long leadsPreviousCallFailed;
	private long leadsWithTrialExpireTmw;
	private long leadsWithoutTrialExpireTmw;
	private long leadsTotal;
	private long needFollowupToday;
	private long followupedToday;
	private long trialTotalToday;
	private long trialFinishAsScheduledToday;
	private long trialFinishWithProblemToday;
	private long trialCanceledToday;
	private long trialBookedToday;
	private long trialTotalYest;
	private long trialFinishAsScheduledYest;
	private long trialFinishWithProblemYest;
	private long trialCanceledYest;
	private long trialBookedYest;
	private long orderTodayPayConfirmed;
	private long orderToPay;
	
	/**
	 * clt mini dashboard - 新分且未跟人数
	 */
	private long cltNeverFollow;
	
	/**
	 * clt mini dashboard - 学生总人数
	 */
	private long cltTotal;
	
	/**
	 * clt mini dashboard - 新生今日未跟
	 */
	private long cltNewNeedFollowupToday;
	
	/**
	 * clt mini dashboard - 新生今日已跟
	 */
	private long cltNewFollowUpedToday;
	
	/**
	 * clt mini dashboard - 老生今日未跟
	 */
	private long cltOldNeedFollowupToday;
	
	/**
	 * clt mini dashboard - 老生今日已跟
	 */
	private long cltOldFollowUpedToday;
	
	//major
	private long cltMajorCourseTotalToday;
	private long cltMajorCourseBookedToday;
	private long cltMajorCourseWithProblemToday;
	private long cltMajorCourseTotalYest;
	private long cltMajorCourseBookedYest;
	private long cltMajorCourseWithProblemYest;
	//ASSESSMENT
	private long cltAssessmentCourseTotalToday;
	private long cltAssessmentCourseBookedToday;
	private long cltAssessmentCourseWithProblemToday;
	private long cltAssessmentCourseTotalYest;
	private long cltAssessmentCourseBookedYest;
	private long cltAssessmentCourseWithProblemYest;
	//Kick-off - GUIDE
	private long cltGuideCourseTotalToday;
	private long cltGuideCourseBookedToday;
	private long cltGuideCourseWithProblemToday;
	private long cltGuideCourseTotalYest;
	private long cltGuideCourseBookedYest;
	private long cltGuideCourseWithProblemYest;
	//CLT-REVIEW
	private long cltReviewCourseTotalToday;
	private long cltReviewCourseBookedToday;
	private long cltReviewCourseWithProblemToday;
	private long cltReviewCourseTotalYest;
	private long cltReviewCourseBookedYest;
	private long cltReviewCourseWithProblemYest;
	
	
	public long getLeadsToday() {
		return leadsToday;
	}
	public void setLeadsToday(long leadsToday) {
		this.leadsToday = leadsToday;
	}
	public long getLeadsTodayNotFollow() {
		return leadsTodayNotFollow;
	}
	public void setLeadsTodayNotFollow(long leadsTodayNotFollow) {
		this.leadsTodayNotFollow = leadsTodayNotFollow;
	}
	public long getLeadsPreviousNotContact() {
		return leadsPreviousNotContact;
	}
	public void setLeadsPreviousNotContact(long leadsPreviousNotContact) {
		this.leadsPreviousNotContact = leadsPreviousNotContact;
	}
	public long getLeadsPreviousCallFailed() {
		return leadsPreviousCallFailed;
	}
	public void setLeadsPreviousCallFailed(long leadsPreviousCallFailed) {
		this.leadsPreviousCallFailed = leadsPreviousCallFailed;
	}
	
	public long getLeadsWithTrialExpireTmw() {
		return leadsWithTrialExpireTmw;
	}
	public void setLeadsWithTrialExpireTmw(long leadsWithTrialExpireTmw) {
		this.leadsWithTrialExpireTmw = leadsWithTrialExpireTmw;
	}
	public long getLeadsWithoutTrialExpireTmw() {
		return leadsWithoutTrialExpireTmw;
	}
	public void setLeadsWithoutTrialExpireTmw(long leadsWithoutTrialExpireTmw) {
		this.leadsWithoutTrialExpireTmw = leadsWithoutTrialExpireTmw;
	}
	public long getLeadsTotal() {
		return leadsTotal;
	}
	public void setLeadsTotal(long leadsTotal) {
		this.leadsTotal = leadsTotal;
	}
	public long getNeedFollowupToday() {
		return needFollowupToday;
	}
	public void setNeedFollowupToday(long needFollowupToday) {
		this.needFollowupToday = needFollowupToday;
	}
	public long getFollowupedToday() {
		return followupedToday;
	}
	public void setFollowupedToday(long followupedToday) {
		this.followupedToday = followupedToday;
	}
	public long getTrialTotalToday() {
		return trialTotalToday;
	}
	public void setTrialTotalToday(long trialTotalToday) {
		this.trialTotalToday = trialTotalToday;
	}
	public long getTrialFinishAsScheduledToday() {
		return trialFinishAsScheduledToday;
	}
	public void setTrialFinishAsScheduledToday(long trialFinishAsScheduledToday) {
		this.trialFinishAsScheduledToday = trialFinishAsScheduledToday;
	}
	public long getTrialFinishWithProblemToday() {
		return trialFinishWithProblemToday;
	}
	public void setTrialFinishWithProblemToday(long trialFinishWithProblemToday) {
		this.trialFinishWithProblemToday = trialFinishWithProblemToday;
	}
	public long getTrialCanceledToday() {
		return trialCanceledToday;
	}
	public void setTrialCanceledToday(long trialCanceledToday) {
		this.trialCanceledToday = trialCanceledToday;
	}
	
	public long getTrialBookedToday() {
		return trialBookedToday;
	}
	public void setTrialBookedToday(long trialBookedToday) {
		this.trialBookedToday = trialBookedToday;
	}
	public long getTrialTotalYest() {
		return trialTotalYest;
	}
	public void setTrialTotalYest(long trialTotalYest) {
		this.trialTotalYest = trialTotalYest;
	}
	public long getTrialFinishAsScheduledYest() {
		return trialFinishAsScheduledYest;
	}
	public void setTrialFinishAsScheduledYest(long trialFinishAsScheduledYest) {
		this.trialFinishAsScheduledYest = trialFinishAsScheduledYest;
	}
	public long getTrialFinishWithProblemYest() {
		return trialFinishWithProblemYest;
	}
	public void setTrialFinishWithProblemYest(long trialFinishWithProblemYest) {
		this.trialFinishWithProblemYest = trialFinishWithProblemYest;
	}
	public long getTrialCanceledYest() {
		return trialCanceledYest;
	}
	public void setTrialCanceledYest(long trialCanceledYest) {
		this.trialCanceledYest = trialCanceledYest;
	}
	
	public long getTrialBookedYest() {
		return trialBookedYest;
	}
	public void setTrialBookedYest(long trialBookedYest) {
		this.trialBookedYest = trialBookedYest;
	}
	public long getOrderTodayPayConfirmed() {
		return orderTodayPayConfirmed;
	}
	public void setOrderTodayPayConfirmed(long orderTodayPayConfirmed) {
		this.orderTodayPayConfirmed = orderTodayPayConfirmed;
	}
	public long getOrderToPay() {
		return orderToPay;
	}
	public void setOrderToPay(long orderToPay) {
		this.orderToPay = orderToPay;
	}
	public long getCltNeverFollow() {
		return cltNeverFollow;
	}
	public void setCltNeverFollow(long cltNeverFollow) {
		this.cltNeverFollow = cltNeverFollow;
	}
	public long getCltTotal() {
		return cltTotal;
	}
	public void setCltTotal(long cltTotal) {
		this.cltTotal = cltTotal;
	}
	public long getCltNewNeedFollowupToday() {
		return cltNewNeedFollowupToday;
	}
	public void setCltNewNeedFollowupToday(long cltNewNeedFollowupToday) {
		this.cltNewNeedFollowupToday = cltNewNeedFollowupToday;
	}
	public long getCltNewFollowUpedToday() {
		return cltNewFollowUpedToday;
	}
	public void setCltNewFollowUpedToday(long cltNewFollowUpedToday) {
		this.cltNewFollowUpedToday = cltNewFollowUpedToday;
	}
	public long getCltOldNeedFollowupToday() {
		return cltOldNeedFollowupToday;
	}
	public void setCltOldNeedFollowupToday(long cltOldNeedFollowupToday) {
		this.cltOldNeedFollowupToday = cltOldNeedFollowupToday;
	}
	public long getCltOldFollowUpedToday() {
		return cltOldFollowUpedToday;
	}
	public void setCltOldFollowUpedToday(long cltOldFollowUpedToday) {
		this.cltOldFollowUpedToday = cltOldFollowUpedToday;
	}
	public long getCltMajorCourseTotalToday() {
		return cltMajorCourseTotalToday;
	}
	public void setCltMajorCourseTotalToday(long cltMajorCourseTotalToday) {
		this.cltMajorCourseTotalToday = cltMajorCourseTotalToday;
	}
	public long getCltMajorCourseBookedToday() {
		return cltMajorCourseBookedToday;
	}
	public void setCltMajorCourseBookedToday(long cltMajorCourseBookedToday) {
		this.cltMajorCourseBookedToday = cltMajorCourseBookedToday;
	}
	public long getCltMajorCourseWithProblemToday() {
		return cltMajorCourseWithProblemToday;
	}
	public void setCltMajorCourseWithProblemToday(
			long cltMajorCourseWithProblemToday) {
		this.cltMajorCourseWithProblemToday = cltMajorCourseWithProblemToday;
	}
	public long getCltMajorCourseTotalYest() {
		return cltMajorCourseTotalYest;
	}
	public void setCltMajorCourseTotalYest(long cltMajorCourseTotalYest) {
		this.cltMajorCourseTotalYest = cltMajorCourseTotalYest;
	}
	public long getCltMajorCourseBookedYest() {
		return cltMajorCourseBookedYest;
	}
	public void setCltMajorCourseBookedYest(long cltMajorCourseBookedYest) {
		this.cltMajorCourseBookedYest = cltMajorCourseBookedYest;
	}
	public long getCltMajorCourseWithProblemYest() {
		return cltMajorCourseWithProblemYest;
	}
	public void setCltMajorCourseWithProblemYest(long cltMajorCourseWithProblemYest) {
		this.cltMajorCourseWithProblemYest = cltMajorCourseWithProblemYest;
	}
	public long getCltAssessmentCourseTotalToday() {
		return cltAssessmentCourseTotalToday;
	}
	public void setCltAssessmentCourseTotalToday(long cltAssessmentCourseTotalToday) {
		this.cltAssessmentCourseTotalToday = cltAssessmentCourseTotalToday;
	}
	public long getCltAssessmentCourseBookedToday() {
		return cltAssessmentCourseBookedToday;
	}
	public void setCltAssessmentCourseBookedToday(
			long cltAssessmentCourseBookedToday) {
		this.cltAssessmentCourseBookedToday = cltAssessmentCourseBookedToday;
	}
	public long getCltAssessmentCourseWithProblemToday() {
		return cltAssessmentCourseWithProblemToday;
	}
	public void setCltAssessmentCourseWithProblemToday(
			long cltAssessmentCourseWithProblemToday) {
		this.cltAssessmentCourseWithProblemToday = cltAssessmentCourseWithProblemToday;
	}
	public long getCltAssessmentCourseTotalYest() {
		return cltAssessmentCourseTotalYest;
	}
	public void setCltAssessmentCourseTotalYest(long cltAssessmentCourseTotalYest) {
		this.cltAssessmentCourseTotalYest = cltAssessmentCourseTotalYest;
	}
	public long getCltAssessmentCourseBookedYest() {
		return cltAssessmentCourseBookedYest;
	}
	public void setCltAssessmentCourseBookedYest(long cltAssessmentCourseBookedYest) {
		this.cltAssessmentCourseBookedYest = cltAssessmentCourseBookedYest;
	}
	public long getCltAssessmentCourseWithProblemYest() {
		return cltAssessmentCourseWithProblemYest;
	}
	public void setCltAssessmentCourseWithProblemYest(
			long cltAssessmentCourseWithProblemYest) {
		this.cltAssessmentCourseWithProblemYest = cltAssessmentCourseWithProblemYest;
	}
	public long getCltGuideCourseTotalToday() {
		return cltGuideCourseTotalToday;
	}
	public void setCltGuideCourseTotalToday(long cltGuideCourseTotalToday) {
		this.cltGuideCourseTotalToday = cltGuideCourseTotalToday;
	}
	public long getCltGuideCourseBookedToday() {
		return cltGuideCourseBookedToday;
	}
	public void setCltGuideCourseBookedToday(long cltGuideCourseBookedToday) {
		this.cltGuideCourseBookedToday = cltGuideCourseBookedToday;
	}
	public long getCltGuideCourseWithProblemToday() {
		return cltGuideCourseWithProblemToday;
	}
	public void setCltGuideCourseWithProblemToday(
			long cltGuideCourseWithProblemToday) {
		this.cltGuideCourseWithProblemToday = cltGuideCourseWithProblemToday;
	}
	public long getCltGuideCourseTotalYest() {
		return cltGuideCourseTotalYest;
	}
	public void setCltGuideCourseTotalYest(long cltGuideCourseTotalYest) {
		this.cltGuideCourseTotalYest = cltGuideCourseTotalYest;
	}
	public long getCltGuideCourseBookedYest() {
		return cltGuideCourseBookedYest;
	}
	public void setCltGuideCourseBookedYest(long cltGuideCourseBookedYest) {
		this.cltGuideCourseBookedYest = cltGuideCourseBookedYest;
	}
	public long getCltGuideCourseWithProblemYest() {
		return cltGuideCourseWithProblemYest;
	}
	public void setCltGuideCourseWithProblemYest(long cltGuideCourseWithProblemYest) {
		this.cltGuideCourseWithProblemYest = cltGuideCourseWithProblemYest;
	}
	public long getCltReviewCourseTotalToday() {
		return cltReviewCourseTotalToday;
	}
	public void setCltReviewCourseTotalToday(long cltReviewCourseTotalToday) {
		this.cltReviewCourseTotalToday = cltReviewCourseTotalToday;
	}
	public long getCltReviewCourseBookedToday() {
		return cltReviewCourseBookedToday;
	}
	public void setCltReviewCourseBookedToday(long cltReviewCourseBookedToday) {
		this.cltReviewCourseBookedToday = cltReviewCourseBookedToday;
	}
	public long getCltReviewCourseWithProblemToday() {
		return cltReviewCourseWithProblemToday;
	}
	public void setCltReviewCourseWithProblemToday(
			long cltReviewCourseWithProblemToday) {
		this.cltReviewCourseWithProblemToday = cltReviewCourseWithProblemToday;
	}
	public long getCltReviewCourseTotalYest() {
		return cltReviewCourseTotalYest;
	}
	public void setCltReviewCourseTotalYest(long cltReviewCourseTotalYest) {
		this.cltReviewCourseTotalYest = cltReviewCourseTotalYest;
	}
	public long getCltReviewCourseBookedYest() {
		return cltReviewCourseBookedYest;
	}
	public void setCltReviewCourseBookedYest(long cltReviewCourseBookedYest) {
		this.cltReviewCourseBookedYest = cltReviewCourseBookedYest;
	}
	public long getCltReviewCourseWithProblemYest() {
		return cltReviewCourseWithProblemYest;
	}
	public void setCltReviewCourseWithProblemYest(
			long cltReviewCourseWithProblemYest) {
		this.cltReviewCourseWithProblemYest = cltReviewCourseWithProblemYest;
	}
	
	
}
