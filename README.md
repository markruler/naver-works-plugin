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
            contentActionLink: env.BUILD_URL
    )
}
```

`Manage Jenkins > Configure System > Jenkins Location > Jenkins URL`
구성은 별도로 설정하지 않았다면 기본적으로 `localhost`로 설정된다.
그럼 `BUILD_URL` 환경 변수는 생성되지 않는다.
로그를 확인해보면 `unconfigured-jenkins-location`과 같은 메시지를 확인할 수 있다.
`Jenkins URL`에 젠킨스 서비스 도메인을 입력하거나,
로컬 환경에서 테스트할 경우 `http://127.0.0.1:8080/jenkins/` 와 같이 Loopback 주소를 입력한다.

![Jenkins Location](images/jenkins-location.png)
