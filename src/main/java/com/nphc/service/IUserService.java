package com.nphc.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.nphc.model.UserEntity;

public interface IUserService {

	public UserEntity getUser(String id);
	
	public void updateUser(UserEntity user);
	
	public void saveUser(UserEntity user);
	
	public void deleteUser(String id);
	
	public List<UserEntity> searchUserList(Map<String, String> searchCriteria);
	
	public Integer uploadAndSaveUser(MultipartFile file);
}
