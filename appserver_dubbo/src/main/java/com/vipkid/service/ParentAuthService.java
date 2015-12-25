package com.vipkid.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.model.AirCraft;
import com.vipkid.model.AirCraftTheme;
import com.vipkid.model.Channel;
import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Family;
import com.vipkid.model.Gender;
import com.vipkid.model.InventionCode;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.LearningProgress.Status;
import com.vipkid.model.MarketingActivity;
import com.vipkid.model.Parent;
import com.vipkid.model.Pet;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.Student.MarketActivities;
import com.vipkid.model.Student.Source;
import com.vipkid.redis.KeyGenerator;
import com.vipkid.redis.RedisClient;
import com.vipkid.repository.AirCraftRepository;
import com.vipkid.repository.AirCraftThemeRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.InventionCodeRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.MarketingActivityRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.repository.PetRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.rest.vo.Response;
import com.vipkid.security.PasswordEncryptor;
import com.vipkid.security.PasswordGenerator;
import com.vipkid.security.SecurityService;
import com.vipkid.security.TokenGenerator;
import com.vipkid.service.exception.ActivityIdInvalidServiceException;
import com.vipkid.service.exception.ActivityIdIsNotInRegisterSourceListException;
import com.vipkid.service.exception.AuthServiceException;
import com.vipkid.service.exception.IncorrectOpenIdServiceException;
import com.vipkid.service.exception.NoVerifyCodeServiceException;
import com.vipkid.service.exception.OpenIdAlreadyUsedServiceException;
import com.vipkid.service.exception.ParentMobileUsedServiceException;
import com.vipkid.service.exception.ServiceExceptionCode;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.exception.UserNotExistServiceException;
import com.vipkid.service.pojo.Binding;
import com.vipkid.service.pojo.Credential;
import com.vipkid.service.pojo.Signup;
import com.vipkid.service.pojo.SignupAndBinding;
import com.vipkid.service.pojo.SignupFromInvitation;
import com.vipkid.service.pojo.SudoCredential;
import com.vipkid.util.Configurations;
import com.vipkid.util.TextUtils;
import com.vipkid.util.UrlUtil;

/**
 * @author VIPKID
 *         history: 添加数据导入的feature。
 */
@Service("parentAuthService")
public class ParentAuthService {
    private static final String AGE_TOO_YOUNG = "TOO_YOUNG";
    private static final String AGE_TOO_OLD = "TOO_OLD";
    private static final String AGE_OK = "OK";

    private Logger logger = LoggerFactory.getLogger(ParentAuthService.class.getSimpleName());

    @Resource
    private ParentRepository parentRepository;

    @Resource
    private FamilyRepository familyRepository;

    @Resource
    private AirCraftRepository aircraftRepository;

    @Resource
    private AirCraftThemeRepository aircraftThemeRepository;

    @Resource
    private PetRepository petRepository;

    @Resource
    private StudentRepository studentRepository;

    // 数据导入 -- 为学生指定staff（saler）
    @Resource
    private StaffRepository staffRepository;

    @Resource
    private CourseRepository courseRepository;

    @Resource
    private LearningProgressRepository learingProgressRepository;

    @Resource
    private StudentService studentService;

    @Resource
    private ChannelService channelService;

    @Resource
    private MarketingActivityRepository marketingActivityRepository;

    @Resource
    private InventionCodeRepository inventionCodeRepository;

    @Resource
    private SecurityService securityService;

	@Resource
	private StudentLifeCycleLogService studentLifeCycleLogService;
	
    public Parent login(Credential credential) {
        String username = credential.getUsername();
        String password = credential.getPassword();

        Parent parent = parentRepository.findByUsernameAndPassword(username, password);
        if (parent == null) {
            throw new UserNotExistServiceException("Parent[username: {}] is not exist.", username);
        } else {
            if (TextUtils.isEmpty(parent.getToken())) {
                parent.setToken(TokenGenerator.generate());
            }
            parent.setLastLoginDateTime(new Date());
            parentRepository.update(parent);
            cacheInRedis(parent);
            logger.info("Parent[username: {}] is login.", parent.getUsername());
        }

        return parent;
    }
    
    public Parent sudoLogin(SudoCredential credential) {
        String parentName = credential.getUserName();
        String adminName = credential.getAdminName();
        String password = credential.getPassword();
        
        Staff admin = staffRepository.findByUsernameAndPassword(adminName, password);
        if (admin == null || !admin.getRoles().contains("DEBUGGER")) {
        	throw new UserNotExistServiceException("Admin[username: {}] is not exist.", adminName);
        } else {
        	Parent parent = parentRepository.findByUsername(parentName);
        	if (parent == null) {
                throw new UserNotExistServiceException("Parent[username: {}] is not exist.", parentName);
            } else {
                if (TextUtils.isEmpty(parent.getToken())) {
                    parent.setToken(TokenGenerator.generate());
                }
                parent.setLastLoginDateTime(new Date());
                parentRepository.update(parent);
                cacheInRedis(parent);
                logger.info("Parent[username: {}] is login.", parent.getUsername());
            }
            
            return parent;
        }
    }

    public void cacheInRedis(Parent parent) {
        if (null != parent) {
            logger.info("Cache Parent in redis,parent's name is{}", parent.getUsername());
            String redisKey = KeyGenerator.generateKey(String.valueOf(parent.getId()), parent.getToken());
            Parent cachedParent = new Parent();
            cachedParent.setId(parent.getId());
            cachedParent.setToken(parent.getToken());
            cachedParent.setRoles(parent.getRoles());
            cachedParent.setUsername(parent.getUsername());
            cachedParent.setName(parent.getName());
            cachedParent.setEmail(parent.getEmail());
            cachedParent.setMobile(parent.getMobile());
            RedisClient.getInstance().setObject(redisKey, cachedParent);
        }
    }

