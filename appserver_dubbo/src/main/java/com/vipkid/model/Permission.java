
package com.vipkid.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.vipkid.service.pojo.StringWrapper;

public enum Permission {
	
	// TODO 由于之前put post delete 方法没有配各自的路径, 现在要配的话前后端都需要更改, 所以暂时先将其路径设为上一级路径, 待迁移完成后统一更改前后端
	// 这样的后遗症是put post delete 操作需要第一个定义的permission(通常为create)
	
	/** 学生管理 */
	ALLOW_CREATE_STUDENT("/private/students"), // 新建学生
	ALLOW_UPDATE_STUDENT("/private/students"), // 更新学生
	ALLOW_RESET_PASSWORD_STUDENT("/private/students/resetPassword"), // 重置学生密码
	ALLOW_UPDATE_LEARNING_PROGRESS_STUDENT("/private/learningProgresses/reSchedule"), //更新学生学习进度
	ALLOW_UPDATE_STUDENT_TARGET_CLASSES_PER_WEEK("/private/students/updateTargetClassesPerWeek"), //更新学生学习进度
	ALLOW_ASSIGN_STUDENT_TO_SALES("/private/students/assignToSales"),//给销售分配学生
	ALLOW_ASSIGN_STUDENT_TO_CLT("/private/students/assignToChineseLeadTeacher"),//给CLT分配学生
	ALLOW_ASSIGN_STUDENT_TO_FLT("/private/students/assignToForeignLeadTeacher"),//给FLT分配学生
    ALLOW_UPDATE_FAVORED_TEACHERS("/private/students/updateFavoredTeachers"),//收藏老师
    ALLOW_CREATE_ASSESSMENT_REPORT("/private/assessmentReports"),//创建assessmentReports
	ALLOW_CREATE_LEADS("/private/students/createLeads"), // 创建leads
	
	/** 飞船工厂皮肤管理 */
	ALLOW_CREATE_AIR_CRAFT_THEME("/private/airCraftThemes/create"), // 新建飞船工厂皮肤
	ALLOW_UPDATE_AIR_CRAFT_THEME("/private/airCraftThemes/update"), // 更新飞船工厂皮肤
	
	/** 飞船工厂管理 */
	ALLOW_CREATE_AIR_CRAFT("/private/airCrafts/create"), // 新建飞船工厂
	ALLOW_UPDATE_AIR_CRAFT("/private/airCrafts/update"), // 更新飞船工厂
	
	/** 宠物管理 */
	ALLOW_CREATE_PET("/private/pets/create"), // 新建宠物
	ALLOW_UPDATE_PET("/private/pets/update"), // 更新宠物
	
	/** medal管理 */
	ALLOW_CREATE_MEDAL("/private/medal/create"), // 新建
	ALLOW_UPDATE_MEDAL("/private/medal/update"), // 更新
	
	/** 家长管理 */
	ALLOW_CREATE_PARENT("/private/parents"), // 新建家长
	ALLOW_UPDATE_PARENT("/private/parents"), // 更新家长
	ALLOW_CHANGE_PARENT_PASSWORD("/private/parents/changePassword"), // 家长修改密码
	ALLOW_RESET_PASSWORD_PARENT("/private/parents/resetPassword"), //重置家长密码
	
	/** 教师管理 */
	ALLOW_CREATE_TEACHER("/private/teachers"), // 新建教师
	ALLOW_UPDATE_TEACHER("/private/teachers"), // 更新教师
	ALLOW_UPDATE_PERSONAL_INFO_TEACHER("/private/teachers/updatePersonalInfo"), // 更新教师基本信息
	ALLOW_RESET_PASSWORD_TEACHER("/private/teachers/resetPassword"), // 重置教师密码
	ALLOW_CHANGE_PASSWORD_TEACHER("/private/teachers/changePassword"), // 修改教师密码
	ALLOW_LOCK_TEACHER("/private/teachers/lock"), // 锁定教师
	ALLOW_UNLOCK_TEACHER("/private/teachers/unlock"), // 解锁教师
	ALLOW_QUERY_TEACHER("/private/teachers/query"), // 按一定条件获取老师列表
	ALLOW_UPDATE_APPLY_TEACHER("/private/teachers/updateApply"), // 教师招募时更新教师信息
	ALLOW_NEXT_TEACHER("/private/teachers/next"), // 教师next lifeCycle
	ALLOW_GET_TEACHER_CONTRACT_END_TIME("/private/teachers/getRegularTeacherContractDate"),//教师合同截止日期

	
	/** pay roll 管理 */
	ALLOW_CREATE_PAY_ROLL("/private/payroll"), // 新建pay roll
	ALLOW_UPDATE_PAY_ROLL("/private/payroll"), // 更新pay roll
	
