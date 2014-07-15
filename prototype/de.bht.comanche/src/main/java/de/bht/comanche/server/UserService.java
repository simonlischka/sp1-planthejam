package de.bht.comanche.server;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import de.bht.comanche.logic.LgUser;
import de.bht.comanche.persistence.DaUser;
import de.bht.comanche.server.exceptions.logic.NoUserWithThisIdException;
import de.bht.comanche.server.exceptions.logic.NoUserWithThisNameException;
import de.bht.comanche.server.exceptions.logic.UserWithThisNameExistsException;
import de.bht.comanche.server.exceptions.logic.WrongPasswordException;
import de.bht.comanche.server.exceptions.persistence.OidNotFoundException;

@Path("/user/")
public class UserService extends Service {
	public UserService() {
		super();
	}

	@Path("login")
	@POST
	@Consumes("application/json")
	@Produces({ "application/json" })
	public ResponseObject loginUser(final LgUser userFromClient) {
		final DaUser daUser = factory.getDaUser();
		ResponseObject response = new Transaction<LgUser>(daUser.getPool()) {
			public LgUser executeWithThrows() throws Exception {
				List<LgUser> users = daUser.findByName(userFromClient.getName());
				if (users.isEmpty()) {
					throw new NoUserWithThisNameException();
				}
				LgUser userFromDb = users.get(0);
				if (!userFromDb.passwordMatchWith(userFromClient)) {
					throw new WrongPasswordException();
				}
				LgUser userWithId = new LgUser();
				userWithId.setIdFrom(userFromDb);
				return userWithId;
			}
		}.execute();
		
		if (response.hasError()) {
			throw new WebApplicationException(response.getResponseCode());
		}
		return response;
	}
	
	/**
	 * Return complete User by Id
	 * @param userFromClient
	 * @return
	 */
	@POST
	@Path("getUser")
	@Consumes("application/json")
	@Produces({"application/json"})
	public ResponseObject getUser(final LgUser userFromClient){
		final DaUser daUser = factory.getDaUser();
		ResponseObject response = new Transaction<LgUser>(daUser.getPool()) {
			public LgUser executeWithThrows() throws Exception {
				System.out.println("HUHU from GET");
				System.out.println(userFromClient.getOid());
				
				LgUser lgUser = null;
				try{
				lgUser = daUser.find(userFromClient.getOid());
				} catch (OidNotFoundException oid){
					System.out.println("WRONG ID");
					throw new NoUserWithThisIdException();
				}
				
				System.out.println("RETURNING USER");
				return lgUser;
			}
		}.execute();

		if (response.hasError()) {
			System.out.println("RESPONSE HAS ERROR");
			throw new WebApplicationException(response.getResponseCode());
		}
		
		System.out.println("RESPONSE HAS RESPONSE");
		return response;
	}
   	 
     @Path("register")
     @POST
     @Consumes("application/json")
     @Produces({"application/json"})
     public ResponseObject registerUser(final LgUser newUserFromClient){
    	 final DaUser daUser = factory.getDaUser();
    	 ResponseObject response = new Transaction<LgUser>(daUser.getPool()) {
    		 public LgUser executeWithThrows() throws Exception {
    			 
    			 System.out.println(newUserFromClient);
    			 
    			 String name = newUserFromClient.getName();
    			 List<LgUser> users = daUser.findByName(name);
    			 if (!users.isEmpty()) {
    				 throw new UserWithThisNameExistsException();
    			 }
//    			 LgUser userSaveToDb = new LgUser();
//    			 userSaveToDb.setName(name);
//    			 userSaveToDb.setPassword(newUserFromClient.getPassword());
//    			 userSaveToDb.setEmail(newUserFromClient.getEmail());
//    			 userSaveToDb.setTel(newUserFromClient.getTel());
//    			 daUser.save(userSaveToDb);
    			 daUser.save(newUserFromClient);
//    			 daUser.flush();
    			 
    			 //POST request with this data
//    			 LgUser sendToClient = new LgUser();
//    			 sendToClient.setName(userSaveToDb.getName());
//    			 sendToClient.setPassword(userSaveToDb.getPassword());
//    			 return sendToClient;
    			 return newUserFromClient;
    		 }
    	 }.execute();

    	 if (response.hasError()) {
 			throw new WebApplicationException(response.getResponseCode());
 		}
    	 
    	 return response;
 	}
     
     @Path("delete")
     @DELETE
     @Consumes("application/json")
     @Produces({"application/json"})
     public ResponseObject deleteUser(final LgUser userFromClient){
    	final DaUser daUser = factory.getDaUser();
  		ResponseObject response = new Transaction<LgUser>(daUser.getPool()) {
  			public LgUser executeWithThrows() throws Exception {
  				LgUser userFromDb = null;
  				try{
  					userFromDb = daUser.find(userFromClient.getOid());
  				} catch (OidNotFoundException oid) {
  					throw new NoUserWithThisIdException();
  				}
    			daUser.delete(userFromDb);
    			return null;
    		 }
    	 }.execute();
    	 
    	 if (response.hasError()) {
  			throw new WebApplicationException(response.getResponseCode());
  		}
     	 
     	 return response;
 	} 
     
     @Path("update")
     @POST
     @Consumes("application/json")
     @Produces({"application/json"})
     public ResponseObject updateUser(final LgUser updateUserFromClient){
    	final DaUser daUser = factory.getDaUser();
  		ResponseObject response = new Transaction<LgUser>(daUser.getPool()) {
  			public LgUser executeWithThrows() throws Exception {
//				List<LgUser> users = daUser.findByName(updateUserFromClient.getName());
//				if (users.isEmpty()) {
//					throw new NoUserWithThisNameException();
//				}
//				LgUser saveUsertoDb = users.get(0);^
  				
  				System.out.println(updateUserFromClient);
  				LgUser fromDb = daUser.find(updateUserFromClient.getOid());
  				fromDb.updateWith(updateUserFromClient);
//				daUser.save(updateUserFromClient);
  				daUser.save(fromDb);
				
//				saveUsertoDb.updateWith(updateUserFromClient);
//    			daUser.save(saveUsertoDb);
//    			return saveUsertoDb;
  				return null;
    		 }
    	 }.execute();

    	 if (response.hasError()) {
   			throw new WebApplicationException(response.getResponseCode());
   		}
      	 
      	 return response;
     }
}

