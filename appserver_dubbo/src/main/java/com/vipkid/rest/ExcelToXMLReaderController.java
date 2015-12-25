package com.vipkid.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.rest.vo.Response;
import com.vipkid.service.ExcelToXMLReaderService;

@RestController
@RequestMapping(value="/api/service/public/xml")
public class ExcelToXMLReaderController {
	
	@javax.annotation.Resource
	private ExcelToXMLReaderService excelToXMLReaderService;
	
	
	/*@RequestMapping(value="/course",method = RequestMethod.GET)
	public Response initForCourse(HttpServletResponse response) throws JDOMException, IOException {
		Response res = excelToXMLReaderService.doInitForCourse();

		if (null != res) {
			response.setStatus(res.getStatus());
		}
		return res;
	}*/
	
	@RequestMapping(value="/slide",method = RequestMethod.GET)
	public Response initForSlide(HttpServletResponse response) throws JDOMException, IOException {
		Response res = excelToXMLReaderService.doInitForSlide();
		if (null != res) {
			response.setStatus(res.getStatus());
		}
		return res;
	}
	
	@RequestMapping(value="/slidefortest",method = RequestMethod.GET)
	public Response initForSlideForTest(HttpServletResponse response) throws JDOMException, IOException {
		Response res = excelToXMLReaderService.doInitForSlideForTest();
		if (null != res) {
			response.setStatus(res.getStatus());
		}
		return res;
	}
	
}
