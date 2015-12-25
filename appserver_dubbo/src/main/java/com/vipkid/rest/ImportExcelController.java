package com.vipkid.rest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vipkid.model.FileType;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.mq.producer.queue.LeadsQueueSender;
import com.vipkid.service.FileService;
import com.vipkid.service.ImportExcelService;
import com.vipkid.service.ParentService;
import com.vipkid.service.pojo.ImportExcelErrView;

@RestController
@RequestMapping(value="/api/service/private/importExcel")
public class ImportExcelController {
	
	
	@Resource
	private ImportExcelService importExcelService;
	
	private DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");
	
	@Resource
    LeadsQueueSender leadsQueueSender;
	
	private static Logger logger = LoggerFactory.getLogger(FileService.class.getSimpleName());
	
	@Resource
	private ParentService parentService;
	
	@RequestMapping(value="/upload",method = RequestMethod.POST)
	public List<ImportExcelErrView> upload(@RequestParam("fileType") final FileType fileType, @RequestParam("file") MultipartFile file, @RequestParam("fileSize") String fileSize) {
		logger.info("import excel file = {} with type = {} ", file.getName(), fileType.name());
		return doUpload(fileType, file, fileSize); 
	}
	
	public List<ImportExcelErrView> doUpload(final FileType fileType, MultipartFile file, String fileSize) {

		logger.info("import excel file = {} with type = {} ", file.getOriginalFilename(), fileType.name());
		XSSFWorkbook wb=null;
	    XSSFSheet sheet;
	    XSSFRow row;
	    InputStream inputStream = null;
	    List<ImportExcelErrView> errList = new ArrayList<ImportExcelErrView>();
	    ImportExcelErrView err = null;
		try {
			 inputStream = file.getInputStream();
            wb = new XSSFWorkbook(inputStream);
			sheet = wb.getSheetAt(0);
			 // 遍历每行每列的单元格
			int rowNum = sheet.getLastRowNum();
            for(int j=1;j<=rowNum;j++){
            	row = sheet.getRow(j);
            	if(row==null)continue;
            	int colNum = row.getLastCellNum();
            	//int n = row.getPhysicalNumberOfCells();
            	String parentName="";
            	String studentName="";
            	String englishName="";
            	String age="";
            	Date birDate = null;
            	String gender="";
            	String phone = "";
            	String city="";
            	String email="";
            	String notes="";
            	String source = "";
            	String channel="";
                for(int k=0;k<=colNum;k++){
                	if(k==0){
                		parentName=getStringCellValue(row.getCell(k	));
                	}
                	if(k==1){
                		studentName=getStringCellValue(row.getCell(k));
                	}
                	if(k==2){
                		englishName=getStringCellValue(row.getCell(k));
                	}
                	if(k==3){
                		age=getStringCellValue(row.getCell(k));
                	}
                    if(k==4){ 
                    	birDate = getDateCellValue(row.getCell(k));
                    }
                    if(k==5){
                    	gender = getStringCellValue(row.getCell(k));
                    }
                    if(k==6){
                    	phone = getStringCellValue(row.getCell(k));
                    }
                    if(k==7){
                    	city = getStringCellValue(row.getCell(k));
                    }
                    if(k==8){
                    	email = getStringCellValue(row.getCell(k));
                    }
                    if(k==9){
                    	notes = getStringCellValue(row.getCell(k));
                    }
                    if(k==10){
                    	source = getStringCellValue(row.getCell(k));
                    }
                    if(k==11){
                    	channel = getStringCellValue(row.getCell(k));
                    }
                }
                err = new ImportExcelErrView(parentName, studentName, englishName, age, birDate, gender, phone, city, email, notes, source, channel);
                logger.info("import excel data  parentName={}, studentName={},englishName={},phone={},source={},channel={}",
                		parentName, studentName,englishName,phone,source,channel);
                try {
                	if(phone!=null&&!phone.trim().equals("")){
                    	phone = phone.trim();
                    	Parent findParent = parentService.findByUsername(phone);
                    	if(findParent == null) {
                    		Student student = importExcelService.doCreate(parentName, studentName,
									englishName, age, birDate, gender, phone,
									city, email, notes, source, channel);
                    		if (student != null) {
                    			leadsQueueSender.sendText(String.valueOf(student.getId()));
                    		}
                    	}else {
                    		// 目前update不进行create student操作，如果后续需要进行create，则需发送消息同时create leads
                    		channel = importExcelService.doUpdate(parentName, phone, notes,
									source, channel, findParent);
                    		errList.add(err);
                    	}
                	}
				} catch (Exception e) {
					errList.add(err);
					logger.error("Import excel error",e);
				}
            }
		} catch (Exception  e) {
			logger.error("upload excel error",e);
		} finally{
			try {
				if(inputStream!=null){
					inputStream.close();
				}
				if(wb!=null){
					wb.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return errList;   

	}
	
	 /**
     * 获取单元格数据内容为字符串类型的数据
     * 
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private String getStringCellValue(XSSFCell cell) {
    	if (cell == null) {
            return "";
        }
        String strCell = "";
        switch (cell.getCellType()) {
        case XSSFCell.CELL_TYPE_STRING:
            strCell = cell.getStringCellValue();
            break;
        case XSSFCell.CELL_TYPE_NUMERIC:
        	BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
            strCell = String.valueOf(bd);
            break;
        case XSSFCell.CELL_TYPE_BOOLEAN:
            strCell = String.valueOf(cell.getBooleanCellValue());
            break;
        case XSSFCell.CELL_TYPE_BLANK:
            strCell = "";
            break;
        default:
            strCell = "";
            break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        return strCell;
    }

    /**
     * 获取单元格数据内容为日期类型的数据
     * 
     * @param cell
     *            Excel单元格
     * @return String 单元格数据内容
     */
    private Date getDateCellValue(XSSFCell cell) {
    	if (cell == null) {
            return null;
        }
        try {
            int cellType = cell.getCellType();
            if (cellType == XSSFCell.CELL_TYPE_NUMERIC) {
                Date date = cell.getDateCellValue();
                return date;
            } 
            if (cellType == XSSFCell.CELL_TYPE_STRING){
            	Date date = dateFormat.parse(cell.getStringCellValue());
            	return date;
            }
        } catch (Exception e) {
            System.out.println("日期格式不正确!");
            e.printStackTrace();
        }
        return null;
    }

}
