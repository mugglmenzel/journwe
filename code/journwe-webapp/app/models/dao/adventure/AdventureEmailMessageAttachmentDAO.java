package models.dao.adventure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import models.adventure.email.MessageAttachment;
import models.dao.common.CommonRangeEntityDAO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 28.01.14
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class AdventureEmailMessageAttachmentDAO extends CommonRangeEntityDAO<MessageAttachment> {

    protected AdventureEmailMessageAttachmentDAO() {
        super(MessageAttachment.class);
    }

    public List<MessageAttachment> allOfMessage(String msgId) {
        MessageAttachment attach = new MessageAttachment();
        attach.setMessageId(msgId);

        DynamoDBQueryExpression query = new DynamoDBQueryExpression().withHashKeyValues(attach);

        return pm.query(MessageAttachment.class, query);
    }

}
