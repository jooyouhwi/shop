package com.wow.api.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
public class homeMainController {
	
	@RequestMapping("/index")
	public String layout() {
		System.out.println("index");
		return "index";	
	}
	
	
	@RequestMapping("/home")
	public String home() {
		System.out.println("home");
		return "index";	
	}
	
	
}