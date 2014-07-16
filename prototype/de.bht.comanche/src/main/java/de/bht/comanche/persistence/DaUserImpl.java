package de.bht.comanche.persistence;

import java.util.List;

import de.bht.comanche.exceptions.DaArgumentCountException;
import de.bht.comanche.exceptions.DaArgumentTypeException;
import de.bht.comanche.exceptions.DaNoPersistentClassException;
import de.bht.comanche.exceptions.DaNoQueryClassException;
import de.bht.comanche.logic.LgUser;

public class DaUserImpl extends DaGenericImpl<LgUser> implements DaUser {
	public DaUserImpl(DaPool<LgUser> pool) {
		super(LgUser.class, pool);
	}
	
	/** 
	 * FIXME Attention: Change id declaration in DbObject
	 * @throws DaArgumentTypeException 
	 * @throws DaArgumentCountException 
	 * @throws DaNoQueryClassException 
	 * @throws DaNoPersistentClassException 
	 */
	@Override
	public List<LgUser> findByName(String name) throws DaNoPersistentClassException, DaNoQueryClassException, DaArgumentCountException, DaArgumentTypeException {
		return findByField("name", name);

	}
	public LgUser getDummy() {
		LgUser user = new LgUser();
		user.setOid(123456789);
		user.setName("Tom Sawyer");
		user.setPassword("letmeinplease");
		user.setEmail("tom@sawyer.com");
		user.setTel("00123456");
		return user;
	}

}
