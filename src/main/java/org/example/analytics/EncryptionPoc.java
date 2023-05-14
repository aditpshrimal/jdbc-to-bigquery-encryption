package org.example.analytics;

import com.google.api.services.bigquery.model.TableRow;
import com.google.common.collect.Sets;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EncryptionPoc {

    static class NonPiiParDo extends DoFn<TableRow, TableRow> {
        private final Set<String> piiSet;

        public NonPiiParDo(Set<String> piiSet) {
            this.piiSet = piiSet;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            TableRow row = c.element();
            TableRow newRow = new TableRow();
            Set<String> keys = row.keySet();
            keys = Sets.difference(keys, piiSet);
            for (String key : keys) {
                newRow.set(key, row.get(key));
            }
            c.output(newRow);
        }
    }

    static class PiiPardo extends DoFn<TableRow, TableRow> {
        private final Set<String> piiSet;
        private final String joinKey;

        public PiiPardo(Set<String> piiSet, String joinKey) {
            this.piiSet = piiSet;
            this.joinKey = joinKey;
        }

        @Setup
        public void initialize() throws GeneralSecurityException, IOException {
            KmsEncryption.initializeOnce();
        }

        @ProcessElement
        public void processElement(ProcessContext c) throws GeneralSecurityException,
                IOException {
            TableRow row = c.element();
            TableRow newRow = new TableRow();
            for (String key : piiSet) {
                Object object = row.get(key);
                if (object == null) {
                    newRow.set(key, null);
                } else {
                    byte[] encryptedData = KmsEncryption.encrypt(object.toString());
                    newRow.set(key, encryptedData);
                }
            }
            newRow.set(joinKey, row.get(joinKey));
            c.output(newRow);
        }
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {

        MyOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(MyOptions.class);
        Pipeline pipeline = Pipeline.create(options);

        String[] outputTableNames = options.getOutputTable().get().split(",");
        String piiColumnNames = options.getPiiColumnNames().get();
        String joinKey = options.getJoinKey().get();

        PCollection<TableRow> inputData = pipeline.apply("Reading Database",
                JdbcIO.<TableRow>read()
                        .withDataSourceConfiguration(JdbcIO.DataSourceConfiguration
                                .create(options.getDriverClassName(), options.getJdbcUrl())
                                .withUsername(options.getUsername()).withPassword(options.getPassword()))
                        .withQuery(options.getSqlQuery())
                        .withCoder(TableRowJsonCoder.of())
                        .withRowMapper(new JdbcIO.RowMapper<TableRow>() {
                            @Override
                            public TableRow mapRow(ResultSet resultSet) throws Exception {
                                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                                TableRow outputTableRow = new TableRow();
                                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                                    outputTableRow.set(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                                }
                                return outputTableRow;
                            }
                        }));

        // Split the input data into PII and non-PII data
        Set<String> piiSet = new HashSet<>(Arrays.asList(piiColumnNames.split(",")));
        inputData.apply("Filtering non-PII data", ParDo.of(new NonPiiParDo(piiSet)))
                .apply("Write non-PII data to BigQuery",
                        BigQueryIO.writeTableRows()
                                .withoutValidation()
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
                                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
                                .withCustomGcsTempLocation(options.getBigQueryLoadingTemporaryDirectory())
                                .to(outputTableNames[0]));

        inputData.apply("Filtering PII data", ParDo.of(new PiiPardo(piiSet, joinKey)))
                .apply("Write PII data to BigQuery",
                        BigQueryIO.writeTableRows()
                                .withoutValidation()
                                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
                                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
                                .withCustomGcsTempLocation(options.getBigQueryLoadingTemporaryDirectory())
                                .to(outputTableNames[1]));

        pipeline.run();
    }
}