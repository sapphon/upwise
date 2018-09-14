package org.sapphon.personal.upwise.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.sapphon.personal.upwise.IWisdom;
import org.sapphon.personal.upwise.factory.DomainObjectFactory;
import org.sapphon.personal.upwise.repository.jpa.WisdomJpa;
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
public class WisdomRepositoryTest {

	@Autowired
	private WisdomRepository wisdomRepo;

	private IWisdom[] testWisdoms;

	@Before
    public void setUp() throws Exception {
		testWisdoms = new IWisdom[4];
		testWisdoms[0] = DomainObjectFactory.createWisdom("Good programmer looks both ways one way street", "Larry", "hschaug1", TimeLord.getNowWithOffset(-1000));
		testWisdoms[1] = DomainObjectFactory.createWisdom("Debugging crime movie detective also murderer", "Curly", "rnueter", TimeLord.getNowWithOffset(500000));
		testWisdoms[2] = DomainObjectFactory.createWisdom("What takes one one takes two two", "Moe", "wlata35", TimeLord.getNowWithOffset(-1));
		testWisdoms[3] = DomainObjectFactory.createWisdomWithCreatedTimeNow("Programming is creative problem solving", "MacCarr", "stamat");
		wisdomRepo.clear();
	}

	@Test
	public void canGetOneRecordById(){
		IWisdom wisdomWeWantToFind = testWisdoms[0];
		IWisdom wisdomWeDoNotWant = testWisdoms[1];

		wisdomRepo.save(wisdomWeDoNotWant);
		wisdomRepo.save(wisdomWeWantToFind);

		assertEquals(wisdomWeWantToFind, wisdomRepo.getById(2L));
	}

	@Test
	public void canGetAllOfSeveralDifferentRecords(){

		for(int i = 0; i < testWisdoms.length - 1; i++) {
			wisdomRepo.save(testWisdoms[i].getWisdomContent(), testWisdoms[i].getAttribution(), testWisdoms[i].getAddedByUsername(), testWisdoms[i].getTimeAdded());
		}
		wisdomRepo.save(testWisdoms[testWisdoms.length - 1]);

		List<IWisdom> actual = wisdomRepo.getAll();

		assertArrayEquals(testWisdoms, actual.toArray());
	}

	@Test
	public void doesNotCreateDuplicatesWhenSavingSeveralOfTheSameRecord(){

		for(int i = 0; i < 3; i++) {
			wisdomRepo.save(testWisdoms[0].getWisdomContent(), testWisdoms[0].getAttribution(), testWisdoms[0].getAddedByUsername(), testWisdoms[0].getTimeAdded());
		}
		wisdomRepo.save(testWisdoms[0]);

		List<IWisdom> actual = wisdomRepo.getAll();

		IWisdom[] expectedWisdoms = {testWisdoms[0]};
		assertArrayEquals(expectedWisdoms, actual.toArray());
	}

	@Test
	public void canGetOneRecordBySubmitter(){
		IWisdom wisdomWeWant = testWisdoms[3];
		IWisdom wisdomWeDoNotWant = testWisdoms[2];

		wisdomRepo.save(wisdomWeDoNotWant);
		wisdomRepo.save(wisdomWeWant);

		List<IWisdom> wisdomsBySubmitter = wisdomRepo.getBySubmitter(wisdomWeWant.getAddedByUsername());
		assertNotNull(wisdomsBySubmitter);
		assertEquals(1, wisdomsBySubmitter.size());
		assertEquals(wisdomWeWant, wisdomsBySubmitter.get(0));
	}

	@Test
	public void canGetWisdomsBySubmitter_NewestFirst() {
		String ourBoy = "stamat";
		testWisdoms[0].setAddedByUsername(ourBoy);
		testWisdoms[1].setAddedByUsername(ourBoy);
		wisdomRepo.save(Arrays.asList(testWisdoms));

		List<IWisdom> result = wisdomRepo.getBySubmitter(ourBoy);

		assertArrayEquals(new IWisdom[]{testWisdoms[1], testWisdoms[3], testWisdoms[0]}, result.toArray());
	}

	@Test
	public void canFindAWisdomByUniqueKey(){
		wisdomRepo.save(newArrayList(testWisdoms));
		for(IWisdom wisdom : testWisdoms){
			assertEquals(wisdom, getOrFail(wisdom.getWisdomContent(),wisdom.getAttribution()));
		}
	}

	@Test
	public void canAccuratelyCountStoredWisdoms(){
		wisdomRepo.save(testWisdoms[0]);
		assertEquals(1, wisdomRepo.getCount());
		wisdomRepo.save(testWisdoms[0]);
		assertEquals(1, wisdomRepo.getCount());
		wisdomRepo.save(testWisdoms[1]);
		assertEquals(2, wisdomRepo.getCount());
	}

	private IWisdom getOrFail(String wisdomContent, String attribution) {
		Optional<IWisdom> wisdomFound = wisdomRepo.findWisdom(wisdomContent, attribution);
		boolean good = wisdomFound.isPresent();
		if (!good) Assert.fail("Expected wisdom not found in wisdom repo: " + wisdomContent + " " + attribution);
		return wisdomFound.get();
	}
}