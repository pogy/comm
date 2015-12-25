package com.vipkid.ext.email;

import com.vipkid.ext.dby.AttachDocumentResult;
import com.vipkid.model.*;
import com.vipkid.model.Course.Type;
import com.vipkid.service.pojo.CountOnlineClassByCourseView;
import com.vipkid.service.pojo.parent.StatisticsRenewStudentView;
import com.vipkid.template.AbstractTemplates;
import com.vipkid.template.EmailTemplates;
import com.vipkid.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class EMail {
    private static final Logger logger = LoggerFactory.getLogger(EMail.class.getSimpleName());

    private static Session session;

    static Properties properties = System.getProperties();
    
    static {
        
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", Configurations.EMail.HOST);

        session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Configurations.EMail.USERNAME, Configurations.EMail.PASSWORD);
            }
        });
    }

    private static void send(final String from, final String to, final String subject, final String content) {
        send(from, to, null/*no cc*/, subject, content);
    }

    private static void sendWithCC(final String from, final String to, final String subject, final String content, final String cc) {
        send(from, to, cc, subject, content);
    }

    private static void send(final String from, final String to, final String cc, final String subject, final String content) {
        if (Configurations.Deploy.ENABLE_EMAIL) {
            checkSubjectIsNull(subject);
            checkContentIsNull(content);
            if (!NetworkUtils.checkMacAddressIsValidated()) {
                logger.error("try to send email from: " + NetworkUtils.getMacAddress() + ", but dennied");
                return;
            }
            final String contentAddMacInfo = writeMacAddressToEmailContent(content);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        MimeMessage message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(from));
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        if (cc != null) {
                            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
                        }
                        message.setSubject(MimeUtility.encodeText(subject, CharSet.UTF_8, "B"));
                        message.setContent(contentAddMacInfo, "text/html;charset=utf-8");
                        Transport.send(message);
                    } catch (MessagingException e) {
                        logger.error("Error when sending email from {} to {} with exception {}", from, to, e.getMessage());
                    }
                    return true;
                }
            });
            executorService.submit(futureTask);
        } else {
            logger.info("Debug mode, skip EMAIL Sending");
        }
    }
    
    // send email with another user and passwd
    private static void sendWithUserAndPwd(final String from, final String to, final String cc, final String subject, final String content, String pwd) {
        if (Configurations.Deploy.ENABLE_EMAIL) {
            checkSubjectIsNull(subject);
            checkContentIsNull(content);
            if (!NetworkUtils.checkMacAddressIsValidated()) {
                logger.error("try to send email from: " + NetworkUtils.getMacAddress() + ", but dennied");
                return;
            }
            final String contentAddMacInfo = writeMacAddressToEmailContent(content);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            
            Session tc_session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Configurations.EMail.TC_USERNAME, Configurations.EMail.TC_PASSWORD);
                }
            });
            
            FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        MimeMessage message = new MimeMessage(tc_session);
                        message.setFrom(new InternetAddress(from));
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        if (cc != null) {
                            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
                        }
                        message.setSubject(MimeUtility.encodeText(subject, CharSet.UTF_8, "B"));
                        message.setContent(contentAddMacInfo, "text/html;charset=utf-8");
                        Transport.send(message);
                    } catch (MessagingException e) {
                        logger.error("Error when sending email from {} to {} with exception {}", from, to, e.getMessage());
                    }
                    return true;
                }
            });
            executorService.submit(futureTask);
        } else {
            logger.info("Debug mode, skip EMAIL Sending");
        }
    }
    
 // send email with another user and passwd
    private static void sendWithUserAndPwdForTCEducation(final String to, final String cc, final String subject, final String content) {
        if (Configurations.Deploy.ENABLE_EMAIL) {
            checkSubjectIsNull(subject);
            checkContentIsNull(content);
            if (!NetworkUtils.checkMacAddressIsValidated()) {
                logger.error("try to send email from: " + NetworkUtils.getMacAddress() + ", but dennied");
                return;
            }
            final String contentAddMacInfo = writeMacAddressToEmailContent(content);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            
            Session tc_edu_session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Configurations.EMail.TeacherRecruit_Email_Education_USERNAME, Configurations.EMail.TeacherRecruit_Email_Education_PWD);
                }
            });
            
            FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                	String from = Configurations.EMail.TeacherRecruit_Email_Education_USERNAME;
                    try {
                        MimeMessage message = new MimeMessage(tc_edu_session);
                        message.setFrom(new InternetAddress(from));
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        if (cc != null) {
                            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
                        }
                        message.setSubject(MimeUtility.encodeText(subject, CharSet.UTF_8, "B"));
                        message.setContent(contentAddMacInfo, "text/html;charset=utf-8");
                        Transport.send(message);
                    } catch (MessagingException e) {
                        logger.error("Error when sending email from {} to {} with exception {}", from, to, e.getMessage());
                    }
                    return true;
                }
            });
            executorService.submit(futureTask);
        } else {
            logger.info("Debug mode, skip EMAIL Sending");
        }
    }

    private static String writeMacAddressToEmailContent(String content) {
        String currentMacAddress = NetworkUtils.getMacAddress();
        // 如果不在默认mac地址中也发送邮件了，则记录地址在邮件中
        if (!ArrayUtils.contains(Configurations.DefaultMacAddress.PRODUCE, currentMacAddress)
                && !ArrayUtils.contains(Configurations.DefaultMacAddress.BETA, currentMacAddress)
                && !currentMacAddress.equals(Configurations.DefaultMacAddress.STA)) {
            StringBuilder macAddressInfo = new StringBuilder();
            macAddressInfo
                    .append("<p style='display: none;'>")
                    .append("Mac address : ")
                    .append(currentMacAddress == null ? "" : currentMacAddress)
                    .append("</p>");
            String contentAddMacInfo = content + macAddressInfo.toString();
            logger.error("send mail form server:" + currentMacAddress == null ? "" : currentMacAddress);
            return contentAddMacInfo;
        }
        return content;
    }

    private static void checkSubjectIsNull(String subject) {
        if (subject == null) {
            throw new IllegalStateException("email subject is null.");
        }
    }

    private static void checkContentIsNull(String content) {
        if (content == null) {
            throw new IllegalStateException("email content is null.");
        }
    }

    // 当新用户注册时，发邮件给统计邮件组
    public static void sendNewParentSignupEmail(String mobile) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("mobile", mobile);
        String subject = template.render(EmailTemplates.NewParentSignupEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.NewParentSignupEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, Configurations.EMail.RECEIVER_STATISTICS, subject, content);
    }

    // 向老师发送重置密码邮件
    public static void sendResetTeacherPasswordEmail(String name, String email, String password) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("name", name);
        paramsMap.put("password", password);
        String subject = template.render(EmailTemplates.ResetTeacherPasswordEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ResetTeacherPasswordEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, email, subject, content);
    }

    // 向员工发送重置密码邮件
    public static void sendResetStaffPasswordEmail(String email, String password) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("password", password);
        String subject = template.render(EmailTemplates.ResetStaffPasswordEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ResetStaffPasswordEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, email, subject, content);
    }

    // 当用户登陆时，发邮件给统计邮件组
    public static void sendNewUserLoginEmail(String username) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("username", username);
        String subject = template.render(EmailTemplates.NewUserLoginEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.NewUserLoginEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, Configurations.EMail.RECEIVER_STATISTICS, subject, content);
    }

    //发送用户取消订单的邮件
    public static void sendCancelOrderEmail(String parentName, String orderSN, String parentMobile) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("parentName", parentName);
        paramsMap.put("orderSN", orderSN);
        paramsMap.put("parentMobile", parentMobile);
        String subject = template.render(EmailTemplates.cancelOrderEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.cancelOrderEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, Configurations.EMail.RECEIVER_STATISTICS, subject, content);
    }

    //给老师发送工资邮件
    public static void sendToTeacherMonthlyPaymentEmail(Teacher teacher, final String payrollTableString) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", teacher.getSafeName());
        paramsMap.put("payroll", payrollTableString);
        String subject = template.render(EmailTemplates.currentMonthPayrollEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.currentMonthPayrollEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
    }

    /**
     * 约课相关：邮件to老师 start
     */
    // 1-1课程被预订(48小时内)
    public static void sendToTeacherWhenOneOnOneOnlineClassIsBookedIn48HoursEmail(OnlineClass onlineClass, Student student) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", onlineClass.getTeacher().getName());
        paramsMap.put("studentEnglishName", student.getEnglishName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        paramsMap.put("dateTime", DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(onlineClass.getTeacher().getTimezone())));
        paramsMap.put("serialNumber", onlineClass.getLesson().getSerialNumber());
        paramsMap.put("lesssonName", onlineClass.getLesson().getName());
        String subject = template.render(EmailTemplates.OneOnOneOnlineClassIsBookedIn48HoursEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.OneOnOneOnlineClassIsBookedIn48HoursEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, onlineClass.getTeacher().getEmail(), subject, content);
    }

    // 1-1课程被取消(48小时内)
    public static void sendToTeacherWhenOneOnOneOnlineClassIsCancelledIn48HoursEmail(OnlineClass onlineClass, Student student) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", onlineClass.getTeacher().getName());
        paramsMap.put("studentEnglishName", student.getEnglishName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        paramsMap.put("dateTime", DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(onlineClass.getTeacher().getTimezone())));
        paramsMap.put("serialNumber", onlineClass.getLesson().getSerialNumber());
        paramsMap.put("lessonName", onlineClass.getLesson().getName());
        String subject = template.render(EmailTemplates.OneOnOneOnlineClassIsCancelledIn48HoursEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.OneOnOneOnlineClassIsCancelledIn48HoursEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, onlineClass.getTeacher().getEmail(), subject, content);
    }

    // 面试课预订提醒(每一次)
    public static void sendToTeacherWhenDemoOnlineClassIsBookedEmail(OnlineClass onlineClass, Student student, Course course) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", onlineClass.getTeacher().getName());
        paramsMap.put("studentEnglishName", student.getEnglishName());
        paramsMap.put("courseName", course.getAssignationName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        paramsMap.put("timeToTeacher", DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(onlineClass.getTeacher().getTimezone())));
        String subject = template.render(EmailTemplates.DemoOnlineClassIsBookedEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.DemoOnlineClassIsBookedEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, onlineClass.getTeacher().getEmail(), subject, content);
    }

    // 面试课取消提醒(每一次)
    public static void sendToTeacherWhenDemoOnlineClassIsCanceledEmail(OnlineClass onlineClass, Student student, Course course) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", onlineClass.getTeacher().getName());
        paramsMap.put("studentEnglishName", student.getEnglishName());
        paramsMap.put("courseName", course.getAssignationName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        paramsMap.put("timeToTeacher", DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(onlineClass.getTeacher().getTimezone())));
        String subject = template.render(EmailTemplates.DemoOnlineClassIsCancelledEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.DemoOnlineClassIsCancelledEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, onlineClass.getTeacher().getEmail(), subject, content);
    }

    // 提醒下一周课程or无课(每周六)
    public static void sendToTeacherScheduleTheComingWeekEmail(Teacher teacher, List<OnlineClass> onlineClasses, List<OnlineClass> backUpOnlineClasses) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        StringBuilder onlineClassesList = new StringBuilder();
        if (onlineClasses.isEmpty()) {
            String subject = template.render(EmailTemplates.NoScheduleOnlineClassNextWeekEmailTemplate.SUBJECT, paramsMap);
            String content = template.render(EmailTemplates.NoScheduleOnlineClassNextWeekEmailTemplate.CONTENT, paramsMap);

            send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);

            onlineClassesList.append("Null");
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            onlineClassesList.append("<table  border=1> \n")
                    .append("<tr>")
                    .append("<td>").append("Scheduled Time" + "(" + TimeZone.getTimeZone(teacher.getTimezone()).getID() + ")").append("</td>")
                    .append("<td>").append("Course Name").append("</td>")
                    .append("<td>").append("Lesson Name").append("</td>")
                    .append("<td>").append("Student").append("</td>")
                    .append("</tr>");
            for (OnlineClass onlineClass : onlineClasses) {
                if (onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.DEMO
                        || onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.TRIAL) {
                    onlineClassesList.append("<tr style='color: red;'>");
                } else {
                    onlineClassesList.append("<tr>");
                }
                onlineClassesList
                        .append("<td>").append(DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(teacher.getTimezone()))).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getName()).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getName()).append("</td>")
                        .append("<td>");
                for (Student student : onlineClass.getStudents()) {
                    onlineClassesList.append(student.getEnglishName()).append(TextUtils.SPACE);
                }
                onlineClassesList.append("</td>")
                        .append("</tr>");
            }
            onlineClassesList.append("</table>");
        }

        StringBuilder backUpOnlineClassesList = new StringBuilder();
        if (backUpOnlineClasses.isEmpty()) {

            backUpOnlineClassesList.append("Null");
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            backUpOnlineClassesList.append("<table  border=1> \n")
                    .append("<tr>")
                    .append("<td>").append("Scheduled Time" + "(" + TimeZone.getTimeZone(teacher.getTimezone()).getID() + ")").append("</td>")
                    .append("<td>").append("Course Name").append("</td>")
                    .append("<td>").append("Lesson Name").append("</td>")
                    .append("<td>").append("Student").append("</td>")
                    .append("</tr>");
            for (OnlineClass onlineClass : backUpOnlineClasses) {
                if (onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.DEMO
                        || onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.TRIAL) {
                    backUpOnlineClassesList.append("<tr style='color: red;'>");
                } else {
                    backUpOnlineClassesList.append("<tr>");
                }
                backUpOnlineClassesList
                        .append("<td>").append(DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(teacher.getTimezone()))).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getName()).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getName()).append("</td>")
                        .append("<td>");
                for (Student student : onlineClass.getStudents()) {
                    backUpOnlineClassesList.append(student.getEnglishName()).append(TextUtils.SPACE);
                }
                backUpOnlineClassesList.append("</td>")
                        .append("</tr>");
            }
            backUpOnlineClassesList.append("</table>");
        }

        paramsMap.put("onlineClassesList", onlineClassesList);
        paramsMap.put("backupTeacherOnlineClassList", backUpOnlineClassesList);
        paramsMap.put("teacherName", teacher.getSafeName());

        String subject = template.render(EmailTemplates.SchedulForTheComingWeekEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.SchedulForTheComingWeekEmailTemplate.CONTENT, paramsMap);

        if (!onlineClasses.isEmpty() || !backUpOnlineClasses.isEmpty()) {
            send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
        }

    }

    // 今明日课程提醒(每天)
    public static void sendToTeacherTodayAndTomorrowBookedOnlineClassesEmail(Teacher teacher, List<OnlineClass> todayOnlineClasses, List<OnlineClass> tomorrowOnlineClasses) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        StringBuilder todayOnlineClassesList = new StringBuilder();
        StringBuilder tomorrowOnlineClassesList = new StringBuilder();

        if (todayOnlineClasses.isEmpty()) {
            todayOnlineClassesList.append("Null");
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            todayOnlineClassesList.append("<table  border=1> \n")
                    .append("<tr>")
                    .append("<td>").append("Scheduled Time" + "(" + TimeZone.getTimeZone(teacher.getTimezone()).getID() + ")").append("</td>")
                    .append("<td>").append("Course Name").append("</td>")
                    .append("<td>").append("Lesson Name").append("</td>")
                    .append("<td>").append("Student").append("</td>")
                    .append("<td>").append("Is Short Notice").append("</td>")
                    .append("</tr>");
            for (OnlineClass onlineClass : todayOnlineClasses) {
                if (onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.DEMO
                        || onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.TRIAL) {
                    todayOnlineClassesList.append("<tr style='color: red;'>");
                } else if (onlineClass.isShortNotice() == true) {
                    todayOnlineClassesList.append("<tr style='color: gray;'>");
                } else {
                    todayOnlineClassesList.append("<tr>");
                }
                todayOnlineClassesList
                        .append("<td>").append(DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(teacher.getTimezone()))).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getName()).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getName()).append("</td>")
                        .append("<td>");
                for (Student student : onlineClass.getStudents()) {
                    todayOnlineClassesList.append(student.getEnglishName()).append(TextUtils.SPACE);
                }
                todayOnlineClassesList.append("</td>")
                        .append("<td>").append(onlineClass.isShortNotice()).append("</td>")
                        .append("</tr>");
            }
            todayOnlineClassesList.append("</table>");
        }

        if (tomorrowOnlineClasses.isEmpty()) {
            tomorrowOnlineClassesList.append("Null");
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            tomorrowOnlineClassesList.append("<table border=1> \n")
                    .append("<tr>")
                    .append("<td>").append("Scheduled Time" + "(" + TimeZone.getTimeZone(teacher.getTimezone()).getID() + ")").append("</td>")
                    .append("<td>").append("Course Name").append("</td>")
                    .append("<td>").append("Lesson Name").append("</td>")
                    .append("<td>").append("Student").append("</td>")
                    .append("<td>").append("Is Backup").append("</td>")
                    .append("</tr>");
            for (OnlineClass onlineClass : tomorrowOnlineClasses) {
                if (onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.DEMO
                        || onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType() == Course.Type.TRIAL) {
                    tomorrowOnlineClassesList.append("<tr style='color: red;'>");
                } else if (onlineClass.isShortNotice() == true) {
                    tomorrowOnlineClassesList.append("<tr style='color: gray;'>");
                } else {
                    tomorrowOnlineClassesList.append("<tr>");
                }
                tomorrowOnlineClassesList
                        .append("<td>").append(DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat, TimeZone.getTimeZone(teacher.getTimezone()))).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getName()).append("</td>")
                        .append("<td>").append(onlineClass.getLesson().getName()).append("</td>")
                        .append("<td>");
                for (Student student : onlineClass.getStudents()) {
                    tomorrowOnlineClassesList.append(student.getEnglishName()).append(TextUtils.SPACE);
                }
                tomorrowOnlineClassesList.append("</td>")
                        .append("<td>").append(onlineClass.isShortNotice()).append("</td>")
                        .append("</tr>");
            }
            tomorrowOnlineClassesList.append("</table>");
        }
        paramsMap.put("teacherName", teacher.getSafeName());
        paramsMap.put("todayOnlineClassesList", todayOnlineClassesList);
        paramsMap.put("todayOnlineClassesCount", todayOnlineClasses.size());
        paramsMap.put("tomorrowOnlineClassesList", tomorrowOnlineClassesList);
        String subject = template.render(EmailTemplates.SchedulForTodayAndTomorrowEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.SchedulForTodayAndTomorrowEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
    }

    // 课前进教室提醒(每节课)
    public static void sendToTeacherNextClassReminderEmail(OnlineClass onlineClass) {
        if (null == onlineClass || null == onlineClass.getTeacher() || StringUtils.isBlank(onlineClass.getTeacher().getEmail())) {
            logger.warn("sendToTeacherNextClassReminderEmail fail,because onlineClass is null or onlineClass's teacher is null or teacher's email is null");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder studentsEnglishNameList = new StringBuilder();
        for (Student student : onlineClass.getStudents()) {
            studentsEnglishNameList.append(student.getEnglishName()).append(TextUtils.SPACE);
        }

        paramsMap.put("teacherName", onlineClass.getTeacher().getName());
        paramsMap.put("studentEnglishName", studentsEnglishNameList);
        paramsMap.put("serialNumber", onlineClass.getLesson().getSerialNumber());
        paramsMap.put("lessonName", onlineClass.getLesson().getName());
        String subject = template.render(EmailTemplates.NextClassReminderEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.NextClassReminderEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, onlineClass.getTeacher().getEmail(), subject, content);
        logger.info("Success: send email before every booked online class from = {} to email = {}", Configurations.System.SYSTEM_USER_NAME, onlineClass.getTeacher().getEmail());
    }

    // 提醒放置available时间(每月)
    public static void sendToTeacherArrangeTimeReminderEmail(Teacher teacher) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("teacherName", teacher.getSafeName());
        paramsMap.put("limitation", Configurations.Schedule.AVAILABLE_HOUR_NEXT_MONTH_LIMITATION);
        String subject = template.render(EmailTemplates.ArrangeTimeReminderEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ArrangeTimeReminderEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
    }

    //在老师合同到期28天前发送邮件给Education组
    public static void sendTeacherContractEndDateToEducation(Teacher teacher) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("teacherName", teacher.getSafeName());
        paramsMap.put("teacherContractEndDate", teacher.getContractEndDate());
        String subject = template.render(EmailTemplates.AlertTeacherContractEndDateEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.AlertTeacherContractEndDateEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, educationEmail, subject, content);
    }

    // 每周面试结果汇总(每周六)
    public static void sendToTeacherInterviewResultEmail(Teacher teacher, List<Student> students, int studentCount) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        StringBuilder studentsInfo = new StringBuilder();
        for (Student student : students) {
            studentsInfo.append(student.getSafeName()).append(TextUtils.SEPERATOR).append(student.getEnglishName()).append(TextUtils.COMMA);
        }

        paramsMap.put("teacherName", teacher.getSafeName());
        paramsMap.put("studentCount", studentCount);
        paramsMap.put("StudentList", studentsInfo);
        String subject = template.render(EmailTemplates.InterviewResultEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.InterviewResultEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
    }

    /** 约课相关：邮件to老师 end */

    /**
     * 邮件to员工 start
     */
    static String educationEmail = "education@vipkid.com.cn";
    static String chineseLeadTeacherEmail = "clt@vipkid.com.cn";
    static String itSupportEmail = "it_support@vipkid.com.cn";
    static String teacherItTestCallBackEmail = "huzhongqing@vipkid.com.cn";
    static String R_DEmail = "rd@vipkid.com.cn";

    // IT测试预约完成(48小时内被预约)
    public static void sendToItTheItOnlineClassIsBookedEmail(OnlineClass onlineClass, Student student) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }

        paramsMap.put("studentName", student.getSafeName());
        paramsMap.put("parentsInfo", parentsInfo);
        paramsMap.put("scheduledDateTime", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        String subject = template.render(EmailTemplates.ItOnlineClassIsBookedEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ItOnlineClassIsBookedEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, onlineClass.getTeacher().getEmail(), subject, content);
    }

    // IT测试取消完成(48小时内被取消)
    public static void sendToItTheItOnlineClassIsCancelledEmail(OnlineClass onlineClass, Student student) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }

        paramsMap.put("studentName", student.getSafeName());
        paramsMap.put("parentsInfo", parentsInfo);
        paramsMap.put("scheduledDateTime", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        String subject = template.render(EmailTemplates.ItOnlineClassIsCancelldEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ItOnlineClassIsCancelldEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, onlineClass.getTeacher().getEmail(), subject, content);
    }

    // 提醒老师设置时间(下面这些老师还没有设置月足量的空闲课时)
    public static void sendToEducationNextMonthTeachersNotSetEnoughTimeslotReminderEmail(List<Teacher> teachers) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder teacherList = new StringBuilder();
        for (Teacher teacher : teachers) {
            teacherList.append(teacher.getSafeName()).append(TextUtils.SPACE);
        }
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        paramsMap.put("teacherList", teacherList);
        paramsMap.put("month", currentMonth);
        String subject = template.render(EmailTemplates.NextMonthTeachersNotSetEnoughTimeslotReminderEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.NextMonthTeachersNotSetEnoughTimeslotReminderEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, educationEmail, subject, content);
    }

    // 教学服务课预约完成
    public static void sendToEducationTheGuideOnlineClassIsBookedEmail(OnlineClass onlineClass, Student student) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }

        paramsMap.put("studentName", student.getSafeName());
        paramsMap.put("parentsInfo", parentsInfo);
        paramsMap.put("scheduledDateTime", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        String subject = template.render(EmailTemplates.EducationServiceOnlineClassIsBookedEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.EducationServiceOnlineClassIsBookedEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, chineseLeadTeacherEmail, subject, content);
    }

    // 教学服务课取消完成
    public static void sendToEducationTheGuideOnlineClassIsCancelledEmail(OnlineClass onlineClass, Student student) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }

        paramsMap.put("studentName", student.getSafeName());
        paramsMap.put("parentsInfo", parentsInfo);
        paramsMap.put("scheduledDateTime", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        String subject = template.render(EmailTemplates.EducationServiceOnlineClassIsCancelledEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.EducationServiceOnlineClassIsCancelledEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, chineseLeadTeacherEmail, subject, content);
    }

    // 提醒家长约课
    public static void sendToEducationRemindParentsBookOnlineClassEmail(List<Parent> parents) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : parents) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }
        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.RemindParentsBookOnlineClassEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.RemindParentsBookOnlineClassEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, educationEmail, subject, content);
    }

    // 每周IT测试提醒(下周)
    public static void sendToItNextWeekItOnlineClassesReminderEmail(List<OnlineClass> onlineClasses, Teacher teacher) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder studentsInfo = new StringBuilder();

        studentsInfo.append("<table  border=1> \n")
                .append("<tr>")
                .append("<td>").append("Student").append("</td>")
                .append("<td>").append("Parents").append("</td>")
                .append("</tr>");
        for (OnlineClass onlineClass : onlineClasses) {
            Student student = onlineClass.getStudents().get(0);
            studentsInfo.append("<td>").append(student.getSafeName()).append(TextUtils.COLON).append("</td>");
            for (Parent parent : student.getFamily().getParents()) {
                studentsInfo.append("<td>");
                if (parent.getRelation() != null) {
                    studentsInfo.append(parent.getRelation()).append(TextUtils.SEPERATOR);
                }
                studentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
            }
            studentsInfo.append("</tr>");
        }
        Calendar startOfNextWeekCalendar = Calendar.getInstance();
        Calendar endOfNextWeekCalendar = Calendar.getInstance();
        startOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        startOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        startOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        startOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startOfNextWeekCalendar.set(Calendar.MINUTE, 0);
        startOfNextWeekCalendar.set(Calendar.SECOND, 0);
        startOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
        endOfNextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        endOfNextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        endOfNextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        endOfNextWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endOfNextWeekCalendar.set(Calendar.MINUTE, 59);
        endOfNextWeekCalendar.set(Calendar.SECOND, 59);
        endOfNextWeekCalendar.set(Calendar.MILLISECOND, 0);
        Date nextWeekStart = startOfNextWeekCalendar.getTime();
        Date nextWeekEnd = endOfNextWeekCalendar.getTime();
        String timeRange = DateTimeUtils.format(nextWeekStart, DateTimeUtils.DATE_FORMAT) + '~' + DateTimeUtils.format(nextWeekEnd, DateTimeUtils.DATE_FORMAT);

        paramsMap.put("studentsInfo", studentsInfo);
        paramsMap.put("timeRange", timeRange);
        String subject = template.render(EmailTemplates.NextWeekItOnlineClassesReminderEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.NextWeekItOnlineClassesReminderEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
    }

    // 每日IT测试提醒(明日)
    public static void sendToItTomorrowItOnlineClassesReminderEmail(List<OnlineClass> todayItOnlineClasses, List<OnlineClass> tomorrowItOnlineClasses, Teacher teacher) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder todayItOnlineClassesInfo = new StringBuilder();
        StringBuilder tomorrowItOnlineClassesInfo = new StringBuilder();

        todayItOnlineClassesInfo.append("<table  border=1> \n")
                .append("<tr>")
                .append("<td>").append("Student").append("</td>")
                .append("<td>").append("Parents").append("</td>")
                .append("</tr>");
        for (OnlineClass onlineClass : todayItOnlineClasses) {
            Student student = onlineClass.getStudents().get(0);
            todayItOnlineClassesInfo.append("<td>").append(student.getSafeName()).append(TextUtils.COLON).append("</td>");
            for (Parent parent : student.getFamily().getParents()) {
                todayItOnlineClassesInfo.append("<td>");
                if (parent.getRelation() != null) {
                    todayItOnlineClassesInfo.append(parent.getRelation()).append(TextUtils.SEPERATOR);
                }
                todayItOnlineClassesInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
            }
            todayItOnlineClassesInfo.append("</tr>");
        }
        todayItOnlineClassesInfo.append("</table> \n");

        tomorrowItOnlineClassesInfo.append("<table  border=1> \n")
                .append("<tr>")
                .append("<td>").append("Student").append("</td>")
                .append("<td>").append("Parents").append("</td>")
                .append("</tr>");
        for (OnlineClass onlineClass : tomorrowItOnlineClasses) {
            Student student = onlineClass.getStudents().get(0);
            tomorrowItOnlineClassesInfo.append("<td>").append(student.getSafeName()).append(TextUtils.COLON).append("</td>");
            for (Parent parent : student.getFamily().getParents()) {
                tomorrowItOnlineClassesInfo.append("<td>");
                if (parent.getRelation() != null) {
                    tomorrowItOnlineClassesInfo.append(parent.getRelation()).append(TextUtils.SEPERATOR);
                }
                tomorrowItOnlineClassesInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
            }
            tomorrowItOnlineClassesInfo.append("</tr>");
        }
        tomorrowItOnlineClassesInfo.append("</table> \n");

        Date todayDateTime = new Date();
        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrowDateTime = tomorrowCalendar.getTime();
        paramsMap.put("todayItOnlineClasses", todayItOnlineClassesInfo);
        paramsMap.put("todayDateTime", DateTimeUtils.format(todayDateTime, DateTimeUtils.DATE_FORMAT));
        paramsMap.put("tomorrowItOnlineClasses", tomorrowItOnlineClassesInfo);
        paramsMap.put("tomorrowDateTime", DateTimeUtils.format(tomorrowDateTime, DateTimeUtils.DATE_FORMAT));
        String subject = template.render(EmailTemplates.TodayAndTomorrowItOnlineClassesReminderEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.TodayAndTomorrowItOnlineClassesReminderEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
    }

    // 老师翘课通知
    public static void sendToEducationTeacherNoShowEmail(OnlineClass onlineClass, Student student) {
        if (!checkOnlineClassBeforeSendMail(onlineClass)) {
            logger.warn("Can't send mail to Education,because online class check not pass!!");
            return;
        }
        if (!checkStudentBeforeSendMail(student)) {
            logger.warn("Can't send mail to Education,because student check not pass!!");
            return;
        }
        logger.info("Email sendToEducationTeacherNoShowEmail,online class id={}", onlineClass.getId());
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        String subject = "";
        String content;
        try {
            for (Parent parent : student.getFamily().getParents()) {
                parentsInfo.append(null == parent.getName() ? "" : parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
            }
            paramsMap.put("studentName", (null == student.getEnglishName() ? "" : student.getEnglishName()));
            paramsMap.put("teacherName", onlineClass.getTeacher().getName());
            paramsMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
            paramsMap.put("serialNumber", onlineClass.getLesson().getSerialNumber());
            paramsMap.put("lessonName", onlineClass.getLesson().getName());

            paramsMap.put("parentsInfo", parentsInfo);
            subject = template.render(EmailTemplates.TeacherNoShowEmailTemplate.SUBJECT, paramsMap);
            content = template.render(EmailTemplates.TeacherNoShowEmailTemplate.CONTENT, paramsMap);
        } catch (Exception e) {
            logger.error("Email content parse error when sendToEducationTeacherNoShowEmail,onlineClassID={}", onlineClass.getId(), e);
            sendSystemErrotToRD("Email sendToEducationTeacherNoShowEmail");
            return;
        }

        if (Type.IT_TEST == onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getType()) {
            send(Configurations.EMail.FROM, chineseLeadTeacherEmail, subject, content); // VK-1507
        } else {
            send(Configurations.EMail.FROM, educationEmail, subject, content);
        }
    }

    private static boolean checkStudentBeforeSendMail(Student student) {
        boolean checkStatus = false;
        if (null != student) {
            if (null == student.getFamily()) {
                logger.warn("Student's family is null! Student's id={},name={}", student.getId(), student.getUsername());
                return checkStatus;
            }
            if (CollectionUtils.isEmpty(student.getFamily().getParents())) {
                logger.warn("Student's parents is null! Student's id={},name={}", student.getId(), student.getUsername());
                return checkStatus;
            }
            checkStatus = true;
        }
        return checkStatus;
    }

    // 学生翘课通知
    public static void sendToEducationStudentNoShowEmail(OnlineClass onlineClass, Student student) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }
        paramsMap.put("studentName", student.getEnglishName());
        paramsMap.put("teacherName", onlineClass.getTeacher().getName());
        paramsMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        paramsMap.put("serialNumber", onlineClass.getLesson().getSerialNumber());
        paramsMap.put("lessonName", onlineClass.getLesson().getName());

        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.StudentNoShowEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.StudentNoShowEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, chineseLeadTeacherEmail, subject, content);
    }

    // 试听课完成提醒
    public static void sendToSaleTheDemoOnlineClassIsFinishedEmail(OnlineClass onlineClass, Student student) {
        if (null == student || null == student.getSales()) {
            logger.warn(" !!!!!!!!!!!!!!!!!!!  warning: student is null or student's sales is null,do not sent Email");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }

        StringBuilder firemanToStudentCommentInfo = new StringBuilder();
        FiremanToStudentComment firemanToStudentComment = onlineClass.getFiremanToStudentComments().get(0);
        firemanToStudentCommentInfo
                .append("Student Behavior Problem").append(TextUtils.COLON).append(firemanToStudentComment.getStudentBehaviorProblem()).append(TextUtils.COMMA).append("<br>")
                .append("Student IT Problem").append(TextUtils.COLON).append(firemanToStudentComment.getStudentITProblem()).append(TextUtils.COMMA).append("<br>")
                .append("supplement").append(TextUtils.COLON).append(firemanToStudentComment.getSupplement()).append(TextUtils.COMMA).append("<br>");

        StringBuilder firemanToTeacherCommentInfo = new StringBuilder();
        FiremanToTeacherComment firemanToTeacherComment = onlineClass.getFiremanToTeacherComment();
        firemanToTeacherCommentInfo
                .append("Teacher Behavior Problem").append(TextUtils.COLON).append(firemanToTeacherComment.getTeacherBehaviorProblem()).append(TextUtils.COMMA).append("<br>")
                .append("Student IT Problem").append(TextUtils.COLON).append(firemanToTeacherComment.getTeacherITProblem()).append(TextUtils.COMMA).append("<br>")
                .append("supplement").append(TextUtils.COLON).append(firemanToTeacherComment.getSupplement()).append(TextUtils.COMMA).append("<br>");

        paramsMap.put("studentName", student.getSafeName());
        paramsMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        paramsMap.put("finishType", onlineClass.getFinishType());
        paramsMap.put("firemanToStudentComment", firemanToStudentCommentInfo);
        paramsMap.put("firemanToTeacherComment", firemanToTeacherCommentInfo);
        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.DemoOnlineClassIsFinishedEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.DemoOnlineClassIsFinishedEmailTemplate.CONTENT, paramsMap);

        if (checkStudentSalesBeforeSendMail(student)) {
            send(Configurations.EMail.FROM, student.getSales().getEmail(), subject, content);
        } else {
            logger.warn("Can not send mail sendToSaleTheDemoOnlineClassIsFinishedEmail,studentID={},studentEnglishName={}",student.getId(),student.getEnglishName());
        }
    }

    // IT测试课完成提醒
    public static void sendToSaleTheItOnlineClassIsFinishedEmail(OnlineClass onlineClass, Student student) {
        if (null == student || null == student.getSales() || null == onlineClass) {
            logger.warn(" !!!!!!!!!!!!!!!!!!!  warning: student is null or student's sales is null or onlineClass is null,do not sent Email");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }
        paramsMap.put("studentName", student.getSafeName());
        paramsMap.put("time", DateTimeUtils.format(onlineClass.getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        paramsMap.put("finishType", onlineClass.getFinishType());
        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.ItOnlineClassIsFinishedEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ItOnlineClassIsFinishedEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, student.getSales().getEmail(), subject, content);
    }

    // 课时将尽提醒
    public static void sendToCLTTheLearningProgressIsComingToFinishEmail(LearningProgress learningProgress) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder("");
        for (Parent parent : learningProgress.getStudent().getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }
        paramsMap.put("studentName", learningProgress.getStudent().getSafeName());
        paramsMap.put("courseName", learningProgress.getCourse().getName());
        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.LearningProgressIsComingToFinishEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.LearningProgressIsComingToFinishEmailTemplate.CONTENT, paramsMap);

        if (null != learningProgress.getStudent() && null != learningProgress.getStudent().getChineseLeadTeacher() && StringUtils.isNotBlank(learningProgress.getStudent().getChineseLeadTeacher().getEmail())) {
            send(Configurations.EMail.FROM, learningProgress.getStudent().getChineseLeadTeacher().getEmail(), subject, content);
        } else {
            logger.warn("sendToCLTTheLearningProgressIsComingToFinishEmail:Student is null or Student's CLT is null or CLT's email is null,so can not send email when the LearningProgress is ComingToFinish");
        }
    }

    // 课程完成提醒
    public static void sendToSaleAndCLTTheLearningProgressIsFinishedEmail(LearningProgress learningProgress) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder("");
        for (Parent parent : learningProgress.getStudent().getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }
        paramsMap.put("studentName", learningProgress.getStudent().getSafeName());
        paramsMap.put("courseName", learningProgress.getCourse().getName());
        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.LearningProgressIsFinishedEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.LearningProgressIsFinishedEmailTemplate.CONTENT, paramsMap);

        if (null != learningProgress.getStudent() && null != learningProgress.getStudent().getSales() && StringUtils.isNotBlank(learningProgress.getStudent().getSales().getEmail())) {
            send(Configurations.EMail.FROM, learningProgress.getStudent().getSales().getEmail(), subject, content);
        } else {
            logger.warn("sendToSaleTheLearningProgressIsFinishedEmail:Student is null or Student's sales is null or sale's email is null,so can not send email when the LearningProgress is ComingToFinish");
        }

        if (null != learningProgress.getStudent() && null != learningProgress.getStudent().getChineseLeadTeacher() && StringUtils.isNotBlank(learningProgress.getStudent().getChineseLeadTeacher().getEmail())) {
            send(Configurations.EMail.FROM, learningProgress.getStudent().getChineseLeadTeacher().getEmail(), subject, content);
        } else {
            logger.warn("sendToCLTTheLearningProgressIsFinishedEmail:Student is null or Student's CLT is null or CLT's email is null,so can not send email when the LearningProgress is ComingToFinish");
        }
    }

    // 老师合约到期提醒
    public static void sendToEducationTheTeacherContractWillRunOutEmail(Teacher teacher) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", teacher.getSafeName());
        paramsMap.put("endTime", DateTimeUtils.format(teacher.getContractEndDate(), DateTimeUtils.DATE_FORMAT));
        String subject = template.render(EmailTemplates.TeacherContractWillRunOutTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.TeacherContractWillRunOutTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, educationEmail, subject, content);
    }

    /**
     * 邮件to员工 end
     */
    // 面试课程结束后教学主管已经确认报告发送给家长，发送邮件通知销售
    public static void sendToSaleTheDemoReportIsConfirmedEmail(DemoReport demoReport, Student student, String staffName) {
        if (null == student || null == student.getSales()) {
            logger.warn(" !!!!!!!!!!!!!!!!!!!  warning: student is null or student's sales is null,do not sent Email");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }
        paramsMap.put("studentName", student.getSafeName());
        paramsMap.put("dateTime", DateTimeUtils.format(demoReport.getOnlineClass().getScheduledDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        paramsMap.put("staffName", staffName);
        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.DemoReportIsConfirmedEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.DemoReportIsConfirmedEmailTemplate.CONTENT, paramsMap);
        if (checkStudentBeforeSendMail(student)) {
            send(Configurations.EMail.FROM, student.getSales().getEmail(), subject, content);
        } else {
            logger.warn("Can not send mail in sendToSaleTheDemoReportIsConfirmedEmail,studentID={},studentEnglishName={}",student.getId(),student.getEnglishName());
        }
    }

    // audit成功，发邮件给申请者，并且cc给RP（如果有RP得话）
    public static void sendToTeacherRecruitmentResult(TeacherApplication teacherApplication, String strReferMail) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        String subject = null;
        String content = null;
        String cc = null;

        try {
            cc = teacherApplication.getTeacher().getPartner().getEmail(); // cc to partner.
        } catch (Exception e) {
            logger.info("partner's mail is null!");
        }

        // // 2015-09-17 为推荐渠道 - 加入refer的邮箱
        if (null == cc && null != strReferMail) {
        	cc = strReferMail;
        }
        paramsMap.put("teacherName", teacherApplication.getTeacher().getName());
        paramsMap.put("recruitmentId", teacherApplication.getTeacher().getRecrutmentId());

        TeacherApplication.Result result = TeacherApplication.Result.FAIL;
        TeacherApplication.Status status = TeacherApplication.Status.BASIC_INFO;

        String strEmail = null;
        try {
            strEmail = teacherApplication.getTeacher().getEmail();

            result = teacherApplication.getResult();
            status = teacherApplication.getStatus();
        } catch (Exception e) {
            logger.error("ERROR:  cant't get result and status, exit with no email!!!");
            return;
        }

        switch (status) {
            case BASIC_INFO:
                subject = template.render(EmailTemplates.TeacherRecruitmentPhase1PassResultEmailTemplate.SUBJECT, paramsMap);

                switch (result) {
                    case PASS:
                        cc = null;
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase1PassResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case FAIL:
                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase1FailResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase1FailResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case REAPPLY:
                        String strFailReason = teacherApplication.getFailedReason();
                        if (null == strFailReason) {
                            strFailReason = "";
                        }
                        paramsMap.put("failedReason", strFailReason);

                        content = template.render(EmailTemplates.TeacherRecruitmentPhase1ReapplyResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    default:
                        break;
                }
                break;
            case INTERVIEW:
                subject = template.render(EmailTemplates.TeacherRecruitmentPhase2ReapplyResultEmailTemplate.SUBJECT, paramsMap);

                paramsMap.put("contractURL", teacherApplication.getTeacher().getContract());

                switch (result) {
                    case PASS:
                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase2PassResultEmailTemplate.SUBJECT, paramsMap);

                        StringBuilder interviewResultTable = new StringBuilder();
                        interviewResultTable.append("<table style='border-collapse: collapse;' border=1> \n")
                                .append("<tr>")
                                .append("<td style='padding:6px; min-width:100px;  height: 55px;'>").append("").append("</td>")
                                .append("<td style='padding:6px;min-width:120px; text-align: center;background-color:#FEF1E7'>").append("Inadequate<div >1</div>").append("</td>")
                                .append("<td style='padding:6px;min-width:120px; text-align: center;background-color:#FEF1E7'>").append("Developing<div >2</div>").append("</td>")
                                .append("<td style='padding:6px;min-width:120px; text-align: center;background-color:#FEF1E7'>").append("Proficient<div >3</div>").append("</td>")
                                .append("<td style='padding:6px;min-width:120px; text-align: center;background-color:#FEF1E7'>").append("Exemplary<div >4</div>").append("</td>")
                                .append("<td style='padding:6px;min-width:40px; text-align: center;background-color:#FEF1E7'>").append("Score").append("</td>")
                                .append("</tr>");

                        List<String[]> scoreItemRows = new ArrayList<String[]>();
                        String[] rowScoreItems = new String[6];
                        rowScoreItems[0] = "Interaction & energy";
                        rowScoreItems[1] = "Equivalent of Cinderella’s evil step- mother";
                        rowScoreItems[2] = "Smiles some, some praises";
                        rowScoreItems[3] = "Lot of smiles, uses reward with every slide";
                        rowScoreItems[4] = "Very excited & positive, generous with praises";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getInteractionRapportScore());
                        scoreItemRows.add(rowScoreItems);

                        rowScoreItems = new String[6];
                        rowScoreItems[0] = "Awareness & adaptability";
                        rowScoreItems[1] = "Didn’t notice that the student wasn’t there";
                        rowScoreItems[2] = "Aware of student’s needs but not always adapt";
                        rowScoreItems[3] = "Adapt to most of student’s needs";
                        rowScoreItems[4] = "Adapt to student’s every need";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getTimeManagementScore());
                        scoreItemRows.add(rowScoreItems);

                        rowScoreItems = new String[6];
                        rowScoreItems[0] = "Teaching method";
                        rowScoreItems[1] = "Equivalent of a self- absorbed 15- year-old girl gossiping on the phone";
                        rowScoreItems[2] = "Spoke quickly, 7-9 words per sentence, big words, few pauses";
                        rowScoreItems[3] = "Spoke slowly, 5-7 words per sentence, small words, some pauses";
                        rowScoreItems[4] = "Spoke slowly & clearly, 3-4 words per sentence, small words, many pauses";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getTeachingMethod());
                        scoreItemRows.add(rowScoreItems);

                        rowScoreItems = new String[6];
                        rowScoreItems[0] = "Student output";
                        rowScoreItems[1] = "Might as well have taught a stuffed toy";
                        rowScoreItems[2] = "Student spoke during some slides";
                        rowScoreItems[3] = "Student spoke during most slides";
                        rowScoreItems[4] = "Student spoke during every slide";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getStudentOutputScore());
                        scoreItemRows.add(rowScoreItems);

                        rowScoreItems = new String[6];
                        rowScoreItems[0] = "Preparation & planning";
                        rowScoreItems[1] = "Had no idea what was in the slide deck";
                        rowScoreItems[2] = "Reward system not drawn, familiar with some slides";
                        rowScoreItems[3] = "Reward system may be drawn, familiar with most slides";
                        rowScoreItems[4] = "Reward system drawn, familiar with all slides";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getPreparationPlanningScore());
                        scoreItemRows.add(rowScoreItems);

                        rowScoreItems = new String[6];
                        rowScoreItems[0] = "English language";
                        rowScoreItems[1] = "This person should consider taking our classes.";
                        rowScoreItems[2] = "Improper sentence structure and grammar";
                        rowScoreItems[3] = "Some improper sentence structure and grammar";
                        rowScoreItems[4] = "Perfect sentence structure and grammar";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getEnglishLanguageScore());
                        scoreItemRows.add(rowScoreItems);

                        rowScoreItems = new String[6];
                        rowScoreItems[0] = "Lesson objectives";
                        rowScoreItems[1] = "Taught a different lesson";
                        rowScoreItems[2] = "Met the objective on some slides";
                        rowScoreItems[3] = "Met the objective on most slides";
                        rowScoreItems[4] = "Met the objective on each slide";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getLessonObjectivesScore());
                        scoreItemRows.add(rowScoreItems);

                        rowScoreItems = new String[6];
                        rowScoreItems[0] = "Time management";
                        rowScoreItems[1] = "Did not understand the concept of time";
                        rowScoreItems[2] = "Took an extra ~5 minutes to finish class";
                        rowScoreItems[3] = "Rushed or skipped slides to finish or went over";
                        rowScoreItems[4] = "Perfectly paced 10- minute lesson";
                        rowScoreItems[5] = String.valueOf(teacherApplication.getTimeManagementScore());
                        scoreItemRows.add(rowScoreItems);


                        for (Iterator<String[]> scoreItemRow = scoreItemRows.iterator(); scoreItemRow
                                .hasNext(); ) {
                            String[] scoreItem = (String[]) scoreItemRow.next();
                            interviewResultTable.append("<tr>")
                                    .append("<td>").append(scoreItem[0]).append("</td>")
                                    .append("<td style='padding:6px; background-color:#F9BF92'>").append(scoreItem[1])
                                    .append("</td>")
                                    .append("<td style='padding:6px; background-color:#F9BF92'>").append(scoreItem[2])
                                    .append("</td>")
                                    .append("<td style='padding:6px; background-color:#F9BF92'>").append(scoreItem[3])
                                    .append("</td>")
                                    .append("<td style='padding:6px; background-color:#F9BF92'>").append(scoreItem[4])
                                    .append("</td>")
                                    .append("<td style='text-align: center;background-color:#F9BF92'>").append(scoreItem[5])
                                    .append("</td>")
                                    .append("</tr>");

                        }

                        interviewResultTable.append("<tr>")
                                .append("<td>").append("Total Score").append("</td>")
                                .append("<td ></td>")
                                .append("<td ></td>")
                                .append("<td ></td>")
                                .append("<td ></td>")
                                .append("<td style='text-align: center;'>").append(teacherApplication.getInterviewScores()).append("</td>")
                                .append("</tr>");

                        interviewResultTable.append("</table>");
                        paramsMap.put("resultTable", interviewResultTable);
                        paramsMap.put("totalScore", teacherApplication.getInterviewScores());
                        paramsMap.put("basePay", teacherApplication.getBasePay());
                        paramsMap.put("contractUrl", teacherApplication.getContractURL());
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase2PassResultEmailTemplate.CONTENT, paramsMap);

                        break;
                    case FAIL:
                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase2FailResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase2FailResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case REAPPLY:
                        String strFailReason = teacherApplication.getFailedReason();
                        if (null == strFailReason) {
                            strFailReason = "";
                        }
                        paramsMap.put("failedReason", strFailReason);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase2ReapplyResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    default:
                        break;
                }
                break;
            case SIGN_CONTRACT:
                switch (result) {
                    case PASS:
                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase3PassResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase3PassResultEmailTemplate.CONTENT, paramsMap);

                        cc = Configurations.EMail.TeacherRecruit_Email_Contract_CC;
                        break;
                    case FAIL:

                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase3FailResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase3FailResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case REAPPLY:
                        String strFailReason = teacherApplication.getFailedReason();
                        if (null == strFailReason) {
                            strFailReason = "";
                        }
                        paramsMap.put("failedReason", strFailReason);

                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase3PassResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase3ReapplyResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    default:
                        break;
                }
                break;
            case TRAINING:
                cc = null;    //2015-05-08 per James: no cc email in step 4 and 5
                subject = template.render(EmailTemplates.TeacherRecruitmentPhase4PassResultEmailTemplate.SUBJECT, paramsMap);

                switch (result) {
                    case PASS:
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase4PassResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case FAIL:
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase4FailResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case REAPPLY:
                        String strFailReason = teacherApplication.getFailedReason();
                        if (null == strFailReason) {
                            strFailReason = "";
                        }
                        paramsMap.put("failedReason", strFailReason);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase4ReapplyResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    default:
                        break;
                }
                break;
            case PRACTICUM:
                cc = null;//Configurations.EMail.TeacherRecruit_Email_Contract_CC;//TeacherCruit_Result_CC;

                switch (result) {
                    case PASS:        //teacherRecruitmentPhase5PassResultEmailSubjectTemplate.st
                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase5PassResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase5PassResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case FAIL:
                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase5FailResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase5FailResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case REAPPLY:
                        String strFailReason = teacherApplication.getFailedReason();
                        if (null == strFailReason) {
                            strFailReason = "";
                        }
                        paramsMap.put("failedReason", strFailReason);

                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase5ReapplyResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase5ReapplyResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    case PRACTICUM2:
                        subject = template.render(EmailTemplates.TeacherRecruitmentPhase5Practicum2ResultEmailTemplate.SUBJECT, paramsMap);
                        content = template.render(EmailTemplates.TeacherRecruitmentPhase5Practicum2ResultEmailTemplate.CONTENT, paramsMap);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;

        }

