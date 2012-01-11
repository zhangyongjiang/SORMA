package com.gaoshin.sorma.examples.addressbook;

import com.gaoshin.sorma.annotation.Table;

@Table(
	name="contact",
	keyColumn="id",
	autoId=true,
	create="create table if not exists contact (" 
			+ " id INTEGER primary key autoincrement"
			+ ", firstName text"
			+ ", lastName text"
			+ ", married tinyint"
			+ ", gender text"
			+ ")"
)
public class Contact {
	private Integer id;
	private String firstName;
	private String lastName;
	private boolean married;
	private Gender gender;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}
}
