package de.bht.comanche.rest;

import static multex.MultexUtil.create;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import de.bht.comanche.logic.LgGroup;
import de.bht.comanche.logic.LgTransaction;

@Path("/group/")
public class ReGroupService extends RestService {

	@Path("getGroups")
	@POST
	@Consumes("application/json")
	@Produces({ "application/json" })
	public List<LgGroup> get(@Context final HttpServletRequest request) {
		return new LgTransaction<List<LgGroup>>(request) {
			@Override
			public List<LgGroup> execute() throws Exception {
				final List<LgGroup> result;
				try {
					result = startSession().getGroups();
				} catch (Exception ex) {
					throw create(RestGetGroupsFailure.class, ex, getSession().getUser().getName());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * Could not get groups for user "{0}"
	 */
	@SuppressWarnings("serial")
	public static final class RestGetGroupsFailure extends multex.Failure {}


	@Path("save")
	@POST
	@Consumes("application/json")
	@Produces({ "application/json" })
	public LgGroup save(final LgGroup group, @Context final HttpServletRequest request) {
		return new LgTransaction<LgGroup>(request) {
			@Override
			public LgGroup execute() throws Exception {
				final LgGroup result;
				try {
					result = startSession().save(group);
				} catch (Exception ex) {
					throw create(RestSaveGroupFailure.class, ex, group.getName(), group.getOid(), getSession().getUser().getName());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * Could not save group "{0}" with oid "{1}" for user "{2}"
	 */
	@SuppressWarnings("serial")
	public static final class  RestSaveGroupFailure extends multex.Failure {}

	@Path("delete")
	@DELETE
	@Consumes("application/json")
	@Produces({ "application/json" })
	public LgGroup delete(final long oid, @Context final HttpServletRequest request) {
		return new LgTransaction<LgGroup>(request) {
			@Override
			public LgGroup execute() throws Exception {
				try {
					startSession().deleteGroup(oid);
				} catch (Exception ex) {
					throw create(RestDeleteGroupFailure.class, ex, oid, getSession().getUser().getName() );

				}
				return null;
			}
		}.getResult();
	}

	/**
	 * Could not delete group with oid "{0}" for user "{1}"
	 */
	@SuppressWarnings("serial")
	public static final class RestDeleteGroupFailure extends multex.Failure {}

}
