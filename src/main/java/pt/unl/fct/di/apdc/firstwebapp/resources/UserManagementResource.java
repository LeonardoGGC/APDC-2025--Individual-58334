package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.QOLfunctions;
import pt.unl.fct.di.apdc.firstwebapp.util.wrappers.*;
import pt.unl.fct.di.apdc.firstwebapp.util.data.*;

@Path("/user")
public class UserManagementResource {

	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private static final String TOKEN_EXPIRATION_DATA = "token_expiration_data";

	private static final String USER_NAME = "user_name";
	private static final String USER_PWD = "user_pwd";
	private static final String USER_PHONENUMBER = "user_phonenumber";
	private static final String USER_EMAIL = "user_email";
	private static final String USER_ROLE = "user_role";
	private static final String USER_STATE = "user_state";
	private static final String USER_CC = "user_CC";
	private static final String USER_NIF = "user_NIF";
	private static final String USER_EMPLOYER = "user_employer";
	private static final String USER_JOB = "user_job";
	private static final String USER_ADDRESS = "user_address";
	private static final String USER_EMPLOYER_NIF = "user_employer_NIF";
	private static final String USER_PROFILE_STATUS = "user_profile_status";

	private static final String USER_DOES_NOT_EXIST = "User doesn't exist";

	private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
	private static final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("Token");

	private final Gson g = new Gson();

	public UserManagementResource() {
	} // Default constructor, nothing to do

