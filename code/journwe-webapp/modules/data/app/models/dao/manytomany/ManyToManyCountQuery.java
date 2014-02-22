package models.dao.manytomany;

import com.amazonaws.services.dynamodbv2.model.QueryResult;
import models.dao.manytomany.ManyToManyDAO;

/**
 * Created by markus on 16/01/14.
 */
public class ManyToManyCountQuery<M,N> {

    private ManyToManyDAO<M,N> dao;

    public ManyToManyCountQuery(Class<M> clazzM, Class<N> clazzN) {
        dao = new ManyToManyDAO<M,N>(clazzM, clazzN);
    }

    /**
     * Count number of relations with hashKey of type M oject
     * @param hashKey
     * @return
     */
    public Integer countM(String hashKey) {
        QueryResult queryResult = dao.listRelations(hashKey, "", 0, true);
        return queryResult.getCount();
    }

    /**
     * Count number of relations with hashKey of type N oject
     * @param hashKey
     * @return
     */
    public Integer countN(String hashKey) {
        QueryResult queryResult = dao.listRelations(hashKey, "", 0, false);
        return queryResult.getCount();
    }

}
