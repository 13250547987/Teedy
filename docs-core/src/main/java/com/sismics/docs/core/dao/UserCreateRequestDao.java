// 文件：com/sismics/docs/core/dao/UserCreateRequestDao.java
package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.AuditLogType;
import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.model.jpa.UserCreateRequest;
import com.sismics.docs.core.util.AuditLogUtil;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 处理用户注册申请的 DAO（包括游客提交、管理员审批）。
 */
public class UserCreateRequestDao {
    /**
     * 游客提交新的注册申请
     * @param req       包含 username/password/email 等字段
     * @param creatorId null 表示匿名游客，否则为登录用户 ID
     * @return 申请记录 ID
     */
    public String create(UserCreateRequest req, String creatorId) throws Exception {
        req.setId(UUID.randomUUID().toString());
        req.setStatus("PENDING");
        req.setCreateDate(new Date());
        req.setCreatorId(creatorId);

        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(req);

        AuditLogUtil.create(req, AuditLogType.CREATE, creatorId);
        return req.getId();
    }

    /**
     * 管理员查看：查询所有申请
     */
    @SuppressWarnings("unchecked")
    public List<UserCreateRequest> findAll() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("SELECT r FROM UserCreateRequest r");
        return q.getResultList();
    }

    /**
     * 管理员查看：查询指定状态的申请
     */
    public List<UserCreateRequest> findByStatus(String status) {
    return ThreadLocalContext.get()
        .getEntityManager()
        .createQuery(
            "SELECT r FROM UserCreateRequest r WHERE r.status = :status ORDER BY r.createDate DESC",
            UserCreateRequest.class
        )
        .setParameter("status", status)
        .getResultList();
    }


    /**
     * 根据 ID 获取单条申请
     */
    public UserCreateRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(UserCreateRequest.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * 管理员审批：通过（并创建实际用户）
     */
    public void approve(UserCreateRequest req, String password, String reviewerId) throws Exception {
        // 1) 创建真实用户
        UserDao userDao = new UserDao();
        com.sismics.docs.core.model.jpa.User u = new com.sismics.docs.core.model.jpa.User();
        u.setUsername(req.getUsername());
        u.setPassword(password);
        u.setEmail(req.getEmail());
        u.setStorageQuota(req.getStorageQuota());
        u.setOnboarding(true);
        u.setRoleId(Constants.DEFAULT_USER_ROLE);
        userDao.create(u, reviewerId);

        // 2) 更新申请记录
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        req.setStatus("APPROVED");
        req.setReviewerId(reviewerId);
        req.setReviewDate(new Date());
        em.merge(req);

        AuditLogUtil.create(req, AuditLogType.UPDATE, reviewerId);
    }

    /**
     * 管理员审批：拒绝
     */
    public void reject(UserCreateRequest req, String reviewerId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        req.setStatus("REJECTED");
        req.setReviewerId(reviewerId);
        req.setReviewDate(new Date());
        em.merge(req);

        AuditLogUtil.create(req, AuditLogType.UPDATE, reviewerId);
    }
}
