package com.nphc.repository;

import java.util.Date;

import org.springframework.data.jpa.domain.Specification;

import com.nphc.model.UserEntity;
import com.nphc.model.UserEntity_;

public class UserSpecification {


	public static Specification<UserEntity> lowerSalaryRange(Double lowerLimit) {
		return (root, query, builder) -> {
			return builder.greaterThanOrEqualTo(root.get(UserEntity_.salary), lowerLimit);
		};
	}

	public static Specification<UserEntity> upperSalaryRange(Double upperLimit) {
		return (root, query, builder) -> {
			return builder.lessThan(root.get(UserEntity_.salary), upperLimit);
		};
	}
	
	public static Specification<UserEntity> wildCardName(String matchString) {
		return (root, query, builder) -> {
			return builder.like(builder.lower(root.get(UserEntity_.name)), matchString.toLowerCase());
		};
	}

	public static Specification<UserEntity> wildCardId(String matchString) {
		return (root, query, builder) -> {
			return builder.like(builder.lower(root.get(UserEntity_.id)), matchString.toLowerCase());
		};
	}
	
	public static Specification<UserEntity> wildCardLogin(String matchString) {
		return (root, query, builder) -> {
			return builder.like(builder.lower(root.get(UserEntity_.login)), matchString.toLowerCase());
		};
	}
	
	public static Specification<UserEntity> lowerRangeStartDate(Date matchDate) {
		return (root, query, builder) -> {
			return builder.greaterThanOrEqualTo(root.get(UserEntity_.startDate), matchDate);
		};
	}
	
	public static Specification<UserEntity> upperRangeStartDate(Date matchDate) {
		return (root, query, builder) -> {
			return builder.lessThanOrEqualTo(root.get(UserEntity_.startDate), matchDate);
		};
	}
}
