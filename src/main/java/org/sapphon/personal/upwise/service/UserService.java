package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.IUser;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService{

    private UserRepository userRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository)
    {
        this.userRepo = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    public List<IUser> getAllUsers() {
        return userRepo.getAll();

    }

    public IUser getUserWithLogin(String login) {
        return userRepo.getByLoginUsername(login);
    }

    public IUser addOrUpdateUser(IUser toAdd) {
        return userRepo.save(toAdd);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final IUser userWithLogin = this.getUserWithLogin(username);
        if(userWithLogin == null){
            return null;
        }
        else{
            return DomainObjectFactory.createUserDetailsFromUser(userWithLogin);
        }
    }

    public BCryptPasswordEncoder getPasswordEncoder(){
        return this.passwordEncoder;
    }
}