    public Parent loginByUserNamePasswordOpenid(Binding binding) {
        String username = binding.getUsername();
        String password = binding.getPassword();
        String openId = binding.getWechatOpenId();

        Parent parent = parentRepository.findByUsernameAndPassword(username, password);
        if (parent == null) {
            logger.error("Username or password is incorrect!");
            throw new AuthServiceException("Username or password or openid is incorrect!");
        } else {
            parent = parentRepository.find(parent.getId());
            if (parent == null) {
                logger.error("Permission dennied!");
                throw new AuthServiceException("Permission dennied!");
            }
        }

        if (openId == null || !parent.getWechatOpenId().equals(openId)) {
            logger.error("Openid error");
            throw new IncorrectOpenIdServiceException("OpenId is null or the user is not using the orignal wechat account.");
        }

        parent.setLastLoginDateTime(new Date());
        if (TextUtils.isEmpty(parent.getToken())) {
            parent.setToken(TokenGenerator.generate());
        }
        parentRepository.update(parent);
        logger.info("Parent[" + parent.getUsername() + "] logged on");
        return parent;
    }

    private void doCreateSignupByMarketActivity(Parent parent, Student student, Family family) {
        // 新建家长
        String parentPassword = parent.getPassword();
        parent.setPassword(PasswordEncryptor.encrypt(parentPassword));
        parent.setUsername(parent.getMobile());
        parent.setMobile(parent.getUsername());
        parent.setFamily(family);
        parentRepository.create(parent);
        List<Parent> parents = new ArrayList<Parent>();
        parents.add(parent);
        family.setParents(parents);

        // 新建学生
        String studentPassword = PasswordGenerator.generate();
        student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
        student.setPassword(studentPassword);
        student.setWelcome(true);
        student.setFamily(family);
        student.setCreater(parent);
        student.setLifeCycle(LifeCycle.SIGNUP);
        student.setAvatar("boy_3");
        studentRepository.create(student);
		studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
        List<Student> students = new ArrayList<Student>();
        students.add(student);
        family.setStudents(students);

        //给学生一个默认的飞机
        AirCraft airCraft = new AirCraft();
        airCraft.setStudent(student);
        airCraft.setSequence(1);
        aircraftRepository.create(airCraft);

        AirCraftTheme airCraftTheme = new AirCraftTheme();
        airCraftTheme.setAirCraft(airCraft);
        airCraftTheme.setCurrent(true);
        airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
        airCraftTheme.setLevel(1);
        airCraftTheme.setName("阿波罗号");
        airCraftTheme.setPrice(0);
        airCraftTheme.setUrl("ac1_1");
        aircraftThemeRepository.create(airCraftTheme);

        //给学生一个默认的宠物
        Pet pet = new Pet();
        pet.setStudent(student);
        pet.setName(student.getEnglishName() + "'s Spirit");
        pet.setSequence(10);
        pet.setPrice(0);
        pet.setUrl("pet10");
        pet.setCurrent(true);
        pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
        petRepository.create(pet);


    }

