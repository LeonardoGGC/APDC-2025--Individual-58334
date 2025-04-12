package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;
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
import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.QOLfunctions;
import pt.unl.fct.di.apdc.firstwebapp.util.data.WorkSheetData;
import pt.unl.fct.di.apdc.firstwebapp.util.wrappers.WrapperWorkSheet;

@Path("/worksheet")
public class WorkSheetManagementResource {

	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	
	public WorkSheetManagementResource() {}	// Default constructor, nothing to do
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerOrUpdateWorkSheet(WrapperWorkSheet wrapper) {

		AuthToken token = wrapper.token;
		WorkSheetData data = wrapper.data;

		LOG.fine("Attempt to register worksheet: " + data.worksheetId);

		if (!data.isValid()) {
			return Response.status(Status.BAD_REQUEST).entity("Wrong parameters.").build();
		}

		Transaction txn = datastore.newTransaction();
		try {

			Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(token.magicNumber);

			Entity t = txn.get(tokenKey);

			if (t == null) {
				LOG.warning("Token not found");
				return Response.status(Status.FORBIDDEN)
						.entity("TOKEN IS NOT VALID.")
						.build();
			}

			if (t.getLong("token_expiration_data") < System.currentTimeMillis()) {
				LOG.warning("Token not valid");
				txn.delete(tokenKey);
				txn.commit();
				return Response.status(Status.FORBIDDEN)
						.entity("TOKEN IS NOT VALID.")
						.build();
			}

			Key worksheetKey = datastore.newKeyFactory().setKind("Worksheet").newKey(data.worksheetId);

			Entity worksheet = txn.get(worksheetKey);

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.username);

			Entity user = txn.get(userKey);
			
			if (worksheet != null) {

				if (!data.validUpdate()) {
					return Response.status(Status.BAD_REQUEST).entity("Wrong parameters for update.").build();
				}

				if(QOLfunctions.isPartner(token.role)){
					if(!worksheet.getString("worksheet_partner_username").equals(token.username)){
						LOG.warning("This partner account doesn't have permission.");
						return Response.status(Status.FORBIDDEN)
								.entity("This partner account doesn't have permission.")
								.build();
					}

					if(!data.validChangeForPartner()){
						LOG.warning("Wrong parameters.");
						return Response.status(Status.FORBIDDEN)
								.entity("Wrong parameters.")
								.build();
					}

					worksheet = Entity.newBuilder(txn.get(worksheetKey)).set("worksheet_status", data.status).build();

					txn.put(worksheet);
					txn.commit();
					return Response.ok().build();
				}

				if(!QOLfunctions.isBackOffice(token.role)){
					LOG.warning("Only backofficers and partners can change a worksheet.");
					return Response.status(Status.FORBIDDEN)
							.entity("Only backofficers and partners can change a worksheet.")
							.build();
				}

				if(worksheet.getString("worksheet_adjudicated_status").equalsIgnoreCase("not adjudicated")){
					if(data.adjudicatedStatus.equalsIgnoreCase("adjudicated") && !data.validAdjudicatedRegistration()){
						return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter for adjudicated update.").build();
					}
					if(!data.adjudicatedStatus.equalsIgnoreCase("adjudicated") && !data.validNonAdjudicatedRegistration()){
						return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter for not adjudicated update.").build();
					}
				}

				if(data.nonEmptyOrBlankField(data.username)){
					Key partnerUserKey = datastore.newKeyFactory().setKind("User").newKey(data.username);

					Entity partner = txn.get(partnerUserKey);

					if (partner == null) {
						LOG.warning("Partner does not exist: " + data.username);
						return Response.status(Status.FORBIDDEN)
								.entity("partner does not exist")
								.build();
					}

					if(!partner.getString("user_role").equalsIgnoreCase("partner")){
						LOG.warning("User is not a partner: " + data.username);
						return Response.status(Status.FORBIDDEN)
								.entity("User " + data.username + " is not a partner")
								.build();
					}
				}

				worksheet = Entity.newBuilder(txn.get(worksheetKey))
						.set("worksheet_description", data.update(data.description, worksheet.getString("worksheet_description")))
						.set("worksheet_adjudicated_by", data.update(data.adjudicatedBy, worksheet.getString("worksheet_adjudicated_by")))
						.set("worksheet_adjudicated_status", data.update(data.adjudicatedStatus, worksheet.getString("worksheet_adjudicated_status")))
						.set("worksheet_adjudicator_entity_nif", data.update(data.nif, worksheet.getString("worksheet_adjudicator_entity_nif")))
						.set("worksheet_observations", data.update(data.observations, worksheet.getString("worksheet_observations")))
						.set("worksheet_status", data.update(data.status, worksheet.getString("worksheet_status")))
						.set("worksheet_target_type", data.update(data.targetType, worksheet.getString("worksheet_target_type")))
						.set("worksheet_partner_username", data.update(data.username, worksheet.getString("worksheet_partner_username")))
						.set("worksheet_adjudication_date", data.update(data.dateOfadjudication, worksheet.getString("worksheet_adjudication_date")))
						.set("worksheet_predicted_end", data.update(data.predictedEnd, worksheet.getString("worksheet_predicted_end")))
						.set("worksheet_predicted_start", data.update(data.predictedStart, worksheet.getString("worksheet_predicted_start"))).build();

				txn.put(worksheet);
				txn.commit();
				LOG.info("Worksheet updated " + data.worksheetId);
				return Response.ok().build();
			
			} else {

				if (!data.validRegistration()) {
					return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter for registration.").build();
				}

				if(!QOLfunctions.isBackOffice(token.role)){
					LOG.warning("Only backofficers can register a worksheet");
					return Response.status(Status.FORBIDDEN)
							.entity("Only backofficers can register a worksheet")
							.build();
				}

				if (!data.validAdjudicatedRegistration() && data.adjudicatedStatus.equalsIgnoreCase("adjudicated")) {
					return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter for adjudicated registration.").build();
				}

				if (!data.validNonAdjudicatedRegistration() && data.adjudicatedStatus.equalsIgnoreCase("not adjudicated")) {
					return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter for non adjudicated registration.").build();
				}

				if(data.adjudicatedStatus.equalsIgnoreCase("adjudicated")){
					Key partnerUserKey = datastore.newKeyFactory().setKind("User").newKey(data.username);

					Entity partner = txn.get(partnerUserKey);

					if (partner == null) {
						LOG.warning("Partner does not exist: " + data.username);
						return Response.status(Status.FORBIDDEN)
								.entity("partner does not exist")
								.build();
					}

					if(!partner.getString("user_role").equalsIgnoreCase("partner")){
						LOG.warning("User is not a partner: " + data.username);
						return Response.status(Status.FORBIDDEN)
								.entity("User " + data.username + " is not a partner")
								.build();
					}
				}

				worksheet = Entity.newBuilder(worksheetKey).set("worksheet_description", data.description)
						.set("worksheet_adjudicated_by", data.adjudicatedBy)
						.set("worksheet_adjudicated_status", data.adjudicatedStatus)
						.set("worksheet_adjudicator_entity_nif", data.nif)
						.set("worksheet_observations", data.observations)
						.set("worksheet_status", data.status)
						.set("worksheet_target_type", data.targetType)
						.set("worksheet_adjudication_date", data.dateOfadjudication)
						.set("worksheet_predicted_start", data.predictedStart)
						.set("worksheet_predicted_end", data.predictedStart)
						.set("worksheet_partner_username", data.username).build();

				txn.put(worksheet);
				txn.commit();
				LOG.info("Worksheet registered " + data.worksheetId);
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