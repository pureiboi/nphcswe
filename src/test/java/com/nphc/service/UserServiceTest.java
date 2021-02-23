package com.nphc.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import com.nphc.helper.ResponseMessage;
import com.nphc.helper.exception.BadInputException;
import com.nphc.model.UserEntity;
import com.nphc.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Test
	public void TestValidation() {

		UserEntity userForTesting = new UserEntity();

		// =========== separator ============= //
		
		// when field mandatory fields not provided
		Throwable exception = assertThrows(BadInputException.class,
				() -> userService.validateMandatoryFieldWithError(userForTesting));

		assertEquals("[id,login,name,salary] is mandatory", exception.getMessage());

		// =========== separator ============= //

		userForTesting.setId("abc");

		exception = assertThrows(BadInputException.class,
				() -> userService.validateMandatoryFieldWithError(userForTesting));

		assertEquals("[login,name,salary] is mandatory", exception.getMessage());

		// =========== separator ============= //

		userForTesting.setLogin("harry");

		exception = assertThrows(BadInputException.class,
				() -> userService.validateMandatoryFieldWithError(userForTesting));

		assertEquals("[name,salary] is mandatory", exception.getMessage());

		// =========== separator ============= //

		userForTesting.setName("port");

		exception = assertThrows(BadInputException.class,
				() -> userService.validateMandatoryFieldWithError(userForTesting));

		assertEquals("[salary] is mandatory", exception.getMessage());

		// =========== separator ============= //


		userForTesting.setSalary(-1.0);

		userForTesting.setStartDate(new Date());

		assertDoesNotThrow(() -> userService.validateMandatoryFieldWithError(userForTesting));

		// =========== separator ============= //

		// when fields not valid
		
		exception = assertThrows(BadInputException.class, () -> userService.validateFormatWithError(userForTesting));

		assertEquals("Invalid salary", exception.getMessage());

		// =========== separator ============= //

		userForTesting.setStartDate(null);

		exception = assertThrows(BadInputException.class, () -> userService.validateFormatWithError(userForTesting));

		assertEquals("Invalid salary,date", exception.getMessage());

		// =========== separator ============= //

		userForTesting.setSalary(0.0);

		exception = assertThrows(BadInputException.class, () -> userService.validateFormatWithError(userForTesting));

		assertEquals("Invalid date", exception.getMessage());

		// =========== separator ============= //

		userForTesting.setStartDate(new Date());

		assertDoesNotThrow(() -> userService.validateMandatoryFieldWithError(userForTesting));

		// =========== separator ============= //

		// when employee not exists
		
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(false);

		exception = assertThrows(BadInputException.class,
				() -> userService.validateDeleteUserWithError(userForTesting));

		assertEquals(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE, exception.getMessage());
		
		// when employee login is not unique
		
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(true);

		Mockito.when(userRepository.existsByLoginAndIdNot(userForTesting.getLogin(), userForTesting.getId()))
				.thenReturn(true);

		exception = assertThrows(BadInputException.class,
				() -> userService.validateUpdateUserWithError(userForTesting));

		assertEquals(ResponseMessage.MSG_ERR_NOT_UNIQUE_EMPLOYEE_LOGIN, exception.getMessage());

		// =========== separator ============= //
	}

	@Test
	public void TestCrudUser() {
		
		Throwable exception = null;

		Calendar today = Calendar.getInstance();

		UserEntity userForTesting = new UserEntity();
		userForTesting.setId("e00022");
		userForTesting.setLogin("harry");
		userForTesting.setName("Potter Party");
		userForTesting.setSalary(3500.0);
		userForTesting.setStartDate(today.getTime());

		// get //
		
		// when exployee does not exists
		Mockito.when(userRepository.findById(userForTesting.getId())).thenReturn(Optional.empty());

		exception = assertThrows(BadInputException.class, () -> userService.getUser(userForTesting.getId()));
		
		assertEquals(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE, exception.getMessage());
		
		// when exployee does not exists

		Mockito.when(userRepository.findById(userForTesting.getId())).thenReturn(Optional.of(userForTesting));

		assertDoesNotThrow(() -> userService.getUser(userForTesting.getId()));

		Mockito.verify(userRepository, Mockito.times(2)).findById(userForTesting.getId());

		// =========== separator ============= //

		// create // 
		
		// when employee ID exists
		
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(true);

		exception = assertThrows(BadInputException.class, ()-> userService.saveUser(userForTesting));
		
		assertEquals(ResponseMessage.MSG_ERR_EMPLOYEE_EXIST, exception.getMessage());
		
		// when mandatory fields
		
		UserEntity userForInvalid = new UserEntity();
		
		exception = assertThrows(BadInputException.class, ()-> userService.saveUser(userForInvalid));

		assertEquals("[id,login,name,salary] is mandatory,Invalid salary,date", exception.getMessage());
		
		// when valid data
		
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(false);

		assertDoesNotThrow(()-> userService.saveUser(userForTesting));


		userService.saveUser(userForTesting);
		
		// =========== separator ============= //

		// update //
		
		// when employee does not exists
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(false);
		
		exception = assertThrows(BadInputException.class, () -> userService.updateUser(userForTesting));

		assertEquals(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE, exception.getMessage());

		// when employye exists but login already used
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(true);
		
		Mockito.when(userRepository.existsByLoginAndIdNot(userForTesting.getLogin(), userForTesting.getId())).thenReturn(true);

		exception = assertThrows(BadInputException.class, () -> userService.updateUser(userForTesting));

		assertEquals(ResponseMessage.MSG_ERR_NOT_UNIQUE_EMPLOYEE_LOGIN, exception.getMessage());

		// when salary and startDate is invalid
		
		Mockito.when(userRepository.existsByLoginAndIdNot(userForTesting.getLogin(), userForTesting.getId())).thenReturn(false);
		
		userForTesting.setSalary(-1.0);
		
		userForTesting.setStartDate(null);
		
		exception = assertThrows(BadInputException.class, () -> userService.updateUser(userForTesting));
		
		assertEquals("Invalid salary,date", exception.getMessage());

		// when invalid salary
		userForTesting.setStartDate(today.getTime());
		
		exception = assertThrows(BadInputException.class, () -> userService.updateUser(userForTesting));

		assertEquals("Invalid salary", exception.getMessage());

		
		// when valid data
		userForTesting.setSalary(0.0);
		
		assertDoesNotThrow(()-> userService.updateUser(userForTesting));

		Mockito.verify(userRepository, Mockito.times(3)).save(userForTesting);

		// =========== separator ============= //

		// delete //
		
		// when exployee does not exists
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(false);
		
		exception = assertThrows(BadInputException.class, () -> userService.deleteUser(userForTesting.getId()));

		assertEquals(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE, exception.getMessage());


		// when valid data
		Mockito.when(userRepository.existsById(userForTesting.getId())).thenReturn(true);

		assertDoesNotThrow(() -> userService.deleteUser(userForTesting.getId()));

		Mockito.verify(userRepository, Mockito.times(1)).deleteById(userForTesting.getId());

	}
}
