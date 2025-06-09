/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.service.internal.cache;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class EntityCacheTest {

    //TODO: FIXME: PRIORITY: rewrite
    //    @Before
    //    public void setFakeLocator() {
    //        System.setProperty(org.eclipse.kapua.locator.KapuaLocator.LOCATOR_CLASS_NAME_SYSTEM_PROPERTY, MockitoLocator.class.getName());
    //    }
    //
    //    @Test
    //    public void entityCacheTest() {
    //        String idCacheName = "idCacheName";
    //        Cache<Serializable, Serializable> expectedIdCache = StaticKapuaCacheManager.getCache(idCacheName);
    //        Cache<Serializable, Serializable> expectedListsCache = StaticKapuaCacheManager.getCache(idCacheName + "_list");
    //        //non sense! test to be removed or at least refactored!
    ////        Counter expectedCacheMiss = MetricServiceFactory.getInstance().getCounter("commons", "cache", "entity", "miss", "count");
    ////        Counter expectedCacheHit = MetricServiceFactory.getInstance().getCounter("commons", "cache", "entity", "hit", "count");
    ////        Counter expectedCacheRemoval = MetricServiceFactory.getInstance().getCounter("commons", "cache", "entity", "removal", "count");
    //        NullPointerException nullPointerException = new NullPointerException();
    //
    //        EntityCache entityCache = new EntityCache(idCacheName);
    //        Assert.assertEquals(expectedIdCache, entityCache.idCache);
    //        Assert.assertEquals(expectedListsCache, entityCache.listsCache);
    //        //non sense! test to be removed or at least refactored!
    ////        Assert.assertEquals(expectedCacheMiss, entityCache.cacheMiss);
    ////        Assert.assertEquals(expectedCacheHit, entityCache.cacheHit);
    ////        Assert.assertEquals(expectedCacheRemoval, entityCache.cacheRemoval);
    //
    //        try {
    //            EntityCache invalidEntityCache = new EntityCache(null);
    //        } catch (Exception e) {
    //            Assert.assertEquals("NullPointerException expected", nullPointerException.toString(), e.toString());
    //        }
    //    }
    //
    //    @Test
    //    public void getTest() {
    //        String idCacheName = "idCacheName";
    //        EntityCache entityCache = new EntityCache(idCacheName);
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        KapuaId kapuaId = new KapuaIdImpl(BigInteger.TEN);
    //
    //        //COMMENT: Entity is always null (see Cache.get() method)
    //        // Due to that reason the following part of code could not be tested in EntityCache.java:
    //        // else {
    //        // cacheHit.inc();
    //        // }
    //        //and this method always returns null
    //        Assert.assertNull("Null expected", entityCache.get(scopeId, kapuaId));
    //        Assert.assertNull("Null expected", entityCache.get(null, kapuaId));
    //        Assert.assertNull("Null expected", entityCache.get(scopeId, null));
    //        Assert.assertNull("Null expected", entityCache.get(null, null));
    //
    //        // COMMENT: Once the get() method will be changed,
    //        // we will be able to test other results also.
    //    }
    //
    //    @Test
    //    public void getListTest() {
    //        String idCacheName = "idCacheName";
    //        EntityCache entityCache = new EntityCache(idCacheName);
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        KapuaId scopeId1 = new KapuaIdImpl(BigInteger.TEN);
    //        Serializable key = new ComposedKey(scopeId1, null);
    //        Serializable id = new ComposedKey(scopeId1, key);
    //
    //        //COMMENT: This method always returns null, because entity is always null (see Cache.get() method)
    //        Assert.assertNull("Null expected", entityCache.getList(scopeId, id));
    //        Assert.assertNull("Null expected", entityCache.getList(null, id));
    //        Assert.assertNull("Null expected", entityCache.getList(null, null));
    //        Assert.assertNull("Null expected", entityCache.getList(scopeId, null));
    //
    //        // COMMENT: Once the get() method will be changed,
    //        // we will be able to test other results also.
    //    }
    //
    //    @Test
    //    public void putTest() {
    //        EntityCache entityCache1 = new EntityCache("IdCacheName");
    //        EntityCache entityCache2 = new EntityCache("Id Cache Name");
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        KapuaId kapuaId = new KapuaIdImpl(BigInteger.TEN);
    //        KapuaEntity kapuaEntity = entityCache2.get(scopeId, kapuaId);
    //
    //        //COMMENT: kapuaEntity is always null (see Cache.get() method)
    //        // Due to that reason the following part of code could not be tested in EntityCache.java:
    //        // if (entity != null) {
    //        //    idCache.put(entity.getId(), entity);
    //        // }
    //
    //        entityCache1.put(kapuaEntity);
    //
    //        // COMMENT: Once the get() method will be changed,
    //        // we will be able to test other results also.
    //    }
    //
    //    @Test
    //    public void putListTest() {
    //        String idCacheName = "IdCacheName";
    //        EntityCache entityCache = new EntityCache(idCacheName);
    //        KapuaId[] scopeIds = {null, new KapuaIdImpl(BigInteger.ONE)};
    //        KapuaId scopeId1 = new KapuaIdImpl(BigInteger.ONE);
    //        Serializable key = new ComposedKey(scopeId1, null);
    //        Serializable[] ids = {null, new ComposedKey(scopeId1, key)};
    //        KapuaListResult<KapuaEntity> list = new KapuaListResult<>();
    //
    //        for (KapuaId scopeId : scopeIds) {
    //            for (Serializable id : ids) {
    //                entityCache.putList(scopeId, id, list);
    //                entityCache.putList(scopeId, id, null);
    //            }
    //        }
    //
    //        //COMMENT: this method calls Cache.put(Object key, Object value) method,
    //        // which has empty body.
    //        //Once the put(Object key, Object value) method will be changed,
    //        // we will be able to test assertions.
    //    }
    //
    //    @Test
    //    public void removeWithKapuaEntityTest() {
    //        EntityCache entityCache = new EntityCache("IdCacheName");
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        EntityCache entityCache2 = new EntityCache("Id Cache Name");
    //        KapuaId scopeId1 = new KapuaIdImpl(BigInteger.ONE);
    //        KapuaId kapuaId1 = new KapuaIdImpl(BigInteger.TEN);
    //        KapuaEntity kapuaEntity = entityCache2.get(scopeId1, kapuaId1);
    //        NullPointerException nullPointerException = new NullPointerException();
    //
    //        //COMMENT: kapuaEntity is always null (see Cache.get() method)
    //        // Due to that reason entity.getId() always gives NullPointerException in EntityCache.java
    //
    //        try {
    //            entityCache.remove(scopeId, kapuaEntity);
    //        } catch (Exception e) {
    //            Assert.assertEquals("NullPointerException expected", nullPointerException.toString(), e.toString());
    //        }
    //
    //        try {
    //            entityCache.remove(null, kapuaEntity);
    //        } catch (Exception e) {
    //            Assert.assertEquals("NullPointerException expected", nullPointerException.toString(), e.toString());
    //        }
    //    }
    //
    //    @Test
    //    public void removeTest() {
    //        EntityCache entityCache = new EntityCache("IdCacheName");
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        KapuaId kapuaId = new KapuaIdImpl(BigInteger.TEN);
    //        KapuaId nullKapuaId = null;
    //
    //        //COMMENT: Entity is always null (see Cache.get() method)
    //        // Due to that reason the following part of code could not be tested in EntityCache.java:
    //        //if (entity != null) {
    //        //idCache.remove(kapuaId);
    //        // cacheRemoval.inc();
    //        //return entity;
    //        //}
    //        // and this method always returns null
    //        Assert.assertNull("Null expected", entityCache.remove(scopeId, kapuaId));
    //        Assert.assertNull("Null expected", entityCache.remove(null, kapuaId));
    //        Assert.assertNull("Null expected", entityCache.remove(scopeId, nullKapuaId));
    //        Assert.assertNull("Null expected", entityCache.remove(null, nullKapuaId));
    //
    //        // COMMENT: Once the get() method will be changed,
    //        // we will be able to test other results also.
    //    }
    //
    //    @Test
    //    public void removeListTest() {
    //        EntityCache entityCache = new EntityCache("IdCacheName");
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        KapuaId scopeId1 = new KapuaIdImpl(BigInteger.ONE);
    //        Serializable key = new ComposedKey(scopeId1, null);
    //        Serializable id = new ComposedKey(scopeId1, key);
    //
    //        //COMMENT: Entity is always null (see Cache.get() method)
    //        // Due to that reason the following part of code could not be tested in EntityCache.java:
    //        //if (entity != null) {
    //        //listsCache.remove(new ComposedKey(scopeId, id));
    //        //return entity;
    //        //}
    //        // and this method always returns null
    //        Assert.assertNull("Null expected", entityCache.removeList(scopeId, id));
    //        Assert.assertNull("Null expected", entityCache.removeList(null, id));
    //        Assert.assertNull("Null expected", entityCache.removeList(scopeId, null));
    //        Assert.assertNull("Null expected", entityCache.removeList(null, null));
    //
    //        // COMMENT: Once the get() method will be changed,
    //        // we will be able to test other results also.
    //    }
    //
    //    @Test
    //    public void checkResultTest() {
    //        EntityCache entityCache1 = new EntityCache("idCacheName");
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        EntityCache entityCache2 = new EntityCache("idCacheName2");
    //        KapuaId kapuaId = new KapuaIdImpl(BigInteger.TEN);
    //        KapuaEntity kapuaEntity = entityCache2.get(scopeId, kapuaId);
    //        KapuaEntity kapuaNullEntity = null;
    //
    //        //COMMENT: Entity is always null (see Cache.get() method)
    //        // Due to that reason the following part of code could not be tested in EntityCache.java:
    //        //if (entity != null) {
    //        //    if (scopeId == null) {
    //        //        return entity;
    //        //    } else if (entity.getScopeId() == null) {
    //        //        return entity;
    //        //    } else if (entity.getScopeId().equals(scopeId)) {
    //        //        return entity;
    //        //    } else {
    //        //       return null;
    //        //  }
    //        //and this method always returns null
    //        Assert.assertNull("Null expected", entityCache1.checkResult(scopeId, kapuaEntity));
    //        Assert.assertNull("Null expected", entityCache1.checkResult(null, kapuaEntity));
    //        Assert.assertNull("Null expected", entityCache1.checkResult(scopeId, kapuaNullEntity));
    //        Assert.assertNull("Null expected", entityCache1.checkResult(null, kapuaNullEntity));
    //
    //        // COMMENT: Once the get() method will be changed,
    //        // we will be able to test other results also.
    //    }
    //
    //    @Test
    //    public void checkResultListTest() {
    //        KapuaListResult kapuaNullListResult = null;
    //        EntityCache entityCache = new EntityCache("idCacheName");
    //        KapuaListResult<KapuaEntity> kapuaListResult = new KapuaListResultImpl<>();
    //        KapuaId scopeId = new KapuaIdImpl(BigInteger.ONE);
    //        EntityCache entityCache2 = new EntityCache("idCacheName2");
    //        KapuaId scopeId1 = new KapuaIdImpl(BigInteger.ONE);
    //        KapuaId kapuaId = new KapuaIdImpl(BigInteger.TEN);
    //        KapuaEntity kapuaEntity = entityCache2.get(scopeId1, kapuaId);
    //        NullPointerException nullPointerException = new NullPointerException();
    //
    //        //COMMENT: entity.getFirstItem().getScopeId() returns nullPointerException, because entity is always null (see Cache.get() method)
    //        // Due to that reason the following part of code could not be tested in EntityCache.java:
    //        // else if (entity.getFirstItem().getScopeId() == null) {
    //        //return entity;
    //        //} else if (entity.getFirstItem().getScopeId().equals(scopeId)) {
    //        //return entity;
    //        //} else {
    //        //return null;
    //        //}
    //
    //        Assert.assertNull("Null expected", entityCache.checkResult(scopeId, kapuaNullListResult));
    //        Assert.assertEquals(kapuaListResult, entityCache.checkResult(scopeId, kapuaListResult));
    //        kapuaListResult.addItem(kapuaEntity);
    //        Assert.assertEquals(kapuaListResult, entityCache.checkResult(null, kapuaListResult));
    //
    //        try {
    //            Assert.assertEquals(kapuaListResult, entityCache.checkResult(scopeId, kapuaListResult));
    //        } catch (Exception e) {
    //            Assert.assertEquals("NullPointerException expected", nullPointerException.toString(), e.toString());
    //        }
    //
    //        // COMMENT: Once the get() method will be changed,
    //        // we will be able to test other results also.
    //    }
} 
