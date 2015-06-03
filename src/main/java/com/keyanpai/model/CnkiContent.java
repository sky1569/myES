package com.keyanpai.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;




@Entity
@Table(name="t_cnki")
public class CnkiContent implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9112349745857359471L;

	private String id;
	private String name;
	
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(length = 50 ,nullable = false ,unique = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString(){
		return "test: " + id + "," + name;
	}
	
	
}
