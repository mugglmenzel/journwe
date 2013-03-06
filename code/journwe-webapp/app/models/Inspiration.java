package models;

import play.data.validation.Constraints.Required;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="journwe-inspiration")
public class Inspiration {
	
	private String inspirationCategoryId;
	
	private String id;
	
	@Required
	private String name;

	private String description;
	
	private String image;

	/**
	 * @return the inspirationCategoryId
	 */
	@DynamoDBHashKey(attributeName="inspirationCategoryId")
	public String getInspirationCategoryId() {
		return inspirationCategoryId;
	}

	/**
	 * @param inspirationCategoryId the inspirationCategoryId to set
	 */
	public void setInspirationCategoryId(String inspirationCategoryId) {
		this.inspirationCategoryId = inspirationCategoryId;
	}

	/**
	 * @return the id
	 */
	@DynamoDBRangeKey(attributeName="id")
	@DynamoDBAutoGeneratedKey
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	@DynamoDBAttribute
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	@DynamoDBAttribute
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the image
	 */
	@DynamoDBAttribute
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Inspiration [inspirationCategoryId=" + inspirationCategoryId
				+ ", id=" + id + ", name=" + name + ", description="
				+ description + ", image=" + image + "]";
	}
	
	
	
}
