package com.nphc.helper;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

public enum UserSearchParam {

	minSalary("0"),
	maxSalary("4000.00"),
	offset("0"),
	limit(String.valueOf(Integer.MAX_VALUE)),
	startDateFrom(null),
	startDateTo(null),
	name(null),
	id(null),
	login(null),
	sort(Sort.Direction.ASC.toString());
	;
	
	private String defaultValue;
	
	private UserSearchParam(String defaultVal) {
		this.defaultValue = defaultVal;
	};
	
	public String getValue() {
		return this.defaultValue;
	}
	
	public static List<UserSearchParam> getDefaultValues() {
		EnumSet<UserSearchParam> set = EnumSet.range(minSalary, limit);
		return set.stream().collect(Collectors.toList());
	}
	
	public static List<UserSearchParam> getMoreParamValues() {
		EnumSet<UserSearchParam> set = EnumSet.range(startDateFrom, sort);
		return set.stream().collect(Collectors.toList());
	}
	
}



