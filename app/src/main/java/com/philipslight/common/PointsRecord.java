package com.philipslight.common;

import java.util.Date;

/**
 * @author maysara.mohamed
 * @version 1.0
 * @since 2018-10-28
 */

public class PointsRecord {

	private double points;
	private double usedPoints;
	private Date pointsDate;
	private String userName;

	/*
	 */
	public PointsRecord() {
		super();
	}

	/**
	 * @param points
	 * @param usedPoints
	 * @param pointsDate
	 * @param userName
	 */
	public PointsRecord(double points, double usedPoints, Date pointsDate, String userName) {
		super();
		this.points = points;
		this.usedPoints = usedPoints;
		this.pointsDate = pointsDate;
		this.userName = userName;
	}

	/**
	 * @return the points
	 */
	public double getPoints() {
		return points;
	}

	/**
	 * @param points
	 *            the points to set
	 */
	public void setPoints(double points) {
		this.points = points;
	}

	/**
	 * @return the usedPoints
	 */
	public double getUsedPoints() {
		return usedPoints;
	}

	/**
	 * @param usedPoints
	 *            the usedPoints to set
	 */
	public void setUsedPoints(double usedPoints) {
		this.usedPoints = usedPoints;
	}

	/**
	 * @return the date
	 */
	// to use date only.
	public Date getPointsDate() {
		return pointsDate;
	}

	/**
	 * @param -date
	 *            the date to set
	 */
	public void setPointsDate(Date pointsDate) {
		this.pointsDate = pointsDate;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PointsRecord [points=" + points + ", usedPoints=" + usedPoints + ", pointsDate=" + pointsDate
				+ ", userName=" + userName + "]";
	}

}