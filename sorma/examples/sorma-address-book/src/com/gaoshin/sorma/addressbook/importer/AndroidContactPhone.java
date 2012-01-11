package com.gaoshin.sorma.addressbook.importer;

public class AndroidContactPhone {
    private Long contactId;
	private String number;
	private int type;
	private String label;
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Long getContactId() {
        return contactId;
    }
}
