package org.sapphon.upwise.model.datatransfer;

import org.junit.Before;
import org.junit.Test;
import org.sapphon.upwise.factory.RandomObjectFactory;
import org.sapphon.upwise.model.IUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserRegistrationTest {

    private UserRegistration underTest;

    @Before
    public void setup(){
        underTest = RandomObjectFactory.makeValidButRandomUserRegistration();
    }

    @Test
    public void convertToModelObjectReturnsNullIfUsernameNullOrEmpty() {
        underTest.setDesiredUsername(null);
        assertNull(underTest.convertToModelObject());
        underTest.setDesiredUsername("");
        assertNull(underTest.convertToModelObject());
    }

    @Test
    public void convertToModelObjectReturnsNullIfPasswordNullOrEmpty() {
        underTest.setPassword(null).setConfirmPassword(null);
        assertNull(underTest.convertToModelObject());
        underTest.setPassword("").setConfirmPassword("");
        assertNull(underTest.convertToModelObject());
    }

    @Test
    public void convertToModelObjectReturnsNullIfUsernameTooShort() {
        underTest.setDesiredUsername("");
        assertNull(underTest.convertToModelObject());
    }

    @Test
    public void convertToModelObjectReturnsNullIfPasswordsDoNotMatch() {
        underTest.setPassword("A1B2").setConfirmPassword("a1b2");
        assertNull(underTest.convertToModelObject());
    }

    @Test
    public void convertToModelObjectReturnsNullIfPasswordIsTooShort() {
        underTest.setDesiredUsername("");
        assertNull(underTest.convertToModelObject());
    }

    @Test
    public void convertToModelObjectDoesNotTransformTheData() {
        IUser actual = underTest.convertToModelObject();
        assertNotNull(actual);
        assertEquals(underTest.getDesiredUsername(), actual.getLoginUsername());
        assertEquals(underTest.getPassword(), actual.getPassword());
        assertEquals(underTest.getConfirmPassword(), actual.getPassword());
        assertEquals(underTest.getDisplayName(), actual.getDisplayName());
        assertEquals(underTest.getEmail(), actual.getEmail());

    }
}