package io.jenkins.plugins.naverworks;

public class App {

    private final String clientId;
    private final String clientSecret;
    private final String serviceAccount;
    private final String privateKey;

    public App(
            String clientId,
            String clientSecret,
            String serviceAccount, String privateKey) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serviceAccount = serviceAccount;
        this.privateKey = privateKey;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getServiceAccount() {
        return serviceAccount;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
