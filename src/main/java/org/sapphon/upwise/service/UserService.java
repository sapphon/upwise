package org.sapphon.upwise.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.factory.DomainObjectFactory;
import org.sapphon.upwise.repository.Token;
import org.sapphon.upwise.repository.jpa.TokenRepository;
import org.sapphon.upwise.repository.UserRepository;
import org.sapphon.upwise.time.TimeLord;
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
    private TokenRepository tokenRepo;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository)
    {
        this.userRepo = userRepository;
        this.tokenRepo = tokenRepository;
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

    public void enablePasswordResetForUser(String email) {
        tokenRepo.save(new Token(RandomStringUtils.randomAlphanumeric(16), this.userRepo.getByEmail(email)));
    }

    public boolean isValidToken(String token){
        Token tokenFound = this.tokenRepo.getByToken(token);
        return tokenFound != null && TimeLord.getNow().getTime() - tokenFound.getTimeCreated().getTime() < 3600000;
    }

    public boolean resetPasswordForUser(String loginUsername, String token, String desiredNewPassword){
        boolean requestIsValid = isValidToken(token) && this.getUserWithLogin(loginUsername) != null && tokenRepo.getByToken(token).getUser().getLoginUsername().equals(loginUsername);
        if(requestIsValid){
            userRepo.save(this.getUserWithLogin(loginUsername).setPassword(this.passwordEncoder.encode(desiredNewPassword)));
        }
        return requestIsValid;
    }

    public boolean hasUserWithEmail(String email) {
        return this.userRepo.getByEmail(email) != null;
    }
}
