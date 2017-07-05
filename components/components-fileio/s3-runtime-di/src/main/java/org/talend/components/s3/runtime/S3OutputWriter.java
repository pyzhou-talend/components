package org.talend.components.s3.runtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.simplefileio.s3.S3DatasetProperties;
import org.talend.components.simplefileio.s3.output.S3OutputProperties;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.csvreader.CsvWriter;

public class S3OutputWriter implements Writer<IndexedRecord, Result> {

    private transient static final Logger LOG = LoggerFactory.getLogger(S3OutputWriter.class);

    private S3WriteOperation writeOperation;

    protected RuntimeContainer runtime;

    private S3OutputProperties properties;

    private S3Sink sink;

    private Result result;

    private AmazonS3 s3_client;

    private CsvWriter writer;

    private File data_file;

    public S3OutputWriter(S3WriteOperation s3WriteOperation, RuntimeContainer adaptor) {
        this.writeOperation = s3WriteOperation;

        this.runtime = adaptor;
        this.sink = s3WriteOperation.getSink();
        this.properties = this.sink.properties;

        this.result = new Result();
    }

    @Override
    public void open(String uId) throws IOException {
        // connect to s3
        s3_client = S3Connection.createClient(properties);
        try {
            s3_client.listObjects(properties.getDatasetProperties().bucket.getValue(), "any");
        } catch (AmazonServiceException ase) {
            if (ase.getStatusCode() != Constants.NO_SUCH_BUCKET_STATUS_CODE) {
                throw ase;
            }
        }

        // prepare the local target, will upload it to s3 and clear it in the close method
        String tmpDir = System.getProperty("java.io.tmpdir");
        data_file = new File(tmpDir, "data.csv");

        data_file.deleteOnExit();

        OutputStream outputStream = new FileOutputStream(data_file);
        writer = new CsvWriter(new OutputStreamWriter(outputStream), ';');
    }

    private boolean firstRow = true;

    @Override
    public void write(IndexedRecord record) throws IOException {
        // write header
        if (firstRow) {
            for (Schema.Field f : record.getSchema().getFields()) {
                writer.write(String.valueOf(String.valueOf(f.name())));
            }
            writer.endRecord();
            firstRow = false;
        }

        // write data
        for (Schema.Field f : record.getSchema().getFields()) {
            final Object value = record.get(f.pos());
            if (value == null) {
                writer.write(StringUtils.EMPTY);
            } else {
                // TODO maybe adjust it, not sure
                writer.write(String.valueOf(value));
            }
        }

        writer.endRecord();

        result.totalCount++;
    }

    private boolean closed;

    /**
     * not sure the method is called one or two times, it depend on the platform
     */
    @Override
    public Result close() throws IOException {
        if (closed) {
            return result;
        }

        closed = true;

        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }

            S3DatasetProperties data_set = properties.getDatasetProperties();
            PutObjectRequest request = new PutObjectRequest(data_set.bucket.getValue(), data_set.object.getValue(), data_file);

            Boolean serverSideEnc = data_set.encryptDataAtRest.getValue();
            if (serverSideEnc != null && serverSideEnc) {
                request.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams(data_set.kmsForDataAtRest.getValue()));
            }

            s3_client.putObject(request);
        } finally {
            writer = null;
            data_file.delete();

            if (s3_client != null) {
                s3_client.shutdown();
                s3_client = null;
            }
        }

        result.successCount = result.totalCount;
        return result;
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return writeOperation;
    }

}
