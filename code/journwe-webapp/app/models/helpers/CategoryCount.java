package models.helpers;

import models.Category;

public class CategoryCount {

	private Category category;
	
	private Integer count;

	/**
	 * @param category
	 * @param count
	 */
	public CategoryCount(Category category, Integer count) {
		super();
		this.category = category;
		this.count = count;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
	
	
}
