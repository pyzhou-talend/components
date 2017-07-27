package org.talend.components.mongodb.runtime;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.MongoDBProvideConnectionProperties;
import org.talend.components.mongodb.module.MongoDBConnectionModule;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionProperties;
import org.talend.components.mongodb.tmongodbinput.TMongoDBInputProperties;

import com.mongodb.DB;

public class MongoDBReader<T> extends AbstractBoundedReader<T> implements MongoDBProvideConnectionProperties  {
	
	protected TMongoDBConnectionProperties connection;
	
	protected DB db;
	
    protected transient int dataCount;
    
    protected TMongoDBInputProperties properties;
    
    private Result result;


	protected MongoDBReader(RuntimeContainer container,BoundedSource source,TMongoDBInputProperties properties) {
		super(source);
		this.connection = ((MongoDBSource)source).getConnectionProperties();
		this.db = ((MongoDBSource)source).getMongoDBConnection(container);
		this.properties = properties;
		this.result = new Result();
	}

    @Override
    public TMongoDBConnectionProperties getConnectionProperties() {
        if (connection == null) {
            connection = ((MongoDBSourceOrSink) getCurrentSource()).getConnectionProperties();
        }
        return connection;
    }

    @Override
    public Map<String, Object> getReturnValues() {
        Result res = new Result();
        res.totalCount = dataCount;
        Map<String, Object> resultMap = res.toMap();
        return resultMap;
    }

	@Override
	public boolean start() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean advance() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T getCurrent() throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

}
