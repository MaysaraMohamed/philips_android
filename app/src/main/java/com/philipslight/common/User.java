package com.philipslight.common;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author maysara.mohamed
 * @version 1.0
 * @since 2018-10-06
 */

public class User implements Serializable {

	/**
	 * 
	 */
	private String userName;
	private String password;
	private String confirmPassword;
	private String newPassword;
	private Date birthDate;
	private String name;
	private String title;
	private String mail;
	private String phone;
	private String address;
	private String profileImage;
	private String language;
	private boolean keepLogin;
	// 1 means pyramids, 2 means Nabir Besheer.
	private String userType;

	private Integer id;

	private Set<Location> location;

	public User() {

	}

	public User(String name, String userName, String profileImage, String phone, String mail, String userType) {
		super();
		this.name = name;
		this.userName = userName;
		this.profileImage = profileImage;
		this.phone = phone;
		this.mail = mail;
		this.userType = userType;
	}

	public User(String name, String userName, String password,String confirmPassword, String phone, String mail, Date birthDate) {
		super();
		this.name = name;
		this.userName = userName;
		this.mail = mail;
		this.phone = phone;
		this.birthDate = birthDate;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	/**
	 * @param userName
	 * @param password
	 * @param birthDate
	 * @param name
	 * @param title
	 * @param mail
	 * @param phone
	 * @param id
	 * @param address
	 */
	public User(String userName, String password, String confirmPassword,  Date birthDate, String name, String title, String mail, String phone,
			Integer id, String address) {
		this(name, userName, password,confirmPassword,  phone, mail, birthDate);
		this.title = title;
		this.address = address;
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail
	 *            the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
			this.password = password;
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate
	 *            the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the profileImage
	 */
	public String getProfileImage() {
		return profileImage;
	}

	/**
	 * @param profileImage
	 *            the profileImage to set
	 */
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	/**
	 * @return the location
	 */
	public Set<Location> getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Set<Location> location) {
		this.location = location;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isKeepLogin() {
		return keepLogin;
	}

	public void setKeepLogin(boolean keepLogin) {
		this.keepLogin = keepLogin;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + ", birthDate=" + birthDate + ", name=" + name
				+ ", title=" + title + ", mail=" + mail + ", phone=" + phone + ", address=" + address
				+ ", profileImage=" + profileImage + ", id=" + id + ", userType=" + userType +"]";
	}

}