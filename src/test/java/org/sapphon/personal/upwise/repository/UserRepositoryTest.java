package org.sapphon.personal.upwise.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.IUser;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private IUser[] testUsers;

    @Before
    public void setUp() throws Exception {
        testUsers = new IUser[4];
        testUsers[0] = DomainObjectFactory.createUser("tstone10", "Terrence Austin Stoneridge III", TimeLord.getNow());
        testUsers[1] = DomainObjectFactory.createUser("askywalk", "Darth Vader", TimeLord.getNowWithOffset(500000));
        testUsers[2] = DomainObjectFactory.createUser("grivia", "The Witcher", TimeLord.getNowWithOffset(-1));
        testUsers[3] = DomainObjectFactory.createUserWithCreatedTimeNow("jrobiso7", "Jackie Robinson");
        userRepository.clear();
    }

    @Test
    public void canGetOneRecordById(){
        IUser userWeWantToFind = testUsers[0];
        IUser userWeDoNotWant = testUsers[1];

        userRepository.save(userWeDoNotWant);
        userRepository.save(userWeWantToFind);

        assertEquals(userWeWantToFind, userRepository.getById(2L));
    }
}