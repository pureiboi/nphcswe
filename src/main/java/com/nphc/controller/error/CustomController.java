package com.nphc.controller.error;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomController implements ErrorController {

	@RequestMapping(path = "/error")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody String handleError(HttpServletRequest request) {
		return "404";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
