package org.talend.components.mongodb.runtime;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

public class MongoDBSourceOrSink extends MongoDBRuntime implements SourceOrSink{

	@Override
	public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationResult validate(RuntimeContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

	public static ValidationResult validateConnection(TMongoDBConnectionProperties tMongoDBConnectionProperties) {
		// TODO Auto-generated method stub
		return ValidationResult.OK;
	}

}
