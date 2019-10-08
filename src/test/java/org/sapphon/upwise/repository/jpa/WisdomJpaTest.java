package org.sapphon.upwise.repository.jpa;

import org.junit.Test;
import org.sapphon.upwise.TestHelper;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.junit.Assert.*;

public class WisdomJpaTest {
    @Test
    public void testAutoGeneratesIdsWithIdentityStrategy() {
        TestHelper.assertAnnotationsPresentOnField(WisdomJpa.class, "id", Id.class, GeneratedValue.class);
        GeneratedValue actual = TestHelper.assertFieldHasAnnotationOfTypeAndGet(WisdomJpa.class, "id", GeneratedValue.class);
        assertEquals(GenerationType.IDENTITY, actual.strategy());
    }

}