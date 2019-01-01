package com.philipslight.common;

/**
 * @author maysara.mohamed
 * @version 1.0
 * @since 2018-10-28
 */

public class SubCategory {

	private Integer id;
	private String subCategoryName;
	private String arSubCategoryName;
	private String extras;

	public SubCategory() {
		super();
	}

	/**
	 * @param subCategoryName
	 */
	public SubCategory(String subCategoryName, String arSubCategoryName) {
		super();
		this.subCategoryName = subCategoryName;
		this.arSubCategoryName = arSubCategoryName;
	}

	/**
	 * @param subCategoryName
	 * @param extras
	 */
	public SubCategory(String subCategoryName, String extras, String arSubCategoryName) {
		this(subCategoryName, arSubCategoryName);
		this.extras = extras;
	}

	/**
	 * @param id
	 * @param subCategoryName
	 * @param extras
	 */
	public SubCategory(Integer id, String subCategoryName, String extras, String arSubCategoryName) {
		this(subCategoryName, extras, arSubCategoryName);
		this.id = id;
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
	 * @return the extras
	 */
	public String getExtras() {
		return extras;
	}

	/**
	 * @param extras
	 *            the extras to set
	 */
	public void setExtras(String extras) {
		this.extras = extras;
	}

	/**
	 * @return the subCategoryName
	 */
	public String getSubCategoryName() {
		return subCategoryName;
	}

	/**
	 * @param subCategoryName
	 *            the subCategoryName to set
	 */
	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	/**
	 * @return the arSubCategoryName
	 */
	public String getArSubCategoryName() {
		return arSubCategoryName;
	}

	/**
	 * @param arSubCategoryName
	 *            the arSubCategoryName to set
	 */
	public void setArSubCategoryName(String arSubCategoryName) {
		this.arSubCategoryName = arSubCategoryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubCategory [id=" + id + ", subCategoryName=" + subCategoryName + ", arSubCategoryName="
				+ arSubCategoryName + ", extras=" + extras + "]";
	}

}