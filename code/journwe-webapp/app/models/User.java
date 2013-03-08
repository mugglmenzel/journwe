package models;

import play.data.validation.Constraints.Required;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshaller;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;

import controllers.dao.UserDAO;

@DynamoDBTable(tableName = "journwe-user")
public class User {

	private String id;

	@Required
	private String name;

	@Required
	private String email;

	private boolean active;

	private boolean emailValidated;

	private String provider;

	private String providerUserId;

	private UserRole role = UserRole.USER;

	/**
	 * @return the id
	 */
	@DynamoDBAttribute
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the provider user id
	 */
	@DynamoDBHashKey(attributeName = "providerUserId")
	@DynamoDBAttribute
	public String getProviderUserId() {
		return providerUserId;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setProviderUserId(String user) {
		this.providerUserId = user;
	}

	/**
	 * @return the name
	 */
	@DynamoDBAttribute
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
	 * @return the name
	 */
	@DynamoDBAttribute
	public String getEmail() {
		return email;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the active
	 */
	@DynamoDBAttribute
	public boolean getActive() {
		return active;
	}

	/**
	 * @param name
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the active
	 */
	@DynamoDBAttribute
	public boolean getEmailValidated() {
		return emailValidated;
	}

	/**
	 * @param name
	 *            the active to set
	 */
	public void setEmailValidated(boolean validated) {
		this.emailValidated = validated;
	}

	/**
	 * @return the name
	 */
	@DynamoDBAttribute
	public String getProvider() {
		return provider;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * @return the role
	 */
	@DynamoDBMarshalling(marshallerClass = UserRoleMarshaller.class)
	public UserRole getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(UserRole role) {
		this.role = role;
	}

	/**
	 * public static final Finder<Long, User> find = new Finder<Long, User>(
	 * Long.class, User.class);
	 **/

	public static boolean existsByAuthUserIdentity(
			final AuthUserIdentity identity) {
		return getAuthUserFind(identity) != null;
	}

	private static User getAuthUserFind(final AuthUserIdentity identity) {
		return new UserDAO().get(identity.getId());
	}

	public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		return getAuthUserFind(identity);
	}

	public static User create(final AuthUser authUser) {
		final User user = new User();
		user.setProviderUserId(authUser.getId());
		user.setActive(true);
		user.setProvider(authUser.getProvider());

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.setEmail(identity.getEmail());
			user.setEmailValidated(false);
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				user.setName(name);
			}
		}

		new UserDAO().save(user);
		return user;
	}
	
	public static class UserRoleMarshaller implements DynamoDBMarshaller<UserRole> {

		@Override
		public String marshall(UserRole role) {
			return role.name();
		}

		@Override
		public UserRole unmarshall(Class<UserRole> roleClass, String roleString) {
			return UserRole.valueOf(roleString);
		}
		
	}

}