    private Parent doCreateSignup(Parent parent, Student student, Family family, String url) {
        logger.info("do Create SignUp: url: {}", url);
        MarketActivities currentActivity = null;
        // 新建家长
        String parentPassword = parent.getPassword();
        parent.setPassword(PasswordEncryptor.encrypt(parentPassword));
        parent.setUsername(parent.getMobile());
        parent.setMobile(parent.getUsername());
        parent.setFamily(family);
        if (TextUtils.isEmpty(parent.getToken())) {
            parent.setToken(TokenGenerator.generate());
        }
        parentRepository.create(parent);
        List<Parent> parents = new ArrayList<Parent>();
        parents.add(parent);
        family.setParents(parents);

        if (student != null) {
            // 新建学生
            if (student.getAttendedActivities() != null) {
                //Find current activity.
                for (MarketActivities activity : MarketActivities.values()) {
                    //Check the activity(from web) is valid.
                    if (student.getAttendedActivities().indexOf(activity.toString()) > 0) {
                        currentActivity = activity;
                    }
                }
            }

            Source registerSource = null;
            if (student.getSource() != null) {
                //Find current register source.

                for (Source source : Source.values()) {
                    //Check the activity(from web) is valid.
                    if (source.equals(student.getSource())) {
                        registerSource = source;
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(url)) {
                Channel channel = channelService.findChannelByURL(url);
                if (channel != null) {
                    logger.info("Channel source name: {}", channel.getSourceName());
                    student.setChannel(channel);
                }
                if (url.indexOf(channelService.CHANNEL_KEYWORD) > 0) {
                    String kw = StringUtils.substring(url, StringUtils.indexOf(url, channelService.CHANNEL_KEYWORD) + channelService.CHANNEL_KEYWORD.length());
                    if (!StringUtils.isEmpty(kw)) {
                        student.setChannelKeyword(kw);
                    }
                }
            }
            //老生推荐的级别最高，如果是老生推荐的，则将渠道来源改为老生推荐--yan jie
            if(!TextUtils.isEmpty(parent.getRecommendCode())){
            	Parent rparent = parentRepository.findByUsername(parent.getRecommendCode());
            	if(rparent != null){
            		Channel channel2 = channelService.find(217);
                	student.setChannel(channel2);
            	}
            }
            
            String studentPassword = PasswordGenerator.generate();
            student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
            student.setPassword(studentPassword);
            //		student.setSource(Source.WEBSITE);
            student.setWelcome(true);
            student.setFamily(family);
            student.setCreater(parent);
            student.setLifeCycle(LifeCycle.SIGNUP);
            student.setAvatar("boy_3");
            if (registerSource != null) {
                student.setSource(registerSource);
            } else {
                student.setSource(Source.WEBSITE);
            }
            if (currentActivity != null) {
                student.addAttendedActivity(currentActivity);
            }

            studentRepository.create(student);
			studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
            List<Student> students = new ArrayList<Student>();
            students.add(student);
            family.setStudents(students);

            //					// 更新家庭
            //					family.setCreater(parent);
            //					familyAccessor.update(family);

            // 新建ITTest课学习进度
            Course itTestCourse = courseRepository.findByCourseType(Type.IT_TEST);
            LearningProgress itTestLearningProgress = new LearningProgress();
            itTestLearningProgress.setStudent(student);
            itTestLearningProgress.setStatus(Status.STARTED);
            itTestLearningProgress.setCourse(itTestCourse);
            itTestLearningProgress.setLeftClassHour(1);
            itTestLearningProgress.setTotalClassHour(1);
            learingProgressRepository.create(itTestLearningProgress);

            // 新建试听课学习进度
            Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
            LearningProgress demoLearningProgress = new LearningProgress();
            demoLearningProgress.setStudent(student);
            demoLearningProgress.setStatus(Status.STARTED);
            demoLearningProgress.setCourse(demoCourse);
            demoLearningProgress.setLeftClassHour(1);
            demoLearningProgress.setTotalClassHour(1);
            learingProgressRepository.create(demoLearningProgress);

            //给学生一个默认的飞机
            AirCraft airCraft = new AirCraft();
            airCraft.setStudent(student);
            airCraft.setSequence(1);
            aircraftRepository.create(airCraft);

            AirCraftTheme airCraftTheme = new AirCraftTheme();
            airCraftTheme.setAirCraft(airCraft);
            airCraftTheme.setCurrent(true);
            airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
            airCraftTheme.setLevel(1);
            airCraftTheme.setName("阿波罗号");
            airCraftTheme.setPrice(0);
            airCraftTheme.setUrl("ac1_1");
            aircraftThemeRepository.create(airCraftTheme);

            //给学生一个默认的宠物
            Pet pet = new Pet();
            pet.setStudent(student);
            pet.setName(student.getEnglishName() + "'s Spirit");
            pet.setSequence(10);
            pet.setPrice(0);
            pet.setUrl("pet10");
            pet.setCurrent(true);
            pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
            petRepository.create(pet);
        }

        // 发送新家长注册短信
        if (student != null) {
            SMS.sendNewParentSignupSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());
        }

        // 发送新家长注册统计邮件
        EMail.sendNewParentSignupEmail(parent.getMobile());

        return parent;
    }

    private Parent doOtherInvitationStuff(Parent parent, Student student, Family family) {
        // 新建家长
        String parentPassword = parent.getPassword();
        parent.setPassword(PasswordEncryptor.encrypt(parentPassword));
        parent.setUsername(parent.getMobile());
        parent.setMobile(parent.getUsername());
        parent.setFamily(family);
        if (TextUtils.isEmpty(parent.getToken())) {
            parent.setToken(TokenGenerator.generate());
        }
        parentRepository.create(parent);
        List<Parent> parents = new ArrayList<Parent>();
        parents.add(parent);
        family.setParents(parents);

        // 新建学生
        String studentPassword = PasswordGenerator.generate();
        student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
        student.setPassword(studentPassword);
        student.setSource(Source.WEBSITE);
        student.setWelcome(true);
        student.setFamily(family);
        student.setCreater(parent);
        student.setLifeCycle(LifeCycle.SIGNUP);
        student.setAvatar("boy_3");
        studentRepository.create(student);
		studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
        List<Student> students = new ArrayList<Student>();
        students.add(student);
        family.setStudents(students);

        //					// 更新家庭
        //					family.setCreater(parent);
        //					familyAccessor.update(family);

        // 新建ITTest课学习进度
        Course itTestCourse = courseRepository.findByCourseType(Type.IT_TEST);
        LearningProgress itTestLearningProgress = new LearningProgress();
        itTestLearningProgress.setStudent(student);
        itTestLearningProgress.setStatus(Status.STARTED);
        itTestLearningProgress.setCourse(itTestCourse);
        itTestLearningProgress.setLeftClassHour(1);
        itTestLearningProgress.setTotalClassHour(1);
        learingProgressRepository.create(itTestLearningProgress);

        // 新建试听课学习进度
        Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
        LearningProgress demoLearningProgress = new LearningProgress();
        demoLearningProgress.setStudent(student);
        demoLearningProgress.setStatus(Status.STARTED);
        demoLearningProgress.setCourse(demoCourse);
        demoLearningProgress.setLeftClassHour(1);
        demoLearningProgress.setTotalClassHour(1);
        learingProgressRepository.create(demoLearningProgress);

        //给学生一个默认的飞机
        AirCraft airCraft = new AirCraft();
        airCraft.setStudent(student);
        airCraft.setSequence(1);
        aircraftRepository.create(airCraft);

        AirCraftTheme airCraftTheme = new AirCraftTheme();
        airCraftTheme.setAirCraft(airCraft);
        airCraftTheme.setCurrent(true);
        airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
        airCraftTheme.setLevel(1);
        airCraftTheme.setName("阿波罗号");
        airCraftTheme.setPrice(0);
        airCraftTheme.setUrl("ac1_1");
        aircraftThemeRepository.create(airCraftTheme);

        //给学生一个默认的宠物
        Pet pet = new Pet();
        pet.setStudent(student);
        pet.setName(student.getEnglishName() + "'s Spirit");
        pet.setSequence(10);
        pet.setPrice(0);
        pet.setUrl("pet10");
        pet.setCurrent(true);
        pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
        petRepository.create(pet);

        // 发送新家长注册短信
        SMS.sendNewParentSignupSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());

        // 发送新家长注册统计邮件
        EMail.sendNewParentSignupEmail(parent.getMobile());

        return parent;
    }

    public Parent doSignupFromInvitation(SignupFromInvitation signupFromInvitation) {
        Parent parent = signupFromInvitation.getParent();
        Student student = signupFromInvitation.getStudent();
        String invitationId = signupFromInvitation.getInvitationId();

        Parent findParent = parentRepository.findByUsername(parent.getUsername());
        if (findParent == null) {
            // 新建家庭
            Family family = new Family();
            Family inviteFamily = familyRepository.findByInvitationId(invitationId);

            if (inviteFamily != null) {
                Long studentNumber = null;
                try {
                    studentNumber = inviteFamily.getStudentNumberIInvented();
                } catch (NullPointerException e) {
                    studentNumber = (long) 0;
                }
                inviteFamily.setStudentNumberIInvented(studentNumber + 1);
                familyRepository.update(inviteFamily);
                family.setInvitedBy(inviteFamily);
            }

            familyRepository.create(family);

            return doOtherInvitationStuff(parent, student, family);
        } else {
            throw new UserAlreadyExistServiceException("Parent[username: {}] is already exist.", parent.getUsername());
        }
    }

