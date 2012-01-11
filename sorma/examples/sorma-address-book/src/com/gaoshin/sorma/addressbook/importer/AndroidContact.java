package com.gaoshin.sorma.addressbook.importer;

import com.gaoshin.sorma.annotation.Column;
import com.gaoshin.sorma.annotation.Table;

@Table(
    columns={
            @Column(field = "id",               name="_id"),
            @Column(field = "displayName",      name="display_name"),
            @Column(field = "photoId",          name="photo_id"),
            @Column(field = "inVisibleGroup",   name="in_visible_group"),
            @Column(field = "hasPhoneNumber",   name="has_phone_number"),
            @Column(field = "lookupKey",        name="lookup"),
            @Column(field = "timesContacted",   name="times_contacted"),
            @Column(field = "lastTimeContacted",name="last_time_contacted"),
            @Column(field = "starred",          name="starred"),
    }
)
public class AndroidContact {
	private long id;
	private String displayName;
	private Integer photoId;
	private Boolean inVisibleGroup;
	private Boolean hasPhoneNumber;
	private String lookupKey;
	private int timesContacted;
	private Long lastTimeContacted;
	private Boolean starred;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    public String getDisplayName() {
        return displayName;
    }

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Integer photoId) {
		this.photoId = photoId;
	}

	public Boolean getInVisibleGroup() {
		return inVisibleGroup;
	}

	public void setInVisibleGroup(Boolean inVisisbleGroup) {
		this.inVisibleGroup = inVisisbleGroup;
	}

    public Boolean getHasPhoneNumber() {
        return hasPhoneNumber;
    }

    public boolean hasPhoneNumber() {
        return hasPhoneNumber!=null && hasPhoneNumber==true;
    }

	public void setHasPhoneNumber(Boolean hasPhoneNumber) {
		this.hasPhoneNumber = hasPhoneNumber;
	}

	public String getLookupKey() {
		return lookupKey;
	}

	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}

	public int getTimesContacted() {
		return timesContacted;
	}

	public void setTimesContacted(int timesContacted) {
		this.timesContacted = timesContacted;
	}

	public void setStarred(Boolean starred) {
		this.starred = starred;
	}

	public Boolean getStarred() {
		return starred == null ? false : starred;
	}

	public void setLastTimeContacted(Long lastTimeContacted) {
		this.lastTimeContacted = lastTimeContacted;
	}

	public Long getLastTimeContacted() {
		return lastTimeContacted == null ? 0l : lastTimeContacted;
	}

}