//		send(Configurations.EMail.FROM, teacherApplication.getTeacher().getEmail(), subject, content);

        // 错误的cc地址导致发送失败 -- 分解为2此发送。
        // 2015-07-10 如果在招聘组职责内，使用teachvip进行发送
        // 2015-07-23 招聘过程中教室组使用的邮件地址：education@vipkid.com.cn, 区别招聘组的teachvip@vipkid.com.cn
        if (TeacherApplication.Status.BASIC_INFO == status || TeacherApplication.Status.INTERVIEW == status) {
            sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, null, subject, content, Configurations.EMail.TC_PASSWORD);
            if (null != cc) {
            	sendWithUserAndPwd(Configurations.EMail.TC_FROM, cc, null, subject, content, Configurations.EMail.TC_PASSWORD);
            }
        } else  if (TeacherApplication.Status.SIGN_CONTRACT == status && result != TeacherApplication.Result.PASS) {
            sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, null, subject, content, Configurations.EMail.TC_PASSWORD);
            if (null != cc) {
            	sendWithUserAndPwd(Configurations.EMail.TC_FROM, cc, null, subject, content, Configurations.EMail.TC_PASSWORD);
            }
        } else {
        	sendWithUserAndPwdForTCEducation(strEmail, null, subject, content );
//        	send(Configurations.EMail.FROM, strEmail, subject, content);
	        if (null != cc) {
	        	sendWithUserAndPwdForTCEducation(cc, null, subject, content );
//	        	send(Configurations.EMail.FROM, cc, subject, content);
	        }
        }
    }

    /**
     * 教师申请者在注册后，发生邮件
     *
     * @param teacher
     */
    public static void sendToApplicantForApply(Teacher teacher) {
        //
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        String cc = null;

        try {
            cc = teacher.getPartner().getEmail(); // cc to partner.
        } catch (Exception e) {
            logger.info("partner's mail is null!");
        }

        String strEmail = null;
        strEmail = teacher.getEmail();
        String strUserName = teacher.getRealName();
        if (null == strUserName || strUserName.isEmpty()) {
            strUserName = teacher.getUsername();
        }

        paramsMap.put("teacherName", strUserName);
        paramsMap.put("loginName", strEmail);
        paramsMap.put("recruitmentId", teacher.getRecrutmentId());

        String subject = template.render(EmailTemplates.TeacherRecruitmentApplyThxEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.TeacherRecruitmentApplyThxEmailTemplate.CONTENT, paramsMap);

        //
//        sendWithCC(Configurations.EMail.TC_FROM, strEmail, subject, content, cc);
        
        // 使用teacher recruitment的teachvip@vipkid.com.cn进行发送。
        sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, cc, subject, content, Configurations.EMail.TC_PASSWORD);
    }

    public static void sendToApplicantForInterviewBefore48Hour(TeacherApplication teacherApplication) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", teacherApplication.getTeacher().getName());
        //paramsMap.put("scheduledDateTime", teacherApplication.getOnlineClass().getScheduledDateTime());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        paramsMap.put("scheduledDateTime", DateTimeUtils.format(teacherApplication.getOnlineClass().getScheduledDateTime(), dateFormat,
                        TimeZone.getTimeZone(teacherApplication.getTeacher().getTimezone())
                )
        );

        String subject = template.render(EmailTemplates.ReminderApplicantBefore24HoursEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ReminderApplicantBefore24HoursEmailTemplate.CONTENT, paramsMap);

