package msc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import msc.service.TestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/test")
public class TestController {
	private Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Resource
	private TestService testService;
	
	@RequestMapping("/test.json")
    @ResponseBody
    public Map<String,Object> getDtlRel(HttpServletRequest req, Model model) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("msg", "hhhh");
		resultMap.put("list", testService.list());
		return resultMap;
	}

}
