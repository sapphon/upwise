package org.sapphon.upwise.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.sapphon.upwise.model.IUser;
import org.sapphon.upwise.factory.DomainObjectFactory;
import org.sapphon.upwise.time.TimeLord;
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
        testUsers[0] = DomainObjectFactory.createUser("tstone10", "Terrence Austin Stoneridge III", TimeLord.getNow(), "murica", "tstone10@ford.com", true);
        testUsers[1] = DomainObjectFactory.createUser("askywalk", "Darth Vader", TimeLord.getNowWithOffset(500000), "dark_$id3", "anakin@starwars.com", false);
        testUsers[2] = DomainObjectFactory.createUser("grivia", "The Witcher", TimeLord.getNowWithOffset(-1), "v3ng3rb3rg", "geralt@rivia.gov", false);
        testUsers[3] = DomainObjectFactory.createUserWithCreatedTimeNow("jrobiso7", "Jackie Robinson", "butitdo", "jackie@mlb.com", true);
        userRepository.clear();
    }

    @Test
    public void canGetOneRecordById(){
        IUser userWeWantToFind = testUsers[0];
        IUser userWeDoNotWant = testUsers[1];

        userRepository.save(userWeDoNotWant);
        userRepository.save(userWeWantToFind);

        assertEquals(userWeWantToFind, userRepository.getById(2L));
        assertEquals(userWeWantToFind.getEmail(), userRepository.getById(2L).getEmail());
    }


    @Test
    public void canGetAllOfSeveralDifferentRecords(){

        for(int i = 0; i < testUsers.length; i++) {
            userRepository.save(testUsers[i]);
        }

        List<IUser> actual = userRepository.getAll();

        assertArrayEquals(testUsers, actual.toArray());
    }

    @Test
    public void doesNotCreateDuplicatesWhenSavingSeveralOfTheSameRecord(){

        for(int i = 0; i < 3; i++) {
            userRepository.save(testUsers[0]);
        }

        List<IUser> actual = userRepository.getAll();

        IUser[] expectedWisdoms = {testUsers[0]};
        assertArrayEquals(expectedWisdoms, actual.toArray());
    }

    @Test
    public void canGetOneRecordByLoginName(){
        IUser userWeWant = testUsers[3];
        IUser userWeDoNotWant = testUsers[2];

        userRepository.save(userWeDoNotWant);
        userRepository.save(userWeWant);

        IUser usersByLoginName = userRepository.getByLoginUsername(userWeWant.getLoginUsername());
        assertEquals(userWeWant, usersByLoginName);
    }

    @Test
    public void canGetByDisplayName_NewestFirst() {
        String ourBoy = "jrobiso7";
        testUsers[0].setDisplayName(ourBoy);
        testUsers[1].setDisplayName(ourBoy);
        userRepository.save(Arrays.asList(testUsers));

        List<IUser> result = userRepository.getByDisplayName(ourBoy);

        assertArrayEquals(new IUser[]{testUsers[1], testUsers[0]}, result.toArray());
    }

    @Test
    public void canFindAUserByUniqueKey(){
        userRepository.save(newArrayList(testUsers));
        for(IUser user : testUsers){
            assertEquals(user, getOrFail(user.getLoginUsername(),user.getDisplayName()));
        }
    }

    @Test
    public void canAccuratelyCountStoredUsers(){
        userRepository.save(testUsers[0]);
        assertEquals(1, userRepository.getCount());
        userRepository.save(testUsers[0]);
        assertEquals(1, userRepository.getCount());
        userRepository.save(testUsers[1]);
        assertEquals(2, userRepository.getCount());
    }

    @Test
    public void testCanGetByEmail() {
        assertNull(userRepository.getByEmail("tstone10@ford.com"));
        userRepository.save(testUsers[0]);
        assertEquals(testUsers[0], userRepository.getByEmail("tstone10@ford.com"));
    }

    @Test
    public void testCanGetByAnalyticsTracked() {
        for(int i = 0; i < testUsers.length; i++) {
            userRepository.save(testUsers[i]);
        }
        assertEquals(4, userRepository.getCount());
        List<IUser> actualOptedInUsers = userRepository.getByAnalyticsTracked();
        assertEquals(2, actualOptedInUsers.size());
        assertTrue(actualOptedInUsers.contains(testUsers[0]));
        assertTrue(actualOptedInUsers.contains(testUsers[3]));
    }

    private IUser getOrFail(String loginName, String displayName) {
        Optional<IUser> wisdomFound = userRepository.findUser(loginName, displayName);
        boolean good = wisdomFound.isPresent();
        if (!good) Assert.fail("Expected user not found in user repo: " + loginName + " " + displayName);
        return wisdomFound.get();
    }
}