//        send(Configurations.EMail.TC_FROM, teacherApplication.getTeacher().getEmail(), subject, content);
     // 使用teacher recruitment的teachvip@vipkid.com.cn进行发送。
        String strEmail = teacherApplication.getTeacher().getEmail();
        sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, null, subject, content, Configurations.EMail.TC_PASSWORD);
    }


    public static void sendThxEmailForDemoClass(TeacherApplication teacherApplication) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", teacherApplication.getTeacher().getName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        paramsMap.put("scheduledDateTime",
                DateTimeUtils.format(
                        teacherApplication.getOnlineClass().getScheduledDateTime(), dateFormat,
                        TimeZone.getTimeZone(teacherApplication.getTeacher().getTimezone())
                )
        );
        paramsMap.put("scheduledDateTime", teacherApplication.getOnlineClass().getScheduledDateTime());
        String subject = template.render(EmailTemplates.ReminderApplicantThanksForDemoClassEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ReminderApplicantThanksForDemoClassEmailTemplate.CONTENT, paramsMap);

//        send(Configurations.EMail.TC_FROM, teacherApplication.getTeacher().getEmail(), subject, content);
        // 使用teacher recruitment的teachvip@vipkid.com.cn进行发送。
        String strEmail = teacherApplication.getTeacher().getEmail();
        sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, null, subject, content, Configurations.EMail.TC_PASSWORD);
    }

    /**
     * 发送absent the demo class email
     *
     * @param teacherApplication
     */
    public static void sendAbsentEmailForDemoClass(TeacherApplication teacherApplication) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", teacherApplication.getTeacher().getName());
        String subject = template.render(EmailTemplates.ReminderApplicantAbsentDemoClassEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.ReminderApplicantAbsentDemoClassEmailTemplate.CONTENT, paramsMap);

        String cc = null;
        String strEmail = null;

        try {
            strEmail = teacherApplication.getTeacher().getEmail();
            cc = teacherApplication.getTeacher().getPartner().getEmail(); // cc to partner.
        } catch (Exception e) {
            logger.info("email or partner's mail is null!");
        }

