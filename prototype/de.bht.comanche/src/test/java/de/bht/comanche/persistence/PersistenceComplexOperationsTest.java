package de.bht.comanche.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javassist.NotFoundException;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.bht.comanche.logic.LgInvite;
import de.bht.comanche.logic.LgUser;
import de.bht.comanche.server.TransactionWithStackTrace;
import de.bht.comanche.server.exceptions.persistence.ArgumentCountException;
import de.bht.comanche.server.exceptions.persistence.ArgumentTypeException;
import de.bht.comanche.server.exceptions.persistence.NoPersistentClassException;
import de.bht.comanche.server.exceptions.persistence.NoQueryClassException;
import de.bht.comanche.server.exceptions.persistence.OidNotFoundException;

public class PersistenceComplexOperationsTest {
	private final boolean THROW_STACKTRACE = true;
	private final boolean ROLLBACK = false;
	
	private DaFactory daFactory;
	
	@Before public void setUp(){
		daFactory = new JpaDaFactory();
	}
	
	@Test public void saveTwoContactsTest() {
		final DaUser daUser = daFactory.getDaUser();
		final Pool pool = daUser.getPool();
		boolean success = new TransactionWithStackTrace<LgUser>(pool, THROW_STACKTRACE, ROLLBACK) {
			public void executeWithThrows() throws Exception {
				LgUser alice = new LgUser();
				alice.setName("Alice");
				alice.setEmail("alice@user.tst");
				alice.setPassword("nosafepwd");
				alice.setTel("0301234567");
				LgUser bob = new LgUser();
				bob.setName("Bob");
				bob.setEmail("bob@test.usr");
				bob.setPassword("hiiambob");
				bob.setTel("0309876543");
				bob.addContact(alice);
				alice.addContact(bob);
				daUser.save(alice);
				daUser.save(bob);
				/*
				 * Transaction has to be closed so that entities are saved to DB.
				 * Then we can search by persisted ID.
				 * JPA generates the ID via PostgreSQL
				 */
				forceRestartTransaction();
				LgUser aliceFromDb = daUser.find(alice.getOid());
				LgUser bobFromDb = daUser.find(bob.getOid());
				assertEquals(bob.getOid(), bobFromDb.getOid());
				assertEquals(alice.getOid(), aliceFromDb.getOid());
				assertTrue(aliceFromDb.getHasContacts().contains(bobFromDb));
				assertTrue(bobFromDb.getIsContacts().contains(aliceFromDb));
				
				
			}
		}.execute();
		assertTrue(success);
    }
	
	@Ignore
	@Test public void saveInviteTest() {
		final DaUser daUser = daFactory.getDaUser();
		final DaInvite daInvite = daFactory.getDaInvite();
		final Pool pool = daUser.getPool();
		daInvite.setPool(pool);
		boolean success = new TransactionWithStackTrace<LgUser>(pool, THROW_STACKTRACE, ROLLBACK) {
			public void executeWithThrows() throws Exception {
				LgUser lgUser = new LgUser();
				lgUser.setName("Ralf");
				lgUser.setEmail("simon@a-studios.org");
				lgUser.setPassword("myPwIsEasy");
				lgUser.setTel("030-3223939");
				daUser.save(lgUser);
				/*
				 * RETRIEVE RALF
				 */
				System.out.println(">>> RETRIEVE RALF");
				LgUser lgUserFromDb = daUser.find(lgUser.getOid());
				/*
				 * CREATE & SAVE INVITES 
				 */
				System.out.println(">>> CREATE & SAVE INVITES");
				LgInvite lgInvite = new LgInvite();
				LgInvite lgInvite2 = new LgInvite();
				lgInvite.setUser(lgUserFromDb);
				lgInvite2.setUser(lgUserFromDb);
				lgInvite2.setHost(true);
				daInvite.save(lgInvite);
				daInvite.save(lgInvite2);
				/*
				 * RETRIEVE INVITES FROM DB
				 */
				System.out.println(">>> RETRIEVE INVITES FROM DB");
				LgInvite lgInviteFromDb = daInvite.find(lgInvite.getOid());
				LgInvite lgInvite2FromDb = daInvite.find(lgInvite2.getOid());
			}
		}.execute();
		assertTrue(success);
	}
	
}