	@POST
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeRole(WrapperRoleChange wrapper) {
		AuthToken token = wrapper.token;
		RoleChangeData data = wrapper.data;

		LOG.fine("Attempt to change role of user: " + token.username);

		if (!data.validChange()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}

		Transaction txn = datastore.newTransaction();

		try {

			Response r = isValid(txn, token);

			if (!(r.getStatus() == 200))
				return r;

			Key userKey = userKeyFactory.newKey(data.username);

			Entity user = txn.get(userKey);

			if (user == null) {
				LOG.warning("Cant change role of " + data.username);
				return Response.status(Status.FORBIDDEN)
						.entity(USER_DOES_NOT_EXIST)
						.build();
			}

			if (QOLfunctions.isEnduser(token.role) || QOLfunctions.isPartner(token.role)) {
				LOG.warning("Endusers and partners can't change anyone's roles.");
				return Response.status(Status.FORBIDDEN)
						.entity("Endusers and partners can't change anyone's roles.")
						.build();
			}

			if (QOLfunctions.isBackOffice(token.role) && !(QOLfunctions.isEnduser(user.getString(USER_ROLE))
					|| QOLfunctions.isPartner(user.getString(USER_ROLE)))) {
				LOG.warning("Backofficers can only change roles of endusers and partners.");
				return Response.status(Status.FORBIDDEN)
						.entity("Backofficers can only change roles of endusers and partners.")
						.build();
			}

			if (QOLfunctions.isBackOffice(token.role)
					&& !(QOLfunctions.isEnduser(data.role) || QOLfunctions.isPartner(data.role))) {
				LOG.warning("Backofficers can only change roles to enduser or partner.");
				return Response.status(Status.FORBIDDEN)
						.entity("Backofficers can only change roles to enduser or partner.")
						.build();
			}

			user = Entity.newBuilder(txn.get(userKey)).set(USER_ROLE, data.role).build();

			txn.put(user);

			deleteAllTokenFromUser(txn, data.username);
			
			txn.commit();
			LOG.info("Role changed of " + data.username);
			return Response.ok().build();

		} catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/state")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeState(WrapperStateChange wrapper) {
		AuthToken token = wrapper.token;
		StateChangeData data = wrapper.data;

		LOG.fine("Attempt to change state of user: " + token.username);

		if (!data.isValid()) {
			return Response.status(Status.BAD_REQUEST).entity("Wrong parameter.").build();
		}

		Transaction txn = datastore.newTransaction();

		try {

			Response r = isValid(txn, token);

			if (!(r.getStatus() == 200))
				return r;

			Key userKey = userKeyFactory.newKey(data.username);

			Entity user = txn.get(userKey);

			if (user == null) {
				LOG.warning("Cant change state of " + data.username);
				return Response.status(Status.FORBIDDEN)
						.entity(USER_DOES_NOT_EXIST)
						.build();
			}

			if (QOLfunctions.isEnduser(token.role) || QOLfunctions.isPartner(token.role)) {
				LOG.warning("Endusers and partners can't change the state of anyone");
				return Response.status(Status.FORBIDDEN)
						.entity("Endusers and partners can't change the state of anyone")
						.build();
			}

			if (QOLfunctions.isBackOffice(token.role) && !(QOLfunctions.isEnduser(user.getString(USER_ROLE))
					|| QOLfunctions.isPartner(user.getString(USER_ROLE)))) {
				LOG.warning("Backofficers can only change states of partners or endusers");
				return Response.status(Status.FORBIDDEN)
						.entity("Backofficers can only change states of partners or endusers")
						.build();
			}

			user = Entity.newBuilder(txn.get(userKey)).set(USER_STATE, data.state).build();

			txn.put(user);
			txn.commit();
			LOG.info("State changed of " + data.username);
			return Response.ok().build();

		} catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeUser(WrapperRemoveUser wrapper) {

		AuthToken token = wrapper.token;
		RemoveUserData data = wrapper.data;

		LOG.fine("Attempt to remove state of user: " + token.username);

		if (!data.isValid()) {
			return Response.status(Status.BAD_REQUEST).entity("Wrong parameter.").build();
		}

		Transaction txn = datastore.newTransaction();

		try {
			Response r = isValid(txn, token);

			if (!(r.getStatus() == 200))
				return r;

			Key userKey = userKeyFactory.newKey(data.username);

			Entity user = txn.get(userKey);

			if (user == null) {
				LOG.warning("Can't remove user: " + token.username);
				return Response.status(Status.FORBIDDEN)
						.entity(USER_DOES_NOT_EXIST)
						.build();
			}

			if (QOLfunctions.isEnduser(token.role) || QOLfunctions.isPartner(token.role)) {
				LOG.warning("Endusers and partners can't remove anyone's account");
				return Response.status(Status.FORBIDDEN)
						.entity("Endusers and partners can't remove anyone's account")
						.build();
			}

			if (QOLfunctions.isBackOffice(token.role) && !(QOLfunctions.isEnduser(user.getString(USER_ROLE))
					|| QOLfunctions.isPartner(user.getString(USER_ROLE)))) {
				LOG.warning("Backofficers can only remove accounts of endusers and partners");
				return Response.status(Status.FORBIDDEN)
						.entity("Backofficers can only remove accounts of endusers and partners")
						.build();
			}

			deleteAllTokenFromUser(txn, data.username);

			txn.delete(userKey);
			txn.commit();
			LOG.info("User removed: " + data.username);
			return Response.ok().build();

		} catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUsers(AuthToken token) {
		LOG.fine("Attempt to list users, request made by: " + token.username);

		Transaction txn = datastore.newTransaction();

		try {
			Response r = isValid(txn, token);

			if (!(r.getStatus() == 200))
				return r;

			if (QOLfunctions.isEnduser(token.role) || QOLfunctions.isPartner(token.role)) {
				Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(
								CompositeFilter.and(
										PropertyFilter.eq(USER_ROLE, "enduser"),
										PropertyFilter.eq(USER_STATE, "activated"),
										PropertyFilter.eq(USER_PROFILE_STATUS, "public")))
						.build();

				QueryResults<Entity> results = datastore.run(query);

				List<List<String>> users = new ArrayList<List<String>>();
				results.forEachRemaining(user -> {
					List<String> l = new ArrayList<String>();
					l.add("username");
					l.add(user.getKey().getName());
					user.getProperties().forEach((propertyName, property) -> {
						if (propertyName.equals(USER_EMAIL) || propertyName.equals(USER_NAME)) {
							l.add(propertyName);
							l.add((String) property.get());
						}
					});
					users.add(l);
				});

				return Response.ok(g.toJson(users)).build();
			}

			if (QOLfunctions.isBackOffice(token.role)) {
				Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq(USER_ROLE, "enduser")).build();

				QueryResults<Entity> results = datastore.run(query);

				List<List<String>> users = new ArrayList<List<String>>();
				results.forEachRemaining(user -> {
					List<String> l = new ArrayList<String>();
					l.add("username");
					l.add(user.getKey().getName());
					user.getProperties().forEach((propertyName, property) -> {
						if (!propertyName.equals(USER_PWD)) {
							l.add(propertyName);
							l.add((String) property.get());
						}
					});
					users.add(l);
				});

				return Response.ok(g.toJson(users)).build();
			}

			Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").build();

			QueryResults<Entity> results = datastore.run(query);

			List<List<String>> users = new ArrayList<List<String>>();
			results.forEachRemaining(user -> {
				List<String> l = new ArrayList<String>();
				l.add("username");
				l.add(user.getKey().getName());
				user.getProperties().forEach((propertyName, property) -> {
					if (!propertyName.equals(USER_PWD)) {
						l.add(propertyName);
						l.add((String) property.get());
					}
				});
				users.add(l);
			});

			return Response.ok(g.toJson(users)).build();

		} catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(WrapperAccountChange wrapper) {

		AuthToken token = wrapper.token;
		AccountChangeData data = wrapper.data;

		LOG.fine("Attempt to change data of user: " + data.originalUsername);

		if (!data.validChange()) {
			return Response.status(Status.BAD_REQUEST).entity("Wrong parameter.").build();
		}

		Transaction txn = datastore.newTransaction();

		try {
			Response r = isValid(txn, token);

			if (!(r.getStatus() == 200))
				return r;

			Key userKey = userKeyFactory.newKey(data.originalUsername);

			Entity user = txn.get(userKey);

			if (user == null) {
				LOG.warning("Cant change data of " + data.originalUsername);
				return Response.status(Status.FORBIDDEN)
						.entity(USER_DOES_NOT_EXIST)
						.build();
			}

			if ((QOLfunctions.isEnduser(token.role) || QOLfunctions.isPartner(token.role))
					&& data.invalidChangeForEnduser()) {
				LOG.warning("Wrong parameters for endusers or partners.");
				return Response.status(Status.FORBIDDEN)
						.entity("Wrong parameters for endusers or partners.")
						.build();
			}

			if ((QOLfunctions.isEnduser(token.role) || QOLfunctions.isPartner(token.role))
					&& !data.originalUsername.equalsIgnoreCase(token.username)) {
				LOG.warning("Endusers and partners can only change their own account.");
				return Response.status(Status.FORBIDDEN)
						.entity("Endusers and partners can only change their own account.")
						.build();
			}

			if (QOLfunctions.isBackOffice(token.role) && !(QOLfunctions.isEnduser(user.getString(USER_ROLE))
					|| QOLfunctions.isPartner(user.getString(USER_ROLE)))) {
				LOG.warning("Backofficers can only change data for endusers and partners.");
				return Response.status(Status.FORBIDDEN)
						.entity("Backofficers can only change data for endusers and partners.")
						.build();
			}

			if (QOLfunctions.isBackOffice(token.role) && data.invalidChangeForBackoffice()) {
				LOG.warning("Backofficers can't change usernames or emails of users");
				return Response.status(Status.FORBIDDEN)
						.entity("Backofficers can't change usernames or emails of users")
						.build();
			}
			
			if(data.nonEmptyOrBlankField(data.username)){
				Key newUserKey = userKeyFactory.newKey(data.username);

				Entity updatedUser = txn.get(newUserKey);

				if (updatedUser != null) {
					LOG.warning("Username already exists " + data.username);
					return Response.status(Status.FORBIDDEN)
							.entity("USERNAME ALREADY EXISTS.")
							.build();
				}
				txn.delete(userKey);

				user = Entity.newBuilder(newUserKey)
								.set(USER_NAME, data.update(data.name, user.getString(USER_NAME)))
								.set(USER_PHONENUMBER, data.update(data.phoneNumber, user.getString(USER_PHONENUMBER)))
								.set(USER_PWD, user.getString(USER_PWD))
								.set(USER_EMAIL, data.update(data.email, user.getString(USER_EMAIL)))
								.set(USER_ROLE, data.update(data.role, user.getString(USER_ROLE)))
								.set(USER_CC, data.update(data.cc, user.getString(USER_CC)))
								.set(USER_NIF, data.update(data.nif, user.getString(USER_NIF)))
								.set(USER_EMPLOYER, data.update(data.employer, user.getString(USER_EMPLOYER)))
								.set(USER_ADDRESS, data.update(data.address, user.getString(USER_ADDRESS)))
								.set(USER_EMPLOYER_NIF, data.update(data.employerNIF, user.getString(USER_EMPLOYER_NIF)))
								.set(USER_PROFILE_STATUS, data.update(data.profileStatus, user.getString(USER_PROFILE_STATUS)))
								.set(USER_STATE, data.update(data.state, user.getString(USER_STATE)))
								.set(USER_JOB, data.update(data.job, user.getString(USER_JOB))).build();

				txn.put(user);

				deleteAllTokenFromUser(txn, data.originalUsername);

				txn.commit();
				return Response.ok().build();
			}

			user = Entity.newBuilder(userKey)
							.set(USER_NAME, data.update(data.name, user.getString(USER_NAME)))
							.set(USER_PWD, user.getString(USER_PWD))
							.set(USER_PHONENUMBER, data.update(data.phoneNumber, user.getString(USER_PHONENUMBER)))
							.set(USER_EMAIL, data.update(data.email, user.getString(USER_EMAIL)))
							.set(USER_ROLE, data.update(data.role, user.getString(USER_ROLE)))
							.set(USER_CC, data.update(data.cc, user.getString(USER_CC)))
							.set(USER_NIF, data.update(data.nif, user.getString(USER_NIF)))
							.set(USER_EMPLOYER, data.update(data.employer, user.getString(USER_EMPLOYER)))
							.set(USER_ADDRESS, data.update(data.address, user.getString(USER_ADDRESS)))
							.set(USER_EMPLOYER_NIF, data.update(data.employerNIF, user.getString(USER_EMPLOYER_NIF)))
							.set(USER_PROFILE_STATUS, data.update(data.profileStatus, user.getString(USER_PROFILE_STATUS)))
							.set(USER_STATE, data.update(data.state, user.getString(USER_STATE)))
							.set(USER_JOB, data.update(data.job, user.getString(USER_JOB))).build();

			if(data.nonEmptyOrBlankField(data.role)) deleteAllTokenFromUser(txn, data.originalUsername);

			txn.put(user);
			txn.commit();
			LOG.info("Updated " + data.originalUsername);
			return Response.ok().build();

		} catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/changePWD")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePWD(WrapperChangePassword wrapper) {

		AuthToken token = wrapper.token;
		ChangePasswordData data = wrapper.data;

		LOG.fine("Attempt to change pwd of user: " + token.username);

		if (!data.verified()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong password or confirmation.").build();
		}

		Transaction txn = datastore.newTransaction();

		try {
			Response r = isValid(txn, token);

			if (!(r.getStatus() == 200))
				return r;

			Key userKey = userKeyFactory.newKey(token.username);

			Entity user = txn.get(userKey);

			if (user.getString(USER_PWD).equals(DigestUtils.sha512Hex(data.password))) {
				return Response.status(Status.BAD_REQUEST).entity("Can't change to same password.").build();
			}

			user = Entity.newBuilder(txn.get(userKey))
					.set(USER_PWD, DigestUtils.sha512Hex(data.password)).build();

			txn.put(user);
			txn.commit();
			LOG.info("PWD changed of " + token.username);
			return Response.ok().build();

		} catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(AuthToken token) {
		LOG.fine("Attempt to logout user: " + token.username);

		Transaction txn = datastore.newTransaction();

		try {
			Key tokenKey = tokenKeyFactory.newKey(token.magicNumber);

			Entity t = txn.get(tokenKey);

			if (t == null) {
				LOG.warning("Token not found");
				return Response.status(Status.FORBIDDEN)
						.entity("TOKEN IS NOT VALID.")
						.build();
			}

			if (t.getLong(TOKEN_EXPIRATION_DATA) < System.currentTimeMillis()) {
				LOG.warning("Token not valid");
				txn.delete(tokenKey);
				txn.commit();
				return Response.status(Status.FORBIDDEN)
						.entity("TOKEN IS NOT VALID.")
						.build();
			}

			txn.delete(tokenKey);

			txn.commit();
			LOG.info("Loged out user: " + token.username);
			return Response.ok().build();

		} catch (DatastoreException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	private Response isValid(Transaction txn, AuthToken token) {
		Key tokenKey = tokenKeyFactory.newKey(token.magicNumber);

		Entity t = txn.get(tokenKey);

		if (t == null) {
			LOG.warning("Token not found");
			return Response.status(Status.FORBIDDEN)
					.entity("TOKEN IS NOT VALID.")
					.build();
		}

		if (t.getLong(TOKEN_EXPIRATION_DATA) < System.currentTimeMillis()) {
			LOG.warning("Token is expired");
			txn.delete(tokenKey);
			txn.commit();
			return Response.status(Status.FORBIDDEN)
					.entity("TOKEN IS EXPIRED.")
					.build();
		}

		return Response.ok().build();
	}

	private void deleteAllTokenFromUser(Transaction txn, String username) {

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Token")
						.setFilter(PropertyFilter.eq("token_username", username)).build();

		QueryResults<Entity> results = datastore.run(query);

		results.forEachRemaining(token -> {
			Key tokenKey = tokenKeyFactory.newKey(token.getKey().getName());

			txn.delete(tokenKey);
		});
	}

}