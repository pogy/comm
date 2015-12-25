package com.vipkid.rest;



import com.vipkid.ext.dby.AttachDocumentResult;
import com.vipkid.ext.dby.ListDocumentsResult;
import com.vipkid.ext.dby.RemoveDocumentResult;
import com.vipkid.model.Role;
import com.vipkid.rest.vo.Response;
import com.vipkid.service.DBYService;
import com.vipkid.service.pojo.Room;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value="/api/service/public/dby")
public class DBYController {
	private Logger logger = LoggerFactory.getLogger(DBYController.class.getSimpleName());

	@Resource
	private DBYService dbyService;
	
	@RequestMapping(value="/getDBYRoomURL",method = RequestMethod.GET)
	public Room getDBYRoomURL(@RequestParam("userId") String userId, @RequestParam("name") String name, @RequestParam(value = "roomId", required = false) String roomId,@RequestParam("role") Role role) {
		logger.info("get DBY room URL for userId = {}, name = {}, roomId = {}, role = {}", userId, name, roomId, role);
		return dbyService.getDBYRoomURL(userId, name, roomId, role);
	}
	

	@RequestMapping(value="/reAttatchDocument",method = RequestMethod.GET)
	public AttachDocumentResult reAttatchDocument(@RequestParam("onlineClassId") long onlineClassId, @RequestParam("userId") long userId) {
		logger.info("reAttatchDocument for onlineClassId = {}, userId = {}", onlineClassId, userId);
		return dbyService.doReAttatchDocument(onlineClassId, userId);
	}

	@RequestMapping(value="/uploadDocuments",method = RequestMethod.GET)
	public Response uploadDocuments(HttpServletRequest request,HttpServletResponse response) {
		logger.info("Upload Documents");
        String filePath = request.getSession().getServletContext().getRealPath("/WEB-INF/ppt");
        if (StringUtils.isBlank(filePath)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return new Response(HttpStatus.NOT_FOUND.value(),"PPT File Path not exists");
        }
        Response res = dbyService.doUploadDocuments(filePath);
        if (null != res) {
            response.setStatus(res.getStatus());
        }
		return res;
	}
	
	@RequestMapping(value="/listDocuments",method = RequestMethod.GET)
	public ListDocumentsResult listDocument(@RequestParam("onlineClassId") long onlineClassId){
		logger.info("reAttatchDocument for onlineClassId = {}", onlineClassId);
		return dbyService.listDocument(onlineClassId);
	}
	
	
	@RequestMapping(value="/removeDocuments",method = RequestMethod.GET)
	public RemoveDocumentResult removeDocument(@RequestParam("onlineClassId") long onlineClassId,@RequestParam("documentId") String documentId){
		logger.info("removeDocument for onlineClassId = {},documentId={}", onlineClassId,documentId);
		return dbyService.removeDocument(onlineClassId, documentId);
	}
	
	@RequestMapping(value="/reScheduleDbyDocuments",method = RequestMethod.GET)
	public Response reScheduleDbyDocuments (HttpServletResponse response){
		logger.info("reScheduleDbyDocuments ");
        Response resp = dbyService.doReScheduleDbyDocuments();
        if (null != resp) {
            response.setStatus(resp.getStatus());
        }
		return resp;
	
	}
	
	/**
	 * 2015-09-01 trial foundation课件--多个文档上传
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/uploadFoundationTrialMultiDocuments",method = RequestMethod.GET)
	public Response uploadFoundationTrialMultiDocuments(HttpServletRequest request,HttpServletResponse response) {
		logger.info("uploadTrialMultiDocuments ");
        String filePath = request.getSession().getServletContext().getRealPath("/WEB-INF/trial_found_ppt");
        if (StringUtils.isBlank(filePath)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return new Response(HttpStatus.NOT_FOUND.value(),"PPT File Path not exists");
        }
        Response res = dbyService.doUploadTrialDocuments(filePath,"FoundationTrial");
        if (null != res) {
            response.setStatus(res.getStatus());
        }
		return res;
	}
	
	/**
	 * 2015-09-01 trial level1课件--多个文档上传
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/uploadLevel1TrialMultiDocuments",method = RequestMethod.GET)
	public Response uploadLevel1TrialMultiDocuments(HttpServletRequest request,HttpServletResponse response) {
		logger.info("uploadTrialMultiDocuments ");
        String filePath = request.getSession().getServletContext().getRealPath("/WEB-INF/trial_level1_ppt");
        if (StringUtils.isBlank(filePath)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return new Response(HttpStatus.NOT_FOUND.value(),"PPT File Path not exists");
        }
        Response res = dbyService.doUploadTrialDocuments(filePath,"Level1Trial");
        if (null != res) {
            response.setStatus(res.getStatus());
        }
		return res;
	}
	
	/**
	 * 2015-09-01 trial Travel 课件--多个文档上传
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/uploadTravelTrialMultiDocuments",method = RequestMethod.GET)
	public Response uploadTravelTrialMultiDocuments(HttpServletRequest request,HttpServletResponse response) {
		logger.info("uploadTrialMultiDocuments ");
        String filePath = request.getSession().getServletContext().getRealPath("/WEB-INF/trial_travel_ppt");
        if (StringUtils.isBlank(filePath)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return new Response(HttpStatus.NOT_FOUND.value(),"PPT File Path not exists");
        }
        Response res = dbyService.doUploadTrialDocuments(filePath,"TravelTrial");
        if (null != res) {
            response.setStatus(res.getStatus());
        }
		return res;
	}
}
