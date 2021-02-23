package com.nphc.model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nphc.helper.UserDateDeserialiser;

@Entity()
@Audited
@Table(name = "TB_USER")
@EntityListeners(AuditingEntityListener.class)
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public class UserEntity {

	private static DecimalFormat df2 = new DecimalFormat("#.##");

	@Id
	private String id;

	private String login;

	private String name;

	@Column(precision = 2, scale = 11)
	private Double salary;

	@Column(name = "START_DATE")
	@JsonDeserialize(using = UserDateDeserialiser.class)
	private Date startDate;

	@Version
	@JsonIgnore
	private long version;

	@Column(name = "CREATED_BY", updatable = false)
	@JsonIgnore
	@CreatedBy
	private String createdBy;

	@Column(name = "CREATED_DATE", updatable = false)
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	@LastModifiedBy
	@JsonIgnore
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date updatedDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {

		if (salary != null) {
			df2.setRoundingMode(RoundingMode.DOWN);
			salary = Double.valueOf(df2.format(salary));
		}
		this.salary = salary;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@JsonIgnore
	public boolean isNullorEmptyId() {
		if (id == null || id.trim().isEmpty()) {
			return true;
		}

		return false;
	}

	@JsonIgnore
	public boolean isNullorEmptyLogin() {
		if (login == null || login.trim().isEmpty()) {
			return true;
		}

		return false;
	}

	@JsonIgnore
	public boolean isNullorEmptyName() {
		if (name == null || name.trim().isEmpty()) {
			return true;
		}

		return false;
	}

	@JsonIgnore
	public boolean isNullSalary() {
		if (salary == null) {
			return true;
		}

		return false;
	}

	@JsonIgnore
	public boolean isValidSalary() {
		if (isNullSalary() || salary < 0) {
			return false;
		}

		return true;
	}

	@JsonIgnore
	public boolean isValidStartDate() {
		if (startDate == null) {
			return false;
		}
		return true;
	}

	@JsonIgnore
	public UserEntity mergeUpdate(UserEntity user) {

		if (!user.isNullorEmptyLogin()) {
			this.setLogin(user.getLogin());
		}

		if (!user.isNullorEmptyName()) {
			this.setName(user.getName());
		}

		if (!user.isNullSalary()) {
			this.setSalary(user.getSalary());
		}

		if (user.isValidStartDate()) {
			this.setStartDate(user.getStartDate());
		}

		return this;
	}

	@JsonIgnore
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append("id=" + this.id + ",");
		sb.append("login=" + this.login + ",");
		sb.append("name=" + this.name + ",");
		sb.append("salary=" + this.salary + ",");
		sb.append("startDate=" + this.startDate + ",");
		sb.append("version=" + this.version + ",");
		sb.append("createdBy=" + this.createdBy + ",");
		sb.append("createdDate=" + this.createdDate + ",");
		sb.append("updatedBy=" + this.updatedBy + ",");
		sb.append("updatedDate=" + this.updatedDate + ",");
		sb.append("]");
		return sb.toString();
	}

	@JsonIgnore
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof UserEntity)) {
			return false;
		}

		UserEntity user = (UserEntity) o;

		return Objects.equals(this.id, user.getId()) 
				&& Objects.equals(this.login, user.getLogin())
				&& Objects.equals(this.name, user.getName())
				&& Objects.equals(this.salary, user.getSalary())
				&& Objects.equals(this.startDate, user.getStartDate()) ;
	}
	
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.id !=null ? this.id.hashCode() : 0);
        result = 31 * result + (this.name !=null ? this.name.hashCode() : 0);
        result = 31 * result + (this.login !=null ? this.login.hashCode() : 0);
        result = 31 * result + (this.salary !=null ? this.salary.hashCode() : 0);
        result = 31 * result + (this.startDate !=null ? this.startDate.hashCode() : 0);
        return result;
    }
}
