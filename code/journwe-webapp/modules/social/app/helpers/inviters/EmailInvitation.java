package helpers.inviters;

/**
 * Created by mugglmenzel on 05/05/14.
 */
public class EmailInvitation extends AbstractInvitation {

    public EmailInvitation() {
    }

    public String getInviteeName(String inviteeContact) throws Exception {
        return null;
    }

    public String getInviteeEmail(String inviteeContact) throws Exception {
        return inviteeContact;
    }
}
