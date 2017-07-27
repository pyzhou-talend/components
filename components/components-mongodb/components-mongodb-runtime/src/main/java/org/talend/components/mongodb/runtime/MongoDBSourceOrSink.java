package org.talend.components.mongodb.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.ValidationResult;

public class MongoDBSourceOrSink extends MongoDBRuntime implements SourceOrSink{
	
    @Override
    public ValidationResult initialize(RuntimeContainer runtimeContainer, ComponentProperties properties) {
        ValidationResult validationResult = super.initialize(runtimeContainer, properties);
        if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
            return validationResult;
        }

        return ValidationResult.OK;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        List<NamedThing> result = new ArrayList<>();
            Set<String> collectionNames = getMongoDBConnection(container).getCollectionNames();
            for (String collectionName : collectionNames) {
				result.add(new SimpleNamedThing(collectionName,collectionName));
			}
        return result;
    }

	@Override
	public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
		return null;
	}

	@Override
	public ValidationResult validate(RuntimeContainer container) {
        // Nothing to validate here
        return ValidationResult.OK;
	}
	
    public static ValidationResult validateConnection(TMongoDBConnectionProperties properties) {
    	MongoDBSourceOrSink sos = new MongoDBSourceOrSink();
        ValidationResult vr = sos.initialize(null, (ComponentProperties) properties);
        if (ValidationResult.Result.OK != vr.getStatus()) {
            return vr;
        }

        sos.getMongoDBConnection(null);

        return ValidationResult.OK;

    }


}
