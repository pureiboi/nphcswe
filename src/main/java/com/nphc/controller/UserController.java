package com.nphc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.nphc.helper.ResponseMessage;
import com.nphc.model.ResponseObject;
import com.nphc.model.SearchResult;
import com.nphc.model.UserEntity;
import com.nphc.service.IUserService;

@RestController
public class UserController {
	
	@Autowired
	private IUserService userService;

	private static final Logger logger = LogManager.getLogger();

	@RequestMapping(value = "/users/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE , method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<ResponseObject> uploadUsers(@RequestParam("file") MultipartFile file) {

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_NO_CREATE);

		logger.info("file name: {}, file size: {}", file.getOriginalFilename(), file.getSize());

		Integer createCount = userService.uploadAndSaveUser(file);
		if (createCount > 0) {
			response.setMessage(ResponseMessage.MSG_SUC_CREATE_UPDATE);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}

		return ResponseEntity.ok().body(response);
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public @ResponseBody SearchResult searchUsers(@RequestParam Map<String, String> searchCriteria) {

		logger.info("search criteria: {}", searchCriteria);

		List<UserEntity> userList = new ArrayList<>();
		
		userList = userService.searchUserList(searchCriteria);

		return new SearchResult(userList);
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public @ResponseBody UserEntity getUserById(@PathVariable String id) {

		logger.info("id: {}", id);

		UserEntity userResult = userService.getUser(id);

		return userResult;
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody ResponseObject createUser(@RequestBody UserEntity userReq) {

		logger.info("request object: {}", userReq);

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_CREATED);

		userService.saveUser(userReq);

		return response;
	}

	@RequestMapping(value = "/users/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
	public @ResponseBody ResponseObject updateUserById(@PathVariable String id,
			@RequestBody UserEntity userSearchObject) {

		userSearchObject.setId(id);
		
		logger.info("id: {}, request object: {}", id, userSearchObject);

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_UPDATED);

		userService.updateUser(userSearchObject);

		return response;
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public @ResponseBody ResponseObject deleteUserById(@PathVariable String id) {

		logger.info("id: {}", id);

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_DELETED);

		userService.deleteUser(id);

		return response;
	}

	@RequestMapping(value = "/")
	public @ResponseBody String welcome() {
		return "Welcome";
	}

}
