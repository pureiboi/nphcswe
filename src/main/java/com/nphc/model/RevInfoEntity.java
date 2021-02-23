package com.nphc.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@Table(name = "REV_INFO")
@RevisionEntity
@AttributeOverrides({
    @AttributeOverride(name = "timestamp", column = @Column(name = "rev_timestamp")),
    @AttributeOverride(name = "id", column = @Column(name = "revision_id"))})
public class RevInfoEntity extends DefaultRevisionEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1978040345090693964L;

}
