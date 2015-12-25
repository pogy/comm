package com.vipkid.repository;

import com.vipkid.model.Channel;
import com.vipkid.model.SourceChannel;
import com.vipkid.model.SourceChannel_;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ChannelRepository extends BaseRepository<Channel> {
    public ChannelRepository() {
        super(Channel.class);
    }

    public List<Channel> list(String search, Integer start, Integer length) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT cn FROM Channel cn where 0=0");
        if (StringUtils.isNotBlank(search)) {
            sql.append(" AND cn.sourceName LIKE ?1 OR cn.channelName1 LIKE ?2 OR cn.channelName2 LIKE ?3"
                    + "  OR cn.channelName3 LIKE ?4 OR cn.channelName4 LIKE ?5 OR cn.channelName5 LIKE ?6");
        }
        sql.append(" order by cn.createTime desc");
        TypedQuery<Channel> query = entityManager.createQuery(sql.toString(), Channel.class);
        if (StringUtils.isNotBlank(search)) {
            query.setParameter(1, "%" + search + "%");
            query.setParameter(2, "%" + search + "%");
            query.setParameter(3, "%" + search + "%");
            query.setParameter(4, "%" + search + "%");
            query.setParameter(5, "%" + search + "%");
            query.setParameter(6, "%" + search + "%");
        }
        query.setFirstResult(start);
        query.setMaxResults(length);

        if (query.getResultList() != null) {
            if (query.getResultList().size() > 0) {
                return query.getResultList();
            } else {
                return null;
            }
        }
        return null;
    }

    public long count(String search) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  COUNT(DISTINCT cn) FROM Channel cn where 0=0 ");
        if (StringUtils.isNotBlank(search)) {
            sql.append(" and cn.sourceName LIKE ?1 OR cn.channelName1 LIKE ?2 OR cn.channelName2 LIKE ?3"
                    + "  OR cn.channelName3 LIKE ?4 OR cn.channelName4 LIKE ?5 OR cn.channelName5 LIKE ?6");
        }
        TypedQuery<Long> query = entityManager.createQuery(sql.toString(), Long.class);
        query.setMaxResults(1);
        if (StringUtils.isNotBlank(search)) {
            query.setParameter(1, "%" + search + "%");
            query.setParameter(2, "%" + search + "%");
            query.setParameter(3, "%" + search + "%");
            query.setParameter(4, "%" + search + "%");
            query.setParameter(5, "%" + search + "%");
            query.setParameter(6, "%" + search + "%");
        }
        return (long) query.getSingleResult();
    }

    public Channel findChannelByName(String name1, String name2, String name3, String name4, String name5) {
        String sql = "SELECT cm FROM Channel cm" + " WHERE cm.channelName1 = :name1 AND  cm.channelName2 = :name2 AND  cm.channelName3 = :name3 AND  cm.channelName4 = :name4 AND  cm.channelName5 = :name5";
        TypedQuery<Channel> typedQuery = entityManager.createQuery(sql, Channel.class);
        typedQuery.setParameter("name1", name1);
        typedQuery.setParameter("name2", name2);
        typedQuery.setParameter("name3", name3);
        typedQuery.setParameter("name4", name4);
        typedQuery.setParameter("name5", name5);

        typedQuery.setMaxResults(1);
        if (typedQuery.getResultList() != null) {
            if (typedQuery.getResultList().size() > 0) {
                return typedQuery.getResultList().get(0);
            } else {
                return null;
            }
        }
        return null;
    }

    public Channel findById(long channelId) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT cn FROM Channel cn WHERE cn.id = :channelId");
        // typed query and query are different
        TypedQuery<Channel> query = entityManager.createQuery(sql.toString(), Channel.class);
        query.setParameter("channelId", channelId);

        if (query.getResultList() != null) {
            if (query.getResultList().size() > 0) {
                return query.getResultList().get(0);
            } else {
                return null;
            }
        }
        return null;
    }

    public Channel findBySourceName(String name) {
        String sql = "SELECT c FROM Channel c WHERE c.sourceName = :sourceName";
        TypedQuery<Channel> typedQuery = entityManager.createQuery(sql, Channel.class);
        typedQuery.setParameter("sourceName", name);

        typedQuery.setMaxResults(1);
        if (typedQuery.getResultList() != null) {
            if (typedQuery.getResultList().size() > 0) {
                return typedQuery.getResultList().get(0);
            } else {
                return null;
            }
        }
        return null;
    }

    public List<String> getChannelList() {
        String sql = "SELECT c.sourceName FROM Channel c WHERE c.sourceName IS NOT NULL";
        TypedQuery<String> typedQuery = entityManager.createQuery(sql, String.class);

        return typedQuery.getResultList();
    }

    public Channel findByOldSource(String oldSourceName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SourceChannel> criteriaQuery = criteriaBuilder.createQuery(SourceChannel.class).distinct(true);
        Root<SourceChannel> sourceChannel = criteriaQuery.from(SourceChannel.class);
        List<Predicate> andPredicates = new LinkedList<Predicate>();
        andPredicates.add(criteriaBuilder.equal(sourceChannel.get(SourceChannel_.sourceName), oldSourceName));
        Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
        criteriaQuery.where(andPredicate);
        TypedQuery<SourceChannel> typedQuery = entityManager.createQuery(criteriaQuery);
        List<SourceChannel> sourceChannelList = typedQuery.getResultList();
        if (sourceChannelList == null || sourceChannelList.size() == 0) {
            return null;
        } else {
            return sourceChannelList.get(0).getChannel();
        }
    }
}
