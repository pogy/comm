package com.vipkid.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vipkid.model.Activity;
import com.vipkid.model.Course;
import com.vipkid.model.LearningCycle;
import com.vipkid.model.Lesson;
import com.vipkid.model.Level;
import com.vipkid.model.PPT;
import com.vipkid.model.Resource;
import com.vipkid.model.Unit;
import com.vipkid.repository.ActivityRepository;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.LearningCycleRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.PPTRepository;
import com.vipkid.repository.ResourceRepository;
import com.vipkid.repository.SlideRepository;
import com.vipkid.repository.UnitRepository;
import com.vipkid.util.Configurations;

@Service
public class ImportCourseService {
	@javax.annotation.Resource
	private CourseRepository courseRepository;
	
	@javax.annotation.Resource
	private UnitRepository unitRepository;
	
	@javax.annotation.Resource
	private LearningCycleRepository learningCycleRepository;
	
	@javax.annotation.Resource
	private LessonRepository lessonRepository;
	
	@javax.annotation.Resource
	private ActivityRepository activityRepository;
	
	@javax.annotation.Resource
	private ResourceRepository resourceRepository;
	
	@javax.annotation.Resource
	private SlideRepository slideRepository;
	
	@javax.annotation.Resource
	private PPTRepository pptRepository;
	
