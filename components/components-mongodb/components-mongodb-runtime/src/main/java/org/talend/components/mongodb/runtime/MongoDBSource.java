package org.talend.components.mongodb.runtime;

import java.util.List;

import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.tmongodbinput.TMongoDBInputProperties;

public class MongoDBSource extends MongoDBSourceOrSink implements BoundedSource{
	

	@Override
	public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor)
			throws Exception {
		return null;
	}

	@Override
	public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
		return 0;
	}

	@Override
	public boolean producesSortedKeys(RuntimeContainer adaptor) {
		return false;
	}

	@Override
	public BoundedReader createReader(RuntimeContainer container) {
        if (properties instanceof TMongoDBInputProperties) {
            return new MongoDBReader(container, this, (TMongoDBInputProperties) properties);
        }
        return null;
	}



}
