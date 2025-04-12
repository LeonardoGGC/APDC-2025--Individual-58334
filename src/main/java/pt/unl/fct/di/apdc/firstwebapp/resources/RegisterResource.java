package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import pt.unl.fct.di.apdc.firstwebapp.util.data.RegisterData;

@Path("/register")
public class RegisterResource {

	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();


	public RegisterResource() {}	// Default constructor, nothing to do
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);

		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			// If the entity does not exist null is returned...
			if (user != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("User already exists.").build();
			} else {
				 // ... otherwise
				user = Entity.newBuilder(userKey).set("user_name", data.name)
						.set("user_pwd", DigestUtils.sha512Hex(data.password))
						.set("user_email", data.email)
						.set("user_phonenumber", data.phoneNumber)
						.set("user_profile_status", data.profileStatus)
						.set("user_role", "enduser")
						.set("user_state", "deactivated")
						.set("user_CC", data.changeIfEmpty(data.cc))
						.set("user_NIF", data.changeIfEmpty(data.nif))
						.set("user_employer", data.changeIfEmpty(data.employer))
						.set("user_job", data.changeIfEmpty(data.job))
						.set("user_address", data.changeIfEmpty(data.address))
						.set("user_employer_NIF", data.changeIfEmpty(data.employerNIF)).build();

				txn.put(user);
				txn.commit();
				LOG.info("User registered " + data.username);
				return Response.ok().build();
			}
		}
		catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}	

}