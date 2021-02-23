package com.nphc.model;

import java.util.List;

public class SearchResult {

	private List<UserEntity> results;

	public SearchResult(List<UserEntity> result) {
		this.results = result;
	}
	
	public List<UserEntity> getResults() {
		return results;
	}

	public void setResult(List<UserEntity> result) {
		this.results = result;
	}
	
	
}
