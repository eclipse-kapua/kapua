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
package org.eclipse.kapua.commons.model.query;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.RandomUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class KapuaListResultTest {

    private final static Random RANDOM = RandomUtils.getInstance();

    @Test
    public void kapuaListResultTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        Assert.assertNotNull(kapuaListResult.getItems());
        Assert.assertTrue(kapuaListResult.getItems().isEmpty());
        Assert.assertFalse(kapuaListResult.isLimitExceeded());
        Assert.assertNull(kapuaListResult.getTotalCount());
    }

    @Test
    public void addItemsTest() {
        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);

        List<AbstractKapuaEntity> items = new ArrayList<>();
        items.add(kapuaEntity1);
        items.add(kapuaEntity2);

        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();
        kapuaListResult.addItems(items);

        Assert.assertEquals(2, kapuaListResult.getItems().size());
        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItems().get(0));
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItems().get(1));
        Assert.assertEquals(items, kapuaListResult.getItems());
    }

    @Test
    public void addItemTest() {
        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);

        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();
        kapuaListResult.addItem(kapuaEntity1);
        kapuaListResult.addItem(kapuaEntity2);

        Assert.assertEquals(2, kapuaListResult.getItems().size());
        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItems().get(0));
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItems().get(1));
    }

    @Test
    public void clearItemsTest() {
        int testSize = 10;
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        for (int i = 0; i < testSize; i++) {
            kapuaListResult.addItem(Mockito.mock(AbstractKapuaEntity.class));
        }

        Assert.assertEquals(testSize, kapuaListResult.getSize());

        kapuaListResult.clearItems();

        Assert.assertEquals(0, kapuaListResult.getSize());
    }

    @Test
    public void getFirstItemTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity1);

        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity2);

        Assert.assertEquals(kapuaEntity1, kapuaListResult.getFirstItem());
        Assert.assertNotEquals(kapuaEntity2, kapuaListResult.getFirstItem());
    }

    @Test
    public void getItemsTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity1);

        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity2);

        Assert.assertEquals(2, kapuaListResult.getItems().size());
        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItems().get(0));
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItems().get(1));
    }

    @Test
    public void getItemsPredicateTest() {
        int testSize = 10;
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(i))));

            kapuaListResult.addItem(kapuaEntity);
        }

        List<AbstractKapuaEntity> filteredItems = kapuaListResult.getItems(i -> i.getId().getId().longValue() % 2 == 0);

        Assert.assertEquals(5, filteredItems.size());
        for (AbstractKapuaEntity filteredItem : filteredItems) {
            Assert.assertEquals(0, filteredItem.getId().getId().longValue() % 2);
        }
    }

    @Test
    public void getItemsAsMapKeyTest() {
        int testSize = 10;
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(i))));

            kapuaListResult.addItem(kapuaEntity);
        }

        Map<KapuaId, AbstractKapuaEntity> itemsAsMap = kapuaListResult.getItemsAsMap(AbstractKapuaEntity::getId);

        Assert.assertEquals(kapuaListResult.getSize(), itemsAsMap.size());
        for (Map.Entry<KapuaId, AbstractKapuaEntity> entry : itemsAsMap.entrySet()) {
            Assert.assertEquals(entry.getKey(), entry.getValue().getId());
        }
    }

    @Test
    public void getItemsAsMapKeyValueTest() {
        int testSize = 10;
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(i))));

            kapuaListResult.addItem(kapuaEntity);
        }

        Map<KapuaId, KapuaId> itemsAsMap = kapuaListResult.getItemsAsMap(AbstractKapuaEntity::getId, AbstractKapuaEntity::getId);

        Assert.assertEquals(kapuaListResult.getSize(), itemsAsMap.size());
        for (Map.Entry<KapuaId, KapuaId> entry : itemsAsMap.entrySet()) {
            Assert.assertEquals(entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void getItemTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        AbstractKapuaEntity kapuaEntity1 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity1);

        AbstractKapuaEntity kapuaEntity2 = Mockito.mock(AbstractKapuaEntity.class);
        kapuaListResult.addItem(kapuaEntity2);

        Assert.assertEquals(kapuaEntity1, kapuaListResult.getItem(0));
        Assert.assertEquals(kapuaEntity2, kapuaListResult.getItem(1));
    }

    @Test
    public void getSizeTest() {
        int testSize = 10;
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        for (int i = 0; i < testSize; i++) {
            kapuaListResult.addItem(Mockito.mock(AbstractKapuaEntity.class));
        }

        Assert.assertEquals(testSize, kapuaListResult.getSize());
    }

    @Test
    public void isEmptyTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();
        Assert.assertTrue(kapuaListResult.isEmpty());

        kapuaListResult.addItem(Mockito.mock(AbstractKapuaEntity.class));
        Assert.assertFalse(kapuaListResult.isEmpty());
    }

    @Test
    public void setGetLimitExceededTrueTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();
        kapuaListResult.setLimitExceeded(true);

        Assert.assertTrue(kapuaListResult.isLimitExceeded());
    }

    @Test
    public void setGetLimitExceededFalseTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();
        kapuaListResult.setLimitExceeded(false);

        Assert.assertFalse(kapuaListResult.isLimitExceeded());
    }

    @Test
    public void setGetTotalCountDefaultTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        Assert.assertNull(kapuaListResult.getTotalCount());
    }

    @Test
    public void setGetTotalCountValueTest() {
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();
        kapuaListResult.setTotalCount(10L);

        Assert.assertEquals(new Long(10L), kapuaListResult.getTotalCount());
    }

    @Test
    public void sortComparatorTest() {
        int testSize = 10;
        KapuaListResult<AbstractKapuaEntity> kapuaListResult = new KapuaListResult<>();

        // Add the max value
        AbstractKapuaEntity kapuaEntityMax = Mockito.mock(AbstractKapuaEntity.class);
        Mockito.when(kapuaEntityMax.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(Long.MAX_VALUE))));
        kapuaListResult.addItem(kapuaEntityMax);

        // Add values between
        for (int i = 0; i < testSize; i++) {
            AbstractKapuaEntity kapuaEntity = Mockito.mock(AbstractKapuaEntity.class);
            Mockito.when(kapuaEntity.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(RANDOM.nextLong()))));

            kapuaListResult.addItem(kapuaEntity);
        }

        // Add the min value
        AbstractKapuaEntity kapuaEntityMin = Mockito.mock(AbstractKapuaEntity.class);
        Mockito.when(kapuaEntityMin.getId()).thenReturn(new KapuaEid(new BigInteger(String.valueOf(Long.MIN_VALUE))));
        kapuaListResult.addItem(kapuaEntityMin);

        // Check result are not sorted by `.getId()` ascending
        Assert.assertTrue(kapuaListResult.getItem(0).getId().getId().compareTo(kapuaListResult.getItem(1).getId().getId()) > 0);
        Assert.assertTrue(kapuaListResult.getItem(0).getId().getId().compareTo(kapuaListResult.getItem(kapuaListResult.getSize() - 1).getId().getId()) > 0);

        // Sort
        kapuaListResult.sort(Comparator.comparing(i -> i.getId().getId()));

        // Check result are sorted
        for (int i = 0; i < kapuaListResult.getSize() - 1; i++) {
            Assert.assertTrue(kapuaListResult.getItem(i).getId().getId().compareTo(kapuaListResult.getItem(i + 1).getId().getId()) < 0);
        }
    }
}
