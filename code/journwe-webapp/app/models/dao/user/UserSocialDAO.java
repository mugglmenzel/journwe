package models.dao.user;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.feth.play.module.pa.user.AuthUser;
import models.dao.common.CommonRangeEntityDAO;
import models.dao.queries.GSIQuery;
import models.user.User;
import models.user.UserSocial;
import play.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    public UserSocial findByUserId(String userId) {
        GSIQuery q = new GSIQuery("journwe-usersocial","userId-index","userId");
        QueryResult res = q.query(userId);
        List<Map<String, AttributeValue>> items = res.getItems();
        Iterator<Map<String, AttributeValue>> itemsIter =
                items.iterator();
        String socialId = "";
        String provider = "";
        // should have found only one result
        while (itemsIter.hasNext()) {
            Map<String, AttributeValue> currentItem =
                    itemsIter.next();
            Iterator currentItemIter = currentItem.
                    keySet().iterator();
            while (currentItemIter.hasNext()) {
                String attr = (String) currentItemIter.next();
                if (attr == "socialId" ) {
                    socialId = currentItem.get(attr).getS();
                }
                if (attr == "provider" ) {
                    provider = currentItem.get(attr).getS();
                }
            }
        }
        return findBySocialId(provider,socialId);
    }

    public UserSocial findBySocialId(String provider, String socialId) {
        return get(socialId, provider);
    }

    /**
     * Add a UserSocial with the userId of oldUser. This method is used when a user is logged in with one social account, e.g. Facebook, and wants to add (link) another social account, for example Twitter.
     */
    public boolean addLinkedAccount(final AuthUser oldAuthUser, final AuthUser newAuthUser) {
        final User oldUser = new UserDAO().findByAuthUserIdentity(oldAuthUser);
        UserSocial newUserSocial = new UserSocial();
        newUserSocial.setProvider(newAuthUser.getProvider());
        newUserSocial.setSocialId(newAuthUser.getId());
        newUserSocial.setUserId(oldUser.getId());
        return save(newUserSocial);
    }

    public UserSocial create(AuthUser auth) {
            UserSocial toReturn = new UserSocial();
        toReturn.setProvider(auth.getProvider());
        toReturn.setSocialId(auth.getId());
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