    public Response doSignup4MarketActivity(Signup signup) {
        logger.error("marketActivity: start signup");
        Response response = new Response(HttpStatus.OK.value());
        Parent parent = signup.getParent();
        Student student = signup.getStudent();

        //Find current activity.
        MarketActivities currentActivity = null;
        for (MarketActivities activity : MarketActivities.values()) {
            //Check the activity(from web) is valid.
            if (activity.toString().equals(student.getAttendedActivities())) {
                currentActivity = activity;
            }
        }

        //Find current register source.
        Source registerSource = null;
        for (Source source : Source.values()) {
            //Check the activity(from web) is valid.
            if (source.equals(student.getSource())) {
                registerSource = source;
                break;
            }
        }

        if (currentActivity == null) {
            throw new ActivityIdInvalidServiceException("The activity id is wrong");
        }

        if (registerSource == null) {
            throw new ActivityIdIsNotInRegisterSourceListException("The activity id is not in register source Enum list");
        }

        logger.error("marketActivity: find parent exist");
        Parent foundParent = parentRepository.findByUsername(parent.getUsername());
        if (foundParent == null) {
            logger.error("marketActivity: parent no exist");
            Family family = new Family();
            if (student.getFamily() != null && student.getFamily().getProvince() != null
                    && student.getFamily().getCity() != null
                    && student.getFamily().getDistrict() != null) {
                family.setProvince(student.getFamily().getProvince());
                family.setCity(student.getFamily().getCity());
                family.setDistrict(student.getFamily().getDistrict());
            }
            familyRepository.create(family);

            student.addAttendedActivity(currentActivity);
            student.setSource(registerSource);

            doCreateSignupByMarketActivity(parent, student, family);

            if (registerSource.equals(Source.haosaishi1)) {
                // 发送新家长注册短信
                SMS.sendNewParentSignupReplySMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());
            } else {
                // 发送新家长注册短信
                SMS.sendfreeInterViewSignupSMS(parent.getMobile(), parent.getMobile(), parent.getMobile());
            }

            // 发送新家长注册统计邮件
//			EMail.sendNewParentSignupEmail(servletContext, parent.getMobile());

            String isAgeOK = isAgeQualified(student.getBirthday());
            if (isAgeOK.equals(AGE_OK)) {
                // 新建ITTest课学习进度
                Course itTestCourse = courseRepository.findByCourseType(Type.IT_TEST);
                LearningProgress itTestLearningProgress = new LearningProgress();
                itTestLearningProgress.setStudent(student);
                itTestLearningProgress.setStatus(Status.STARTED);
                itTestLearningProgress.setCourse(itTestCourse);
                itTestLearningProgress.setLeftClassHour(1);
                itTestLearningProgress.setTotalClassHour(1);
                learingProgressRepository.create(itTestLearningProgress);

                // 新建试听课学习进度
                Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
                LearningProgress demoLearningProgress = new LearningProgress();
                demoLearningProgress.setStudent(student);
                demoLearningProgress.setStatus(Status.STARTED);
                demoLearningProgress.setCourse(demoCourse);
                demoLearningProgress.setLeftClassHour(1);
                demoLearningProgress.setTotalClassHour(1);
                learingProgressRepository.create(demoLearningProgress);

            } else if (isAgeOK.equals(AGE_TOO_OLD)) {
                response.setStatus(ServiceExceptionCode.STUDENT_TOO_OLD);
                return response;
            } else if (isAgeOK.equals(AGE_TOO_YOUNG)) {
                response.setStatus(ServiceExceptionCode.STUDENT_TOO_YOUNG);
                return response;
            }

            return response;

        } else {
            logger.error("marketActivity: parent exist");
            Student foundStudent = null;
            //如果用户已经注册，查看他是否已经参加当前活动。如果是，则提示已经参加。
            if (studentRepository.findByFamilyId(foundParent.getFamily().getId()).size() > 0) {
                foundStudent = studentRepository.findByFamilyId(foundParent.getFamily().getId()).get(0);
            }

            if (foundStudent != null) {
                if (foundStudent.getAttendedActivities() != null && foundStudent.getAttendedActivities().length() > 0) {
                    for (Iterator<MarketActivities> iterator = foundStudent.getAttendedActivitySet().iterator();
                         iterator.hasNext(); ) {
                        MarketActivities activity = (MarketActivities) iterator.next();
                        if (currentActivity.toString().equals(activity.toString())) {
//							throw new StudentAlreadyAttenedCurrentActivityServiceException("Student Already Attended This Activity");
                            response.setStatus(ServiceExceptionCode.STUDENT_ALREADY_ATTENDED_CURRENT_ACTIVITY);
                            return response;
                        }
                    }
                    foundStudent.addAttendedActivity(currentActivity);
                } else {
                    //如果没有，则让他参加。
                    foundStudent.addAttendedActivity(currentActivity);
                }

                studentRepository.update(foundStudent);
            }
        }
        return response;
    }

    public Parent doSignup(Signup signup) {
        Parent parent = signup.getParent();
        Student student = signup.getStudent();
        String url = signup.getUrl();
        if (StringUtils.isBlank(url)) {
            url = Configurations.BaseInfo.WWW_HOME;
            signup.setUrl(url);
        } else {
            url = UrlUtil.decodeURL(url);
            signup.setUrl(url);
        }

        Parent findParent = parentRepository.findByUsername(StringUtils.isBlank(parent.getUsername()) ? parent.getMobile() : parent.getUsername());
        if (findParent == null) {
            // 新建家庭
            Family family = new Family();
            familyRepository.create(family);

            return doCreateSignup(parent, student, family, url);
        } else {
            throw new UserAlreadyExistServiceException("Parent[username: {}] is already exist.", parent.getUsername());
        }
    }

    // 2015-04-23 -- 导入数据进入，作为注册学生

    /**
     * 数据, QueryParam("salername") String strSalerName
     *
     * @return
     */
    public Response doImportDataOperation1(Signup signup) {

        Parent parent = signup.getParent();
        Student student = signup.getStudent();

        // check source==
        //Find current activity.
        MarketActivities currentActivity = MarketActivities.unknown;
        for (MarketActivities activity : MarketActivities.values()) {
            //Check the activity(from web) is valid.
            if (activity.toString().equals(student.getAttendedActivities())) {
                currentActivity = activity;
            }
        }

        //Find current register source.
        Source registerSource = Source.OTHERS;
        for (Source source : Source.values()) {
            //Check the activity(from web) is valid.
            if (source.equals(student.getSource())) {
                registerSource = source;
                break;
            }
        }

        student.setSource(registerSource);
        if (student.getAttendedActivities().equalsIgnoreCase("")) {
            student.addAttendedActivity(currentActivity);
        }

        signup.setParent(parent);
        signup.setStudent(student);

        // 使用市场活动的signup（包括了source）
        //Response resp = this.doSignup4MarketActivity(signup);//返回的reponse没有用？？？？

        this.doSignup4MarketActivity(signup);
        //
        return new Response(HttpStatus.OK.value());
    }

    private String isAgeQualified(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            return AGE_TOO_YOUNG;
        }

        int yearNow = cal.get(Calendar.YEAR);
        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);

        int age = yearNow - yearBirth;
        if (age >= 5 && age <= 12) {
            return AGE_OK;
        } else if (age < 5) {
            return AGE_TOO_YOUNG;
        } else if (age > 12) {
            return AGE_TOO_OLD;
//			Response.status(ServiceExceptionCode.STUDENT_TOO_OLD).type(MediaType.TEXT_PLAIN).build();
        }
        return null;
    }

    public Parent doBindParentOpenId(Binding binding) {
        String openId = binding.getWechatOpenId();
        String username = binding.getUsername();
        String password = binding.getPassword();

        Parent parent = parentRepository.findByUsernameAndPassword(username, password);
        if (parent == null) {
            if (parentRepository.findByUsername(username) == null) {
                logger.error("Username not exsit!");
                throw new UserNotExistServiceException("Username not exsit!");
            } else {
                logger.error("Username or password is incorrect!");
                throw new AuthServiceException("Username or password is incorrect!");
            }
        }

        parent.setLastLoginDateTime(new Date());
        if (TextUtils.isEmpty(parent.getToken())) {
            parent.setToken(TokenGenerator.generate());
        }
        parentRepository.update(parent);

        if (parent.getWechatOpenId() == null) {
            parent.setWechatOpenId(openId);
            parentRepository.update(parent);
        } else {
            throw new IncorrectOpenIdServiceException("Open id already exists.");
        }

        logger.info("Binding successfully with openId = " + openId);

        return parent;
    }

    public Parent doUnbindParentOpenIdByUsername(String username) {

        Parent parent = parentRepository.findByUsername(username);
        if (parent == null) {
            logger.error("Username is incorrect!");
            throw new AuthServiceException("Username is incorrect!");
        }

        if (parent.getWechatOpenId() != null) {
            parent.setWechatOpenId(null);
            parentRepository.update(parent);
        }

        logger.info("unBinding successfully with username = " + username);

        return parent;
    }

    public Parent doSignUpAndBinding(SignupAndBinding signupAndBinding) {
        Parent parent = signupAndBinding.getParent();
        Student student = signupAndBinding.getStudent();
        String openid = signupAndBinding.getWechatOpenId();

        Parent parentDB = parentRepository.findByWechatOpenId(openid);
        if (parentDB != null) {
            logger.error("Openid used");
            throw new OpenIdAlreadyUsedServiceException("本微信账号已经注册。");
        }

        parentDB = parentRepository.findByMobile(parent.getMobile());
        if (parentDB != null) {
            logger.error("Mobile used");
            throw new ParentMobileUsedServiceException("本手机号码已经注册。");
        }

        Signup signup = new Signup();

        parent.setWechatOpenId(openid);
        signup.setParent(parent);
        signup.setStudent(student);
        signup.setUrl("http://parent.vipkid.com.cn/login");
        return doSignup(signup);
    }

