package de.bht.comanche.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;

import de.bht.comanche.logic.LgTransaction;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.bht.comanche.logic.LgInvite;
import de.bht.comanche.logic.LgSession;
import de.bht.comanche.logic.LgSurvey;
import de.bht.comanche.logic.LgUser;

public class SurveyTest {
	final static String ALICE_USER_NAME = "Alice";
	final static String BOB_USER_NAME = "Bob";
	
	@BeforeClass 
	public static void initializeDb() {
		Map<String, String> properties = new HashMap<String, String>(1);
		properties.put("hibernate.hbm2ddl.auto", "create");
		Persistence.createEntityManagerFactory(DaEmProvider.PERSISTENCE_UNIT_NAME, properties);
		assertTrue("Initialized JPA Database -> Pre Test Cleannup", true);
		LgSession session = new LgSession();
		session.getApplication().beginTransaction();
		
		final LgUser alice = new LgUser();
		alice.setName(ALICE_USER_NAME);
		alice.setEmail("test@test.de");
		alice.setPassword("testtest");
		session.register(alice);
		
		final LgUser bob = new LgUser();
		bob.setName("Bob");
		bob.setEmail("bob@test.de");
		bob.setPassword("testtest");
		session.register(bob);
		
		final LgUser pit = new LgUser();
		pit.setName("Pit");
		pit.setEmail("pit@test.de");
		pit.setPassword("testtest");
		session.register(pit);
		session.getApplication().endTransaction(true);	
	}
	
	@Test
	public void saveSingleSurveyAsHost() {
		final LgUser bob = new LgUser();
		bob.setName("Bob");
		bob.setEmail("bob@test.de");
		bob.setPassword("testtest");
		
		final LgUser pit = new LgUser();
		pit.setName("Pit");
		pit.setEmail("pit@test.de");
		pit.setPassword("testtest");
	
		LgSession sessionForCreate = createSessionAndStartTransaction();
		sessionForCreate.startFor(ALICE_USER_NAME);
		
		final LgSurvey demoSurvey = new LgSurvey();
		demoSurvey.setName("DemoSurvey");
		
		demoSurvey.inviteOtherUser(bob);
		demoSurvey.inviteOtherUser(pit);

		alice.saveSurveyAsHost(demoSurvey);

		endTranscation(sessionForCreate);
		
		LgSession sessionForValidation = createSessionAndStartTransaction();
		sessionForValidation.startFor(ALICE_USER_NAME);
		
		LgSurvey persistedSurvey = sessionForValidation
			.getUser()
				.getSurveyByName(demoSurvey.getName());
		
	    LgInvite bobsInvite = persistedSurvey.getInviteByParticipantName(bob.getName());
		LgInvite pitsInvite = persistedSurvey.getInviteByParticipantName(pit.getName());
		
		assertEquals("Bob's invite has status 'participant'", false, bobsInvite.isHost());
		assertEquals("Bob is user of his invite", bob.getName(), bobsInvite.getUser().getName());
		assertEquals("Bob's invite is not set to isIgnored", false, bobsInvite.isIgnored());
	    assertEquals("Pit's invite has status 'participant'", false, bobsInvite.isHost());
	    assertEquals("Pit is user of the invite", pit.getName(), pitsInvite.getUser().getName());
	    assertEquals("Pit's invite is not set to isIgnored", false, pitsInvite.isIgnored());
	}
	
	@Ignore
	@Test
	public void getSingleSurveyAsHost() {
		
	}
	
	@Ignore
	@Test
	public void getManySurveysAsHost() {}
	
	@Ignore
	@Test
	public void getSingleInviteAsParticipant() {}
	
	@Ignore
	@Test
	public void getManyInvitesAsParticipant() {}
	
	@Ignore
	@Test
	public void saveInviteAsParticipant() {}
	
	@Ignore
	@Test
	public void deleteInviteAsParticipant() {}
	
	
	public LgSession createSessionAndStartTransaction(){
		LgSession session = new LgSession();
		session.getApplication().beginTransaction();
		return session;
	}
	
	public void endTranscation(LgSession session){
		session.getApplication().endTransaction(true);
	}
	
}