//		send(Configurations.EMail.FROM, teacherApplication.getTeacher().getEmail(), subject, content);

//        sendWithCC(Configurations.EMail.TC_FROM, strEmail, subject, content, null);
//        sendWithCC(Configurations.EMail.TC_FROM, cc, subject, content, null);
        
     // 使用teacher recruitment的teachvip@vipkid.com.cn进行发送。
        strEmail = teacherApplication.getTeacher().getEmail();
        sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, null, subject, content, Configurations.EMail.TC_PASSWORD);
        if (null != cc) {
        	sendWithUserAndPwd(Configurations.EMail.TC_FROM, cc, null, subject, content, Configurations.EMail.TC_PASSWORD);
        }
    }

    /**
     * TeacherCruit 阶段3 contract-sign 进入后116小时，如果没有提交记录，进行邮件提醒
     */
    public static void sendToApplicantForContractSignReminder(Teacher teacher) {
        //
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", teacher.getSafeName());

        String subject = template.render(EmailTemplates.TeacherRecruitmentReminderContractSign116HoursEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.TeacherRecruitmentReminderContractSign116HoursEmailTemplate.CONTENT, paramsMap);

//        send(Configurations.EMail.TC_FROM, teacher.getEmail(), subject, content);
        
        // 使用teacher recruitment的teachvip@vipkid.com.cn进行发送。
        String strEmail = teacher.getEmail();
        sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, null, subject, content, Configurations.EMail.TC_PASSWORD);
    }

    /**
     * TeacherCruit 阶段3 contract-sign 到期如果没有提交记录，进行邮件告知
     * 2015-07-10 取消此邮件
     */
    public static void sendToApplicantForContractTeminate(Teacher teacher) {
        //
        //
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("teacherName", teacher.getSafeName());

        String subject = template.render(EmailTemplates.TeacherRecruitmentReminderContractSignTerminateEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.TeacherRecruitmentReminderContractSignTerminateEmailTemplate.CONTENT, paramsMap);

//        send(Configurations.EMail.FROM, teacher.getEmail(), subject, content);
        String strEmail = teacher.getEmail();
        sendWithUserAndPwd(Configurations.EMail.TC_FROM, strEmail, null, subject, content, Configurations.EMail.TC_PASSWORD);
    }

    /**
     * 添加 课件不成功，发邮件提示
     *
     * @param onlineClass
     * @param attachDocumentResult
     * @param user
     */
    public static void sendToFiremanAttatchDocumentFailReminder(String firemanEmail, OnlineClass onlineClass,
                                                                AttachDocumentResult attachDocumentResult, User user, String operation) {

        AbstractTemplates template = new EmailTemplates();

        Map<String, Object> paramsMap = new HashMap<String, Object>();

        // paramsMap.put("onlineclassid", onlineClass.getId());

        paramsMap.put("classroom", onlineClass.getClassroom());
        // paramsMap.put("wxtCourseId", onlineClass.getWxtCourseId());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        if (user != null) {
            paramsMap.put("lasteditor", user.getUsername());
        }

        if (attachDocumentResult != null) {
            paramsMap.put("erroCode", attachDocumentResult.getError());
        }

        if (operation != null) {
            paramsMap.put("operation", operation);

            paramsMap.put("scheduledTime", DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat));
            if (onlineClass.getTeacher() != null && onlineClass.getTeacher().getName() != null) {
                paramsMap.put("teacherName", onlineClass.getTeacher().getName());
            }
            String subject = template.render(EmailTemplates.reminderFiremanAttatchDocumentFailEmailTemplate.SUBJECT, paramsMap);
            String content = template.render(EmailTemplates.reminderFiremanAttatchDocumentFailEmailTemplate.CONTENT, paramsMap);
            send(Configurations.EMail.FROM, firemanEmail, subject, content);
        }

    }
    
    /**
     * 创建教室失败，发送邮件
     * @param reminderEmail
     * @param onlineClasses
     */
     public static void sendReminderForAutoCreateClassRoomFailed(String reminderEmail, List<OnlineClass> onlineClasses){
     	AbstractTemplates template = new EmailTemplates();
     	Map<String, Object> paramsMap = new HashMap<String, Object>();
     	
     	StringBuilder onlineClassList = new StringBuilder();
     	if(onlineClasses.isEmpty()){
     		return;
     	}else{
     		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
     		onlineClassList.append("<table  border=1> \n")
     			.append("<tr>")
     			.append("<td>").append("Scheduled Time").append("</td>")
     			.append("<td>").append("Course Name").append("</td>")
     			.append("<td>").append("Lesson Name").append("</td>")
     			.append("<td>").append("Student").append("</td>")
     			.append("<td>").append("Teacher").append("</td>")
     			.append("<td>").append("Classroom").append("</td>")
     			.append("</tr>");
     		for(OnlineClass onlineClass:onlineClasses){
     			onlineClassList.append("<tr>")
     				.append("<td>").append(DateTimeUtils.format(onlineClass.getScheduledDateTime(), dateFormat)).append("</td>")
     				.append("<td>").append(onlineClass.getLesson().getLearningCycle().getUnit().getCourse().getName()).append("</td>")
     				.append("<td>").append(onlineClass.getLesson().getName()).append("</td>")
     				.append("<td>");
     			for(Student student : onlineClass.getStudents()){
     				onlineClassList.append(student.getEnglishName()).append(TextUtils.SPACE);
     			}
     			onlineClassList.append("</td>")
     				.append("<td>").append(onlineClass.getTeacher().getName()).append("</td>")
     				.append("<td>").append(onlineClass.getClassroom()).append("</td>")//正常应填"";测试：onlineClass.getClassroom()
     				.append("</tr>");
     		}
     		onlineClassList.append("</table>");
     		paramsMap.put("onlineClassesList", onlineClassList);
     		String subject = template.render(EmailTemplates.reminderForAutoCreateClassRoomFailed.SUBJECT, paramsMap);
             String content = template.render(EmailTemplates.reminderForAutoCreateClassRoomFailed.CONTENT, paramsMap);
     		send(Configurations.EMail.FROM,reminderEmail , subject, content);
     	}
     	
     }
    

    // family it test normal 结果发送
    public static void sendToRelatedPersonAfterFamilyItTestNormalEmail(ItTest itTest, Student student) {
        if (null == itTest || null == student) {
            logger.warn("Can not send mail in sendToRelatedPersonAfterFamilyItTestNormalEmail,because itTest is null or student is null");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }

        paramsMap.put("familyName", student.getFamily().getName());
        paramsMap.put("testDateTime", DateTimeUtils.format(itTest.getTestDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        paramsMap.put("parentsInfo", parentsInfo);
        String subject = template.render(EmailTemplates.FamilyItTestNormalEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.FamilyItTestNormalEmailTemplate.CONTENT, paramsMap);

        if (checkStudentSalesBeforeSendMail(student)) {
            send(Configurations.EMail.FROM, student.getSales().getEmail(), subject, content);
        } else {
            logger.warn("Can not send mail in sendToRelatedPersonAfterFamilyItTestNormalEmail,student's sales is null or student's sales email is null,studentID={},studentEnglishName={}", student.getId(), student.getEnglishName());
        }
    }

    // family it test abnormal 结果发送
    public static void sendToRelatedPersonAfterFamilyItTestAbnormalEmail(ItTest itTest, Student student) {
        if (null == itTest || null == student) {
            logger.warn("Can not send mail in sendToRelatedPersonAfterFamilyItTestAbnormalEmail,because itTest is null or student is null");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder parentsInfo = new StringBuilder();
        for (Parent parent : student.getFamily().getParents()) {
            parentsInfo.append(parent.getName()).append(TextUtils.SEPERATOR).append(parent.getMobile()).append(TextUtils.SPACE);
        }

        StringBuilder itTestResultInfo = new StringBuilder();
        itTestResultInfo.append("<table  border=1> \n")
                .append("<tr>")
                .append("<td>").append("system").append("</td>")
                .append("<td>").append("browser").append("</td>")
                .append("<td>").append("flash").append("</td>")
                .append("<td>").append("connect").append("</td>")
                .append("<td>").append("delay").append("</td>")
                .append("<td>").append("bandWidth").append("</td>")
                .append("<td>").append("sound").append("</td>")
                .append("<td>").append("mic").append("</td>")
                .append("<td>").append("camera").append("</td>")
                .append("</tr>");
        itTestResultInfo
                .append("<tr>")
                .append("<td>").append(itTest.getSystem()).append(TextUtils.SPACE).append(itTest.getSystemResult()).append("</td>")
                .append("<td>").append(itTest.getBrowser()).append(TextUtils.SPACE).append(itTest.getBrowserResult()).append("</td>")
                .append("<td>").append(itTest.getFlash()).append(TextUtils.SPACE).append(itTest.getFlashResult()).append("</td>")
                .append("<td>").append(itTest.getConnect()).append(TextUtils.SPACE).append(itTest.getConnectResult()).append("</td>")
                .append("<td>").append(itTest.getDelay()).append(TextUtils.SPACE).append(itTest.getDelayResult()).append("</td>")
                .append("<td>").append(itTest.getBandWidth()).append(TextUtils.SPACE).append(itTest.getBandWidthResult()).append("</td>")
                .append("<td>").append(itTest.getSound()).append(TextUtils.SPACE).append(itTest.getSoundResult()).append("</td>")
                .append("<td>").append(itTest.getMic()).append(TextUtils.SPACE).append(itTest.getMicResult()).append("</td>")
                .append("<td>").append(itTest.getCamera()).append(TextUtils.SPACE).append(itTest.getCameraResult()).append("</td>")
                .append("</tr>");
        itTestResultInfo.append("</table>");

        paramsMap.put("familyName", student.getFamily().getName());
        paramsMap.put("itTestResultInfo", itTestResultInfo);
        paramsMap.put("parentsInfo", parentsInfo);
        paramsMap.put("testDateTime", DateTimeUtils.format(itTest.getTestDateTime(), DateTimeUtils.DATETIME_FORMAT2));
        String subject = template.render(EmailTemplates.FamilyItTestAbnormalEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.FamilyItTestAbnormalEmailTemplate.CONTENT, paramsMap);

        if (null != student.getSales() && StringUtils.isNotBlank(student.getSales().getEmail())) {
            send(Configurations.EMail.FROM, student.getSales().getEmail(), subject, content);
        } else {
            logger.warn("This student has no sales or sales email is null,studentID={},studentEnglishName={}", student.getId(), student.getEnglishName());
        }
        if (null != student.getChineseLeadTeacher() && StringUtils.isNotBlank(student.getChineseLeadTeacher().getEmail())) {
            send(Configurations.EMail.FROM, student.getChineseLeadTeacher().getEmail(), subject, content);
        } else {
            logger.warn("This student has no CLT or CLT email is null,studentID={},studentEnglishName={}", student.getId(), student.getEnglishName());
        }
        send(Configurations.EMail.FROM, itSupportEmail, subject, content);
    }

    // teacher it test 结果发送
    public static void sendToRelatedPersonAfterTeacherItTestEmail(ItTest itTest, Teacher teacher) {
        if (null == itTest || null == teacher) {
            logger.warn("Can not send mail in sendToRelatedPersonAfterTeacherItTestEmail,because itTest is null or teacher is null");
            return;
        }

        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        StringBuilder itTestResultInfo = new StringBuilder();
        itTestResultInfo.append("<table  border=1> \n")
                .append("<tr>")
                .append("<td>").append("system").append("</td>")
                .append("<td>").append("browser").append("</td>")
                .append("<td>").append("flash").append("</td>")
                .append("<td>").append("connect").append("</td>")
                .append("<td>").append("delay").append("</td>")
                .append("<td>").append("bandWidth").append("</td>")
                .append("<td>").append("sound").append("</td>")
                .append("<td>").append("mic").append("</td>")
                .append("<td>").append("camera").append("</td>")
                .append("</tr>");
        itTestResultInfo
                .append("<tr>")
                .append("<td>").append(itTest.getSystem()).append(TextUtils.SPACE).append(itTest.getSystemResult()).append("</td>")
                .append("<td>").append(itTest.getBrowser()).append(TextUtils.SPACE).append(itTest.getBrowserResult()).append("</td>")
                .append("<td>").append(itTest.getFlash()).append(TextUtils.SPACE).append(itTest.getFlashResult()).append("</td>")
                .append("<td>").append(itTest.getConnect()).append(TextUtils.SPACE).append(itTest.getConnectResult()).append("</td>")
                .append("<td>").append(itTest.getDelay()).append(TextUtils.SPACE).append(itTest.getDelayResult()).append("</td>")
                .append("<td>").append(itTest.getBandWidth()).append(TextUtils.SPACE).append(itTest.getBandWidthResult()).append("</td>")
                .append("<td>").append(itTest.getSound()).append(TextUtils.SPACE).append(itTest.getSoundResult()).append("</td>")
                .append("<td>").append(itTest.getMic()).append(TextUtils.SPACE).append(itTest.getMicResult()).append("</td>")
                .append("<td>").append(itTest.getCamera()).append(TextUtils.SPACE).append(itTest.getCameraResult()).append("</td>")
                .append("</tr>");
        itTestResultInfo.append("</table>");

        paramsMap.put("teacherName", teacher.getSafeName());
        paramsMap.put("finalResult", itTest.getFinalResult());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        paramsMap.put("testDateTime", DateTimeUtils.format(itTest.getTestDateTime(), dateFormat, TimeZone.getTimeZone(teacher.getTimezone())));
        paramsMap.put("itTestResultInfo", itTestResultInfo);
        String subject = template.render(EmailTemplates.TeacherItTestEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.TeacherItTestEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, teacherItTestCallBackEmail, subject, content);
    }

    // 学生水平自测 结果发送
    public static void sendStudentLevelExamEmail(StudentExam studentExam, Student student) {
        if (null == studentExam || null == student) {
            logger.warn("Can not send mail in sendStudentLevelExamEmail,because studentExam is null or student is null");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(student.getName())) {
            sb.append(student.getName());
        }
        if (!TextUtils.isEmpty(student.getEnglishName())) {
            sb.append(student.getEnglishName());
        }

        paramsMap.put("studentName", sb.toString());
        paramsMap.put("examResult", studentExam.getExamLevel());

        String subject = template.render(EmailTemplates.StudentLevelExamEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.StudentLevelExamEmailTemplate.CONTENT, paramsMap);
        // 分别发送给cli和sales
        if (student.getSales() != null && student.getSales().getEmail() != null) {
            send(Configurations.EMail.FROM, student.getSales().getEmail(), subject, content);
        }
        if (null != student.getChineseLeadTeacher() && null != student.getChineseLeadTeacher().getEmail()) {
            send(Configurations.EMail.FROM, student.getChineseLeadTeacher().getEmail(), subject, content);
        }
    }
    
    // 2015-08-31 学生Trial水平自测 结果发送
    public static void sendStudentTrialLevelExamEmail(Student student, String saleEmail, String strResult) {
        if (null == student || null == strResult) {
            logger.warn("Can not send mail in sendStudentTrialLevelExamEmail,because student is null or strResult is null");
            return;
        }
        
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        
        paramsMap.put("studentName", student.getName());
        paramsMap.put("examResult", strResult);

        String subject = template.render(EmailTemplates.StudentTrialLevelExamEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.StudentTrialLevelExamEmailTemplate.CONTENT, paramsMap);
        
        // 分别发送给cli和sales
        if (saleEmail != null) {
            send(Configurations.EMail.FROM, saleEmail, subject, content);
        }
        
        if (null != student.getChineseLeadTeacher() && null != student.getChineseLeadTeacher().getEmail()) {
            send(Configurations.EMail.FROM, student.getChineseLeadTeacher().getEmail(), subject, content);
        }
    }

    public static boolean checkOnlineClassBeforeSendMail(OnlineClass onlineClass) {
        boolean checkStatus = false;
        if (null != onlineClass) {
            if (null == onlineClass.getTeacher()) {
                logger.warn("OnlineClass's teacher is null,onlineClassID={}", onlineClass.getId());
                return checkStatus;
            }
            if (StringUtils.isBlank(onlineClass.getTeacher().getName())) {
                logger.warn("OnlineClass's teacher's name is null,onlineClassID={},teacherID={}", onlineClass.getId(), onlineClass.getTeacher().getId());
                return checkStatus;
            }
            if (null == onlineClass.getLesson()) {
                logger.warn("OnlineClass's lesson is null,onlineClassID={}", onlineClass.getId());
                return checkStatus;
            }
            if (null == onlineClass.getLesson().getLearningCycle()) {
                logger.warn("OnlineClass's learning cycle is null,onlineClassID={},lessonID={}", onlineClass.getId(), onlineClass.getLesson().getId());
                return checkStatus;
            }
            if (null == onlineClass.getLesson().getLearningCycle().getUnit()) {
                logger.warn("OnlineClass's unit is null,onlineClassID={},lessonID={},learning cycleID={}", onlineClass.getId(), onlineClass.getLesson().getId(), onlineClass.getLesson().getLearningCycle());
                return checkStatus;
            }
            if (null == onlineClass.getLesson().getLearningCycle().getUnit().getCourse()) {
                logger.warn("OnlineClass's course is null,onlineClassID={},lessonID={},learning cycleID={}", onlineClass.getId(), onlineClass.getLesson().getId(), onlineClass.getLesson().getLearningCycle());
                return checkStatus;
            }
            checkStatus = true;
        } else {
            logger.warn("OnlineClass is null");
        }
        return checkStatus;
    }

    public static boolean checkStudentSalesBeforeSendMail(Student student) {
        return (null != student && null != student.getSales() && StringUtils.isNotBlank(student.getSales().getEmail()));
    }

    public static void sendSystemErrotToRD(String msg) {
        logger.warn("System error,send msg to R&D");
        send(Configurations.EMail.FROM, R_DEmail, "System Error", "Email sendToEducationTeacherNoShowEmail() error,pls check out the log,key msg={}", msg);
    }

    public static void main(String[] args) {
    }

    public static void sendOnlineClassGroupByCourseEmail(List<CountOnlineClassByCourseView> list, String toEmail) {
        if (StringUtils.isBlank(toEmail)) {
            logger.warn("Can not send mail in sendOnlineClassGroupByCourseEmail,because toEmail is null");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder contentText = new StringBuilder();
        contentText.append("<table  border=1> \n")
                .append("<tr>")
                .append("<td>").append("Course Name").append("</td>")
                .append("<td>").append("Count").append("</td>")
                .append("</tr>");
        if (list != null && !list.isEmpty()) {
            for (CountOnlineClassByCourseView view : list) {
                contentText.append("<tr>");
                contentText.append("<td>").append(view.getCourseName()).append("</td>");
                contentText.append("<td>").append(view.getNum()).append("</td>");
                contentText.append("<tr>");
            }
        }
        contentText.append("</table>");
        paramsMap.put("contentText", contentText);
        String subject = template.render(EmailTemplates.CountOnlineClassGroupByCourseTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.CountOnlineClassGroupByCourseTemplate.CONTENT, paramsMap);
        send(Configurations.EMail.FROM, toEmail, subject, content);
    }
    
    public static void sendTrialOnlineClassFromOpenClass(Student student,String phone,String lessonSerialNumber,String toEmail) {
        if (StringUtils.isBlank(toEmail)) {
            logger.warn("Can not send mail in sendTrialOnlineClassFromOpenClass,because toEmail is null");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("studentName", student.getName()==null?student.getEnglishName():student.getName());
        paramsMap.put("phone", phone);
        paramsMap.put("time", DateTimeUtils.format(new Date(), DateTimeUtils.DATETIME_FORMAT));
        paramsMap.put("lessonSerialNumber", lessonSerialNumber);
        String subject = template.render(EmailTemplates.TrialOnlineClassFromOpenClassTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.TrialOnlineClassFromOpenClassTemplate.CONTENT, paramsMap);
        send(Configurations.EMail.FROM, toEmail, subject, content);
    }
    
    // jms故障(重试3次消息仍未发出)，向开发组发送邮件
    public static void sendJMSExceptionToRDTeamEmail(String msgContent, String message, String exception) {
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("msgContent", msgContent);
        paramsMap.put("message", message);
        paramsMap.put("exception", exception);
        String subject = template.render(EmailTemplates.JMSExceptionToRDTeamEmailTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.JMSExceptionToRDTeamEmailTemplate.CONTENT, paramsMap);

        send(Configurations.EMail.FROM, R_DEmail, subject, content);
    }
    
    // 每天定时发送最新的需要续费的学生
    public static void sendStatisticsRenewStudentEmail(List<StatisticsRenewStudentView> list, String toEmail) {
        if (StringUtils.isBlank(toEmail)) {
            logger.warn("Can not send mail in sendStatisticsRenewStudentEmail,because toEmail is null");
            return;
        }
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder contentText = new StringBuilder();
        contentText.append("<table  border=1> \n")
                .append("<tr>")
                .append("<td>").append("studentId").append("</td>")
                .append("<td>").append("name").append("</td>")
                .append("<td>").append("leftClassHour").append("</td>")
                .append("<td>").append("cltTeacherId").append("</td>")
                .append("<td>").append("cltTeacherName").append("</td>")
                .append("</tr>");
        if (list != null && !list.isEmpty()) {
            for (StatisticsRenewStudentView view : list) {
                contentText.append("<tr>");
                contentText.append("<td>").append(view.getStudentId()).append("</td>");
                contentText.append("<td>").append(view.getName()).append("</td>");
                contentText.append("<td>").append(view.getLeftClassHour()).append("</td>");
                contentText.append("<td>").append(view.getCltTeacherId()).append("</td>");
                contentText.append("<td>").append(view.getCltTeacherName()).append("</td>");
                contentText.append("<tr>");
            }
        }
        contentText.append("</table>");
        paramsMap.put("contentText", contentText);
        String subject = template.render(EmailTemplates.StatisticsRenewStudentTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.StatisticsRenewStudentTemplate.CONTENT, paramsMap);
        send(Configurations.EMail.FROM, toEmail, subject, content);
    }

    //第一天晚上发送第二天要进行ITTest的学生名单
    public static void sendITTestEmail(List<OnlineClass> list, String toEmail){
        AbstractTemplates template = new EmailTemplates();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuilder contentText = new StringBuilder();
        contentText.append("<table border = '1'> \n")
        .append("<tr>")
        .append("<td>").append("课程时间").append("</td>")
        .append("<td>").append("测试时间").append("</td>")
        .append("<td>").append("学生姓名").append("</td>")
        .append("<td>").append("手机号").append("</td>")
        .append("<td>").append("学生ID").append("</td>")
        .append("<td>").append("课标").append("</td>")
        .append("<td>").append("课程类型").append("</td>")
        .append("</tr>");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat df_time = new SimpleDateFormat("HH:mm");
	    if (list != null && !list.isEmpty()) {
	        for (OnlineClass view : list) {
	        	Calendar calendar = Calendar.getInstance();
	        	calendar.setTime(view.getScheduledDateTime());
	        	calendar.add(Calendar.MINUTE, -30);
	            contentText.append("<tr>");
	            contentText.append("<td>").append(df.format(view.getScheduledDateTime())).append("</td>");
	            contentText.append("<td>").append(df_time.format(calendar.getTime())).append("</td>");
	            contentText.append("<td>").append(StringUtils.isEmpty(view.getStudents().get(0).getName())?"":view.getStudents().get(0).getName()).append("</td>");
	            if(view.getStudents().get(0).getFamily() != null && view.getStudents().get(0).getFamily().getParents() != null && view.getStudents().get(0).getFamily().getParents().size()>0){
	            	contentText.append("<td>").append(view.getStudents().get(0).getFamily().getParents().get(0).getMobile()).append("</td>");
	            }else{
	            	contentText.append("<td>").append("</td>");
	            }
	            contentText.append("<td>").append(view.getStudents().get(0).getId()).append("</td>");
	            contentText.append("<td>").append(view.getLesson().getSerialNumber()).append("</td>");
	            contentText.append("<td>").append(view.getLesson().getLearningCycle().getUnit().getCourse().getDescription()).append("</td>");
	            contentText.append("</tr>");
	        }
	    }
	    contentText.append("</table>");
        paramsMap.put("contentText", contentText);
        String subject = template.render(EmailTemplates.InfoITTestOfTomorrowTemplate.SUBJECT, paramsMap);
        String content = template.render(EmailTemplates.InfoITTestOfTomorrowTemplate.CONTENT, paramsMap);
        if (StringUtils.isBlank(toEmail)) {
            logger.warn("Can not send mail in ITTest Email,because toEmail is null");
        }
        send(Configurations.EMail.FROM, toEmail, subject, content);
    }
}
