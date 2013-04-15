package models.helpers;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshaller;
import models.EAdventurerParticipation;
import models.EUserRole;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 15.04.13
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
public class EnumMarshaller<T extends Enum> implements DynamoDBMarshaller<T> {

    @Override
    public String marshall(T e) {
        return e.name();
    }

    @Override
    public T unmarshall(Class<T> clazz, String eS) {
        return Enum.valueOf(clazz, eS);
    }
}
