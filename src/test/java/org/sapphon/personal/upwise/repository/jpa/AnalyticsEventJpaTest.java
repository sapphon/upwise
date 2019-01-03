package org.sapphon.personal.upwise.repository.jpa;

import org.junit.Test;
import org.sapphon.personal.upwise.TestHelper;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.junit.Assert.*;

public class AnalyticsEventJpaTest {
    @Test
    public void testAutoGeneratesIdsWithIdentityStrategy() {
        TestHelper.assertAnnotationsPresentOnField(AnalyticsEventJpa.class, "id", Id.class, GeneratedValue.class);
        GeneratedValue actual = TestHelper.assertFieldHasAnnotationOfTypeAndGet(AnalyticsEventJpa.class, "id", GeneratedValue.class);
        assertEquals(GenerationType.IDENTITY, actual.strategy());
    }
}