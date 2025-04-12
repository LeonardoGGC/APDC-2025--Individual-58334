package pt.unl.fct.di.apdc.firstwebapp.util.data;


public class WorkSheetData {

    public String worksheetId;
 	public String description;
 	public String targetType;
 	public String adjudicatedStatus;
 	public String dateOfadjudication;
	public String predictedStart;
	public String predictedEnd;
	public String username;
	public String adjudicatedBy;
	public String nif;
	public String status;
	public String observations;
 	
 	
 	public WorkSheetData() {
 		
 	}
 	
 	public WorkSheetData(String worksheetId, String description, String targetType,
						String adjudicatedStatus, String dateOfadjudication, String predictedStart, String predictedEnd,
						String username, String adjudicatedBy, String nif,
						String status, String observations) {
 		this.username = username;
 		this.description = description;
 		this.worksheetId = worksheetId;
 		this.targetType = targetType;
 		this.adjudicatedStatus = adjudicatedStatus;
		this.dateOfadjudication = dateOfadjudication;
		this.predictedStart = predictedStart;
		this.predictedEnd = predictedEnd;
		this.adjudicatedBy = adjudicatedBy;
		this.nif = nif;
		this.status = status;
		this.observations = observations;
 	}
 	
 	public boolean nonEmptyOrBlankField(String field) {
 		return field != null && !field.isBlank();
 	}

    public String update(String field, String originalField){
        String result = originalField;
        if(nonEmptyOrBlankField(field)) result = field;
        return result;
    }

    public boolean isValid() {
 		 	
        return nonEmptyOrBlankField(worksheetId);
    }
 	
 	public boolean validRegistration() {
 		 	
 		return nonEmptyOrBlankField(description) &&
 			   nonEmptyOrBlankField(targetType) &&
                (targetType.equalsIgnoreCase("private property") || targetType.equalsIgnoreCase("public property")) &&
 			   nonEmptyOrBlankField(adjudicatedStatus) &&
                (adjudicatedStatus.equalsIgnoreCase("adjudicated") || adjudicatedStatus.equalsIgnoreCase("not adjudicated"));
 	}

     public boolean validUpdate() {
 		 	
        return (!nonEmptyOrBlankField(status) ||
                    (status.equalsIgnoreCase("not started") || status.equalsIgnoreCase("ongoing") || status.equalsIgnoreCase("finished"))) &&
                (!nonEmptyOrBlankField(targetType) ||
                    (targetType.equalsIgnoreCase("private property") || targetType.equalsIgnoreCase("public property"))) &&
               (!nonEmptyOrBlankField(adjudicatedStatus) ||
                    (adjudicatedStatus.equalsIgnoreCase("adjudicated") || adjudicatedStatus.equalsIgnoreCase("not adjudicated")));
    }

    public boolean validAdjudicatedRegistration() {
 		 	
        return nonEmptyOrBlankField(dateOfadjudication) &&
               nonEmptyOrBlankField(username) &&
               nonEmptyOrBlankField(predictedStart) &&
               nonEmptyOrBlankField(predictedEnd) &&
               nonEmptyOrBlankField(adjudicatedBy) &&
               nonEmptyOrBlankField(nif) &&
               nonEmptyOrBlankField(status) &&
               (status.equalsIgnoreCase("not started") || status.equalsIgnoreCase("ongoing") || status.equalsIgnoreCase("finished")) &&
               nonEmptyOrBlankField(observations);
    }

    public boolean validNonAdjudicatedRegistration() {
 		 	
        return !nonEmptyOrBlankField(dateOfadjudication) &&
               !nonEmptyOrBlankField(username) &&
               !nonEmptyOrBlankField(predictedStart) &&
               !nonEmptyOrBlankField(predictedEnd) &&
               !nonEmptyOrBlankField(adjudicatedBy) &&
               !nonEmptyOrBlankField(nif) &&
               !nonEmptyOrBlankField(status) &&
               !nonEmptyOrBlankField(observations);
    }

    public boolean validChangeForPartner() {
 		 	
        return  !nonEmptyOrBlankField(description) &&
                !nonEmptyOrBlankField(targetType) &&
                !nonEmptyOrBlankField(adjudicatedStatus) &&  
                !nonEmptyOrBlankField(dateOfadjudication) &&
                !nonEmptyOrBlankField(username) &&
                !nonEmptyOrBlankField(predictedStart) &&
                !nonEmptyOrBlankField(predictedEnd) &&
                !nonEmptyOrBlankField(adjudicatedStatus) &&
                !nonEmptyOrBlankField(nif) &&
                nonEmptyOrBlankField(status) &&
                (status.equalsIgnoreCase("not started") || status.equalsIgnoreCase("ongoing") || status.equalsIgnoreCase("finished")) &&
                !nonEmptyOrBlankField(observations);
    }
    
}
