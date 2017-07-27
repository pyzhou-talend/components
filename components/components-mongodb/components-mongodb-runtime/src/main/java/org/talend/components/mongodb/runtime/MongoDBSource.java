package org.talend.components.mongodb.runtime;

import java.util.List;

import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;

public class MongoDBSource extends MongoDBSourceOrSink implements BoundedSource{

	@Override
	public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean producesSortedKeys(RuntimeContainer adaptor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BoundedReader createReader(RuntimeContainer adaptor) {
		// TODO Auto-generated method stub
		return null;
	}

}
