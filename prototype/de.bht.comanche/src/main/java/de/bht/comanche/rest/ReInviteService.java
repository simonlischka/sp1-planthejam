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

import de.bht.comanche.logic.LgInvite;
import de.bht.comanche.logic.LgTransaction;
/**
 * This class provide a LgInvite service as a network-accessible endpoint by using Representational State Transfer (RESTful) web service (JAX-RS). 
 * Jersey implements support for the annotations defined in the specification and used in this class. Resources are identified by URIs, 
 * which provide a global addressing space for resource and service discovery. The @Path annotation identifies the URI path template to which the 
 * resource responds and is specified at the class or method level of a resource. 
 * 
 * @author Maxim Novichkov
 *
 */
@Path("/invite/")
public class ReInviteService extends RestService {
	/**
	 * Returns the list of all LgInvites for current user.
	 * @param request The request information from HTTP service.
	 * @return The list of invites.
	 * @exception Throws the RestGetInvitesFailure if it was not possible to get list of invites for current user.
	 */
	@POST
	@Path("getInvites")
	@Consumes("application/json")
	@Produces({ "application/json" })
	public List<LgInvite> get(@Context final HttpServletRequest request) {
		return new LgTransaction<List<LgInvite>>(request) {
			@Override
			public List<LgInvite> execute() throws Exception {
				final List<LgInvite> result;
				try {
					result = startSession().getInvites();
				} catch (Exception ex) {
					throw create(RestGetInvitesFailure.class, ex, getSession().getUser().getName());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * Could not get invites for user "{0}"
	 */
	@SuppressWarnings("serial")
	public static final class RestGetInvitesFailure extends multex.Failure {}

	/**
	 * Returns the LgInvite specified by oid.
	 * @param oid The LgInvite oid.
	 * @param request The request information from HTTP service.
	 * @return The LgInvite.
	 * @exception Throws the RestGetInviteFailure if it was not possible to get invite for current user.
	 */
	@POST
	@Path("get")
	@Consumes("application/json")
	@Produces({ "application/json" })
	public LgInvite get(final long oid, @Context final HttpServletRequest request) {
		return new LgTransaction<LgInvite>(request) {
			@Override
			public LgInvite execute() throws Exception {
				final LgInvite result;
				try {
					result = startSession().getInvite(oid);
				} catch (Exception ex) {
					throw create(RestGetInviteFailure.class, ex, oid, getSession().getUser().getName());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * Could not get invite with oid "{0}" for user "{1}"
	 */
	@SuppressWarnings("serial")
	public static final class RestGetInviteFailure extends multex.Failure {}

	/**
	 * Save incoming LgInvite for current user. 
	 * @param invite The incoming LgInvite.
	 * @param request The request information from HTTP service.
	 * @exception Throws the RestSaveInviteFailure if it was not possible to save invite for current user.
	 */
	@Path("save")
	@POST
	@Consumes("application/json")
	@Produces({ "application/json" })
	public LgInvite save(final LgInvite invite, @Context final HttpServletRequest request) {
		return new LgTransaction<LgInvite>(request) {
			public LgInvite execute() throws Exception {
				final LgInvite result;
				try {
					result = startSession().save(invite);
				} catch (Exception ex) {
					throw create(RestSaveInviteFailure.class, ex, invite.getOid(), getSession().getUser().getName());
				}
				return result;
			}
		}.getResult();
	}

	/**
	 * Could not save invite with oid "{0}" for user "{1}"
	 */
	@SuppressWarnings("serial")
	public static final class RestSaveInviteFailure extends multex.Failure {}

	/**
	 * Delete LgInvite by specified oid.
	 * @param oid The LgInvite oid.
	 * @param request The request information from HTTP service.
	 * @exception Throws the RestDeleteInviteFailure if it was not possible to delete invite of current user.
	 */
	@Path("delete")
	@DELETE
	@Consumes("application/json")
	@Produces({ "application/json" })
	public LgInvite delete(final long oid, @Context final HttpServletRequest request) {
		return new LgTransaction <LgInvite>(request) {
			public LgInvite execute() throws Exception {
				try {
					startSession().deleteInvite(oid);
				} catch (Exception ex) {
					throw create(RestDeleteInviteFailure.class, ex, oid, getSession().getUser().getName());
				}
				return null;
			}
		}.getResult();
	}

	/**
	 * Could not delete invite with oid "{0}" for user "{1}"
	 */
	@SuppressWarnings("serial")
	public static final class RestDeleteInviteFailure extends multex.Failure {}

}