	/** 教师招聘管理 */
	ALLOW_APPLY_TEACHER_APPLICATION("/private/teacherApplications/apply"), // 教师招募apply
	ALLOW_AUDIT_TEACHER_APPLICATION("/private/teacherApplications/audit"), // 教师招募audit
	ALLOW_UPDATE_TEACHER_APPLICATION("/private/teacherApplications"), // 教师招募update
	ALLOW_GET_TEACHER_LIFECYCLE_LOG("/private/teacherLifeCycleLog"),//教师招聘获取招聘流程转化
	ALLOW_GET_TEACHER_LIFECYCLE_LOG_OPERATOR("/private/teacherLifeCycleLog/getOperatorOptions"),//教师招聘获取招聘流程转化
												
	/** 家庭管理 */
	ALLOW_CREATE_FAMILY("/private/families"), // 新建家庭
	ALLOW_UPDATE_FAMILY("/private/families"), // 更新家庭
	ALLOW_DELETE_FAMILY("/private/families/delete"), // 更新家庭
	
	/** 员工管理 */
	ALLOW_CREATE_STAFF("/private/staffs"), // 新建员工
	ALLOW_UPDATE_STAFF("/private/staffs"), // 更新员工
	ALLOW_RESET_PASSWORD_STAFF("/private/staffs/resetPassword"), // 重置员工密码
	ALLOW_CHANGE_PASSWORD_STAFF("/private/staffs/changePassword"), // 修改员工密码
	ALLOW_LOCK_STAFF("/private/staffs/lock"), // 锁定员工
	ALLOW_UNLOCK_STAFF("/private/staffs/unlock"), // 解锁员工
	
	/** 销售团队管理 */
	ALLOW_CREATE_SALES_TEAM("/private/salesTeams/createSalesTeam"), // 新建sales团队
	ALLOW_CREATE_TMK_TEAM("/private/salesTeams/createTMKTeam"), // 新建TMK团队
	ALLOW_ASSIGN_SALES_TEAM("/private/salesTeams/assignToSalesTeam"), // 分配销售团队
	ALLOW_ASSIGN_TMK_TEAM("/private/salesTeams/assignToTMKTeam"), // 分配TMK团队
	
	/** 用户管理 */
	ALLOW_CHANGE_PASSWORD_USER("/private/users/changePassword"), // 修改用户密码
	
	/** partner 管理 */
	ALLOW_CREATE_PARTNER("/private/partner"), // 新建partner
	ALLOW_UPDATE_PARTNER("/private/partner"), // 修改partner
	ALLOW_LOCK_PARTNER("/private/partner/lock"), // 锁定partner
	ALLOW_UNLOCK_PARTNER("/private/partner/unLock"), // 解锁partner
	
	/** 教师评价管理 */
	ALLOW_CREATE_EDUCATIONAL_COMMENT("/private/educationalComments"), // 新建教学评价
	ALLOW_UPDATE_EDUCATIONAL_COMMENT("/private/educationalComments"), // 更新教学评价
	
	/** 教师评价管理 */
	ALLOW_CREATE_TEACHER_COMMENT("/private/teacherComments"), // 新建教师评价
	ALLOW_UPDATE_TEACHER_COMMENT("/private/teacherComments"), // 更新教师评价
	ALLOW_UPDATE_STAR_TEACHER_COMMENT("/private/teacherComments/updateStar"), // 更新星星
	
	/** 面试报告管理 */
	ALLOW_CREATE_DEMO_REPORT("/private/demoReports"), // 新建面试报告
	ALLOW_UPDATE_DEMO_REPORT("/private/demoReports"), // 更新面试报告
	ALLOW_CONFIRMED_DEMO_REPORT("/private/demoReports/confirm"), // 教学主管确认面试报告
	ALLOW_SUBMITED_DEMO_REPORT("/private/demoReports/submit"), //面试报告已提交 
	
