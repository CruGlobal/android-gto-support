-keepclassmembernames class com.okta.oidc.clients.sessions.SessionClientImpl {
    # keep for com.okta.oidc.clients.sessions.SessionClientInternalsKt#implSyncSessionClientField
    com.okta.oidc.clients.sessions.SyncSessionClient mSyncSessionClient;
}

-keepclassmembernames class com.okta.oidc.clients.sessions.SyncSessionClientImpl {
    # keep for com.okta.oidc.clients.sessions.SyncSessionClientInternalsKt#implOktaStateField
    com.okta.oidc.OktaState mOktaState;
}

-keepclassmembernames class com.okta.oidc.storage.OktaRepository {
    # keep for com.okta.oidc.storage.OktaRepositoryInternalsKt#storageField
    com.okta.oidc.storage.OktaStorage storage;
}

-keepclassmembernames class com.okta.oidc.OktaState {
    # keep for com.okta.oidc.OktaStateInternalsKt#oktaRepoField
    com.okta.oidc.storage.OktaRepository mOktaRepo;
}
