package models.dao.user;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.dao.common.CommonRangeEntityDAO;
import models.user.ETokenType;
import models.user.TokenAction;
import models.user.User;
import play.Logger;

import java.util.Date;
import java.util.List;

public class TokenActionDAO extends CommonRangeEntityDAO<TokenAction> {

    /**
     * Verification time frame (until the user clicks on the link in the email)
     * in seconds
     * Defaults to one week
     */
    private final static long VERIFICATION_TIME = 7 * 24 * 3600;

    public TokenActionDAO() {
        super(TokenAction.class);
    }

    public TokenAction findByToken(final String token, final ETokenType type) {
        Logger.debug("TokenActionDAO ETokenType.name: "+type.name());
        return get(type.name(),token);
    }

    public void deleteByUser(final User u, final ETokenType type) {
        TokenAction ta = new TokenAction();
        ta.setType(type);
        DynamoDBQueryExpression<TokenAction> queryExpression = new DynamoDBQueryExpression<TokenAction>()
                .withHashKeyValues(ta);
        List<TokenAction> results = pm.query(TokenAction.class, queryExpression);
        if(results.size()>1)
            Logger.warn("Querying TokenAction.deleteByUser returned "+results.size()+" results when it should only return 1 result for user "+u.getId());
        if(results.size()>0)
            delete(results.get(0));
        else
            Logger.debug("TokenAction.deleteByUser query returned 0 results. No TokenAction was deleted.");
    }

    public TokenAction create(final ETokenType type, final String token,
                                     final User targetUser) {
        final TokenAction ta = new TokenAction();
        ta.setTargetUserId(targetUser.getId());
        ta.setToken(token);
        ta.setType(type);
        final Date created = new Date();
        ta.setCreated(created);
        ta.setExpires(new Date(created.getTime() + VERIFICATION_TIME * 1000));
        save(ta);
        return ta;
    }

}
