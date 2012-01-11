package com.gaoshin.sorma.examples.addressbook;

public class Phone {
	private Long id;
	private Long contactId;
	private String phone;
	private PhoneNumberType type;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public PhoneNumberType getType() {
		return type;
	}

	public void setType(PhoneNumberType type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
}
