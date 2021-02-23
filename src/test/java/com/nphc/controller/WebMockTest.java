package com.nphc.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.nphc.service.UserService;

@WebMvcTest(UserController.class)
public class WebMockTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Test
	public void testIndex_with200() throws Exception {

		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Welcome")));
	}
	
	@Test
	public void testInvalidPath_with400() throws Exception {

		this.mockMvc.perform(get("/hi")).andDo(print()).andExpect(status().isNotFound());
	}
}
