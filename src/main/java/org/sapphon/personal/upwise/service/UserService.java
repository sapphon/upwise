package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.model.IUser;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        return userRepo.getByLoginUsername(login == null ? null : login.toLowerCase());
    }

    public boolean hasUserWithLogin(String login){
        return this.getUserWithLogin(login) != null;
    }

    public IUser addOrUpdateUser(IUser toAdd) {
        toAdd.setLoginUsername(toAdd.getLoginUsername().toLowerCase());
        return userRepo.save(toAdd);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final IUser userWithLogin = this.getUserWithLogin(username);
        if(userWithLogin == null){
            throw new UsernameNotFoundException("User not found");
        }
        else{
            userWithLogin.setLoginUsername(userWithLogin.getLoginUsername().toLowerCase());
            return DomainObjectFactory.createUserDetailsFromUser(userWithLogin);
        }
    }

    public BCryptPasswordEncoder getPasswordEncoder(){
        return this.passwordEncoder;
    }
}
