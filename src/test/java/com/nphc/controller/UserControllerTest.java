package com.nphc.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nphc.helper.ResponseMessage;
import com.nphc.helper.exception.BadInputException;
import com.nphc.model.ResponseObject;
import com.nphc.model.SearchResult;
import com.nphc.model.UserEntity;
import com.nphc.service.UserService;

@WebMvcTest
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void whenGetUser_with200_withMessage() throws Exception {

		UserEntity user = new UserEntity();
		when(userService.getUser("1")).thenReturn(user);

		this.mockMvc.perform(get("/users/1")).andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(user))));
	}

	@Test
	public void whenGetInvalidUser_with400_withMessage() throws Exception {

		when(userService.getUser("1")).thenThrow(new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE));

		this.mockMvc.perform(get("/users/1")).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(objectMapper
						.writeValueAsString(new ResponseObject(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)))));
	}

	@Test
	public void whenCreateValidUser_with201_withMessage() throws Exception {

		UserEntity user = new UserEntity();

		doNothing().when(userService).saveUser(user);

		this.mockMvc
				.perform(post("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isCreated()).andExpect(content().string(
						containsString(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_CREATED)))));
	}

	@Test
	public void whenCreateInvalidUser_with400_withMessage() throws Exception {

		UserEntity user = new UserEntity();

		doThrow(new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)).when(userService).saveUser(user);

		this.mockMvc
				.perform(post("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isBadRequest()).andExpect(content().string(containsString(objectMapper
						.writeValueAsString(new ResponseObject(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)))));
	}

	@Test
	public void whenPutValidUser_with200_withMessage() throws Exception {

		UserEntity user = new UserEntity();

		doNothing().when(userService).updateUser(user);

		this.mockMvc
				.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk()).andExpect(content().string(
						containsString(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_UPDATED)))));
	}

	@Test
	public void whenPatchValidUser_with200_withMessage() throws Exception {

		UserEntity user = new UserEntity();

		doNothing().when(userService).updateUser(user);

		this.mockMvc
				.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk()).andExpect(content().string(
						containsString(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_UPDATED)))));
	}

	@Test
	public void whenDeleteUser_with200_withMessage() throws Exception {

		this.mockMvc.perform(delete("/users/1")).andExpect(status().isOk()).andExpect(content()
				.string(containsString(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_DELETED)))));
	}

	@Test
	public void whenDeleteInvalidUser_with400_withMessage() throws Exception {

		doThrow(new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)).when(userService).deleteUser("1");

		this.mockMvc.perform(delete("/users/1")).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(objectMapper
						.writeValueAsString(new ResponseObject(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)))));
	}

	@Test
	public void whenFetchUser_with200_withMessage() throws Exception {

		when(userService.searchUserList(anyMap())).thenReturn(null);

		this.mockMvc.perform(get("/users/")).andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(new SearchResult(null)))));

	}

	@Test
	public void whenFetchUser_with400_withMessage() throws Exception {

		when(userService.searchUserList(anyMap()))
				.thenThrow(new BadInputException(String.format(ResponseMessage.MSG_ERR_INVALID_PARAM_SEARCH, "test")));

		this.mockMvc.perform(get("/users/")).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(
						new ResponseObject(String.format(ResponseMessage.MSG_ERR_INVALID_PARAM_SEARCH, "test"))))));

	}

	@Test
	public void whenUploadUser_with200_withMessage() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "testData.csv", MediaType.TEXT_PLAIN_VALUE,
				"testing".getBytes());

		when(userService.uploadAndSaveUser(multipartFile)).thenReturn(0);

		this.mockMvc.perform(multipart("/users/upload").file(multipartFile)).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_NO_CREATE)))));

	}

	@Test
	public void whenUploadUser_with201_withMessage() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "testData.csv", MediaType.TEXT_PLAIN_VALUE,
				"testing".getBytes());

		when(userService.uploadAndSaveUser(multipartFile)).thenReturn(1);

		this.mockMvc.perform(multipart("/users/upload").file(multipartFile)).andExpect(status().isCreated())
				.andExpect(content().string(containsString(
						objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_CREATE_UPDATE)))));

	}

	@Test
	public void whenUploadUser_with400_withMessage() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "testData.csv", MediaType.TEXT_PLAIN_VALUE,
				"testing".getBytes());

		when(userService.uploadAndSaveUser(multipartFile))
				.thenThrow(new BadInputException(ResponseMessage.MSG_ERR_EMPLOYEE_EXIST));

		this.mockMvc.perform(multipart("/users/upload").file(multipartFile)).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(
						objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_ERR_EMPLOYEE_EXIST)))));
	}

}
