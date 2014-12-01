package de.bht.comanche.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Represents a single instance of <code>EntityManagerFactory</code>. 
 * 
 * Avoids reinitializing the <code> EntityManagerFactory </code> on 
 * every transaction begin.
 * 
 * <p>This class is bound to JPA/Hibernate and a specific DB name.
 * When changing configuration, these dependencies must be modified.
 * 
 * @author Simon Lischka
 * 
 */
public class DaEmProvider {
	private DaEmProvider() {}
	
	/**
	 * Constructs a new <code>DaEmProvider</code> singleton
	 */
    private static final DaEmProvider singleton = new DaEmProvider();
    
    /**
	 * Instance of <code>EntityManagerFactory</code>
	 */
	private EntityManagerFactory emf;
	
	/**
	 * Name of persistence unit (watch concurrency with <code>persistence.xml</code>)
	 */
	public static final String persistenceUnitName = "planthejam.jpa";
	
	/**
	 * Returns an instance of the <code>DaEmProvider</code> singleton.
	 * 
	 * <p> The factory may not be initialized jet.
	 * 
	 * @return <code>DaEmProvider</code> instance
	 * 
	 */
	public static DaEmProvider getInstance() {
		return singleton; 
	}
    
	/**
	 * Returns a single instance of EntityManagerFactory.
	 * 
	 * <p> The factory is initialized on the first call of the method.
	 * 
	 * @return Initialized <code>EntityManagerFactory</code> instance
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		} 
		return emf;
	}
	
	/**
	 * Closes <code>EntityManagerFactory</code> if previously
	 * opened.
	 */
	public void closeEntityManagerFactory() {
		if (emf.isOpen() && emf != null) {
			emf.close();
			emf = null;
		}
	}
}