	/** 商品权限 */
	ALLOW_CREATE_PRODUCT("/private/products"), // 新建商品
	ALLOW_UPDATE_PRODUCT("/private/products"), // 更新商品
	ALLOW_OFF_SALE_PRODUCT("/private/products/offSale"), // 商品下架
	ALLOW_ON_SALE_PRODUCT("/private/products/onSale"), // 商品上海
	
	/** 订单管理 */
	ALLOW_CREATE_ORDER("/private/orders"), // 新建订单
	ALLOW_UPDATE_ORDER("/private/orders"), // 更新订单
	ALLOW_CONFIRM_ORDER("/private/orders/confirm"), //确认订单
	ALLOW_CONFIRM_ORDER_FROM_QUERY_PAGE("/private/orders/confirmFromQueryPage"), //确认订单
	ALLOW_CANCEL_ORDER("/private/orders/cancel"),	//取消订单
    ALLOW_TRANSFER_CONFIRM_ORDER("/private/orders/transferConfirm"),//转账确认
	
	/** 在线课程 */
	ALLOW_CREATE_ONLINE_CLASS("/private/onlineClasses"), // 创建在线课程
	ALLOW_UPDATE_ONLINE_CLASS("/private/onlineClasses"), // 更新在线课程
	ALLOW_REMOVE_ONLINE_CLASS("/private/onlineClasses"), // 移除available slot
	ALLOW_CREATE_ONLINE_CLASS_CLASSROOM("/private/onlineClasses/classroom/create"), // 创建在线课程教室
	ALLOW_UPDATE_ONLINE_CLASS_CLASSROOM("/private/onlineClasses/classroom/update"), // 更新在线课程教室
	ALLOW_BOOK_ONLINE_CLASS("/private/onlineClasses/book"), // 预约在线课程
	ALLOW_CANCEL_ONLINE_CLASS("/private/onlineClasses/cancel"), // 取消在线课程
	ALLOW_FINISH_ONLINE_CLASS("/private/onlineClasses/finish"), // 完成在线课程
	ALLOW_UNDO_FINISH_ONLINE_CLASS("/private/onlineClasses/undoFinish"), // undo完成在线课程
	ALLOW_CHANGE_TEACHER_ONLINE_CLASS("/private/onlineClasses/changeTeacher"), // 换老师
	ALLOW_GET_ONLINE_CLASS_SALARY("/private/onlineClasses/getSalary"), // 获取工资信息
	
	/** 文件管理 */
	ALLOW_UPLOAD_FILE("/private/file/upload"), // 上传文件
	ALLOW_UPLOAD_EXCEL("/private/importExcel/upload"), // 上传excel
	ALLOW_DOWNLOAD_EXCEL("/private/inventionCodes/downLoadExcel"), // 下载excel
	
	/** 获取server 时间 */
	ALLOW_GET_SERVER_TIME("/private/servers/datetime"), // 获取server 时间
	
	/** 权限管理 */
	ALLOW_CREATE_ROLE_PERMISSIONS("/private/rolePermissions"), // 新建权限
	ALLOW_UPDATE_ROLE_PERMISSIONS("/private/rolePermissions"), // 编辑权限
	
	/** 面试报告管理 */
	ALLOW_CREATE_DEMOREPORT("/private/demoReports"), // 新建面试报告
	ALLOW_UPDATE_DEMOREPORT("/private/demoReports"), // 编辑面试报告
	
	/** 代理商管理 */
	ALLOW_CREATE_AGENT("/private/agent"), // 创建代理商
	ALLOW_CHANGE_STATUS_AGENT("/private/agent/changeStatus"), // 更改状态
	
	/** 市场活动管理 */
	ALLOW_CREATE_MARKETING_ACTICITY("/private/marketingActivities"), // 创建市场活动
	
