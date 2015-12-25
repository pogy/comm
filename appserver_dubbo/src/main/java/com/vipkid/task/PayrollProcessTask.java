package com.vipkid.task;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Payroll;
import com.vipkid.model.PayrollItem;
import com.vipkid.model.Teacher;
import com.vipkid.model.Timezone;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.PayrollItemRepository;
import com.vipkid.repository.PayrollRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.security.SecurityService;

@Component
public class PayrollProcessTask {
	private Logger logger = LoggerFactory.getLogger(PayrollProcessTask.class.getSimpleName());
	
	@Resource
	private OnlineClassRepository onlineClassRepository;
	
	@Resource
	private TeacherRepository teacherRepository;
	
	@Resource
	private PayrollRepository payrollRepository;
	
	@Resource
	private PayrollItemRepository payrollItemRepository;
	
	@Resource
	private SecurityService securityService;
	
	/*@Context
	private ServletContext servletContext;*/
	
	private List<Teacher> getTopEvaluationTeachers() {
		Calendar now = Calendar.getInstance();
		return teacherRepository.getTopEvaluationTeachersByDate(now);
	}
	
	//每月固定时间发工资通知
	//@Schedule(dayOfMonth = "5", hour = "20", minute = "0", second = "0")
	@Scheduled(cron = "0 0 20 5 * ?") 
	public void sendPayrollNotificationScheduler() {
		logger.debug("Begin processing payrolls.");	
		try{
			sendPayrollToTeachers();
		}catch(Exception e){
		    //audit log
			securityService.logSystemAudit(Level.ERROR, Category.SYSTEM_ERROR, e.toString());
		    logger.error("Exception found when executing sendPayrollNotificationScheduler()" + e.getMessage(), e);
		}
	}
	
	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void sendPayrollToTeachers() {
		List<Teacher> teachers = teacherRepository.findAll();
		
		for (Teacher teacher : teachers) {
			logger.debug("sendPaymentEmailToTeacher: " + teacher.getName());
			
			if (teacher.getTimezone() == null) {
				teacher.setTimezone(Timezone.ASIA_SHANGHAI);
			}
			
			Payroll currentMonthPayroll = payrollRepository.findCurrentByTeacherId(teacher.getId());
			
			if (currentMonthPayroll != null) {
				List<PayrollItem> payrollItems = payrollItemRepository.findByPayrollId(currentMonthPayroll.getId());
				
				// standard class
				long asScheduledCount = 0;
				float asScheduledSum = 0;
				// student no show
				long studentNoShowCount = 0;
				float studentNoShowSum = 0;
				// Non-teacher tech issue
				long studentITProblemCount = 0;
				float studentITProblemSum = 0;
				// teacher no show
				long teacherNoShowCount = 0;
				float teacherNoShowSum = 0;
				// teacher tech issue
				long teacherITProblemCount = 0;
				float teacherITProblemSum = 0;
				// backup duty
				long backupDutyCount = onlineClassRepository.countBackupDutyByTeacher(teacher);
				float backupDutySum = backupDutyCount * 2;
				
				// evaluation
				long evaluation = currentMonthPayroll.getEvaluation();
				String evaluationWinner = "";
				Double evaluationExtra = (double) 0;
				List<Teacher> topEvaluation = getTopEvaluationTeachers();
				if (topEvaluation != null && topEvaluation.contains(teacher)) {
					evaluationExtra = (double)1/topEvaluation.size();
					evaluationWinner = "Which was the highest evaluation, so you will receive an extra $" + evaluationExtra + "USD per lesson - congratulations!";
				}
				
				float evaluationMoney = 0;
				float newBaseSalary = 0;
				float percentage = 0;
				float newSalary = 0;

				for (PayrollItem payrollItem : payrollItems) {
					evaluationMoney = (float) (evaluation * 0.02);
					newBaseSalary = (float) (evaluationMoney + evaluationExtra + payrollItem.getSalary());
					percentage = payrollItem.getSalaryPercentage();
					newSalary = percentage * newBaseSalary;
					//payrollItem.setBaseSalary(newBaseSalary);
					//float percentage = payrollItem.getSalaryPercentage();
					//payrollItem.setSalary(percentage * newBaseSalary);
					//payrollItemAccessor.update(payrollItem);
					
					switch (payrollItem.getOnlineClass().getFinishType()){
					case AS_SCHEDULED:
						asScheduledCount ++ ;
						asScheduledSum += newSalary;
						break;
					case STUDENT_NO_SHOW:
						studentNoShowCount ++ ;
						studentITProblemSum += newSalary;
						break;
					case STUDENT_IT_PROBLEM:
						studentITProblemCount ++ ;
						studentITProblemSum += newSalary;
						break;
					case TEACHER_NO_SHOW:
						teacherNoShowCount ++ ;
						teacherNoShowSum += newSalary;
						break;
					case TEACHER_IT_PROBLEM:
						teacherITProblemCount ++ ;
						teacherITProblemSum += newSalary;
						break;
				    default:
					    break;    
					}
				}
				
				float sumUp = asScheduledSum + studentNoShowSum + studentITProblemSum + teacherNoShowSum + teacherITProblemSum + teacherITProblemSum;
				
				Calendar currentMonthCalendar = Calendar.getInstance();
				currentMonthCalendar.setTime(currentMonthPayroll.getPaidDateTime());
				currentMonthCalendar.add(Calendar.MONTH, -1);
				String evaluationRelatedExplainString = "";
				if (evaluation != 0) {
					evaluationRelatedExplainString = "You obtained " 
							+ evaluation + "% on your monthly evaluation. " + evaluationWinner + " Your standard rate for " 
							+ currentMonthCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) 
							+ " including performance pay will therefore be $" + newBaseSalary + "USD.";
				}
				
				DecimalFormat format = new DecimalFormat("##0.00");
				
				final String payrollSummaryHTMLString = 
						"<html>" +
						"<body>" +
							"<p>Your extra class pay rate per lesson is $" + teacher.getExtraClassSalary() + "USD. " + evaluationRelatedExplainString + "</p>" +
							"<table border=1>" +
								"<thead>" +
									"<tr>" +
										"<th>Standard classes</th>" +
										"<th>Student no-show</th>" +
										"<th>Non-teacher tech issue</th>" +
										"<th>Back-up duty</th>" +
									"</tr>" +
								"</thead>" +
								"<tbody>" +
									"<tr>" +
										"<td>" + asScheduledCount + " lessons</td>" +
										"<td>" + studentNoShowCount + " lessons</td>" +
										"<td>" + studentITProblemCount + " lessons</td>" +
										"<td>" + backupDutyCount + " lessons</td>" +
									"</tr>" +
									"<tr>" +
										"<td>$" + format.format(asScheduledSum) + "</td>" +
										"<td>$" + format.format(studentNoShowSum) + "</td>" +
										"<td>$" + format.format(studentITProblemSum) + "</td>" +
										"<td>$" + format.format(backupDutyCount*2) + "</td>" +
									"</tr>" +
								"</tbody>" +
							"</table>" +
							"<p>Total: $" + format.format(sumUp) + "USD</p>" +
						"</body>" +
						"</html>";
				
				EMail.sendToTeacherMonthlyPaymentEmail(teacher, payrollSummaryHTMLString);
			}
			
		}
	}
}
