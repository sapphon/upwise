package org.sapphon.personal.upwise.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.IVote;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.VoteJpa;
import org.sapphon.personal.upwise.time.TimeLord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VoteRepositoryTest {

	@Autowired
	private VoteRepository voteRepo;

	private IWisdom[] testWisdoms;

	private IVote[] testVotes;

	@Before
    public void setUp() throws Exception {
		testVotes = new IVote[3];
		testWisdoms = new IWisdom[2];

		testWisdoms[0] = DomainObjectFactory.createWisdom("Good programmer looks both ways one way street", "Larry", "hschaug1", TimeLord.getNowWithOffset(-1000));
		testWisdoms[1] = DomainObjectFactory.createWisdom("Debugging crime movie detective also murderer", "Curly", "rnueter", TimeLord.getNowWithOffset(500000));

		testVotes[0] = DomainObjectFactory.createVote(testWisdoms[0], "stamat", TimeLord.getTimestampForMillis(2));
		testVotes[1] = DomainObjectFactory.createVote(testWisdoms[1], "rnueter", TimeLord.getTimestampForMillis(1));
		testVotes[2] = DomainObjectFactory.createVote(testWisdoms[0], "rnueter", TimeLord.getTimestampForMillis(0));
		voteRepo.clear();
	}

	@Test
	public void canSaveTwoRecordsThenGetTheRightOneById(){
		IVote voteWeWantToFind = testVotes[0];
		IVote voteWeDoNotWant = testVotes[1];

        voteRepo.save(voteWeDoNotWant);
		voteRepo.save(voteWeWantToFind);

		assertEquals(voteWeWantToFind, voteRepo.getById(4L));
	}

	@Test
	public void canGetAllOfSeveralRecords(){
		for(int i = 0; i < testVotes.length - 1; i++) {
			voteRepo.save(testVotes[i].getWisdom(), testVotes[i].getAddedByUsername(), testVotes[i].getTimeAdded());
		}
		voteRepo.save(testVotes[testVotes.length - 1]);

		List<IVote> actual = voteRepo.getAll();

		assertArrayEquals(testVotes, actual.toArray());
	}


	@Test
	public void doesNotCreateDuplicatesWhenSavingSeveralOfTheSameRecord() {

		for (int i = 0; i < 3; i++) {
			voteRepo.save(testVotes[0].getWisdom(), testVotes[0].getAddedByUsername(), testVotes[0].getTimeAdded());
		}
		voteRepo.save(testVotes[0]);

		List<IVote> actual = voteRepo.getAll();

		IVote[] expectedVotes = {testVotes[0]};
		assertArrayEquals(expectedVotes, actual.toArray());
	}

	@Test
	public void canGetOneRecordBySubmitter(){
		IVote voteWeWant = testVotes[0];
        IVote voteWeDoNotWant = testVotes[2];

		voteRepo.save(voteWeDoNotWant);
		voteRepo.save(voteWeWant);

		List<IVote> actual = voteRepo.getBySubmitter(voteWeWant.getAddedByUsername());
		assertNotNull(actual);
		assertEquals(1, actual.size());
		assertEquals(voteWeWant, actual.get(0));
	}

	@Test
	public void canGetBySubmitter_NewestFirst() {
		String ourBoy = "hgrasam";
		testVotes[0].setAddedByUsername(ourBoy);
		testVotes[1].setAddedByUsername(ourBoy);
		voteRepo.save(Arrays.asList(testVotes));

        IVote[] expected = {testVotes[0], testVotes[1]};
        Object[] actual = voteRepo.getBySubmitter(ourBoy).toArray();

        assertArrayEquals(expected, actual);
	}

    @Test
    public void canGetByWisdom() {
        voteRepo.save(Arrays.asList(testVotes));

        IVote[] expected = {testVotes[0], testVotes[2]};
        Object[] actual = voteRepo.getByWisdom(testWisdoms[0]).toArray();
        assertArrayEquals(expected, actual);

        IVote[] expectedToo = {testVotes[1]};
        Object[] actualToo = voteRepo.getByWisdom(testWisdoms[1]).toArray();

        assertArrayEquals(expectedToo, actualToo);
    }


}