	@SuppressWarnings("unchecked")
	public void readCourse(MultipartFile file) throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		try{
			Document document = (Document)saxBuilder.build(file.getInputStream());
            Element rootElement = document.getRootElement();
            Namespace namespace = rootElement.getNamespace();
            
                        
            Element worksheetElement = rootElement.getChild("Worksheet",namespace); 
            Namespace worksheetNamespace = worksheetElement.getNamespace();
                        
            Element tableElement = worksheetElement.getChild("Table", worksheetNamespace); 
            Namespace tableNamespace = tableElement.getNamespace();
	
            List<Element> rowElements = tableElement.getChildren("Row", tableNamespace);           
            
            Course course = null;
            Unit unit = null;
            LearningCycle learningCycle = null;
            Lesson lesson = null; 
            
            for(int i = 1; i < rowElements.size(); i++) { // 从第二行开始
            	Element rowElement = rowElements.get(i);
            	Namespace rowNamespace = rowElement.getNamespace();
            	List<Element> cellElements = rowElement.getChildren("Cell", rowNamespace);
            	
            	/*
            	 * 如果没有数据了就跳出
            	 * 有时excel转xml会出现一行冗余空数据，比如 <Cell ss:MergeDown="11" ss:StyleID="s216"/>
            	 * 为避免出现上述情况，所以进行两次判断，如果连续两行Cell都没有Data，则表示没有数据了，这时候跳出
            	 */
                Element firstCellElement = cellElements.get(0);
                Namespace firstCellElementNamespace = firstCellElement.getNamespace();
                Element firstDataElement = firstCellElement.getChild("Data", firstCellElementNamespace);
                
                Element secondCellElement = cellElements.get(1);
                Namespace secondCellElementNamespace = secondCellElement.getNamespace();
                Element secondDataElement = secondCellElement.getChild("Data", secondCellElementNamespace);
                
                if(firstDataElement == null && secondDataElement == null) {
                	break;
                }
                
                int count = 0;
                boolean updateCourse = false;
                boolean updateUnit = false;
                boolean updateLearningCycle = false;
                for(int j = cellElements.size() - 1; j >= 0; j--){
                	count = count + 1;
                	Element cellElement = cellElements.get(j);
                	Namespace cellNamespace = cellElement.getNamespace();
                	Element dataElement = cellElement.getChild("Data",cellNamespace); 
                	if(dataElement == null) {
                		continue;
                	}
                	
                    String stringContent = dataElement.getValue(); 
                    System.out.println(stringContent + " " + "count:" + count + " " + "row:" + " " + i );
                	
                    stringContent = setEnter(stringContent);
//                    stringContent = setColor(stringContent, dataElement);
                    
                    switch (count) {
	                    case 1:
	                		lesson = new Lesson();
	                		lesson.setSequence(Integer.parseInt(stringContent));
	                		break;  
	                    case 2:
	                		lesson.setSerialNumber(stringContent);
	                        break;                  	                 	
                    	case 3:
                    		lesson.setReviewTarget(stringContent);
                    		break;
                    	case 4:
                    		lesson.setMathTarget(stringContent);                		
                    		break;
                    	case 5:
                    		lesson.setLssTarget(stringContent);
                    		break;
                    	case 6:
                    		lesson.setSentencePatterns(stringContent);
                    		break;
                    	case 7:
                    		lesson.setVocabularies(stringContent);
                    		break;
                    	case 8:
                    		lesson.setGoal(stringContent);
                    		break;
                    	case 9:
                    		lesson.setObjective(stringContent); 
                    		break;
                    	case 10:
                    		lesson.setDomain(stringContent);
                    		break;
                    	case 11:
                    		lesson.setTopic(stringContent);
                    		break;
                    	case 12:
                    		lesson.setNumber(stringContent);
                    		lesson.setName(lesson.getNumber() + ' ' + lesson.getTopic());
                    		break;
                    	case 13:
                    		updateLearningCycle = true;
                    		learningCycle = new LearningCycle();
                    		learningCycle.setSequence(Integer.parseInt(stringContent));                   		                   		
                    		break;
                    	case 14:
                    		learningCycle.setSerialNumber(stringContent); 
                    		break;
                    	case 15:
                    		learningCycle.setReviewTarget(stringContent);
                    		break;
                    	case 16:
                    		learningCycle.setCcssMath(stringContent);
                    		break;
                    	case 17:
                    		learningCycle.setMathTopic(stringContent);
                    		break;
                    	case 18:
                    		learningCycle.setCcssLanguageArt(stringContent);
                    		break;
                    	case 19:
                    		learningCycle.setSentencePatterns(stringContent);
                    		break;
                    	case 20:
                    		learningCycle.setGrammar(stringContent);
                    		break;
                    	case 21:
                    		learningCycle.setVocabularies(stringContent);
                    		break;
                    	case 22:
                    		learningCycle.setHighFrenquncyWords(stringContent);
                    		break;
                    	case 23:
                    		learningCycle.setLetters(stringContent);
                    		break;
                    	case 24:
                    		learningCycle.setPhonemicAwareness(stringContent);
                    		break;
                    	case 25:
                    		learningCycle.setObjective(stringContent);
                    		break;
                    	case 26:
                    		learningCycle.setTopic(stringContent);
                    		break;
                    	case 27:
                    		learningCycle.setNumber(stringContent);
                    		learningCycle.setName(learningCycle.getNumber() + ' ' + learningCycle.getTopic());
                    		break;      		                   	
                    	case 28:
                    		updateUnit = true;
                    		unit = new Unit();
                    		unit.setSequence(Integer.parseInt(stringContent));
                    		break;
                    	case 29:
                    		unit.setSerialNumber(stringContent);
                    		break;
                    	case 30:
                    		if(stringContent.equals("L0")) {
                    			unit.setLevel(Level.LEVEL_0);
                    		}else if(stringContent.equals("L1")) {
                    			unit.setLevel(Level.LEVEL_1);
                    		}else if(stringContent.equals("L2")) {
                    			unit.setLevel(Level.LEVEL_2);
                    		}else if(stringContent.equals("L3")) {
                    			unit.setLevel(Level.LEVEL_3);
                    		}else if(stringContent.equals("L4")) {
                    			unit.setLevel(Level.LEVEL_4);
                    		}else if(stringContent.equals("L5")) {
                    			unit.setLevel(Level.LEVEL_5);
                    		}else if(stringContent.equals("L6")) {
                    			unit.setLevel(Level.LEVEL_6);
                    		}
                    		break;
                    	case 31:
                    		unit.setNameInLevel(stringContent);
                    	case 32:
                    		unit.setCcssMath(stringContent);
                    		break;
                    	case 33:
                    		unit.setMathTopic(stringContent);
                    		break;
                    	case 34:
                    		unit.setCcssLanguageArt(stringContent);
                    		break;
                    	case 35:
                    		unit.setSentencePatterns(stringContent);
                    		break;
                    	case 36:
                    		unit.setGrammar(stringContent);
                    		break;
                    	case 37:
                    		unit.setVocabularies(stringContent);
                    		break;
                    	case 38:
                    		unit.setHighFrenquncyWords(stringContent);
                    		break;
                    	case 39:
                    		unit.setLetters(stringContent);
                    		break;
                    	case 40:
                    		unit.setPhonemicAwareness(stringContent);
                    		break;
                    	case 41:
                    		unit.setObjective(stringContent);
                    		break;
                    	case 42:
                    		unit.setTopic(stringContent);
                    		break;
                    	case 43:
                    		unit.setDomain(stringContent);
                    		break;
                    	case 44:
//                    		unit.setName(stringContent);
                    		break;
                    	case 45:
                    		unit.setNumber(stringContent);
                    		unit.setName(unit.getNumber() + ' ' + unit.getTopic());
                    		break;
                    	case 46:
                    		unit.setEntryPointCriteria(stringContent);                		
                    		break;
                    	case 47:
                    		updateCourse = true;
                    		course = new Course();
                    		if(!stringContent.isEmpty()){
                    			Unit findEntryUnit = unitRepository.findBySerialNumber(stringContent);
                    			if(findEntryUnit != null){
                    				course.setEntryUnit(findEntryUnit);
                    			}                			
                    		}
                    		break;
                    	case 48:
                    		if(stringContent.equals("yes")){
                    			course.setFree(true);
                    		}else {
                    			course.setFree(false);
                    		}                  		
                    		break;
                    	case 49:
                    		if(stringContent.equals("yes")){
                    			course.setNeedBackupTeacher(true);
                    		}else {
                    			course.setNeedBackupTeacher(false);
                    		}                   		
                    		break;
                    	case 50:
                    		if(stringContent.equals("yes")){
                    			course.setSequential(true);
                    		}else {
                    			course.setSequential(false);
                    		}
                    		break;
                    	case 51:
                    		course.setType(Course.Type.valueOf(stringContent));
//                    		if(stringContent.equals("NORMAL")){
//                    			course.setType(Course.Type.NORMAL);
//                    		}else if(stringContent.equals("DEMO")) {
//                    			course.setType(Course.Type.DEMO);
//                    		}else if(stringContent.equals("IT_TEST")) {
//                    			course.setType(Course.Type.IT_TEST);
//                    		}else if(stringContent.equals("GUIDE")) {
//                    			course.setType(Course.Type.GUIDE);
//                    		}else if(stringContent.equals("MAJOR")) {
//                    			course.setType(Course.Type.MAJOR);
//                    		}else if(stringContent.equals("TRIAL")) {
//                    			course.setType(Course.Type.TRIAL);
//                    		}
                    		break;
                    	case 52:                    		
                    		if(stringContent.equals("ONE_ON_ONE")){
                    			course.setMode(Course.Mode.ONE_ON_ONE);
                    		}else{
                    			course.setMode(Course.Mode.ONE_TO_MANY);
                    		}
                    		break;
                    	case 53:
                    		course.setDescription(stringContent);
                    		break;
                    	case 54:
                    		course.setShowName(stringContent);
                    		break;
                    	case 55:
                    		course.setName(stringContent);
                    		break;
                    	case 56:
                    		course.setSerialNumber(stringContent);                   		                   		                   		
                    		break;
                    }                                       	
                }
                
                if(updateCourse) {
                	// 如果走到至少46列，说明有了新的course, 则进行course的新建或更新
                	Course findCourse = courseRepository.findBySerialNumber(course.getSerialNumber());
                    
                    if(findCourse == null) {
                    	course.setChildType("unit");
                    	course = courseRepository.create(course);
                    }else {
                    	findCourse.setEntryUnit(course.getEntryUnit());
                    	findCourse.setFree(course.isFree());
                    	findCourse.setNeedBackupTeacher(course.isNeedBackupTeacher());
                    	findCourse.setSequential(course.isSequential());
                    	findCourse.setType(course.getType());
                    	findCourse.setMode(course.getMode());
                    	findCourse.setDescription(course.getDescription());
                    	findCourse.setName(course.getName());
                    	course = courseRepository.update(findCourse);
                    }
                }
                
                if(updateUnit) {
                	// 如果走到至少28列，说明有了新的unit, 则进行unit的新建或更新     		
                    Unit findUnit = unitRepository.findBySerialNumber(unit.getSerialNumber());
                    if(findUnit == null) {
                    	unit.setCourse(course);
                    	unit.setUnitTestPath("");
                    	unit = unitRepository.create(unit);
                    }else {
                    	findUnit.setSequence(unit.getSequence());
                    	findUnit.setCcssMath(unit.getCcssMath());
                    	findUnit.setMathTopic(unit.getMathTopic());
                    	findUnit.setCcssLanguageArt(unit.getCcssLanguageArt());
                    	findUnit.setSentencePatterns(unit.getSentencePatterns());
                    	findUnit.setGrammar(unit.getGrammar());
                    	findUnit.setVocabularies(unit.getVocabularies());
                    	findUnit.setHighFrenquncyWords(unit.getHighFrenquncyWords());
                    	findUnit.setLetters(unit.getLetters());
                    	findUnit.setPhonemicAwareness(unit.getPhonemicAwareness());
                    	findUnit.setObjective(unit.getObjective());
                    	findUnit.setLevel(unit.getLevel());
                    	findUnit.setTopic(unit.getTopic());
                    	findUnit.setDomain(unit.getDomain());
                    	findUnit.setName(unit.getName());
                    	findUnit.setNumber(unit.getNumber());
                    	findUnit.setEntryPointCriteria(unit.getEntryPointCriteria());
                    	unit = unitRepository.update(findUnit);
                    }
                }
                
                if(updateLearningCycle) {
                	// 如果走到至少13列，说明有了新的learning cycle, 则进行learning cycle的新建或更新
                	 LearningCycle findLearningCycle = learningCycleRepository.findBySerialNumber(learningCycle.getSerialNumber());
                     if(findLearningCycle == null) {
                    	learningCycle.setUnit(unit);
                     	learningCycle = learningCycleRepository.create(learningCycle);
                     }else {
                     	findLearningCycle.setSequence(learningCycle.getSequence());
                     	findLearningCycle.setReviewTarget(learningCycle.getReviewTarget());
                     	findLearningCycle.setCcssMath(learningCycle.getCcssMath());
                     	findLearningCycle.setMathTopic(learningCycle.getMathTopic());
                     	findLearningCycle.setCcssLanguageArt(learningCycle.getCcssLanguageArt());
                     	findLearningCycle.setSentencePatterns(learningCycle.getSentencePatterns());
                     	findLearningCycle.setGrammar(learningCycle.getGrammar());
                     	findLearningCycle.setVocabularies(learningCycle.getVocabularies());
                     	findLearningCycle.setHighFrenquncyWords(learningCycle.getHighFrenquncyWords());
                     	findLearningCycle.setLetters(learningCycle.getLetters());
                     	findLearningCycle.setPhonemicAwareness(learningCycle.getPhonemicAwareness());
                     	findLearningCycle.setObjective(learningCycle.getObjective());
                     	findLearningCycle.setTopic(learningCycle.getTopic());
                     	findLearningCycle.setName(learningCycle.getName());
                     	findLearningCycle.setNumber(learningCycle.getNumber());
                     	learningCycle = learningCycleRepository.update(findLearningCycle);
                     }
                	
                }
                            
                Lesson findLesson = lessonRepository.findBySerialNumber(lesson.getSerialNumber());
                if(findLesson == null) {
                	lesson.setLearningCycle(learningCycle);
                	lessonRepository.create(lesson);
                	
                	Activity activity = new Activity();
                	activity.setLesson(lesson);
                	activity.setName("Teaching " + lesson.getName());
                	activityRepository.create(activity);
                	
                	List<Activity> activities = new ArrayList<Activity>();
                	activities.add(activity);
                	Resource resource = new Resource();
                	resource.setName(lesson.getSerialNumber() + " PPT");
                	resource.setActivities(activities);
                	resource.setType(Resource.Type.PPT);
                	resource.setUrl(Configurations.OSS.Template.PPT.replace(Configurations.OSS.Parameter.PPT, lesson.getSerialNumber()));
                	resourceRepository.create(resource);
                	
                	PPT ppt = new PPT();
                	ppt.setResource(resource);
                	pptRepository.create(ppt);
                }else {
                	findLesson.setSequence(lesson.getSequence());
                	findLesson.setReviewTarget(lesson.getReviewTarget());
                	findLesson.setMathTarget(lesson.getMathTarget());
                	findLesson.setLssTarget(lesson.getLssTarget());
                	findLesson.setSentencePatterns(lesson.getSentencePatterns());
                	findLesson.setVocabularies(lesson.getVocabularies());
                	findLesson.setGoal(lesson.getGoal());
                	findLesson.setObjective(lesson.getObjective());
                	findLesson.setDomain(lesson.getDomain());
                	findLesson.setName(lesson.getName());
                	findLesson.setTopic(lesson.getTopic());
                	findLesson.setNumber(lesson.getNumber());
                	findLesson.setLearningCycle(learningCycle);
                	lessonRepository.update(findLesson);
                }
            }
            
		}finally{
			
		}		
		
	}
	
	
	
	public String setEnter(String stringContent){
		String[] stringContents = stringContent.split("\r");
		if(stringContents.length > 1){
			String stringContentTemp = "";
			for(int i=0; i<stringContents.length; i++){
				stringContentTemp = stringContentTemp + stringContents[i];
				if(i<stringContents.length - 1){
					stringContentTemp = stringContentTemp + "<br/>";
				}
    		}
			stringContent = stringContentTemp;
		}else{
			stringContent = stringContents[0];
		} 
		return stringContent;
	}
	
	@SuppressWarnings("unchecked")
	public String setColor(String stringContent, Element dataElement){
		String stringContentTemp = "";
		String color = "";
		String colorContentFirst = "";
		String colorContentLast = "";
		Element fontElement = null;
        List<Element> fonts = null;
        
		Namespace ns =  Namespace.getNamespace("http://www.w3.org/TR/REC-html40");
     	fonts = dataElement.getChildren("Font", ns);   
		
     	if(fonts.size() > 1){
     		for(int k=0; k<fonts.size(); k++){
    			fontElement = fonts.get(k);
    			Namespace fontns =  fontElement.getNamespace();
    			Attribute colorElement = fontElement.getAttribute("Color", fontns);
    			if(colorElement != null){
    				color = colorElement.getValue();
    				colorContentFirst = "<span style = 'color: " + color + "'>";
    				colorContentLast = "</span>";
    			}                			
    			stringContentTemp = stringContentTemp + colorContentFirst + fontElement.getValue() + colorContentLast;
    			colorContentFirst = "";
    			colorContentLast = "";
    		}
     		stringContent = stringContentTemp;
     	}		
		return stringContent;
	}
}
