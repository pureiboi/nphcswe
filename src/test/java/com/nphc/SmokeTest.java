package com.nphc;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.nphc.controller.UserController;
import com.nphc.service.UserService;

@SpringBootTest
public class SmokeTest {

	@Autowired
	private UserController controller;

	@Autowired
	private UserService userService;

	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
		assertThat(userService).isNotNull();
	}
}
