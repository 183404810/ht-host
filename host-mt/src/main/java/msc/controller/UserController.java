package msc.controller;

import ht.msc.base.BaseController;
import ht.msc.base.BaseService;

import javax.annotation.Resource;

import msc.entity.SCUser;
import msc.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/scuser")
public class UserController extends BaseController<SCUser>{
	@Resource
	private UserService userService;
	
	@Override
	protected BaseService init() {
		return userService;
	}
}
