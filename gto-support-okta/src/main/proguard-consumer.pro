-keepclassmembernames class com.okta.authfoundation.credential.CredentialDataSource {
    # keep for com.okta.authfoundation.credential.CredentialDataSourceInternalsKt#storageField
    com.okta.authfoundation.credential.TokenStorage storage;
}

-keepclassmembernames class com.okta.authfoundation.credential.SharedPreferencesTokenStorage {
    # keep for com.okta.authfoundation.credential.SharedPreferencesTokenStorageInternals#getSharedPreferences()
    android.content.SharedPreferences getSharedPreferences();
}
