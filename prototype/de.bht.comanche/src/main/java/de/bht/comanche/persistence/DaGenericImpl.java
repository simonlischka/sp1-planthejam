package de.bht.comanche.persistence;

import java.util.List;

import de.bht.comanche.persistence.DaPoolImpl.DaNoPersistentClassExc;
import de.bht.comanche.persistence.DaPoolImpl.DaOidNotFoundExc;

//---------------last multex ready----------------
public class DaGenericImpl<E> implements DaGeneric<E> {
	private Class<E> type;
	private DaPool<E> pool;
	
	public DaGenericImpl(Class<E> type, DaPool<E> pool) {
		this.type = type;
		this.pool = pool;
	}

	@Override
	public void save(E entity) {
		pool.save(entity);
	}

	@Override
	public void delete(E entity) {
		pool.delete(entity);
	}
	
	@Override
	public E update(E io_object) {
		return pool.merge(io_object);
	}
	
	@Override
	public E find(long id) throws DaNoPersistentClassExc, DaOidNotFoundExc {
		return pool.find(type, id);
	}

	@Override
	public List<E> findAll() throws DaNoPersistentClassExc {
		return pool.findAll(type);
	}

	@Override
	public List<E> findByField(String fieldName, Object fieldValue) throws DaNoPersistentClassExc { 
		final String OBJECT_NAME = type.getSimpleName();
		String [] args = {
				fieldName,
				OBJECT_NAME,
				(String) fieldValue
		};
		return pool.findManyByQuery(type, "SELECT c FROM %2$s AS c WHERE c.%1$s LIKE '%3$s'", args);
	}

	@Override
	public void beginTransaction() {
		pool.beginTransaction();
	}

	@Override
	public void endTransaction(boolean success) {
		pool.endTransaction(success);
	}
	
	public DaPool getPool() {
		return this.pool;
	}
	
	public void setPool(DaPool pool) {
		this.pool = pool;
	}
}
