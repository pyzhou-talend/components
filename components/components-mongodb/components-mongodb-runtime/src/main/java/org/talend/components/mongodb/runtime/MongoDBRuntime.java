package org.talend.components.mongodb.runtime;

import org.apache.commons.lang3.StringUtils;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBProvideConnectionProperties;
import org.talend.components.mongodb.module.MongoDBConnectionModule;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionProperties;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBRuntime implements RuntimableRuntime<ComponentProperties> {
	
	MongoDBProvideConnectionProperties properties ;
	
	public static final String KEY_CONNECTION_PROPERTIES = "connection";
	
	private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
			.getI18nMessages(MongoDBRuntime.class);

	@Override
	public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        // init
        this.properties = (TMongoDBConnectionProperties) properties;
        MongoDBConnectionModule conn = getUsedConnection(container);

        // Validate connection properties

        String errorMessage = "";
        if (conn == null) { // check connection failure

            errorMessage = i18nMessages.getMessage("error.VacantConnection"); 

        } else if (!conn.requiredAuthentication.getValue()) { // checks 
            if (StringUtils.isEmpty(conn.server.getStringValue())) {
                errorMessage = i18nMessages.getMessage("error.EmptyServer"); 
            } else if(StringUtils.isEmpty(conn.port.getStringValue())){
            	errorMessage = i18nMessages.getMessage("error.EmptyPort"); 
            } else if(StringUtils.isEmpty(conn.database.getStringValue())){
            	errorMessage = i18nMessages.getMessage("error.EmptyDatabase"); 
            }

        } else if (conn.requiredAuthentication.getValue() && (StringUtils.isEmpty(conn.username.getStringValue())
                || StringUtils.isEmpty(conn.password.getStringValue()))) { // checks connection's username and password

            errorMessage = i18nMessages.getMessage("error.EmptyUserNameOrKey");
        }
        // Return result
        if (errorMessage.isEmpty()) {
            return ValidationResult.OK;
        } else {
            return new ValidationResult(ValidationResult.Result.ERROR, errorMessage);
        }
	}
	
    public MongoDBConnectionModule getUsedConnection(RuntimeContainer runtimeContainer) {
        TMongoDBConnectionProperties connectionProperties = ((MongoDBProvideConnectionProperties) properties)
                .getConnectionProperties();
        String refComponentId = connectionProperties.connection.getReferencedComponentId();

        // Using another component's connection
        if (refComponentId != null) {
            // In a runtime container
            if (runtimeContainer != null) {
                TMongoDBConnectionProperties sharedConn = (TMongoDBConnectionProperties) runtimeContainer
                        .getComponentData(refComponentId, KEY_CONNECTION_PROPERTIES);
                if (sharedConn != null) {
                    return sharedConn.connection;
                }
            }
            // Design time
            connectionProperties = connectionProperties.connection.getReferencedConnectionProperties();
        }
        if (runtimeContainer != null) {
            runtimeContainer.setComponentData(runtimeContainer.getCurrentComponentId(), KEY_CONNECTION_PROPERTIES,
                    connectionProperties);
        }
        return connectionProperties.connection;
    }
    
    public TMongoDBConnectionProperties getConnectionProperties() {
        return properties.getConnectionProperties();
    }
    

    public DB getMongoDBConnection(RuntimeContainer runtimeContainer) {

    	MongoDBConnectionModule conn = getUsedConnection(runtimeContainer);
        if (!conn.requiredAuthentication.getValue()) {
			com.mongodb.MongoClientOptions clientOptions = new com.mongodb.MongoClientOptions.Builder()
					.build();

			// Empty client credentials list
			java.util.List<MongoCredential> mongoCredentialList = new java.util.ArrayList<MongoCredential>();

			ServerAddress serverAddress = new ServerAddress(conn.server.getValue(), conn.port.getValue());
			Mongo mongo = new MongoClient(serverAddress,mongoCredentialList,clientOptions);
			mongo.getAddress();
			DB db = mongo.getDB(conn.database.getValue());
			return db;

        } else {
        	//to do realize

            return null;
        }

    }

}
