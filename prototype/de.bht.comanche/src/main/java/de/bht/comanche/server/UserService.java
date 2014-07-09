package de.bht.comanche.server;

import javax.persistence.Column;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.bht.comanche.logic.LgUser;
import de.bht.comanche.persistence.DaFactory;
import de.bht.comanche.persistence.DaUser;
import de.bht.comanche.persistence.JpaDaFactory;

@Path("/user/")
//@Produces({"text/xml", "application/json"})
//@Consumes({"text/xml", "application/json"})
public class UserService {
	
	@Path("/login")
    @POST
    @Consumes("application/json")
    @Produces({"application/json"})
    public ResponseObject loginUser(final LgUser userFromClient) {
		 return new Transaction<LgUser>() {
			public LgUser executeWithThrows() throws Exception {
				DaFactory jpaDaFactory = new JpaDaFactory();
				DaUser daUser = jpaDaFactory.getDaUser();
				LgUser userFromDb = daUser.findByName(userFromClient.getName()).iterator().next();
				if (!userFromDb.validatePassword(userFromClient.getPassword())) {
					
					System.out.println(userFromClient.getPassword());
					System.out.println(userFromDb.getPassword());
					
					throw new WrongPasswordExc();
				}
				LgUser userWithId = new LgUser();
				userWithId.setIdFrom(userFromDb);
				return userWithId;
			}
   	 }.execute();
    }
	
	//get full User by Id
	@POST
	@Path("get/")
	@Consumes("application/json")
	@Produces({"application/json"})
	public ResponseObject getUser(final LgUser userIdFromClient){
		return new Transaction<LgUser>() {
			public LgUser executeWithThrows() throws Exception {
				DaFactory jpaDaFactory = new JpaDaFactory();
				DaUser daUser = jpaDaFactory.getDaUser();
				//throws Exc if Id not exist
				LgUser userFromDb = daUser.find(userIdFromClient.getOid());
				return userFromDb;
			}
   	 }.execute();
	}
   	 
     @Path("/create")
     @POST
     @Consumes("application/json")
     @Produces({"application/json"})
     public ResponseObject createUser(final LgUser newUserFromClient){
    	 return new Transaction<LgUser>() {
 			public LgUser executeWithThrows() throws Exception {
 				DaFactory jpaDaFactory = new JpaDaFactory();
 				DaUser daUser = jpaDaFactory.getDaUser();
 				//throws Exc if name not exist
 				LgUser userFromDb = daUser.findByName(newUserFromClient.getName()).iterator().next(); 
 				
 				//save new User to DbUser or LgUser?
 				LgUser newUserSaveToDb = new LgUser(); 
 				newUserSaveToDb.setName(newUserFromClient.getName());
				newUserSaveToDb.setEmail(newUserFromClient.getEmail());
				newUserSaveToDb.setPassword(newUserFromClient.getPassword());
 				return newUserSaveToDb;
 			}
    	 }.execute();
 	}
     
     
     @Path("/delete")
     @DELETE
     @Consumes("application/json")
     @Produces({"application/json"})
     public ResponseObject deleteUser(final LgUser oldUserFromClient){
    	 return new Transaction<LgUser>() {
 			public LgUser executeWithThrows() throws Exception {
 				DaFactory jpaDaFactory = new JpaDaFactory();
 				DaUser daUser = jpaDaFactory.getDaUser();
 				//throws Exc if Id exist
 				LgUser userFromDb = daUser.find(oldUserFromClient.getOid()); 
 				
 				//save new User to DbUser or LgUser?
 				LgUser newUserSaveToDb = new LgUser(); 
 				newUserSaveToDb.setName(oldUserFromClient.getName());
				newUserSaveToDb.setEmail(oldUserFromClient.getEmail());
				newUserSaveToDb.setPassword(oldUserFromClient.getPassword());
 				return newUserSaveToDb;
 			}
    	 }.execute();
 	} 
     
     
  
}




//@Path("/login")
//@GET
//@Consumes(MediaType.APPLICATION_JSON) ????
//public User login(@QueryParam("name") final String name,
//		 		   @QueryParam("password") final String password) {
//
//	 new Transaction<LgUser>() {
//		 public LgUser executeWithThrows() throws Exception {
//			 LgUser lgUser1 = new LgUser();
//			 Validation.validateName(name);
//			 Validation.validatePassword(password);
//			 DaFactory jpaDaFactory = new JpaDaFactory();
//			 DaUser daUser = jpaDaFactory.getDaUser();
//			 daUser.save(lgUser1);
//			 
//			 lgUser1 = daUser.findByName(name);
//			 lgUser1.validatePassword(password);
//			 
//			 return lgUser1;
//		 }
//	 }.execute();
//	 
//
//	 if (lgUser1.passwordCorrect) {
//		 // Build Sucess JSON
//		 
//	 } else {
//		 // Build Error JSON
//	 }
	 
	 
//}
