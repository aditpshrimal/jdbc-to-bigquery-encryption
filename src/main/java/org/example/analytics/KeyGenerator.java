package org.example.analytics;

import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.integration.gcpkms.GcpKmsClient;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class KeyGenerator {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        AeadConfig.register();
        KeysetHandle keysetHandle = KeysetHandle.generateNew(
                KeyTemplates.get("AES256_GCM"));

        // and write it to a file...
        String keysetFilename = "encrypted_keys.json";
        // encrypted with the this key in GCP KMS
        String masterKeyUri = "gcp-kms://projects/{PROJECT_ID}/locations/{LOCATION}/keyRings/{KEYRING}/cryptoKeys/{KEY}";
        keysetHandle.write(JsonKeysetWriter.withFile(new File(keysetFilename)),
                new GcpKmsClient().withDefaultCredentials().getAead(masterKeyUri));
    }
}
