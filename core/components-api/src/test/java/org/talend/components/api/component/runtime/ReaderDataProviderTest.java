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
package org.talend.components.api.component.runtime;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.joda.time.Instant;
import org.junit.Test;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.java8.Consumer;

public class ReaderDataProviderTest {
    
    public static final Schema SCHEMA = SchemaBuilder.record("record").fields().requiredString("count").endRecord();
    
    public static final IndexedRecord RECORD1 = new GenericData.Record(SCHEMA);
    
    public static final IndexedRecord RECORD2 = new GenericData.Record(SCHEMA);
    
    static {
        RECORD1.put(0, "1");
        RECORD2.put(0, "2");
    }

    @Test
    public void testProviderEmptyReader() throws IOException {
        Reader<IndexedRecord> readerMock = mock(Reader.class);
        when(readerMock.start()).thenReturn(false);
        Consumer<IndexedRecord> consumer = mock(Consumer.class);
        ReaderDataProvider<IndexedRecord> readerDataProvider = new ReaderDataProvider<>(readerMock, 100, consumer);
        readerDataProvider.retrieveData();
        verify(consumer, times(0)).accept((IndexedRecord) any());
        verify(readerMock, times(1)).close();
    }

    @Test
    public void testReaderDataProviderWithRecords() throws IOException {
        Reader<IndexedRecord> reader = spy(new OneTwoReader());
        Consumer<IndexedRecord> consumer = mock(Consumer.class);
        ReaderDataProvider<IndexedRecord> readerDataProvider = new ReaderDataProvider<>(reader, 100, consumer);
        readerDataProvider.retrieveData();
        verify(consumer).accept(RECORD1);
        verify(consumer).accept(RECORD2);
        verify(consumer, times(2)).accept((IndexedRecord) any());
        verify(reader, times(1)).close();
    }

    @Test
    public void testReaderDataProviderWithLimitTo0() throws IOException {
        Reader<IndexedRecord> reader = spy(new OneTwoReader());
        Consumer<IndexedRecord> consumer = mock(Consumer.class);
        ReaderDataProvider<IndexedRecord> readerDataProvider = new ReaderDataProvider<>(reader, 0, consumer);
        readerDataProvider.retrieveData();
        verify(consumer, never()).accept((IndexedRecord) any());
        verify(reader, times(1)).close();
    }

    @Test
    public void testReaderDataProviderWithLimitTo1() throws IOException {
        Reader<IndexedRecord> reader = spy(new OneTwoReader());
        Consumer<IndexedRecord> consumer = mock(Consumer.class);
        ReaderDataProvider<IndexedRecord> readerDataProvider = new ReaderDataProvider<>(reader, 1, consumer);
        readerDataProvider.retrieveData();
        verify(consumer).accept(RECORD1);
        verify(consumer, times(1)).accept((IndexedRecord) any());
        verify(reader, times(1)).close();
    }

    @Test
    public void testReaderDataProviderWithException() throws IOException {
        Reader<IndexedRecord> reader = mock(Reader.class);
        Consumer<IndexedRecord> consumer = mock(Consumer.class);
        ReaderDataProvider<IndexedRecord> readerDataProvider = new ReaderDataProvider<>(reader, 100, consumer);

        // reader start throws an IOE
        when(reader.start()).thenThrow(new IOException());
        try {
            readerDataProvider.retrieveData();
            fail("the code above should have thrown an exception");
        } catch (TalendRuntimeException tre) {
            // expected exception
            verify(reader, times(1)).close();
        }

        // reader getCurrent throws an IOE
        reset(reader);
        when(reader.start()).thenReturn(true);
        when(reader.getCurrent()).thenThrow(new NoSuchElementException());
        try {
            readerDataProvider.retrieveData();
            fail("the code above should have thrown an exception");
        } catch (TalendRuntimeException tre) {
            // expected exception
            verify(reader, times(1)).close();
        }

        // reader close throws an IOE
        reset(reader);
        when(reader.start()).thenReturn(false);
        doThrow(new IOException()).when(reader).close();
        try {
            readerDataProvider.retrieveData();
            fail("the code above should have thrown an exception");
        } catch (TalendRuntimeException tre) {
            // expected exception
        }
    }

    public class OneTwoReader implements Reader<IndexedRecord> {

        Iterator<IndexedRecord> it = Arrays.asList(RECORD1, RECORD2).iterator();

        private IndexedRecord current;

        @Override
        public boolean start() throws IOException {
            current = it.next();
            return true;
        }

        @Override
        public boolean advance() throws IOException {
            if (it.hasNext()) {
                current = it.next();
            } else {
                current = null;
            }
            return current != null;
        }

        @Override
        public IndexedRecord getCurrent() throws NoSuchElementException {
            if (current == null) {
                throw new NoSuchElementException();
            }
            return current;
        }

        @Override
        public Instant getCurrentTimestamp() throws NoSuchElementException {
            return null;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public Source getCurrentSource() {
            return null;
        }

        @Override
        public Map<String, Object> getReturnValues() {
            return null;
        }

    }
}
