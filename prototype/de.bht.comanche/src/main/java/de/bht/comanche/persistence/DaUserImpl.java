package de.bht.comanche.persistence;

import java.util.Collection;

import de.bht.comanche.logic.LgUser;

public class DaUserImpl extends DaGenericImpl<LgUser> implements DaUser {
	public DaUserImpl(Pool pool) {
		super(LgUser.class, pool);
	}

	
	@Override
	public Collection<LgUser> findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
