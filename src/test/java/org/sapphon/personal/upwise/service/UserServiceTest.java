package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.factory.RandomObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.TokenRepository;
import org.sapphon.personal.upwise.repository.UserRepository;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService underTest;
    private UserRepository mockUserRepository;
    private TokenRepository tokenRepository;

    @Before
    public void before(){
        mockUserRepository = Mockito.mock(UserRepository.class);
        tokenRepository = Mockito.mock(TokenRepository.class);
        underTest = new UserService(mockUserRepository, tokenRepository);
    }

    @Test
    public void testFunctionsAsUserDetailsService(){
        assertTrue(underTest instanceof UserDetailsService);
    }

    @Test
    public void collaboratesWithRepoToProvideAllUsers() {
        List<IUser> expected = new ArrayList<>();
        when(mockUserRepository.getAll()).thenReturn(expected);

        List<IUser> actual = underTest.getAllUsers();

        verify(mockUserRepository, times(1)).getAll();
        assertSame(expected, actual);
    }

    @Test
    public void queriesRepoForUsersByLoginNameAndIgnoresCase() {
        IUser expected = DomainObjectFactory.createUser("lmccoy67", "Leonard McCoy", TimeLord.getNow(), "pword", "");
        when(mockUserRepository.getByLoginUsername("lmccoy67")).thenReturn(expected);
        IUser actual = underTest.getUserWithLogin("LmcCOY67");
        verify(mockUserRepository).getByLoginUsername("lmccoy67");
        assertSame(expected, actual);
    }

    @Test
    public void addsToRepoWhenRealUserIsAddedAndLowersCaseOfLoginIfNecessary(){
        IUser expected = DomainObjectFactory.createUser("LmCCoy67", "Leonard McCoy", TimeLord.getNow(), "drowp", "");
        underTest.addOrUpdateUser(expected);
        verify(mockUserRepository).save(expected);
    }

    @Test
    public void addsToRepoWhenMockUserIsAddedAndLowersCaseOfLoginIfNecessary(){
        IUser expected = Mockito.mock(IUser.class);
        when(expected.getLoginUsername()).thenReturn("LMCcoy67");
        underTest.addOrUpdateUser(expected);
        verify(expected).setLoginUsername("lmccoy67");
        verify(mockUserRepository).save(expected);
    }

    @Test
    public void loadsUserDetailsFromTheUserRepositoryAndLowersCaseIfNecessary() {
        final IUser expectedUser = RandomObjectFactory.makeRandomUser();
        expectedUser.setLoginUsername("SthWithCapsAndLowers");
        when(mockUserRepository.getByLoginUsername(expectedUser.getLoginUsername().toLowerCase())).thenReturn(expectedUser);
        final UserDetails actualUserDetails = underTest.loadUserByUsername(expectedUser.getLoginUsername());
        verify(mockUserRepository).getByLoginUsername(expectedUser.getLoginUsername().toLowerCase());
        assertEquals(expectedUser.getLoginUsername().toLowerCase(), actualUserDetails.getUsername());
        assertEquals(expectedUser.getPassword(), actualUserDetails.getPassword());
    }

    @Test
    public void throwsExpectedExceptionWhenUserIsNotFound() {

        when(mockUserRepository.getByLoginUsername(any())).thenReturn(null);
        String caughtMsg = "";
        try {
            final UserDetails actualUserDetails = underTest.loadUserByUsername("sammybrown");
        } catch (UsernameNotFoundException e) {
            caughtMsg = e.getMessage();
        }
        assertEquals("User not found", caughtMsg);
    }

    @Test
    public void usesTheSamePasswordEncoderEachTimeYouAskForOne() {
        assertSame(underTest.getPasswordEncoder(), underTest.getPasswordEncoder());
    }

    @Test
    public void testHasUserWithLogin() {
        final IUser expectedUser = RandomObjectFactory.makeRandomUser();
        assertFalse(underTest.hasUserWithLogin(expectedUser.getLoginUsername()));
        when(mockUserRepository.getByLoginUsername(expectedUser.getLoginUsername().toLowerCase())).thenReturn(expectedUser);
        assertTrue(underTest.hasUserWithLogin(expectedUser.getLoginUsername()));
    }

    @Test
    public void testHasUserWithEmail() {
        final IUser expectedUser = RandomObjectFactory.makeRandomUser();
        assertFalse(underTest.hasUserWithEmail(expectedUser.getEmail()));
        when(mockUserRepository.getByEmail(expectedUser.getEmail())).thenReturn(expectedUser);
        assertTrue(underTest.hasUserWithEmail(expectedUser.getEmail()));
    }

    @Test
    public void testWhenResetPasswordIsEnabledForAUser_ANewPasswordResetTokenIsSavedToTheTokenRepository() {
        underTest.enablePasswordResetForUser("jungkoo");
        verify(tokenRepository).save(any());
    }


}