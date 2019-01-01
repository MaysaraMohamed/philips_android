package com.philipslight.common;

import java.util.List;

public class CategoriesWithSub {

	private Integer id;
	private String categoryName;
	private String arCategoryName;
	private String extras;

	private List<SubCategory> subCategories;

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
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName
	 *            the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the arCategoryName
	 */
	public String getArCategoryName() {
		return arCategoryName;
	}

	/**
	 * @param arCategoryName
	 *            the arCategoryName to set
	 */
	public void setArCategoryName(String arCategoryName) {
		this.arCategoryName = arCategoryName;
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
	 * @return the subCategories
	 */
	public List<SubCategory> getSubCategories() {
		return subCategories;
	}

	/**
	 * @param subCategories
	 *            the subCategories to set
	 */
	public void setSubCategories(List<SubCategory> subCategories) {
		this.subCategories = subCategories;
	}

	@Override
	public String toString() {
		return "CategoriesWithSub{" +
				"id=" + id +
				", categoryName='" + categoryName + '\'' +
				", arCategoryName='" + arCategoryName + '\'' +
				", extras='" + extras + '\'' +
				", subCategories=" + subCategories +
				'}';
	}
}
