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
package org.talend.components.jdbc.runtime.writer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.jdbc.CommonUtils;
import org.talend.components.jdbc.JDBCTemplate;
import org.talend.components.jdbc.runtime.setting.JDBCSQLBuilder;
import org.talend.components.jdbc.runtime.type.JDBCMapping;

public class JDBCOutputUpdateWriter extends JDBCOutputWriter {

    private transient static final Logger LOG = LoggerFactory.getLogger(JDBCOutputUpdateWriter.class);

    private String sql;

    public JDBCOutputUpdateWriter(WriteOperation<Result> writeOperation, RuntimeContainer runtime) {
        super(writeOperation, runtime);
    }

    @Override
    public void open(String uId) throws IOException {
        super.open(uId);
        try {
            conn = sink.getConnection(runtime);
            sql = JDBCSQLBuilder.getInstance().generateSQL4Update(setting.getTablename(),
                    CommonUtils.getMainSchemaFromInputConnector((ComponentProperties) properties));
            statement = conn.prepareStatement(sql);
        } catch (ClassNotFoundException | SQLException e) {
            throw new ComponentException(e);
        }

    }

    @Override
    public void write(IndexedRecord record) throws IOException {
        super.write(record);

        List<Schema.Field> keys = JDBCTemplate.getKeyColumns(record.getSchema().getFields());
        List<Schema.Field> values = JDBCTemplate.getValueColumns(record.getSchema().getFields());

        try {
            int index = 0;
            for (Schema.Field value : values) {
                JDBCMapping.setValue(++index, statement, value, record.get(value.pos()));
            }

            for (Schema.Field key : keys) {
                JDBCMapping.setValue(++index, statement, key, record.get(key.pos()));
            }
        } catch (SQLException e) {
            throw new ComponentException(e);
        }

        try {
            updateCount += execute(record, statement);
        } catch (SQLException e) {
            if (dieOnError) {
                throw new ComponentException(e);
            } else {
                LOG.warn(e.getMessage());
            }

            handleReject(record, e);
        }

        try {
            updateCount += executeCommit(statement);
        } catch (SQLException e) {
            if (dieOnError) {
                throw new ComponentException(e);
            } else {
                LOG.warn(e.getMessage());
            }
        }
    }

    @Override
    public Result close() throws IOException {
        if (useBatch && batchCount > 0) {
            try {
                batchCount = 0;
                updateCount += executeBatchAndGetCount(statement);
            } catch (SQLException e) {
                if (dieOnError) {
                    throw new ComponentException(e);
                } else {
                    LOG.warn(e.getMessage());
                }
            }
        }

        closeStatementQuietly(statement);
        statement = null;

        commitAndCloseAtLast();

        constructResult();

        return result;
    }

}