	/** fireman log 管理 */
	ALLOW_CREATE_FIREMAN_LOG("/private/firemanLog/create"), // 创建fireman log
	ALLOW_UPDATE_FIREMAN_LOG("/private/firemanLog/update"), // 更新fireman log
	ALLOW_GET_STATUS_IN_FIREMAN_LOG("/private/firemanLog/findOnlineClassSupportingStatus"), // 获取状态
	ALLOW_RESOLVE_TEACHER_PROBLEM_IN_FIREMAN_LOG("/private/firemanLog/resolvedTeacherProblem"), // 解决老师问题
	ALLOW_RESOLVE_STUDENT_PROBLEM_IN_FIREMAN_LOG("/private/firemanLog/resolvedStudentProblem"), // 解决学生问题
	ALLOW_CHECK_TEACHER_NOT_ENTER_IN_FIREMAN_LOG("/private/firemanLog/checkTeacherNotEnterYell"), // 检查老师是否进入
	ALLOW_CHECK_STUDENT_NOT_ENTER_IN_FIREMAN_LOG("/private/firemanLog/checkStudentNotEnterYell"), // 检查学生是否进入

	/** fireman comment 管理 */
	ALLOW_CREATE_FIREMAN_TO_STUDENTS_COMMENT("/private/firemanToStudentComments"), // 创建fireman to students comment
	ALLOW_UPDATE_FIREMAN_TO_STUDENTS_COMMENT("/private/firemanToStudentComments"), // 更新fireman to students comment
	ALLOW_CREATE_FIREMAN_TO_TEACHER_COMMENT("/private/firemanToTeacherComments"), // 创建fireman to teacher comment
	ALLOW_UPDATE_FIREMAN_TO_TEACHER_COMMENT("/private/firemanToTeacherComments"), // 更新fireman to teacher comment
	
	/** setting 管理 */
	ALLOW_CREATE_SETTING("/private/settings/create"), // 新建全局设置
	ALLOW_UPDATE_SETTING("/private/settings/update"), // 编辑全局设置
	ALLOW_CREATE_CHANNEL_LEVEL_SETTING("/private/settings/createChannelLevel"), // 在redis中新建渠道评级设置
	
	/** follow up 管理 */
	ALLOW_CREATE_FOLLOW_UP("/private/followUps"), // 创建follow up

    /** fireman 兼课 */
    ALLOW_STUDENT_SET_IN_CLASSROOM("/private/FiremanOnlineClassSupportingStatusRedisService/setStudentInClassroom"), //学生进教室
    ALLOW_TEACHER_SET_IN_CLASSROOM("/private/FiremanOnlineClassSupportingStatusRedisService/setTeacherInClassroom"), //老师进教室
    ALLOW_STUDENT_HAVE_PROBLEM("/private/FiremanOnlineClassSupportingStatusRedisService/setStudentHavingProblem"),	//学生求助
    ALLOW_TEACHER_HAVE_PROBLEM("/private/FiremanOnlineClassSupportingStatusRedisService/setTeacherHavingProblem"),	//老师求助
    ALLOW_RESOLVE_STUDENT_PROBLEM("/private/FiremanOnlineClassSupportingStatusRedisService/setStudentResolvedProblem"), //解决学生问题
    ALLOW_RESOLVE_TEACHER_PROBLEM("/private/FiremanOnlineClassSupportingStatusRedisService/setTeacherResolvedProblem"), //解决老师问题

    /**课程管理**/
    ALLOW_UPDATE_COURCE("/private/courses"),
    ALLOW_UPDATE_LEVEL("/private/levels"),
    ALLOW_UPDATE_UNIT("/private/units"),
    ALLOW_UPDATE_LEARNINGCYCLE("/private/learningCycles"),
    ALLOW_UPDATE_LESSON("/private/lessons"),
    
    /**微信支付**/
    ALLOW_START_WECHATPAY_REQUEST("/private/wechatpay/buildRequest"),
    ALLOW_START_WECHATPAY_NOTIFY("/private/wechatpay/jsPayNotify"),
    
	/** channel level 管理 */
	ALLOW_CREATE_CHANNEL_LEVEL("/private/channelLevels/create"), // 新建评级渠道
    
    /**channel manage 管理**/
    ALLOW_UPDATE_CHANNEL_MANAGE("/private/channelManage/update"),
    ALLOW_CREATE_CHANNEL_MANAGE("/private/channelManage/create"),
    ALLOW_CHANGESTATUS_CHANNEL_MANAGE("/private/channelManage/changeStatus"),
    
    /**channel 管理**/
    ALLOW_UPDATE_CHANNEL("/private/channel/update"),
    ALLOW_CREATE_CHANNEL("/private/channel/create"),
    ALLOW_CHANGESTATUS_CHANNEL("/private/channel/changeStatus"),
	