//	@POST
//	@Path("/resetPassword")
//	public Response resetPassword(Resetter resetter) {
//		Random ran = new Random((new Date()).getTime());
//		String newPassword = Integer.toString(ran.nextInt(899999) + 100000);
//
//		Parent parentFromResetter = resetter.getParent();
//		String mobile = resetter.getMobile();
//
//		Parent parent = parentAccessor.findByUsername(parentFromResetter.getUsername());
//		//send the new password to (if parent, mobile; if teacher, email)
//		final String newPasswordFinal = newPassword;
//		final String mobileFinal = mobile;
//
//		if(parent != null) {
//			SMS.sendNewPasswordToParentSMS(mobileFinal, newPasswordFinal);
//		}
//
//		logger.info("New password message sending request shot out: " + newPasswordFinal);
//
//		return Response.ok().build();
//	}

    public Parent findByOpenId(String openId) {
        logger.info("find parent with open id");
        Parent parent = parentRepository.findByWechatOpenId(openId);

        if (parent != null) {
            parent.setLastLoginDateTime(new Date());
            if (TextUtils.isEmpty(parent.getToken())) {
                parent.setToken(TokenGenerator.generate());
            }
            parentRepository.update(parent);
        }
        return parent;
    }

//	@GET
//	@Path("/setUrlParamForInvitationPage")
//	public Parent setUrlParamForInvitationPage(@QueryParam("parentId") long parentId) {
//		logger.info("setUrlParamForInvitationPage");
//		Parent parent = parentAccessor.find(parentId);
//
//		if (parent.getInvitationId() == null) {
//			parent.setInvitationId(PasswordEncryptor.encryptWithBase64(parent.getId() + parent.getOpenId()));
//			parentAccessor.update(parent);
//		}
//
//		return parent;
//	}

    public Parent findRegisteredByMobile(String mobile) {
        logger.info("find parent with mobile");
        return parentRepository.findRegisteredByMobile(mobile);
    }

    public Response logout(long id) {
        Parent parent = parentRepository.find(id);
        if (parent != null) {
            parent.setToken(null);
            parent.setLastLoginDateTime(new Date());
            parentRepository.update(parent);
        }
        return new Response(HttpStatus.OK.value());
    }

    public Parent findByUsername(String username) {
        logger.info("find parents by username = {}", username);
        return parentRepository.findByUsername(username);
    }

    public Parent changePassword(Parent parent) {
        if (parent.getVerifyCode() != null) {
            parent.setPassword(PasswordEncryptor.encrypt(parent.getPassword()));
            parentRepository.update(parent);
            return parent;
        } else {
            throw new NoVerifyCodeServiceException("we need verify code to change password");
        }
    }

    public MarketingActivity findByChannel(String channel) {
        logger.info("findByChannel channel: {}", channel);
        return marketingActivityRepository.findByChannel(channel);
    }


    public Response doRegistByMarketingActiviy(Signup signup) {
        Long channelId = signup.getStudent().getChannel().getId();
        String sourceOld = signup.getStudent().getChannel().getSourceOld();
        if (null == channelId || channelId == 0) {
            Channel channel = channelService.findByOldSource(sourceOld);
            if (null == channel) {
                channel = channelService.getDefaultChannel();
            }
            signup.getStudent().setChannel(channel);
            signup.getParent().setChannel_id(channel.getId());
        }

        Response response = new Response(HttpStatus.OK.value());
        Parent parent = signup.getParent();
        Student student = signup.getStudent();
        String inventCode = signup.getInventionCode();
        boolean hasInventionCode = false;

        Parent foundParent = parentRepository.findByUsername(parent.getUsername());
        //此活动只针对新注册用户，老用户不让参加
        if (foundParent == null) {
            //校验邀请码
            if (inventCode != null && !"".equals(inventCode)) {
                InventionCode code = inventionCodeRepository.findByCode(inventCode);
                if (code != null) {
                    if (code.isHasUsed() == true) {
                        response.setStatus(ServiceExceptionCode.INVENTION_CODE_ALREADY_USED);
                        return response;
                    } else {
                        //设置邀请码已使用
                        code.setHasUsed(true);
                        inventionCodeRepository.update(code);
                        hasInventionCode = true;
                    }
                } else {
                    response.setStatus(ServiceExceptionCode.INVENTION_CODE_ERROR);
                    return response;
                }
            }  //----校验完成

            return createFamilyByMarketingActivity(parent, student, hasInventionCode);
        } else {
            response.setStatus(ServiceExceptionCode.ACTIVITY_NOT_FOR_OLD_STUDENT);
            return response;
        }
    }

    private Response createFamilyByMarketingActivity(Parent parent, Student student, boolean hasInventionCode) {
        //判断人数是否已报满
        MarketingActivity activity = student.getMarketingActivity();
        if (activity.isHasLimited() == true) {
            long activityId = activity.getId();
            List<Student> students2 = studentRepository.findByMarketingActivityId(activityId);
            if (students2.size() >= student.getMarketingActivity().getLimitedNumber()) {
                return new Response(ServiceExceptionCode.NO_QUOTA);
            }
        }

        Family family = new Family();
        familyRepository.create(family);

        // 新建家长
        String parentPassword = parent.getPassword();
        parent.setPassword(PasswordEncryptor.encrypt(parentPassword));
        parent.setUsername(parent.getMobile());
        parent.setMobile(parent.getUsername());
        parent.setFamily(family);
        parentRepository.create(parent);
        List<Parent> parents = new ArrayList<Parent>();
        parents.add(parent);
        family.setParents(parents);
        familyRepository.update(family);

        // 新建学生
//        String studentPassword = PasswordGenerator.generate();
//        student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
//        student.setPassword(studentPassword);
//        student.setWelcome(true);
//        student.setFamily(family);
//        student.setCreater(parent);
//        student.setLifeCycle(LifeCycle.SIGNUP);
//        student.setAvatar("boy_3");
//        studentRepository.create(student);
//        List<Student> students = new ArrayList<Student>();
//        students.add(student);
//        family.setStudents(students);
        doSignupStudentWithoutSMS(parent, student, family);



        String isAgeOK = isAgeQualified(student.getBirthday());
        if (isAgeOK.equals(AGE_OK)) {
            String channel = activity.getChannel().getSourceName();
            long channelId = activity.getChannel().getId();
            if (channel != null) {
                channel = channel.trim();
            }
            logger.error("Registered from the activit.channel={}", channel);
            if (channel != null && channel.equals("free_gongkaike_huiben_ditui")) {
                logger.error("Send email for free_gongkaike_huiben_ditui for parent={}, student={}", parent.getMobile(), student.getEnglishName());
                SMS.sendFreeGongKaiKeHuiBenDiTuiSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());
            } else if (channel != null && (channel.equals("free_gongkaike_huiben_duanxin") || channel.equals("free_gongkaike_huiben_duanxin2") || channel.equals("free_gongkaike_huiben_duanxin1") || channel.equals("bd_online_duanxin"))) {
                logger.error("Send email for free_gongkaike_huiben_duanxin for parent={}, student={}", parent.getMobile(), student.getEnglishName());
                SMS.sendFreeGongKaiKeHuiBenDiTuiSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());
            } else if(channelId == 228){
            	logger.error("Send email for 趣配音大赛专题页 for parent={}, student={}", parent.getMobile(), student.getEnglishName());
            	SMS.sendXPYFreeSMS(parent.getMobile());
            }else {
                SMS.sendNewParentSignupSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());
            }
        } else if (isAgeOK.equals(AGE_TOO_OLD)) {
            if (hasInventionCode == true) {
                return new Response(ServiceExceptionCode.USE_INVENTION_CODE_AGE_NOT_SUITE);
            } else {
                return new Response(ServiceExceptionCode.STUDENT_TOO_OLD);
            }
        } else if (isAgeOK.equals(AGE_TOO_YOUNG)) {
            if (hasInventionCode == true) {
                return new Response(ServiceExceptionCode.USE_INVENTION_CODE_AGE_NOT_SUITE);
            } else {
                return new Response(ServiceExceptionCode.STUDENT_TOO_YOUNG);
            }
        }
        return new Response(HttpStatus.OK.value());
    }

    private Parent doCreateSignup(Parent parent, Family family) {
        // 新建家长
        String parentPassword = parent.getPassword();
        parent.setPassword(PasswordEncryptor.encrypt(parentPassword));
        parent.setUsername(parent.getMobile());
        parent.setMobile(parent.getUsername());
        parent.setFamily(family);
        if (TextUtils.isEmpty(parent.getToken())) {
            parent.setToken(TokenGenerator.generate());
        }
        parentRepository.create(parent);
        List<Parent> parents = new ArrayList<Parent>();
        parents.add(parent);
        family.setParents(parents);

//		// 发送新家长注册短信
//		SMS.sendNewParentSignupSMS(parent.getMobile(),student.getEnglishName(),student.getUsername(),student.getPassword());

        // 发送新家长注册统计邮件
        EMail.sendNewParentSignupEmail(parent.getMobile());

        return parent;
    }

    public Parent doSignupExceptStudent(Signup signup) {
        Parent parent = signup.getParent();
        parent.setVerifyCode(signup.getInventionCode());

        Parent findParent = parentRepository.findByUsername(parent.getUsername());
        if (findParent == null) {
            // 新建家庭
            Family family = new Family();
            familyRepository.create(family);

            return doCreateSignup(parent, family);
        } else {
            throw new UserAlreadyExistServiceException("Parent[username: {}] is already exist.", parent.getUsername());
        }
    }

    public Parent AddFirstChildForParent(Signup signup) {
        Parent parent = signup.getParent();
        Student student = signup.getStudent();
        Family family = parent.getFamily();

        return doSignupStudent(parent, student, family);

    }

    private Parent doSignupStudent(Parent parent, Student student, Family family) {
        MarketActivities currentActivity = null;
        if (student.getAttendedActivities() != null) {
            //Find current activity.
            for (MarketActivities activity : MarketActivities.values()) {
                //Check the activity(from web) is valid.
                if (student.getAttendedActivities().indexOf(activity.toString()) > 0) {
                    currentActivity = activity;
                }
            }
        }

        Source registerSource = null;
        if (student.getSource() != null) {
            //Find current register source.
            for (Source source : Source.values()) {
                //Check the activity(from web) is valid.
                if (source.equals(student.getSource())) {
                    registerSource = source;
                    break;
                }
            }
        }

        // 新建学生
        String studentPassword = PasswordGenerator.generate();
        student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
        student.setPassword(studentPassword);
//		student.setSource(Source.WEBSITE);
        student.setWelcome(true);
        student.setFamily(family);
        student.setCreater(parent);
        student.setLifeCycle(LifeCycle.SIGNUP);
        if (student.getGender().equals(Gender.FEMALE)) {
            student.setAvatar("girl_1");
        } else {
            student.setAvatar("boy_3");
        }
        if (registerSource != null) {
            student.setSource(registerSource);
        } else {
            student.setSource(Source.WEBSITE);
        }
        if (currentActivity != null) {
            student.addAttendedActivity(currentActivity);
        }

        studentRepository.create(student);
		studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);
        List<Student> students = new ArrayList<Student>();
        students.add(student);
        family.setStudents(students);

        //					// 更新家庭
        //					family.setCreater(parent);
        //					familyAccessor.update(family);

        // 新建ITTest课学习进度
        Course itTestCourse = courseRepository.findByCourseType(Type.IT_TEST);
        LearningProgress itTestLearningProgress = new LearningProgress();
        itTestLearningProgress.setStudent(student);
        itTestLearningProgress.setStatus(Status.STARTED);
        itTestLearningProgress.setCourse(itTestCourse);
        itTestLearningProgress.setLeftClassHour(1);
        itTestLearningProgress.setTotalClassHour(1);
        learingProgressRepository.create(itTestLearningProgress);

        // 新建试听课学习进度
        Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
        LearningProgress demoLearningProgress = new LearningProgress();
        demoLearningProgress.setStudent(student);
        demoLearningProgress.setStatus(Status.STARTED);
        demoLearningProgress.setCourse(demoCourse);
        demoLearningProgress.setLeftClassHour(1);
        demoLearningProgress.setTotalClassHour(1);
        learingProgressRepository.create(demoLearningProgress);

        //给学生一个默认的飞机
        AirCraft airCraft = new AirCraft();
        airCraft.setStudent(student);
        airCraft.setSequence(1);
        aircraftRepository.create(airCraft);

        AirCraftTheme airCraftTheme = new AirCraftTheme();
        airCraftTheme.setAirCraft(airCraft);
        airCraftTheme.setCurrent(true);
        airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
        airCraftTheme.setLevel(1);
        airCraftTheme.setName("阿波罗号");
        airCraftTheme.setPrice(0);
        airCraftTheme.setUrl("ac1_1");
        aircraftThemeRepository.create(airCraftTheme);

        //给学生一个默认的宠物
        Pet pet = new Pet();
        pet.setStudent(student);
        pet.setName(student.getEnglishName() + "'s Spirit");
        pet.setSequence(10);
        pet.setPrice(0);
        pet.setUrl("pet10");
        pet.setCurrent(true);
        pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
        petRepository.create(pet);

        //发送新家长注册短信
        SMS.sendNewParentSignupSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());

