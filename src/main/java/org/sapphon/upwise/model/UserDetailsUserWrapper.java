package org.sapphon.upwise.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

public class UserDetailsUserWrapper implements UserDetails {
    private final IUser user;

    public UserDetailsUserWrapper(IUser user) {
        this.user = user;
    }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
        if(user.getLoginUsername().equalsIgnoreCase("sapphon")){
            return newArrayList(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN"));
        }
            return newArrayList(new SimpleGrantedAuthority("USER"));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getLoginUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
}
