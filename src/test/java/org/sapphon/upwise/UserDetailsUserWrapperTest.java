package org.sapphon.upwise;

import org.junit.Before;
import org.junit.Test;
import org.sapphon.upwise.factory.RandomObjectFactory;
import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.model.User;
import org.sapphon.upwise.model.UserDetailsUserWrapper;
import org.sapphon.upwise.time.TimeLord;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Iterator;

import static org.junit.Assert.*;

public class UserDetailsUserWrapperTest {

    private UserDetailsUserWrapper underTest;
    private IUser baseUser;

    @Before
    public void beforeEach(){
        baseUser = RandomObjectFactory.makeRandomUser();
        underTest = new UserDetailsUserWrapper(baseUser);
    }

    @Test
    public void getAuthoritiesAlwaysSaysUser() throws Exception {
        assertEquals(1, underTest.getAuthorities().size());
        assertEquals(new SimpleGrantedAuthority("USER"), underTest.getAuthorities().iterator().next());
    }

    @Test
    public void getAuthoritiesAdminsMePersonallyUntilIGetARealDatabase() throws Exception {
        UserDetailsUserWrapper uTest = new UserDetailsUserWrapper(new User("sapphon", "Connor S.", TimeLord.getNow(), "b", "doesntmatter", false));
        assertEquals(2, uTest.getAuthorities().size());
        Iterator<? extends GrantedAuthority> authorityIterator = uTest.getAuthorities().iterator();
        assertEquals(new SimpleGrantedAuthority("USER"), authorityIterator.next());
        assertEquals(new SimpleGrantedAuthority("ADMIN"), authorityIterator.next());
    }

    @Test
    public void getPasswordReturnsPasswordFromWrappedUser() throws Exception {
        assertEquals(baseUser.getPassword(), underTest.getPassword());
    }

    @Test
    public void getUsernameReturnsUsernameFromWrappedUser() throws Exception {
        assertEquals(baseUser.getLoginUsername(), underTest.getUsername());
    }

    @Test
    public void isAccountNonExpiredAlwaysSaysTrue() throws Exception {
        assertTrue(underTest.isAccountNonExpired());
    }

    @Test
    public void isAccountNonLockedAlwaysSaysTrue() throws Exception {
    assertTrue(underTest.isAccountNonLocked());
    }

    @Test
    public void isCredentialsNonExpiredAlwaysSaysTrue() throws Exception {
        assertTrue(underTest.isCredentialsNonExpired());
    }

    @Test
    public void isEnabledAlwaysSaysTrue() throws Exception {
        assertTrue(underTest.isEnabled());

    }

}