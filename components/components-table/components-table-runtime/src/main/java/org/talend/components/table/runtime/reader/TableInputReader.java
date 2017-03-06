// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.table.runtime.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.Result;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.components.table.ttableinput.TableInputProperties;

/**
 * Simple implementation of a reader.
 */
public class TableInputReader extends AbstractBoundedReader<IndexedRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableInputReader.class);

    private final String filePath;

    private boolean started = false;
    
    private boolean hasMore = false;

    private BufferedReader reader = null;

    private IndexedRecord current;
    
    /**
     * Converts datum field values to avro format
     */
    private AvroConverter<String, IndexedRecord> converter;
    
    /**
     * Root schema includes Runtime schema and schema of out of band data (a.k.a flow variables)
     */
    private Schema rootSchema;    
    
    /**
     * Holds values for return properties
     */
    private Result result;

    public TableInputReader(TableInputSource source) {
        super(source);
        this.filePath = source.getFilePath();
    }

    @Override
    public boolean start() throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
        result = new Result();
        LOGGER.debug("open: " + filePath); //$NON-NLS-1$
        started = true;
        return advance();
    }

    @Override
    public boolean advance() throws IOException {
        if (!started) {
            throw new IllegalStateException("Reader wasn't started");
        }
        hasMore = reader.ready();
        if (hasMore) {
            String line = reader.readLine();
            IndexedRecord dataRecord = getConverter(line).convertToAvro(line);
            IndexedRecord outOfBandRecord = new GenericData.Record(TableInputProperties.outOfBandSchema);
            outOfBandRecord.put(0, result.totalCount);
            current = new GenericData.Record(getRootSchema(line));
        	current.put(0, dataRecord);
        	current.put(1, outOfBandRecord);
            result.totalCount++;
        }
        return hasMore;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException("Reader wasn't started");
        }
        if (!hasMore) {
            throw new NoSuchElementException("Has no more elements");
        }
        return current;
    }

    @Override
    public void close() throws IOException {
        if (!started) {
            throw new IllegalStateException("Reader wasn't started");
        }
        reader.close();
        LOGGER.debug("close: " + filePath); //$NON-NLS-1$
        reader = null;
        started = false;
        hasMore = false;
    }

    /**
     * Returns values of Return properties. It is called after component finished his work (after {@link this#close()} method)
     */
    @Override
    public Map<String, Object> getReturnValues() {
        return result.toMap();
    }
    
    @Override
    public TableInputSource getCurrentSource() {
        return (TableInputSource) super.getCurrentSource();
    }

    /**
     * Returns implementation of {@link AvroConverter}, creates it if it doesn't
     * exist.
     * 
     * @param delimitedString
     *            delimited line, which was read from file
     * @return converter
     */
    private AvroConverter<String, IndexedRecord> getConverter(String delimitedString) {
        if (converter == null) {
            converter = getCurrentSource().createConverter(delimitedString);
        }
        return converter;
    }
    
    /**
     * Returns Root schema, which is used during IndexedRecord creation
     * 
     * @param delimitedString delimited line, which was read from file
     * @return avro Root schema
     */
    private Schema getRootSchema(String delimitedString) {
    	if (rootSchema == null) {
    		rootSchema = getCurrentSource().createRootSchema(delimitedString);
    	}
    	return rootSchema;
    }
}
