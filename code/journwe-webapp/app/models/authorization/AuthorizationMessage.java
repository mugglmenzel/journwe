package models.authorization;

import play.mvc.Result;

import static play.mvc.Results.badRequest;

public class AuthorizationMessage {

    public static Result notAuthorizedResponse() {
        return badRequest(EAuthorizationMessage.NO_AUTHORIZATION.toString());
    }
}
