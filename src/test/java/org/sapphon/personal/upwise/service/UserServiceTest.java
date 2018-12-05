package org.sapphon.personal.upwise.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sapphon.personal.upwise.IUser;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.UserRepository;
import org.sapphon.personal.upwise.time.TimeLord;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService underTest;
    private UserRepository mockUserRepository;

    @Before
    public void before(){
        mockUserRepository = Mockito.mock(UserRepository.class);
        underTest = new UserService(mockUserRepository);
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
    public void queriesRepoForUsersByLoginName() {
        IUser expected = DomainObjectFactory.createUser("lmccoy67", "Leonard McCoy", TimeLord.getNow());
        when(mockUserRepository.getByLoginUsername("lmccoy67")).thenReturn(expected);
        IUser actual = underTest.getUserWithLogin("lmccoy67");
        verify(mockUserRepository).getByLoginUsername("lmccoy67");
        assertSame(expected, actual);
    }

    @Test
    public void addsToRepoWhenUserIsAdded(){
        IUser expected = DomainObjectFactory.createUser("lmccoy67", "Leonard McCoy", TimeLord.getNow());
        underTest.addOrUpdateUser(expected);
        verify(mockUserRepository).save(expected);
    }

}