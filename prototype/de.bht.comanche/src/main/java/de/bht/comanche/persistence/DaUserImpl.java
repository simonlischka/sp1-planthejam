package de.bht.comanche.persistence;

import java.util.Collection;

import de.bht.comanche.model.DmUser;

public class DaUserImpl extends DaGenericImpl<DmUser> implements DaUser {

	public DaUserImpl(Class<DmUser> type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<DmUser> findByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
