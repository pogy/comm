package com.vipkid.service;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.InventionCode;
import com.vipkid.repository.InventionCodeRepository;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.StringWrapper;

@Service
public class InventionCodeService {
	private Logger logger = LoggerFactory.getLogger(InventionCodeService.class.getSimpleName());
	
	@Resource
	private InventionCodeRepository inventionCodeRepository;
	
	public List<InventionCode> findByMarketingActivityIdAndStatus(long marketingActivityId, boolean status) {
		logger.debug("find InventionCode for marketingActivityId = {}, status = {}", marketingActivityId, status);
		return inventionCodeRepository.findByMarketingActivityIdAndStatus(marketingActivityId, status);
	}
	
	public InventionCode findByCode(String code) {
		logger.debug("find InventionCode for code = {}", code);
		return inventionCodeRepository.findByCode(code);
	}
	
	public List<InventionCode> list(String hasUsed,long marketingActivityId,int start,int length) {
		logger.debug("list InventionCode with params: hasUsed={},marketingActivityId={}, start = {}, length = {}.", hasUsed,marketingActivityId, start, length);
		return inventionCodeRepository.list(hasUsed,marketingActivityId,start, length);
	}

	public Count count(String hasUsed,long marketingActivityId) {
		logger.debug("count InventionCode with params: hasUsed = {},marketingActivityId={}.", hasUsed,marketingActivityId);
		return new Count(inventionCodeRepository.count(hasUsed,marketingActivityId));
	}
	
	public StringWrapper downLoadExcel(String hasUsed,long marketingActivityId,HttpServletRequest httpRequest,HttpServletResponse httpResponse) {
		logger.debug("export excel with params: hasUsed = {},marketingActivityId={}.", hasUsed,marketingActivityId);
		List<InventionCode>list = inventionCodeRepository.listForExcel(hasUsed,marketingActivityId);
		return new StringWrapper(LoadFile(httpRequest,httpResponse,list,marketingActivityId));
	}
	
	public String LoadFile(HttpServletRequest request,HttpServletResponse response,List<InventionCode> list,long marketingActivityId) {
	   	  String realPath = request.getSession().getServletContext().getRealPath("/")+File.separator+"uploadInventionExcel";
		  // 文件名称与路径
		  String fileName ="marketingActivity"+marketingActivityId+ "_inventionCode.xls";
		  File file = new File(realPath);//导出文件存放的位置
		  if (!file.exists()) {
			  file.mkdirs();
		  }
		  realPath = realPath +File.separator+fileName;
		  // 建立工作薄并写表头
		  try {
			   WritableWorkbook wwb = Workbook.createWorkbook(new File(realPath));
			   WritableSheet ws = wwb.createSheet("Sheet1", 0);// 建立工作簿
			   // 写表头
			   jxl.write.Label label1 = new jxl.write.Label(0, 0, "invention");
			   jxl.write.Label label2 = new jxl.write.Label(1, 0, "status");
	
			   ws.addCell(label1);// 放入工作簿
			   ws.addCell(label2);

			   // 写入信息
			   for (int i = 0; i < list.size(); i++) {
				    label1 = new jxl.write.Label(0, i + 1, list.get(i).getCode());// 建立第一列
				    label2 = new jxl.write.Label(1, i + 1, list.get(i).isHasUsed()?"Yes":"no");// 建立第二列
				    ws.addCell(label1);// 放入工作簿
				    ws.addCell(label2);
			   }
			   // 写入Exel工作表
			   wwb.write();
			   // 关闭Excel工作薄对象
			   wwb.close();
		   
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		  return "uploadInventionExcel"+File.separator+fileName;
		 }
	
}
