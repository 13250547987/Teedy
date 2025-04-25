package com.sismics.docs.core.dao.jpa;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.dao.DocumentDao;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.util.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * DocumentDao JPA integration test.
 */
public class DocumentDaoJpaTest extends BaseTransactionalTest {

    @Test
    public void testCreateAndGetById() {
        DocumentDao documentDao = new DocumentDao();
        Document document = new Document();
        document.setTitle("Test Doc");
        document.setUserId("testUserId");
        document.setCreateDate(new Date());

        String id = documentDao.create(document, "testUserId");
        TransactionUtil.commit();

        Document loaded = documentDao.getById(id);
        Assert.assertNotNull(loaded);
        Assert.assertEquals("Test Doc", loaded.getTitle());
        Assert.assertEquals("testUserId", loaded.getUserId());
    }

    @Test
    public void testUpdate() {
        DocumentDao documentDao = new DocumentDao();
        Document document = new Document();
        document.setTitle("Original Title");
        document.setUserId("updateUser");
        document.setCreateDate(new Date());

        String id = documentDao.create(document, "updateUser");
        TransactionUtil.commit();

        document.setId(id);
        document.setTitle("Updated Title");

        documentDao.update(document, "updateUser");
        TransactionUtil.commit();

        Document updated = documentDao.getById(id);
        Assert.assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    public void testFindByUserId() {
        DocumentDao documentDao = new DocumentDao();

        Document d1 = new Document();
        d1.setTitle("Doc1");
        d1.setUserId("user123");
        d1.setCreateDate(new Date());
        documentDao.create(d1, "user123");

        Document d2 = new Document();
        d2.setTitle("Doc2");
        d2.setUserId("user123");
        d2.setCreateDate(new Date());
        documentDao.create(d2, "user123");

        Document d3 = new Document();
        d3.setTitle("Doc3");
        d3.setUserId("anotherUser");
        d3.setCreateDate(new Date());
        documentDao.create(d3, "anotherUser");

        TransactionUtil.commit();

        List<Document> userDocs = documentDao.findByUserId("user123");
        Assert.assertEquals(2, userDocs.size());
    }

    @Test
    public void testDelete() {
        DocumentDao documentDao = new DocumentDao();

        Document document = new Document();
        document.setTitle("To be deleted");
        document.setUserId("delUser");
        document.setCreateDate(new Date());

        String id = documentDao.create(document, "delUser");
        TransactionUtil.commit();

        documentDao.delete(id, "delUser");
        TransactionUtil.commit();

        Document deleted = documentDao.getById(id);
        Assert.assertNull(deleted);
    }

    @Test
    public void testDocumentCount() {
        DocumentDao documentDao = new DocumentDao();

        Document d1 = new Document();
        d1.setTitle("DocA");
        d1.setUserId("counter");
        d1.setCreateDate(new Date());
        documentDao.create(d1, "counter");

        Document d2 = new Document();
        d2.setTitle("DocB");
        d2.setUserId("counter");
        d2.setCreateDate(new Date());
        documentDao.create(d2, "counter");

        TransactionUtil.commit();

        long count = documentDao.getDocumentCount();
        Assert.assertTrue(count >= 2); // 大于等于是因为测试数据库可能已有数据
    }
}
