package com.nphc.helper;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

	Map<Specification<T>, Conjuntion> specList;

	public SpecificationBuilder() {
		specList = new LinkedHashMap<>();
	}

	public SpecificationBuilder(Specification<T> spec) {
		this();
		specList.put(spec, null);
	}
	
	public SpecificationBuilder<T> with(Specification<T> spec, Conjuntion conj) {

		this.specList.put(spec, conj);
		return this;
	}

	public Specification<T> build() {

		if (specList.size() == 0) {
			return null;
		}

		int idx = 0;

		Specification<T> spec = null;

		for (Map.Entry<Specification<T>, Conjuntion> entry : specList.entrySet()) {

			if (idx == 0) {
				spec = Specification.where(entry.getKey());
			} else {
				switch (entry.getValue()) {
				case OR:
					spec = spec.or(entry.getKey());
					break;

				case AND:
					spec = spec.and(entry.getKey());
					break;
				}
			}
			idx ++;
		}

		return spec;
	}
	
	public int size() {
		return specList.size();
	}

	public enum Conjuntion {
		OR, AND;
	}

}
