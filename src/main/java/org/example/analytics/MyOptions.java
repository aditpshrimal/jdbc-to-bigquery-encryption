package org.example.analytics;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.ValueProvider;

public interface MyOptions extends PipelineOptions {

    @Description("SQL Query")
    @Default.String("SELECT * FROM table")
    ValueProvider<String> getSqlQuery();
    void setSqlQuery(ValueProvider<String> sqlQuery);

    @Description("Driver Class")
    @Default.String("com.mysql.cj.jdbc.Driver")
    ValueProvider<String> getDriverClassName();
    void setDriverClassName(ValueProvider<String> driverClassName);

    @Description("JDBC URL")
    @Default.String("jdbc:mysql://[HOST]:[PORT]/[DATABASE_NAME]?cloudSqlInstance=[INSTANCE_CONNECTION_NAME]&socketFactory=[SOCKET_FACTORY]&user=[USERNAME]&password=[PASSWORD]")
    ValueProvider<String> getJdbcUrl();
    void setJdbcUrl(ValueProvider<String> jdbcUrl);

    @Description("JDBC Username")
    @Default.String("root")
    ValueProvider<String> getUsername();
    void setUsername(ValueProvider<String> username);

    @Description("JDBC Password")
    @Default.String("password123")
    ValueProvider<String> getPassword();
    void setPassword(ValueProvider<String> password);

    @Description("Join key")
    @Default.String("customerNumber")
    ValueProvider<String> getJoinKey();
    void setJoinKey(ValueProvider<String> joinKey);

    @Description("Load type")
    @Default.String("FULL")
    ValueProvider<String> getLoadType();
    void setLoadType(ValueProvider<String> loadType);

    @Description("BigQuery temp location")
    @Default.String("gs://[BUCKET_NAME]/")
    ValueProvider<String> getBigQueryLoadingTemporaryDirectory();
    void setBigQueryLoadingTemporaryDirectory(ValueProvider<String> bigQueryLoadingTemporaryDirectory);

    @Description("BigQuery table")
    @Default.String("[PROJECT_ID]:[DATASET_ID].[TABLE_NAME_NON_PII],[PROJECT_ID]:[DATASET_ID].[TABLE_NAME_PII]")
    ValueProvider<String> getOutputTable();
    void setOutputTable(ValueProvider<String> outputTable);

    @Description("PII Flag")
    @Default.String("yes")
    ValueProvider<String> getPiiFlag();
    void setPiiFlag(ValueProvider<String> piiFlag);

    @Description("PII column names")
    @Default.String("phone,addressLine1,addressLine2")
    ValueProvider<String> getPiiColumnNames();
    void setPiiColumnNames(ValueProvider<String> piiColumnNames);

}
