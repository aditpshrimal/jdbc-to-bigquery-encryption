# JDBC to BigQuery with Encryption using Google Tink and KMS
This project demonstrates how to read data from JDBC and write it to BigQuery with encryption using Google Tink and KMS. It also provides an example of how to customize the encryption by specifying the PII columns.

## Getting Started
To get started with this project, follow these steps:

1. Clone this repository to your local machine:<br> 
`git clone https://github.com/[USERNAME]/jdbc-to-bigquery-encryption.git`
2. Open the project in your favorite IDE, such as IntelliJ or Eclipse.
3. Set up the following variables:
   - GOOGLE_APPLICATION_CREDENTIALS: Path to the service account key file for the Google Cloud project.
   - PROJECT_ID: ID of the Google Cloud project where the BigQuery dataset is located.
   - BUCKET_NAME: Name of the Google Cloud Storage bucket where the encrypted keys file is stored.
   - INSTANCE_CONNECTION_NAME: Connection name of the Cloud SQL instance.
   - SOCKET_FACTORY: Name of the socket factory for the Cloud SQL instance.
   - USERNAME: Username for the Cloud SQL instance.
   - PASSWORD: Password for the Cloud SQL instance.
   - KMS_KEY_URI: URI of the KMS key used for encryption.
4. Build the project using the following command:<br>
`mvn clean install`
5. Run the project using the following command:
```
java -cp target/jdbc-to-bigquery-encryption-1.0-SNAPSHOT.jar org.example.analytics.EncryptionPoc \
--runner=DataflowRunner \
--project=$PROJECT_ID \
--gcpTempLocation=gs://$BUCKET_NAME/tmp \
--outputTable=$PROJECT_ID:[DATASET_NAME].[TABLE_NAME] \
--sqlQuery=SELECT * FROM [TABLE_NAME] \
--driverClassName=com.mysql.cj.jdbc.Driver \
--jdbcUrl=jdbc:mysql://google/[DATABASE_NAME]?cloudSqlInstance=$INSTANCE_CONNECTION_NAME&socketFactory=$SOCKET_FACTORY&user=$USERNAME&password=$PASSWORD \
--joinKey=[JOIN_COLUMN] \
--piiFlag=[PII_FLAG] \
--piiColumnNames=[PII_COLUMN_NAMES]
```
## Configuration
The project uses the following command line options:
- --runner: The Beam runner to use, such as DataflowRunner or DirectRunner.
- --project: The ID of the Google Cloud project where the BigQuery dataset is located.
- --gcpTempLocation: The GCS location to use for temporary files.
- --outputTable: The output BigQuery table, in the format project:dataset.table.
- --sqlQuery: The SQL query to use for reading data from JDBC.
- --driverClassName: The JDBC driver class name.
- --jdbcUrl: The JDBC URL.
- --joinKey: The column to use as the join key for PII data.
- --piiFlag: Whether to include PII data, yes or no.
- --piiColumnNames: A comma-separated list of PII column names.

## How to use
1. Clone the repository
2. Make sure you have the necessary software installed and configured:
    - Java 8+
    - Maven
    - Google Cloud SDK
    - Google Cloud Storage
    - Google BigQuery
    - Google KMS
3. Update the configuration parameters in the MyOptions.java file according to your environment.
4. Build the project using Maven: <br>
   `mvn clean install.`
5. Run the program: <br>
   ```
   java -cp target/<your-project-name>-<version>-jar-with-dependencies.jar org.example.analytics.EncryptionPoc --runner=DataflowRunner --project=<your-gcp-project-id> --stagingLocation=<your-gcs-staging-location> --outputTable=<your-bigquery-output-table> --tempLocation=<your-gcs-temp-location> --piiColumnNames=<your-pii-columns> --joinKey=<your-join-key> --sqlQuery=<your-sql-query> --jdbcUrl=<your-jdbc-url> --driverClassName=<your-driver-classname> --username=<your-username> --password=<your-password>```

#### Note: To make it more generalized and easy to understand I have just provided a framework. You may need to make some changes as per your use-case.
## License
This project is licensed under the terms of the <a href="https://opensource.org/licenses/MIT">MIT License.</a>



