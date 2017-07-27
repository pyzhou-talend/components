package org.talend.components.mongodb;

import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionProperties;

public interface MongoDBProvideConnectionProperties {
	
    /**
     * getConnectionProperties.
     *
     * @return {@link TMongoDBConnectionProperties} properties of connection.
     */
	TMongoDBConnectionProperties getConnectionProperties();

}
