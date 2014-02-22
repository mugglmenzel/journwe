package models.dao.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.dao.common.CommonRangeEntityDAO;
import models.user.User;
import models.user.UserSocial;
import play.Logger;

import java.util.List;

public class UserSocialDAO extends CommonRangeEntityDAO<UserSocial> {

    public UserSocialDAO() {
        super(UserSocial.class);
    }

    /**
     * Find UserSocial by its userId (Global Secondary Index).
     *
     * @param userId
     * @return
     */
    public List<UserSocial> findByUserId(String userId) {
        UserSocial hashKeyObject = new UserSocial();
        hashKeyObject.setUserId(userId);
        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withConsistentRead(false).withIndexName("userId-index").withHashKeyValues(hashKeyObject);
        return pm.query(UserSocial.class, query);
    }

    /**
     * Find UserSocial by its userId (Global Secondary Index).
     *
     * @param userId
     * @return
     */
    public UserSocial findByUserIdAndProvider(String provider, String userId) {
        for (UserSocial us : findByUserId(userId))
            if (provider.equals(us.getProvider()))
                return us;
        return null;
    }

    public UserSocial findBySocialId(String provider, String socialId) {
        return get(socialId, provider);
    }

    /**
     * Add a UserSocial with the userId of oldUser. This method is used when a user is logged in with one social account, e.g. Facebook, and wants to add (link) another social account, for example Twitter.
     */
    public boolean addLinkedAccount(final User user, final String provider, final String socialId) {
        UserSocial newUserSocial = new UserSocial();
        newUserSocial.setProvider(provider);
        newUserSocial.setSocialId(socialId);
        newUserSocial.setUserId(user.getId());
        return save(newUserSocial);
    }

    public UserSocial create(final String provider, final String socialId) {
        UserSocial toReturn = new UserSocial();
        toReturn.setProvider(provider);
        toReturn.setSocialId(socialId);
        save(toReturn);
        return toReturn;
    }

    public void saveFacebookAccessToken(final String facebookId,
                                        final String accessToken) {
        try {
//		UserSocial hashKey = new UserSocial();
//		hashKey.setProvider("facebook");
//		UserSocial userSocial = get(hashKey, facebookId);
            UserSocial userSocial = findBySocialId("facebook", facebookId);
            if (userSocial != null) {
                userSocial.setAccessToken(accessToken);
                save(userSocial);
            } else {
                throw new Exception("Fuck it, something went wrong. Wanted to saveOld a Facebook access token in our database. But: UserSocial object is null.");
            }
        } catch (Exception e) {
            Logger.error(e.getLocalizedMessage());
        }


    }
}
