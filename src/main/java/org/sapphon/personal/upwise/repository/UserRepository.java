package org.sapphon.personal.upwise.repository;

import org.sapphon.personal.upwise.IUser;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("userRepo")
public class UserRepository {


    UserRepositoryJpa jpaUserRepo;

    @Autowired
    public UserRepository(UserRepositoryJpa backingUserRepo){
        jpaUserRepo = backingUserRepo;
    }

    public void clear(){
        jpaUserRepo.deleteAll();
    }

    public void save(IUser userToSave){
        this.jpaUserRepo.save(DomainObjectFactory.createUserJpa(userToSave));
    }

    public IUser getById(Long userId){
        return this.jpaUserRepo.findById(userId).orElse(null);
    }
}