//		//发送新家长注册统计邮件
//		EMail.sendNewParentSignupEmail(parent.getMobile());

        return parent;
    }

    public Parent AddOtherChildForParent(Parent parent, Student student) {
        Family family = parent.getFamily();
        return addNewStudent(parent, student, family);

    }

    private Parent addNewStudent(Parent parent, Student student, Family family) {
        MarketActivities currentActivity = null;
        if (student.getAttendedActivities() != null) {
            //Find current activity.
            for (MarketActivities activity : MarketActivities.values()) {
                //Check the activity(from web) is valid.
                if (student.getAttendedActivities().indexOf(activity.toString()) > 0) {
                    currentActivity = activity;
                }
            }
        }

        Source registerSource = null;
        if (student.getSource() != null) {
            //Find current register source.
            for (Source source : Source.values()) {
                //Check the activity(from web) is valid.
                if (source.equals(student.getSource())) {
                    registerSource = source;
                    break;
                }
            }
        }

        // 更新学生
        String studentPassword = PasswordGenerator.generate();
        student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
        student.setPassword(studentPassword);
        student.setWelcome(true);
        student.setFamily(family);
        student.setCreater(parent);
        student.setLifeCycle(LifeCycle.SIGNUP);

        if (registerSource != null) {
            student.setSource(registerSource);
        } else {
            student.setSource(Source.WEBSITE);
        }
        if (currentActivity != null) {
            student.addAttendedActivity(currentActivity);
        }

        studentRepository.create(student);
		studentLifeCycleLogService.doChangeLifeCycle(student, null, Student.LifeCycle.SIGNUP);

        List<Student> students = family.getStudents();
        students.add(student);
        family.setStudents(students);

        // 新建ITTest课学习进度
        Course itTestCourse = courseRepository.findByCourseType(Type.IT_TEST);
        LearningProgress itTestLearningProgress = new LearningProgress();
        itTestLearningProgress.setStudent(student);
        itTestLearningProgress.setStatus(Status.STARTED);
        itTestLearningProgress.setCourse(itTestCourse);
        itTestLearningProgress.setLeftClassHour(1);
        itTestLearningProgress.setTotalClassHour(1);
        learingProgressRepository.create(itTestLearningProgress);

        // 新建试听课学习进度
        Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
        LearningProgress demoLearningProgress = new LearningProgress();
        demoLearningProgress.setStudent(student);
        demoLearningProgress.setStatus(Status.STARTED);
        demoLearningProgress.setCourse(demoCourse);
        demoLearningProgress.setLeftClassHour(1);
        demoLearningProgress.setTotalClassHour(1);
        learingProgressRepository.create(demoLearningProgress);

        //给学生一个默认的飞机
        AirCraft airCraft = new AirCraft();
        airCraft.setStudent(student);
        airCraft.setSequence(1);
        aircraftRepository.create(airCraft);

        AirCraftTheme airCraftTheme = new AirCraftTheme();
        airCraftTheme.setAirCraft(airCraft);
        airCraftTheme.setCurrent(true);
        airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
        airCraftTheme.setLevel(1);
        airCraftTheme.setName("阿波罗号");
        airCraftTheme.setPrice(0);
        airCraftTheme.setUrl("ac1_1");
        aircraftThemeRepository.create(airCraftTheme);

        //给学生一个默认的宠物
        Pet pet = new Pet();
        pet.setStudent(student);
        pet.setName(student.getEnglishName() + "'s Spirit");
        pet.setSequence(10);
        pet.setPrice(0);
        pet.setUrl("pet10");
        pet.setCurrent(true);
        pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
        petRepository.create(pet);

        //发送新家长注册短信
        SMS.sendNewParentSignupSMS(parent.getMobile(), student.getEnglishName(), student.getUsername(), student.getPassword());

//		//发送新家长注册统计邮件
//		EMail.sendNewParentSignupEmail(parent.getMobile());

        return parent;
    }

    public MarketingActivity find(Long id) {
        return marketingActivityRepository.find(id);
    }

    /**
     * @return MarketingActivity
     * @throws
     * @Title: findBySourceId
     * @Description: sourceId 是 channel 表中 source_old
     * @author zhangfeipeng
     */
    public MarketingActivity findBySourceId(String sourceId) {
        Channel channel = channelService.findByOldSource(sourceId);
        long id = 0;
        if (null != channel) {
            id = channel.getId();
        }
        return marketingActivityRepository.findByChannelId(id);
    }
    
	private Parent doSignupStudentWithoutSMS(Parent parent, Student student, Family family) {
		MarketActivities currentActivity = null;
		if(student.getAttendedActivities() != null){
			//Find current activity.
			for(MarketActivities activity : MarketActivities.values()){
				//Check the activity(from web) is valid.
				if(student.getAttendedActivities().indexOf( activity.toString())  > 0){
					currentActivity = activity;
				}
			}
		}
		
		Source registerSource = null;
		if(student.getSource() != null) {
			//Find current register source.
			for(Source source : Source.values()){
				//Check the activity(from web) is valid.
				if(source.equals(student.getSource())){
					registerSource = source;
					break;
				}
			}
		}		

		// 新建学生
		String studentPassword = PasswordGenerator.generate();
        student.setUsername(String.format("%08d", Long.parseLong(studentRepository.findMaxStudentNumber()) + 1));
		student.setPassword(studentPassword);
//		student.setSource(Source.WEBSITE);
		student.setWelcome(true);
		student.setFamily(family);
		student.setCreater(parent);
		student.setLifeCycle(LifeCycle.SIGNUP);
		if(student.getGender() != null && student.getGender().equals(Gender.FEMALE)){
			student.setAvatar("girl_1");
		}else{
			student.setAvatar("boy_3");
		}
		if(registerSource != null) {
			student.setSource(registerSource);	
		} else {
			student.setSource(Source.WEBSITE);
		}
		if(currentActivity != null) {
			student.addAttendedActivity(currentActivity);
		}
		
		student = studentRepository.create(student);
		List<Student> students = new ArrayList<Student>();
		students.add(student);
		family.setStudents(students);

		//					// 更新家庭
		//					family.setCreater(parent);
		//					familyAccessor.update(family);

		// 新建ITTest课学习进度
		Course itTestCourse = courseRepository.findByCourseType(Type.IT_TEST);
		LearningProgress itTestLearningProgress = new LearningProgress();
		itTestLearningProgress.setStudent(student);
		itTestLearningProgress.setStatus(Status.STARTED);
		itTestLearningProgress.setCourse(itTestCourse);
		itTestLearningProgress.setLeftClassHour(1);
		itTestLearningProgress.setTotalClassHour(1);
		learingProgressRepository.create(itTestLearningProgress);

		// 新建试听课学习进度
		Course demoCourse = courseRepository.findByCourseType(Type.TRIAL);
		LearningProgress demoLearningProgress = new LearningProgress();
		demoLearningProgress.setStudent(student);
		demoLearningProgress.setStatus(Status.STARTED);
		demoLearningProgress.setCourse(demoCourse);
		demoLearningProgress.setLeftClassHour(1);
		demoLearningProgress.setTotalClassHour(1);
		learingProgressRepository.create(demoLearningProgress);

		//给学生一个默认的飞机
		AirCraft airCraft = new AirCraft();
		airCraft.setStudent(student);
		airCraft.setSequence(1);
		aircraftRepository.create(airCraft);

		AirCraftTheme airCraftTheme = new AirCraftTheme();
		airCraftTheme.setAirCraft(airCraft);
		airCraftTheme.setCurrent(true);
		airCraftTheme.setIntroduction("宇宙中最流行的飞船，Miya家有几百艘，到处送人");
		airCraftTheme.setLevel(1);
		airCraftTheme.setName("阿波罗号");
		airCraftTheme.setPrice(0);
		airCraftTheme.setUrl("ac1_1");
		aircraftThemeRepository.create(airCraftTheme);

		//给学生一个默认的宠物
		Pet pet = new Pet();
		pet.setStudent(student);
		pet.setName(student.getEnglishName() + "'s Spirit");
		pet.setSequence(10);
		pet.setPrice(0);
		pet.setUrl("pet10");
		pet.setCurrent(true);
		pet.setIntroduction("长腿精灵，在其他星系很难买到合适的裤子");
		petRepository.create(pet);			

		return parent;	
	}

}
