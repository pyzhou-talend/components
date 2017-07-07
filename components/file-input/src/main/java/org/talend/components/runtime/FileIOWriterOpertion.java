package org.talend.components.runtime;

import java.util.Map;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

public class FileIOWriterOpertion implements WriteOperation<Result> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected FileIOSink sink;
    protected RuntimeContainer runtime;
	
    public FileIOWriterOpertion(Sink sink) {
        this.sink = (FileIOSink) sink;
    }

	@Override
	public void initialize(RuntimeContainer adaptor) {
		this.runtime = adaptor;
		
	}

	@Override
	public Map<String, Object> finalize(Iterable<Result> writerResults, RuntimeContainer adaptor) {
		return Result.accumulateAndReturnMap(writerResults);
	}

	@Override
	public Writer<Result> createWriter(RuntimeContainer adaptor) {
		return new FileIOWriter(this, adaptor);
	}

	@Override
	public Sink getSink() {
		return this.sink;
	}

}