	/** leads管理 */
	ALLOW_ASSIGN_LEADS_TO_SALES_OR_TMK("/private/leadsDispatch/manualLeadsAssign"), // 分配leads给sales或者TMK
	ALLOW_ASSIGN_LEADS_TO_SELF("/private/leadsDispatch/selfLeadsAssign"), // 分配leads给自己
	ALLOW_LOCK_LEADS("/private/leadsDispatch/lockLeads"), // 锁定leads
	ALLOW_UNLOCK_LEADS("/private/leadsDispatch/unlockLeads"), // 解锁leads
	ALLOW_RELEASE_LEADS("/private/leadsDispatch/releaseLeads"), // 释放leads
	
	/**导入course的xml文件*/
	ALLOW_IMPORT_COURSE("private/importCourse/upload"),
	
	/**openclass 公开课*/
	ALLOW_OPEN_CLASS_CREATE("private/openclass/create"),
	ALLOW_OPEN_CLASS_UPDATE("private/openclass/update"),
	ALLOW_OPEN_CLASS_CHANGESTATUS("private/openclass/changeStatus"),
	
	/**private/updateStars 批量更新星星数*/
	ALLOW_UPDATE_STARS("private/updateStars/upload"),
	
	/**private/peakTimeRule peak time相关*/
	ALLOW_PEAKTIME_RULE_CREATE("private/peakTimeRule/createRule"),
	ALLOW_PEAKTIME_SAVE_RULES("private/peakTimeRule/saveRules"),
	ALLOW_PEAKTIME_SAVE_RULE("private/peakTimeRule/saveRule"),
	ALLOW_PEAKTIME_APPLY_RULES("private/peakTimeRule/applyRules"),
	ALLOW_PEAKTIME_GET_BY_TIME_RANGE("private/peakTime/getByTimeRange"),
	
	//允许手机家长端约课
	ALLOW_MOBILEPARENT_BOOK_ONLINECLASS("/mobile/bookOnlineClassformobile"),
	ALLOW_MOBILEPARENT_CANCEL_ONLINECLASS("/mobile/cancelOnlineClassformobile"),
	
	//允许招聘端约课 取消课
	ALLOW_RECRUITMENT_BOOK_ONLINECLASS("/recruitment/bookOnlineClassforRecruitment"),
	ALLOW_RECRUITMENT_CANCEL_ONLINECLASS("/recruitment/cancelOnlineClassforRecruitment"),
	
	/**private/trialThreshold trial threshold相关*/
	ALLOW_TRIALTHRESHOLD_RULE_CREATE("private/trialThresholdRule/createRule"),
	ALLOW_TRIALTHRESHOLD_SAVE_RULES("private/trialThresholdRule/saveRules"),
	ALLOW_TRIALTHRESHOLD_SAVE_RULE("private/trialThresholdRule/saveRule"),
	ALLOW_TRIALTHRESHOLD_APPLY_RULES("private/trialThresholdRule/applyRules"),
	ALLOW_TRIALTHRESHOLD_GET_BY_TIME_RANGE("private/trialThresholdRule/getByTimeRange"),
	
