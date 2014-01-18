package models.dao.manytomany;

import models.dao.manytomany.ManyToManyDAO;

import java.util.List;

/**
 * Created by markus on 15/01/14.
 */
public class ManyToManyListQuery<M,N> {

    private ManyToManyDAO<M,N> dao;

    public ManyToManyListQuery(Class<M> clazzM, Class<N> clazzN) {
        dao = new ManyToManyDAO<M,N>(clazzM, clazzN);
    }

    public List<M> listM(String hashKey, String lastRangeKey, int limit) {
          return dao.listM(hashKey, lastRangeKey, limit);
    }

    public List<N> listN(String hashKey, String lastRangeKey, int limit) {
        return dao.listN(hashKey, lastRangeKey, limit);
    }

}
