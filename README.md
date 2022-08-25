# naver-works-plugin

## Prerequisite

- Java 1.8
- Maven 3.8.3

## Test running

````shell
./mvnw clean hpi:run
# Jenkins is fully up and running
````

- Access to http://localhost:8080/jenkins/

## Build

```shell
./mvnw clean install
# target/naver-works.hpi
```

## Upload

> Manage Jenkins > Manage Plugins > Advanced > Deploy Plugin

![Deploy Plugin](images/deploy-plugin.png)

## Pipeline

```groovy
// Jenkinsfile.groovy
stage('Notification') {
    List issues = [
            [link: "http://jira.markruler.com/browse/MARK-4", title: "MARK-4", subtitle: "Jira Issue 1"],
            [link: "http://jira.markruler.com/browse/MARK-10", title: "MARK-10", subtitle: "Jira Issue 2"]
    ]

    naver(
            clientId: '${CLIENT_ID}',
            clientSecret: '${CLIENT_SECRET}',
            serviceAccount: '${SERVICE_ACCOUNT}',
            credentialId: '${CREDENTIAL_ID}',
            backgroundImageUrl: '${IMAGE_URL}',
            botId: '${BOT_ID}',
            channelId: '${CHANNEL_ID}',
            messages: issues,
            contentActionLabel: 'Go to Jenkins',
            contentActionLink: '${env.BUILD_URL}'
    )
}
```
