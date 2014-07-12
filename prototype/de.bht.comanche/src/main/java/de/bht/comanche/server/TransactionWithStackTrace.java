package de.bht.comanche.server;

import de.bht.comanche.persistence.Pool;

public abstract class TransactionWithStackTrace<E> {
	private final Pool<E> pool;
	private final boolean throwStackTrace;
	private final boolean rollback;
	
	public TransactionWithStackTrace (Pool<E> pool, boolean throwStackTrace, boolean rollback) {
		this.pool = pool;
		this.throwStackTrace = throwStackTrace;
		this.rollback = rollback;
	}
	
	public TransactionWithStackTrace (Pool<E> pool, boolean throwStackTrace) {
		this(pool, throwStackTrace, true);
	}
	
	public TransactionWithStackTrace (Pool<E> pool) {
		this(pool, true, true);
	}
	
	public boolean execute () {
		pool.beginTransaction();
		boolean success = false;
		try {
			executeWithThrows();
			success = true;
		} catch (Exception e) {
			if (throwStackTrace) {
				e.printStackTrace();
			}
		} finally {
			if (rollback) {
				pool.endTransaction(false);
			} else {
				pool.endTransaction(success);
			}
		}
		return success;
	}
	
	public void flush() {
		pool.flush();
	}
	
	public void forceTransactionEnd() {
		pool.endTransaction(true);
	}
	
	public abstract void executeWithThrows() throws Exception;
}