	/** 
	 * 将编码之后的perission 传到前端以减少数据量，以及避免字符太长cookie存不下
	 * 注意: 新增ACCESS 权限后需要在管理端前端的config.js 中的permissionCodesMap 进行同步更新
	 */
	ACCESS_USER("us1"),
	ACCESS_USER_STUDENTS("us2"),
	ACCESS_USER_STUDENTS_ASSIGN_TO_SALES("us3"),
	ACCESS_USER_STUDENTS_ASSIGN_TO_CLT("us4"),
	ACCESS_USER_STUDENTS_ASSIGN_TO_FLT("us5"),
	ACCESS_USER_STUDENTS_STUDENT("us6"),
	ACCESS_USER_STUDENTS_STUDENT_BASIC_INFO("us7"),
	ACCESS_USER_STUDENTS_STUDENT_BASIC_INFO_EDIT("us8"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO("us9"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_CONTACT_FLOW("us10"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_CONTACT_FLOW_ADD("us11"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_LEARNING_PROGRESS("us12"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_LEARNING_PROGRESS_EDIT("us13"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_FEEDBACK("us14"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_CLASS("us15"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_ASSESMENT_REPORT("us16"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_ASSESMENT_REPORT_UPLOAD("us17"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_STAR_BADGE("us18"),
	ACCESS_USER_STUDENTS_STUDENT_RELATED_INFO_STAR_BADGE_EDIT("us19"),
	ACCESS_USER_STUDENTS_STUDENT_BOOK_ONLINE_CLASS("us20"),
	ACCESS_USER_STUDENTS_STUDENT_RESET_PASSWORD("us21"),
	ACCESS_USER_STUDENTS_STUDENT_TARGET_CLASSES_PER_WEEK("us22"),
	
	ACCESS_USER_FAMILIES("uf1"),
	ACCESS_USER_FAMILIES_ADD("uf2"),
	ACCESS_USER_FAMILIES_PARENT_BASIC_INFO("uf3"),
	ACCESS_USER_FAMILIES_PARENT_BASIC_INFO_EDIT("uf4"),
	ACCESS_USER_FAMILIES_PARENT_RESET_PASSWORD("uf5"),
	ACCESS_USER_FAMILIES_FAMILY_ADDRESS_INFO("uf6"),
	ACCESS_USER_FAMILIES_FAMILY_ADDRESS_INFO_EDIT("uf7"),
	ACCESS_USER_FAMILIES_FAMILY_PAYMENT_INFO("uf8"),
	ACCESS_USER_FAMILIES_FAMILY_PAYMENT_INFO_EDIT("uf9"),
	ACCESS_USER_FAMILIES_PARENT_ADD("uf10"),
	ACCESS_USER_FAMILIES_STUDENT_ADD("uf11"),
	
	ACCESS_USER_TEACHERS("ut1"),
	ACCESS_USER_TEACHERS_ADD("ut2"),
	ACCESS_USER_TEACHERS_TEACHER("ut3"),
	ACCESS_USER_TEACHERS_TEACHER_BASIC_INFO("ut4"),
	ACCESS_USER_TEACHERS_TEACHER_BASIC_INFO_EDIT("ut5"),
	ACCESS_USER_TEACHERS_TEACHER_RECRUITMENT_AUDIT("ut6"),
	ACCESS_USER_TEACHERS_TEACHER_RELATED_INFO("ut7"),
	ACCESS_USER_TEACHERS_TEACHER_RELATED_INFO_COMMENT("ut8"),
	ACCESS_USER_TEACHERS_TEACHER_RELATED_INFO_SALARY("ut9"),
	ACCESS_USER_TEACHERS_TEACHER_RELATED_INFO_SALARY_EDIT("ut10"),
	ACCESS_USER_TEACHERS_TEACHER_RELATED_INFO_CERTIFICATE("ut11"),
	ACCESS_USER_TEACHERS_TEACHER_RELATED_INFO_CONTRACT("ut12"),
	ACCESS_USER_TEACHERS_TEACHER_PAYMENT_INFO("ut13"),
	ACCESS_USER_TEACHERS_TEACHER_AVATAR_UPLOAD("ut14"),
	ACCESS_USER_TEACHERS_TEACHER_RESET_PASSWORD("ut15"),
	ACCESS_USER_TEACHERS_TEACHER_LOCK("ut16"),
	ACCESS_USER_TEACHERS_TEACHER_EVALUATE_INFO("ut17"),
	ACCESS_USER_TEACHERS_TEACHER_EVALUATE_INFO_EDIT("ut18"),
	ACCESS_TEACHER_BANK_ACCOUNT(""), // 统一更改后暂不用此项
	
	ACCESS_USER_PARTNERS("up1"),
	ACCESS_USER_PARTNERS_ADD("up2"),
	ACCESS_USER_PARTNERS_PARTNER("up3"),
	ACCESS_USER_PARTNERS_PARTNER_EDIT("up4"),
	ACCESS_USER_PARTNERS_PARTNER_LOCK("up5"),
	
	ACCESS_MARKETING("ma1"),
	ACCESS_MARKETING_AGENTS(""),
	ACCESS_MARKETING_CHANNEL_MANAGER("ma2"),//channel manager 
	ACCESS_MARKETING_CHANNEL("ma3"), // channel
	
	ACCESS_SALES("sp1"),
	ACCESS_SALES_PRODUCTS("sp2"),
	ACCESS_SALES_PRODUCTS_ADD("sp3"),
	ACCESS_SALES_PRODUCTS_EDIT("sp4"),
	
	ACCESS_EDUCATION("ec1"),
	ACCESS_EDUCATION_COMMENTS("ec2"),
	ACCESS_EDUCATION_COMMENTS_TEACHER_COMMENT_EDIT("ec3"),
	ACCESS_EDUCATION_COMMENTS_FIREMAN_COMMENT_EDIT("ec4"),
	
	ACCESS_EDUCATION_DEMO_REPORTS("ecd1"),
	ACCESS_EDUCATION_DEMO_REPORTS_DEMO_REPORT("ed2"),
	ACCESS_EDUCATION_DEMO_REPORTS_DEMO_REPORT_EDIT("ed3"),
	ACCESS_EDUCATION_DEMO_REPORTS_DEMO_REPORT_CONFIRM("ed4"),
	
	ACCESS_EDUCATION_PARENTSFEEDBACK("ecp1"),
	
	ACCESS_OPERATION("op1"),
	ACCESS_OPERATION_PRESCHEDULE("op2"),
	ACCESS_OPERATION_ONLINE_CLASSES("oo1"),
	ACCESS_OPERATION_MOXTRA_USERS("om1"),
	ACCESS_OPERATION_FIREMAN("of1"),
	ACCESS_OPERATION_ONLINE_CLASSES_TEACHER_SEARCH("oo2"),
	ACCESS_OPERATION_ONLINE_CLASSES_STUDENT_SEARCH("oo3"),
	ACCESS_OPERATION_ONLINE_CLASSES_SALES_SEARCH("oo4"),
	ACCESS_OPERATION_ONLINE_CLASSES_BOOK_ACTION("oo5"),
	ACCESS_OPERATION_ONLINE_CLASSES_CANCEL_ACTION("oo6"),
	ACCESS_OPERATION_ONLINE_CLASSES_FINISH_ACTION("oo7"),
	ACCESS_OPERATION_ONLINE_CLASSES_UNDO_FINISH_ACTION("oo8"),
	ACCESS_OPERATION_ONLINE_CLASSES_CHANGE_TEACHER_ACTION("oo9"),
	ACCESS_OPERATION_ONLINE_CLASSES_REPLAY_ACTION("oo10"),
	ACCESS_OPERATION_ONLINE_CLASSES_CREATE_CLASSROOM_ACTION("oo11"),
	ACCESS_OPERATION_ONLINE_CLASSES_GOTO_CLASSROOM_ACTION("oo12"),
	ACCESS_OPERATION_ONLINE_CLASSES_REUPLOAD_ATTACHMENT_ACTION("oo13"),
	ACCESS_OPERATION_ONLINE_CLASSES_BOOK_BUTTON("oo14"),
	
	ACCESS_CURRICULUM("cc1"),
	ACCESS_CURRICULUM_COURSES("cc2"),
	ACCESS_CURRICULUM_COURSES_EDIT("cc3"),
	ACCESS_CURRICULUM_OPEN_CLASS_ADD("cc4"),
	ACCESS_CURRICULUM_OPEN_CLASS_UPDATE("cc5"),
	ACCESS_CURRICULUM_OPEN_CLASS_ON_OFF("cc6"),
	
	ACCESS_FINANCE("fo1"),
	ACCESS_FINANCE_ORDERS("fo2"),
	ACCESS_FINANCE_ORDERS_ADD("fo3"),
	ACCESS_FINANCE_ORDERS_CONFIRM("fo4"),
	ACCESS_FINANCE_ORDERS_CANCEL("fo5"),
	ACCESS_FINANCE_BALANCE("fb1"),
	ACCESS_FINANCE_BALANCE_EDIT("fb2"),
	
	ACCESS_STAFF("ss1"),
	ACCESS_STAFF_STAFFS("ss2"),
	ACCESS_STAFF_STAFFS_ADD("ss3"),
	ACCESS_STAFF_STAFFS_STAFF("ss4"),
	ACCESS_STAFF_STAFFS_STAFF_EDIT("ss5"),
	ACCESS_STAFF_STAFFS_STAFF_LOCK("ss6"),
	ACCESS_STAFF_STAFFS_STAFF_RESET_PASSWORD("ss7"),
	ACCESS_STAFF_SALES_TEAM("sst1"),
	ACCESS_STAFF_SALES_TEAM_SALES_MANAGER("sst2"),
	ACCESS_STAFF_SALES_TEAM_TMK_MANAGER("sst3"),
	ACCESS_STAFF_SALES_TEAM_ASSIGN_SALES("sst4"),
	ACCESS_STAFF_SALES_TEAM_ASSIGN_TMK("sst5"),
	ACCESS_STAFF_SALES_TEAM_ADD_SALES_TEAM("sst6"),
	ACCESS_STAFF_SALES_TEAM_ADD_TMK_TEAM("sst7"),
	ACCESS_STAFF_SALES_TEAM_RESET_QUEUE("sst8"),

	// behaviour 访问权限
	ACCESS_BEHAVIOUR("bhv1"),
	
	ACCESS_DASHBOARD("db1"),
	ACCESS_DASHBOARD_MANAGER("dm1"),
	ACCESS_DASHBOARD_MANAGER_DASHBOARDS("dm2"),
	ACCESS_DASHBOARD_DIRECTOR("dd1"),
	ACCESS_DASHBOARD_DIRECTOR_DASHBOARDS("dd2"),
	
	ACCESS_LEADS("l1"),
	ACCESS_LEADS_STUDENTS("ls1"),
	ACCESS_LEADS_STUDENTS_SELECT_SALES_OF_MANAGER("ls2"),
	ACCESS_LEADS_STUDENTS_SELECT_SALES_ALL("ls3"),
	ACCESS_LEADS_STUDENTS_SELECT_TMK_OF_MANAGER("ls4"),
	ACCESS_LEADS_STUDENTS_SELECT_TMK_ALL("ls5"),
	ACCESS_LEADS_STUDENTS_SELECT_ASSIGN_TO_SALES_TIME("ls6"),
	ACCESS_LEADS_STUDENTS_SELECT_ASSIGN_TO_TMK_TIME("ls7"),
	ACCESS_LEADS_STUDENTS_ASSIGN_TO_SALES("ls8"),
	ACCESS_LEADS_STUDENTS_ASSIGN_TO_TMK("ls9"),
	ACCESS_LEADS_STUDENTS_ONLY_SELF_SALES("ls10"), // 标识只列出sales自己名下的leads
	ACCESS_LEADS_STUDENTS_ONLY_SELF_TMK("ls11"), // 标识只列出tmk自己名下的leads
	ACCESS_LEADS_STUDENTS_STUDENT("ls12"),
	ACCESS_LEADS_STUDENTS_STUDENT_ASSIGN_TO_SELF("ls13"),
	ACCESS_LEADS_ONLINECLASSES("loc1"),
	ACCESS_LEADS_ORDERS("lo1"),
	ACCESS_LEADS_CHANNEL("alc"),
	
	ACCESS_CLT("acc1"),
	ACCESS_CLT_STUDENTS("cs2"),
	ACCESS_CLT_ONLINE_CLASSES("coc3"),
	ACCESS_CLT_CLT_ALL("cca4"),
	ACCESS_CLT_STUDENTS_ASSIGN_TO_CLT("cca5"),
	
	ACCESS_SHOW_PARENTS_CELLPHONE("spc"),
	ACCESS_SHOW_TEACHER_FULLNAME("stf"),
	ACCESS_SHOW_TEACHER_EMAIL("ste"),
	
	ACCESS_SYSTEM("sysa1"),
	ACCESS_SYSTEM_AUDITS(""), // 统一更改后暂不用此项
	ACCESS_SYSTEM_SETTINGS(""), // 统一更改后暂不用此项
	
	ACCESS_MINI_DASHBOARD("md1"),
	ACCESS_MINI_DASHBOARD_SALE("md2"),
	ACCESS_MINI_DASHBOARD_CLT("md3");
	
	
	private String uri;	
	
	private Permission(String uri) {
		this.uri = uri;
	}	

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public static String getNameByUri(String uri){
		for (Permission permission : values()) {
			 if (StringUtils.isNotBlank(permission.getUri()) && StringUtils.endsWithIgnoreCase(uri, permission.getUri())){
				 return permission.name();
			 }
		  }
		return null;
	}

	public static List<StringWrapper> getNames() {
	  List<StringWrapper> names = new ArrayList<StringWrapper>();
	  
	  for (Permission permission : values()) {
		  names.add(new StringWrapper(permission.name()));
	  }
	
	  return names;
  }
}

