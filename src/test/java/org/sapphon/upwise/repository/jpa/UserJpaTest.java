package org.sapphon.upwise.repository.jpa;

import org.junit.Test;
import org.sapphon.upwise.TestHelper;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.junit.Assert.assertEquals;

public class UserJpaTest {
    @Test
    public void testAutoGeneratesIdsWithIdentityStrategy() {
        TestHelper.assertAnnotationsPresentOnField(UserJpa.class, "id", Id.class, GeneratedValue.class);
        GeneratedValue actual = TestHelper.assertFieldHasAnnotationOfTypeAndGet(UserJpa.class, "id", GeneratedValue.class);
        assertEquals(GenerationType.IDENTITY, actual.strategy());
    }

}