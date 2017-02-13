#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.runtime.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.Result;

/**
 * Simple implementation of a reader.
 */
public class ${componentName}Reader extends AbstractBoundedReader<IndexedRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(${componentName}Reader.class);

    private final String filePath;
    
    private final Schema schema;

    private boolean started = false;
    
    private boolean hasMore = false;

    private BufferedReader reader = null;

    private IndexedRecord current;

    public ${componentName}Reader(${componentName}Source source) {
        super(source);
        this.filePath = source.getFilePath();
        this.schema = source.getDesignSchema();
    }

    @Override
    public boolean start() throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
        LOGGER.debug("open: " + filePath); //$NON-NLS-1$
        started = true;
        return advance();
    }

    @Override
    public boolean advance() throws IOException {
        if (!started) {
            throw new IOException("Reader wasn't started");
        }
        String line = reader.readLine();
        hasMore = line != null;
        if (hasMore) {
        	current = new GenericData.Record(schema);
        	current.put(0, line);
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
        reader.close();
        LOGGER.debug("close: " + filePath); //$NON-NLS-1$
        started = false;
        hasMore = false;
    }

    @Override
    public Map<String, Object> getReturnValues() {
        return new Result().toMap();
    }

}
