package com.nphc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.nphc.helper.CsvReader;
import com.nphc.helper.DateUtil;
import com.nphc.helper.ResponseMessage;
import com.nphc.helper.SpecificationBuilder;
import com.nphc.helper.SpecificationBuilder.Conjuntion;
import com.nphc.helper.UserEntityField;
import com.nphc.helper.UserSearchParam;
import com.nphc.helper.exception.BadInputException;
import com.nphc.model.UserEntity;
import com.nphc.repository.UserRepository;
import com.nphc.repository.UserSpecification;

@Service
public class UserService implements IUserService {

	private static final Logger logger = LogManager.getLogger();

	private static long FILE_SIZE_LIMIT_10MB = 10485760;

	@Autowired
	private UserRepository userRepository;

	public UserEntity getUser(String id) {

		Optional<UserEntity> result = userRepository.findById(id);

		if (!result.isPresent()) {
			throw new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE);
		}

		return result.get();
	}

	public void saveUser(UserEntity user) {

		validateCreateUserWithError(user);

		saveOrUpdateUser(user);

	}

	public void updateUser(UserEntity user) {

		validateUpdateUserWithError(user);

		saveOrUpdateUser(user);

	}

	public void deleteUser(String id) {

		UserEntity user = new UserEntity();
		user.setId(id);

		validateDeleteUserWithError(user);

		userRepository.deleteById(id);
	}

	public List<UserEntity> searchUserList(Map<String, String> searchCriteria) {

		validateSearchParam(searchCriteria);

		// set default value
		Double lowerSalaryLimit = Double.parseDouble(UserSearchParam.minSalary.getValue());
		Double upperSalaryLimit = Double.parseDouble(UserSearchParam.maxSalary.getValue());
		int paginationIdx = Integer.parseInt(UserSearchParam.offset.getValue());
		int pageSize = Integer.parseInt(UserSearchParam.limit.getValue());

		List<Order> sortOrderList = new ArrayList<>();
		Order defaultSortOrder = new Order(Sort.Direction.valueOf(UserSearchParam.sort.getValue()),
				UserEntityField.id.toString());

		if (searchCriteria.containsKey(UserSearchParam.minSalary.toString())) {
			lowerSalaryLimit = Double.parseDouble(searchCriteria.get(UserSearchParam.minSalary.toString()));
		}

		if (searchCriteria.containsKey(UserSearchParam.maxSalary.toString())) {
			upperSalaryLimit = Double.parseDouble(searchCriteria.get(UserSearchParam.maxSalary.toString()));
		}

		if (searchCriteria.containsKey(UserSearchParam.offset.toString())) {
			paginationIdx = Integer.parseInt(searchCriteria.get(UserSearchParam.offset.toString()));
		}
		if (searchCriteria.containsKey(UserSearchParam.limit.toString())) {
			pageSize = Integer.parseInt(searchCriteria.get(UserSearchParam.limit.toString()));
		}

		// create spec default values
		Specification<UserEntity> lowerSalarySpec = UserSpecification.lowerSalaryRange(lowerSalaryLimit);
		Specification<UserEntity> upperSalarySpec = UserSpecification.upperSalaryRange(upperSalaryLimit);

		SpecificationBuilder<UserEntity> builder = new SpecificationBuilder<>(lowerSalarySpec);

		builder.with(upperSalarySpec, Conjuntion.AND);

		// spec for addition search criteria
		if (searchCriteria.containsKey(UserSearchParam.startDateFrom.toString())) {

			String strStartDateFrom = searchCriteria.get(UserSearchParam.startDateFrom.toString());

			Date startDateFrom = DateUtil.parseDate(strStartDateFrom, DateUtil.USER_DATE_PATTERNS);

			Specification<UserEntity> startDateFromSpec = UserSpecification.lowerRangeStartDate(startDateFrom);
			builder.with(startDateFromSpec, Conjuntion.AND);
		}

		if (searchCriteria.containsKey(UserSearchParam.startDateTo.toString())) {

			String strStartDateTo = searchCriteria.get(UserSearchParam.startDateTo.toString());

			Date startDateTo = DateUtil.parseDate(strStartDateTo, DateUtil.USER_DATE_PATTERNS);

			Specification<UserEntity> startDateFromSpec = UserSpecification.upperRangeStartDate(startDateTo);
			builder.with(startDateFromSpec, Conjuntion.AND);
		}

		if (searchCriteria.containsKey(UserSearchParam.name.toString())) {
			String nameString = searchCriteria.get(UserSearchParam.name.toString());
			if (!nameString.isEmpty()) {
				nameString = buildWildCardString(nameString);
				Specification<UserEntity> nameSpec = UserSpecification.wildCardName(nameString);
				builder.with(nameSpec, Conjuntion.AND);
			}
		}

		if (searchCriteria.containsKey(UserSearchParam.id.toString())) {
			String idString = searchCriteria.get(UserSearchParam.id.toString());

			if (!idString.isEmpty()) {
				idString = buildWildCardString(idString);
				Specification<UserEntity> idSpec = UserSpecification.wildCardId(idString);
				builder.with(idSpec, Conjuntion.AND);
			}
		}

		if (searchCriteria.containsKey(UserSearchParam.login.toString())) {

			String loginString = searchCriteria.get(UserSearchParam.login.toString());

			if (!loginString.isEmpty()) {
				loginString = buildWildCardString(loginString);
				Specification<UserEntity> loginSpec = UserSpecification.wildCardLogin(loginString);
				builder.with(loginSpec, Conjuntion.AND);
			}
		}

		if (searchCriteria.containsKey(UserSearchParam.sort.toString())) {

			try {
				String[] sortParam = searchCriteria.get(UserSearchParam.sort.toString()).split(",");

				Sort.Direction sortOrder = Sort.Direction.ASC;
				String sortString = UserEntityField.id.toString();

				if (sortParam.length > 1) {
					sortOrder = Sort.Direction.fromString(sortParam[1]);
				}

				if (sortParam.length > 0) {

					String sortField = sortParam[0];

					if (UserEntityField.validValue(sortField)) {
						sortString = UserEntityField.fromString(sortField).toString();
					}
				}

				sortOrderList.add(new Order(sortOrder, sortString));

			} catch (IllegalArgumentException ex) {
				sortOrderList.add(defaultSortOrder);
			}

		} else {
			sortOrderList.add(defaultSortOrder);
		}

		PageRequest pageRequest = null;
		pageRequest = PageRequest.of(paginationIdx, pageSize, Sort.by(sortOrderList));

		return userRepository.findAll(builder.build(), pageRequest).toList();
	}

	public Integer uploadAndSaveUser(MultipartFile file) {

		// when 1 record is updated or created, when 0 no changes
		int result = 0;

		validateFileSide(file);

		List<UserEntity> userFileDataList = new ArrayList<>();

		String csvContent = getAndFilterCsvData(file);

		logger.info("filtered content: \n{}\n", csvContent);

		userFileDataList = CsvReader.toList(UserEntity.class, csvContent.getBytes());

		validateUserList(userFileDataList);

		for (UserEntity user : userFileDataList) {

			Optional<UserEntity> optUser = userRepository.findById(user.getId());
			if (optUser.isPresent()) {
				UserEntity userFromDb = optUser.get();

				userFromDb.mergeUpdate(user);

				UserEntity userUpdate = userRepository.save(userFromDb);
				if (!userUpdate.equals(user)) {
					result = 1;
				}
			} else {
				userRepository.save(user);
				result = 1;
			}

		}

		return result;
	}

	public void validateUserList(List<UserEntity> userList) {

		List<String> errorList = new ArrayList<>();

		errorList.addAll(validateDuplicateUserList(userList));

		IntStream.range(0, userList.size()).forEach(idx -> {
			StringJoiner sj = validateMandatoryField(userList.get(idx));

			if (sj.length() > 0) {
				errorList.add(String.format(ResponseMessage.MSG_ERR_MANDATORY_ROW_FIELD, idx + 2, sj.toString()));
			} else {

				sj = validateFormat(userList.get(idx));

				if (sj.length() > 0) {
					errorList.add(String.format(ResponseMessage.MSG_ERR_INVALID_FORMAT_FIELD, idx + 2, sj.toString()));
				}
			}

		});

		if (errorList.size() > 0) {
			throw new BadInputException(errorList.toString());
		}

		// validate database records
		IntStream.range(0, userList.size()).forEach(idx -> {

			UserEntity validateUser = userList.get(idx);

			if (userRepository.existsByLoginAndIdNot(validateUser.getLogin(), validateUser.getId())) {
				errorList.add(
						String.format(ResponseMessage.MSG_ERR_NOT_UNIQUE_ROW_FIELD, idx + 2, validateUser.getLogin()));
			}

		});

		if (errorList.size() > 0) {
			throw new BadInputException(errorList.toString());
		}
	}

	private void saveOrUpdateUser(UserEntity user) {

		StringJoiner errorList = new StringJoiner(",");

		StringJoiner sj = validateMandatoryField(user);

		if (sj.length() > 0) {
			errorList.add(String.format(ResponseMessage.MSG_ERR_MANDATORY_FIELD, sj.toString()));
		}

		sj = validateFormat(user);

		if (sj.length() > 0) {
			errorList.add(String.format(ResponseMessage.MSG_ERR_INVALID_FIELD, sj.toString()));
		}

		if (errorList.length() > 0) {
			throw new BadInputException(errorList.toString());
		}

		Optional<UserEntity> userDb = userRepository.findById(user.getId());

		UserEntity updateableUser = user;

		if (userDb.isPresent()) {
			updateableUser = userDb.get().mergeUpdate(user);
		}

		userRepository.save(updateableUser);
	}

	private void validateSearchParam(Map<String, String> searchCriteria) {

		StringJoiner errorList = new StringJoiner(",");
		// validate default parameters : minSalary, maxSalary, offset, limit
		StringJoiner errorListParam = new StringJoiner(",");
		for (UserSearchParam searchParam : UserSearchParam.getDefaultValues()) {
			if (searchCriteria.containsKey(searchParam.name())) {
				try {
					Double.parseDouble(searchCriteria.get(searchParam.name()));
				} catch (NumberFormatException ex) {
					errorListParam.add(searchParam.name().toString());
				}
			}
		}

		if (searchCriteria.containsKey(UserSearchParam.startDateFrom.toString())) {

			if (!DateUtil.isParseable(searchCriteria.get(UserSearchParam.startDateFrom.toString()),
					DateUtil.USER_DATE_PATTERNS)) {
				errorList.add(String.format(ResponseMessage.MSG_ERR_INVALID_PARAM_FORMAT_FIELD,
						UserSearchParam.startDateFrom.toString(), DateUtil.USER_DATE_PATTERNS.toString()));
			}

		}

		if (searchCriteria.containsKey(UserSearchParam.startDateTo.toString())) {

			if (!DateUtil.isParseable(searchCriteria.get(UserSearchParam.startDateTo.toString()),
					DateUtil.USER_DATE_PATTERNS)) {
				errorList.add(String.format(ResponseMessage.MSG_ERR_INVALID_PARAM_FORMAT_FIELD,
						UserSearchParam.startDateTo.toString(), DateUtil.USER_DATE_PATTERNS.toString()));
			}

		}

		if (errorListParam.length() > 0) {
			errorList.add(String.format(ResponseMessage.MSG_ERR_INVALID_PARAM_SEARCH, errorListParam.toString()));
		}

		if (errorList.length() > 0) {
			throw new BadInputException(errorList.toString());
		}

	}

	public void validateMandatoryFieldWithError(UserEntity user) {
		StringJoiner sj = validateMandatoryField(user);

		if (sj.length() > 0) {
			throw new BadInputException(String.format(ResponseMessage.MSG_ERR_MANDATORY_FIELD, sj.toString()));
		}

	}

	private StringJoiner validateMandatoryField(UserEntity user) {

		StringJoiner sj = new StringJoiner(",");

		if (user.isNullorEmptyId()) {
			sj.add(UserEntityField.id.toString());
		}

		if (user.isNullorEmptyLogin()) {
			sj.add(UserEntityField.login.toString());
		}

		if (user.isNullorEmptyName()) {
			sj.add(UserEntityField.name.toString());
		}

		if (user.isNullSalary()) {
			sj.add(UserEntityField.salary.toString());
		}

		return sj;
	}

	public void validateFormatWithError(UserEntity user) {
		StringJoiner sj = validateFormat(user);

		if (sj.length() > 0) {
			throw new BadInputException(String.format(ResponseMessage.MSG_ERR_INVALID_FIELD, sj.toString()));
		}

	}

	private StringJoiner validateFormat(UserEntity user) {

		StringJoiner invalidField = new StringJoiner(",");

		if (!user.isValidSalary()) {
			invalidField.add(UserEntityField.salary.toString());
		}

		if (!user.isValidStartDate()) {
			invalidField.add(UserEntityField.startDate.getValue());
		}

		return invalidField;
	}

	private List<String> validateDuplicateUserList(List<UserEntity> userList) {

		Map<String, Integer> duplicateIdList = new HashMap<>();
		Map<String, Integer> duplicateLoginList = new HashMap<>();
		List<String> errorList = new ArrayList<>();

		userList.stream().forEach(item -> {
			duplicateIdList.compute(item.getId().toLowerCase(), (key, val) -> val == null ? 1 : val + 1);
			duplicateLoginList.compute(item.getLogin().toLowerCase(), (key, val) -> val == null ? 1 : val + 1);
		});

		String duplicateList = duplicateIdList.entrySet().stream().filter(map -> map.getValue() > 1)
				.map(map -> map.getKey()).collect(Collectors.joining(","));

		if (!duplicateList.trim().isEmpty()) {
			errorList.add(String.format(ResponseMessage.MSG_ERR_NOT_UNIQUE_IDS, duplicateList));
		}

		String duplicateLogin = duplicateLoginList.entrySet().stream().filter(map -> map.getValue() > 1)
				.map(map -> map.getKey()).collect(Collectors.joining(","));

		if (!duplicateLogin.trim().isEmpty()) {
			errorList.add(String.format(ResponseMessage.MSG_ERR_NOT_UNIQUE_LOGINS, duplicateLogin));
		}

		return errorList;

	}

	public void validateCreateUserWithError(UserEntity user) {

		validateExistingUserWithError(user);

		validateDuplicateUserWithError(user);
	}

	public void validateUpdateUserWithError(UserEntity user) {

		validateNotExistingUserWithError(user);

		validateDuplicateUserWithError(user);

	}

	public void validateDeleteUserWithError(UserEntity user) {
		validateNotExistingUserWithError(user);
	}

	private void validateDuplicateUserWithError(UserEntity user) {

		if (userRepository.existsByLoginAndIdNot(user.getLogin(), user.getId())) {
			throw new BadInputException(ResponseMessage.MSG_ERR_NOT_UNIQUE_EMPLOYEE_LOGIN);
		}

	}

	private void validateExistingUserWithError(UserEntity user) {

		if (userRepository.existsById(user.getId())) {
			throw new BadInputException(ResponseMessage.MSG_ERR_EMPLOYEE_EXIST);
		}
	}

	private void validateNotExistingUserWithError(UserEntity user) {

		if (!userRepository.existsById(user.getId())) {
			throw new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE);
		}
	}

	private String getAndFilterCsvData(MultipartFile file) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			return reader.lines().filter(line -> !line.startsWith("#"))
					.collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException ex) {
			throw new BadInputException(ResponseMessage.MSG_ERR_FILE_READING);
		}
	}

	private String buildWildCardString(String input) {
		return input.contains("#") ? input.replaceAll("#", "%") : "%" + input + "%";
	}

	private void validateFileSide(MultipartFile file) {
		if (file.getSize() > FILE_SIZE_LIMIT_10MB) {
			throw new BadInputException(ResponseMessage.MSG_ERR_FILE_SIZE_LIMIT_10MB);
		}
	}
}
