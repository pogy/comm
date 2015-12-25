package com.vipkid.rest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
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

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.FileType;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.security.SecurityService;
import com.vipkid.service.UpdateStarsService;

@RestController
@RequestMapping(value="/api/service/private/updateStars")
public class UpdateStarsController {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateStarsController.class.getSimpleName());
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private UpdateStarsService updateStarsService;
	
	@RequestMapping(value="/upload",method = RequestMethod.POST)
	public void upload(@RequestParam("fileType") final FileType fileType, @RequestParam("file") MultipartFile file, @RequestParam("fileSize") String fileSize) {
		doUpload(fileType, file, fileSize); 
	}
	
	public void doUpload(final FileType fileType, MultipartFile file, String fileSize) {

		logger.info("updateStars file = {} with type = {} ", file.getOriginalFilename(), fileType.name());
		securityService.logAudit(Level.INFO, Category.UPDATE_STARS, "update stars");
		XSSFWorkbook wb=null;
	    XSSFSheet sheet;
	    XSSFRow row;
	    InputStream inputStream = null;
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
            	String starsNum="";
            	String mobile="";
                for(int k=0;k<=colNum;k++){
                	if(k==4){
                		starsNum=getStringCellValue(row.getCell(k));
                	}
                    if(k==5){ 
                    	mobile =getStringCellValue(row.getCell(k));
                    }
                }
                Parent parent = updateStarsService.findParentByMobile(mobile.trim());
                if(parent==null)continue;
                updateStars(starsNum.trim(), parent);
            }
		} catch (Exception  e) {
			logger.error("updateStars error",e);
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

	}

	private void updateStars(String starsNum, Parent parent) {
		List<Student>list = parent.getFamily().getStudents();
		if(CollectionUtils.isNotEmpty(list)){
			for(Student st : list){
		    	try {
					updateStarsService.updateStarsByStudentId(st.getId(), Integer.valueOf(starsNum));
				} catch (Exception e) {
					logger.error("updateStars name = {} with starsNum = {} ", st.getName(), starsNum);
				}
		    }
		}
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


}
