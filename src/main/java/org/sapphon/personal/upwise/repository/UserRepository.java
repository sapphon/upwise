package org.sapphon.personal.upwise.repository;

import org.sapphon.personal.upwise.IUser;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.UserJpa;
import org.sapphon.personal.upwise.repository.jpa.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("userRepo")
public class UserRepository {

    private UserRepositoryJpa jpaUserRepo;

    @Autowired
    public UserRepository(UserRepositoryJpa backingUserRepo) {
        jpaUserRepo = backingUserRepo;
    }

    public void clear() {
        jpaUserRepo.deleteAll();
    }

    public IUser save(IUser userToSave) {
        Optional<IUser> userFound = findUser(userToSave.getLoginUsername(), userToSave.getDisplayName());
        if(userFound.isPresent()){
            return userFound.get();
        }
        else{
            return jpaUserRepo.save(DomainObjectFactory.createUserJpa(userToSave));
        }
    }

    public void save(List<IUser> usersToSave){
        usersToSave.forEach(this::save);
    }

    public IUser getById(Long userId) {
        return this.jpaUserRepo.findById(userId).orElse(null);
    }

    public List<IUser> getAll() {
        List<IUser> toReturn = new ArrayList<>();
        jpaUserRepo.findAll().forEach((j) -> toReturn.add(DomainObjectFactory.createUser(j)));
        return toReturn;
    }

    public IUser getByLoginUsername(String loginUsername) {
        return DomainObjectFactory.createUser(jpaUserRepo.findTopByLoginUsernameOrderByTimeAddedDesc(loginUsername));
    }

    public List<IUser> getByDisplayName(String displayUsername) {
        List<IUser> toReturn = new ArrayList<>();
        jpaUserRepo.findAllByDisplayUsernameOrderByTimeAddedDesc(displayUsername).forEach((j) -> toReturn.add(DomainObjectFactory.createUser(j)));
        return toReturn;
    }

    public Optional<IUser> findUser(String loginName, String displayName) {
        Optional<UserJpa> userJpa = jpaUserRepo.findTopByLoginUsernameAndDisplayUsernameOrderByTimeAddedDesc(loginName, displayName);
        return userJpa.isPresent() ? Optional.of(userJpa.get()) : Optional.empty();
    }

    public long getCount() {
        return jpaUserRepo.count();
    }

}
