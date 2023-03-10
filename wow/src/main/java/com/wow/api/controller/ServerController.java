package com.wow.api.controller;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wow.api.dao.CompanyMapper;
import com.wow.api.dao.MaccoMapper;
import com.wow.api.dao.PgMapper;
import com.wow.api.dto.PgDto;
import com.wow.api.model.CompanyModel;
import com.wow.api.model.PgModel;
import com.wow.api.model.ServerModel;
import com.wow.api.service.ServerService;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@RestController*/
@Controller
public class ServerController {

	@Autowired
	private ServerService serverService;

	@Autowired
	private CompanyMapper companyMapper;
	
	@Autowired
	private MaccoMapper maccoMapper;

	@Autowired
	private PgMapper pgMapper;
	
	@Autowired
	private settlebankPayment settlebankPayment;
	
	/*
	 * @Autowired private certifiedController certifiedController;
	 */
	
	@Autowired
	private maccoReportController maccoReportController;
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	/*
	 * @GetMapping("/wowApi")
	 * 
	 * @ResponseBody public String requestParam(@RequestParam Map<String, Object>
	 * allParameters) {
	 * return null;
	 * }
	 */
	//?????? ???????????? 
	//@GetMapping("/wowApi")
	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/wowApi")
	public @ResponseBody HashMap<String,Object> requestParam(HttpServletRequest request, HttpServletResponse response,ModelMap modelMap) {

		String comId = request.getParameter("comId") == null ? "" : request.getParameter("comId");
		String kind  = request.getParameter("kind") == null ? "" : request.getParameter("kind");
		String type  = request.getParameter("type") == null ? "" : request.getParameter("type");

		JSONObject resultJson = new JSONObject();
		HashMap<String,Object> retMap = new HashMap<String, Object>();
		HashMap<String, Object> dataInfo = new HashMap<String, Object>();

		if(kind.equals("payment")) {              //??????
			HashMap<String,Object> param = new HashMap<String,Object>();
			String vendor = request.getParameter("vendor") == null ? "" : request.getParameter("vendor");
			param.put("comId",comId);
			param.put("vendor",vendor);
			PgModel pgModel = null;
			pgModel = pgMapper.getWowConfigPGInfo(param);
			if(type.equals("card")) {             //????????????
				// ?????? ?????? 
				try {
					if(pgModel.pgVendor.equals("SETTLEBANK")) {
						retMap = settlebankPayment.payment(request,response);
					} else {
					}
				} catch(Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(type.equals("cancel")) {     //???????????? 
				try {
					if(pgModel.pgVendor.equals("SETTLEBANK")) {
						retMap = settlebankPayment.cancel(request,response);
					} else {
					}
				} catch(Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(type.equals("account")) {    //????????????   
				try {
					if(pgModel.pgVendor.equals("SETTLEBANK")) {
						retMap = settlebankPayment.vaccNumber(request,response);
					} else {
					}
				} catch(Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if(kind.equals("certified")) {      //??????
    		 
			if(type.equals("account")) {          //??????????????????
    			 
				try {
					//retMap = certifiedController.niceAccData(request, response); 
    				 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(type.equals("name")) {       //???????????? 
    			 try {
    				 //retMap = certifiedController.niceNameData(request, response);
    			 }catch (Exception e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
			}
		}else if(kind.equals("report")) {         //????????????  
			try {
    			 
    			 retMap = maccoReportController.maccoReport(request, response);
    			 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	 
		dataInfo = getJsonStringFromMap(retMap);
		resultJson.put("data", dataInfo);
    	
		return resultJson;
	}
    
    
    
    //mybatis sample
	@RequestMapping(value="/card2")
	public @ResponseBody HashMap<String,Object> SeverData2(HttpServletRequest request, HttpServletResponse response) {	
		String reData = "";
        HashMap<String,Object> retMap = new HashMap<String, Object>();
        HashMap<String, Object> dataInfo = new HashMap<String, Object>();
        JSONObject resultJson = new JSONObject();
		
		// db ??? ????????? ?????? ????????? 
        try {
        	
        
        	
        	//CompanyModel comList = this.serverService.listCompany();
        	//CompanyModel comList = companyMapper.listCompany(comId);
        	
            HashMap<String,Object> paramObj = new HashMap<String,Object>();
            paramObj.put("comId", request.getParameter("comId"));         // Ord_NO : ????????????
            paramObj.put("ordNo", request.getParameter("orderid"));         // Ord_NO : ????????????
            //paramObj.put("GUBUN", "0");                         // Gubun : ???0???       -- ???????????? ?????? ????????????. (????????? ???1???)
            paramObj.put("gubun", request.getParameter("gubun"));                       // Gubun : ???0???       -- ???????????? ?????? ????????????. (????????? ???1???)
            paramObj.put("licenseNo", request.getParameter("GuaranteeCode")); // License_NO : ???????????? ? ?????? ?????? ????????? ????????? ????????????, ??????????????? ??????
            paramObj.put("sendYn", request.getParameter("guaranteeResult"));  // Send_YN : ???N???         -- ?????? ?????? ???. guaranteeResult
            paramObj.put("errCd", request.getParameter("ErrorCode"));         // Err_CD : ??????        -- ????????? ???????????? ??????, ???????????? ???????????? ???????????? ?????? ??????.

            paramObj.put("amt", request.getParameter("totalmoney"));         // Amt : ????????????
            paramObj.put("sellerCd", "0");                       // Seller_CD : ???0???
            paramObj.put("userName", request.getParameter("name"));         // UserName : ?????????
            paramObj.put("juminNo", request.getParameter("userid"));        // Jumin_NO : ??? 7??????
            paramObj.put("userId", request.getParameter("mem_id"));         // Userid : ????????????
            paramObj.put("workUser", request.getParameter("workUser"));                  // Work_User : ????????? ?????????
            paramObj.put("ordTime", request.getParameter("ordTime"));                    // Ord_Time : ???????????? (YYMMDD-HH24MISS)
            
            // ?????? (??????????????? DB ??????) =======================================
            //commonService.insert("wowOrder.insertOrdGuild", paramObj);
            
           // maccoMapper.insertOrdGuild(paramObj);
        	
        	maccoMapper.insertOrdGuild(paramObj);
        	//companyMapper.insertTable(comId);
        	// insert test mybatis 
        	

        	logger.info("------------------------------3333");
        	
        }catch( Exception e) {
        	logger.info("ee:::" + e.getMessage());
        	
        }
        
        // json ????????? ??????
        resultJson.put("data", dataInfo);
        
		return resultJson;//reData ;  
	}
	
	//jpa sample
	@RequestMapping(value="/card1")
	private @ResponseBody HashMap<String,Object>  SeverData(@RequestParam(value = "comId")String comId, @RequestParam(value = "userid")String userid){
		
		String reData = "";
        HashMap<String,Object> retMap = new HashMap<String, Object>();
        HashMap<String, Object> dataInfo = new HashMap<String, Object>();
        JSONObject resultJson = new JSONObject();
		
		// db ??? ????????? ?????? ????????? 
        try {
        	ServerModel members = this.serverService.getuserInfo(comId, userid);
        	logger.info("members : "+ members.getUsername());
        	logger.info("cntCd   : "+ members.getCntCd());
        	
        	//reData = members.getUsername();
        	retMap.put("userName", members.getUsername());
        	retMap.put("cntCd", members.getCntCd());
        	
        	dataInfo = getJsonStringFromMap(retMap);
        	
        	logger.info("dataInfo :::"+ dataInfo);
        	
           
        }catch( Exception e) {
        	logger.info("ee:::" + e.getMessage());
        	
        }
        
        // json ????????? ??????
        resultJson.put("data", dataInfo);
        
		return resultJson;//reData ;  
	}

	
	/**
	 * Map??? jsonString?????? ????????????.
	 *
	 * @param map Map<String, Object>.
	 * @return String.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getJsonStringFromMap( Map<String, Object> map ) {

		JSONObject json = new JSONObject();
		for( Map.Entry<String, Object> entry : map.entrySet() ) {
			String key = entry.getKey();
			Object value = entry.getValue();
			json.put(key, value);
		}
		
		return json;
	}

	
	
}
