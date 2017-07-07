package org.talend.components.runtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.FileOutput.FileOutputProperties;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.common.runtime.GenericIndexedRecordConverter;
import org.talend.daikon.avro.AvroUtils;

public class FileIOWriter implements Writer<Result>{
	
    private FileIOWriterOpertion wope;

    private FileIOSink sink;

    private FileOutputProperties props;

    private RuntimeContainer runtime;

    private Schema writeSchema;

    private Result result;
    
    private String fileName;
    
    BufferedWriter writer;
    
    public FileIOWriter(FileIOWriterOpertion ope,RuntimeContainer runtime) {
    	
        this.wope = ope;
        this.runtime = runtime;
        this.sink = (FileIOSink) getWriteOperation().getSink();
        this.props = (FileOutputProperties) this.sink.fileOutputProperties;
        fileName = props.filename.getStringValue();
    	
    }

	@Override
	public void open(String uId) throws IOException {
		this.result = new Result(uId);
        if (writeSchema == null) {
            writeSchema = props.schema.schema.getValue();
            if (AvroUtils.isIncludeAllFields(writeSchema)) {
                // if design schema include dynamic,need to get schema from record
                writeSchema = null;
            }
        }
        File file = new File(fileName);
        if(!file.exists()) {
        	file.createNewFile();
        }
        writer = new BufferedWriter(new FileWriter(file));
	}

	@Override
	public void write(Object object) throws IOException {
        if (object == null)
            return;
        result.totalCount++;
        if (writeSchema == null) {
            writeSchema = ((IndexedRecord) object).getSchema();
        }
        GenericIndexedRecordConverter factory = new GenericIndexedRecordConverter();
        factory.setSchema(writeSchema);
        IndexedRecord inputRecord = factory.convertToAvro((IndexedRecord) object);
        List<Field> fs = writeSchema.getFields();
        for (int i = 0; i< fs.size();i++) {
        	writer.write(inputRecord.get(fs.get(i).pos()).toString());
        	if(i!=fs.size()-1) {
        		writer.write(",");
        	}
        }
        writer.write("\n");
        writer.flush();
		
	}

	@Override
	public Result close() throws IOException {
		writer.close();
		return result;
	}

	@Override
	public WriteOperation<Result> getWriteOperation() {
		return wope;
	}

}
