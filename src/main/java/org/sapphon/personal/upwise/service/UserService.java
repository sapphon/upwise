package org.sapphon.personal.upwise.service;

import org.sapphon.personal.upwise.IUser;
import org.sapphon.personal.upwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepo = userRepository;
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
}
