package org.sapphon.upwise.controller.ui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sapphon.upwise.TestHelper;
import org.sapphon.upwise.factory.RandomObjectFactory;
import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UIControllerAdviceTest {

    private UIControllerAdvice underTest;
    private UserService mockUserService;

    @Before
    public void setup() {
        mockUserService = Mockito.mock(UserService.class);
        underTest = new UIControllerAdvice(mockUserService);
    }

    @Test
    public void testGetLoggedInUserAsksUserServiceAboutPrincipalsName() {
        IUser expectedUser = RandomObjectFactory.makeRandomUser();
        expectedUser.setLoginUsername("abrauserdabra");
        when(mockUserService.getUserWithLogin(expectedUser.getLoginUsername())).thenReturn(expectedUser);
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(expectedUser.getLoginUsername());
        IUser actualUser = underTest.loggedInUser(mockPrincipal);
        verify(mockUserService).getUserWithLogin(mockPrincipal.getName());
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testControllerAdviceHasCorrectAnnotationsToMakeItHappen() {
        assertNotNull(TestHelper.getClassAnnotation(UIControllerAdvice.class, ControllerAdvice.class));
        ModelAttribute modelAttributeAnnotation = TestHelper.getMethodAnnotation(UIControllerAdvice.class, "loggedInUser", ModelAttribute.class, Principal.class);
        assertNotNull(modelAttributeAnnotation);
        assertEquals("loggedInUser", modelAttributeAnnotation.value());
    }


}