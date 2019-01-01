package com.philipslight.common;

import java.io.Serializable;


/**
 * @author maysara.mohamed
 * @version 1.0
 * @since 2018-10-06
 */

public class Location implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private Double latitude;
	private Double longitude;
	private Integer id;

	private User user;

	/**
	 * @param latitude
	 * @param longitude
	 */
	public Location(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location() {
		super();
	}

	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Location [latitude=" + latitude + ", longitude=" + longitude + ", user=" + user + ", id=" + id + "]";
	}

}