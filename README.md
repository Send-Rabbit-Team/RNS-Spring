<!-- PROJECT LOGO -->
<br />
<div align="center">
    <img src="https://user-images.githubusercontent.com/53989167/221722105-ed9941b8-9b1f-45dc-a0f8-783c2947c9b1.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Rabbit Notification Service</h3>

  <p align="center">
    안정적이고 빠르게 연락을 제공해주는 서비스
    <br />
    가천대학교 카카오 엔터프라이즈 기업 실무 프로젝트 - 메시지 분배 발송 솔루션
    <br />
    <br />
    <a href="https://sendrabbi.notion.site/Rabbit-Notification-Service-f60867efc9124c5c96f1d5c8d12d428d"><strong>RNS 프로젝트 정리 페이지 »</strong></a> </br>
    <a href="https://sendrabbi.notion.site/Send-Rabbit-Team-b300566e57a2416fafbfcb8cddd1a0ad"><strong>Send Rabbit Team 기업 실무 프로젝트 최종 보고서 »</strong></a> </br>
    <a href="https://sendrabbi.notion.site/ad5ff614794e42938d8fc8e47807363b"><strong>데일리 스크럼 일지 »</strong></a>
    <br />
    <br />
  </p>
</div>

<!-- 팀원 소개 -->
## 팀원 소개 (Send Rabbit Team)
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/hyeong-jun-kim"><img src="https://user-images.githubusercontent.com/53989167/221746475-6bebd066-f230-4b7d-83bf-778f2b561899.png" width="250px;" alt=""/><br /><sub><b> PM, BE : 김형준 </b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/OYJ-hansung"><img src="https://user-images.githubusercontent.com/53989167/221746481-2ab88c43-c8bd-4ae1-863a-e7d5b8b720e0.png" width="250px;" alt=""/><br /><sub><b stype >BE, FE : 오영주</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/shinyena"><img src="https://user-images.githubusercontent.com/53989167/221746483-077c28d3-65aa-4d85-bd07-8357913f1d97.png" width="250px;" alt=""/><br /><sub><b>BE, FE : 신예나</b></sub></a><br /></td>
    </tr>
  </tbody>
</table>

<!-- ABOUT THE PROJECT -->
## RNS 프로젝트 소개

가천 카카오 엔터프라이즈 기업 실무 프로젝트 3번 과제, 메시지 분배 발송 솔루션을 주제로 구현한 프로젝트입니다. 
<br />
<br />
고객별로 중계사별 분배 비율을 설정할 수 있고, 설정한 비율대로 1000MPS 이상의 성능으로 고객에게 메시지가 전달되어야하며, 메시지는 유실되지 않고 결과 조회시 모든 기록들이 보여야합니다. 
<br />
<br />
현재 성능으로 1초에 5000MPS 이상이 보장되며, 과제에서 제시한 요구사항들을 모두 구현하고 디벨롭해 놓은 상태입니다.

### 프로젝트 수행 기간 
2022년 12월 26일 ~ 2022년 2월 19일 (8주)

## 사용 기술
### 개발 환경
* ![Spring Boot][SpringBoot.icon]
* ![RabbitMQ][RabbitMQ.icon]
* ![Redis][Redis.icon]
* ![MySQL][MySQL.icon]
* ![Kubenetis][Kubenetis.icon]

### CI & CD
* ![Jenkins][Jenkins.icon]
* ArgoCD

### 모니터링
* ![Prometheus][Prometheus.icon]
* ![Grafana][Grafana.icon]
* ![ElasticSearch][ElasticSearch.icon]
* ![fluentD][fluentD.icon]
* ![Kibana][Kibana.icon]

## 테스트
* ![JUnit][JUnit.icon]
* Jmeter

### 협업 도구
* ![Jira][Jira.icon]
* ![Slack][Slack.icon]
* ![Notion][Notion.icon]

## 쿠버네티스 아키텍처
<img src="https://user-images.githubusercontent.com/53989167/221738660-476f52ca-2033-4627-9278-c392f06658dd.png"/>

- CI&CD 적용 Pod
    - Sender Server, Receiver Server, React Pod
- 고 가용성 확보
    - Redis Clustering (3Master-3Slave), MySQL Clustering (1Master-3Slave), RabbitMQ Clustering & Mirroring (1Disk Node, 2RAM Node)
- 모니터링
    - 노드와 파드의 리소스 모니터링을 위해 Prometheus 및 Grafana 사용
    - 서버에서의 로그 분석을 위해 EFK 스택 사용
    - 쿠버네티스 파드 모니터링을 위해 KubeWatch 사용
    - 모든 모니터링 툴은 Slack과 연동 됨
- HPA (Horizontal Pod AutoScaler) 적용
    - Sender Server, Receiver Server pod
    - Produce > Consume 현상 방지를 위해 Receiver 서버의 가중치를 더 크게 두었음
- Setting & Security
    - ConfigMap을 통해 빈번하게 자주 변경되는 환경 변수, 클러스터링 정보들을 저장한 다음 config으로 배포하였음
    - 데이터베이스와 같이 민감한 정보들은 Secret을 통해 따로 배포하고, persisten volume을 mount하여서 필요한 곳에서 따로 쓸 수 있게 하였음
    - Prometheus, Kubewatch, FluentD 같이 노드와 파드의 metrics를 수집하는 파드들은, Admin 권한을 주는 것이 아닌 필요한 권한만을 파악한 후, 따로 ClusterRole과 ServiceAccount를 정의해줬음
         
## 메시지 발송 구조 아키텍처
<img src="https://user-images.githubusercontent.com/53989167/221741794-f5bdcc8f-fec7-4730-8edd-8056d18452e7.png"/>

메시지 발송 -> Sender Server -> 상태 DB 저장 (Redis) -> 사용자가 설정한 라우팅 규칙에 맞게 Message 전송 -> 해당하는 브로커 서버에서 Consume -> Receiver 서버로 Message 전송 
-> Receiver 서버에서 Consume 받아 성공/실패 저장
          
## 대규모 요청 처리 프로세스
<img src="https://user-images.githubusercontent.com/53989167/221741930-618c7bd8-17e4-414c-8d39-74fc4fa23662.png"/>

## Message Queue 구조
<img src="https://user-images.githubusercontent.com/53989167/221742198-8f4a3a9d-13dc-4c63-ad32-4434fd4e1a16.png"/>

- 해당 큐 구조를 통해 브로커 서버가 꺼져있을시에, 다른 중계사로 보내고 모든 중계사를 다 돌았을 경우에 메시지 발송이 안되면 실패 처리하는 프로세스를 구현할 수 있다.
- 전송 실패 시나리오 (DLX Error Handling)
  - (retryCount <= 1) KT WorkQueue Publish -> KT 중계사 꺼져있음 (Consume X) -> KT WorkQueue 10초간 대기 -> DLX 큐인 WorkDead 큐로 이동 -> Receiver 서버에서 retryCount 판별 retryCount == 1 일시, 해당 과정 다시한번 반복
  - (retryCount == 2) SKT WorkrQueue Publish -> SKT 중계사 꺼져있음 (Consume X) '' 위의 과정 반복
  - (retryCount == 3) LG WorkrQueue Publish -> LG 중계사 꺼져있음 (Consume X) '' 위의 과정 반복
  - (retryCount == 4) 처음 보낸 중계사의 브로커(KT)로 실패 처리 함, 실패 사유로 중계사 오류 및 거쳐온 중계사들을 적어서 실패 처리함 

## RNS 기능
### 발송 기능
- [메시지 발송](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/message/MessageService.java): 메시지 작성, [발송 분배 비율 설정](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/message/MessageRuleService.java), [포인트 결제](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/PointService.java)
- [알림톡 발송](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/kakao/KakaoMessageService.java): 알림톡 작성, [발송 분배 비율 설정](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/kakao/KakaoMessageRuleService.java), [포인트 결제](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/PointService.java)

### 발송 관리 기능
- [발신번호 관리](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/SenderNumberService.java): 발신번호 조회, 추가, 삭제
- [그룹 관리](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/ContactGroupService.java): 연락처 그룹 조회, 추가, 수정, 삭제 
- [연락처 관리](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/ContactService.java): 연락처 조회, 추가, 수정, 삭제
- [탬플릿 관리(메시지)](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/message/TemplateService.java): 메시지 탬플릿 조회, 추가, 수정, 삭제
- [탬플릿 관리(알림톡)](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/kakao/KakaoTemplateService.java): 알림톡 탬플릿 조회, 추가, 수정, 삭제

### 발송 조회 기능
- [메시지 발송 결과 조회](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/message/MessageResultService.java): 발송한 메시지 조회, 검색, 필터, 상세 내역 조회
- [알림톡 발송 결과 조회](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/kakao/KakaoMessageResultService.java): 발송한 알림톡 조회, 검색, 필터, 상세 내역 조회
- [메시지 예약 내역 조회](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/message/ReserveMessageService.java): 예약된 메시지 조회, 예약 취소
- [알림톡 예약 내역 조회](https://github.com/Send-Rabbit-Team/RNS-Spring/blob/main/src/main/java/com/srt/message/service/kakao/KakaoMessageReserveService.java): 예약된 알림톡 조회, 예약 취소

## RNS 관련 깃허브
<a href="https://github.com/Send-Rabbit-Team/RNS-React"><strong>RNS-React</strong></a></br>
<a href="https://github.com/Send-Rabbit-Team/RNS-RECEIVER"><strong>RNS-Receiver</strong></a></br>
<a href="https://github.com/Send-Rabbit-Team/RNS-Broker-KT"><strong>SMS-KT-Broker</strong></a></br>
<a href="https://github.com/Send-Rabbit-Team/RNS-Broker-SKT"><strong>SMS-SKT-Broker</strong></a></br>
<a href="https://github.com/Send-Rabbit-Team/RNS-Broker-LG"><strong>SMS-LG-Broker</strong></a></br>
<a href="https://github.com/Send-Rabbit-Team/RNS-Broker-KE"><strong>Kakao-KE-Broker</strong></a></br>
<a href="https://github.com/Send-Rabbit-Team/RNS-Broker-CNS"><strong>Kakao-CNS-Broker</strong></a></br>

          
<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[RabbitMQ.icon]: https://img.shields.io/badge/rabbitmq-%23FF6600.svg?&style=for-the-badge&logo=rabbitmq&logoColor=white
[Redis.icon]: https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white
[SpringBoot.icon]: https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot
[Kubenetis.icon]: https://img.shields.io/badge/kubernetes-326ce5.svg?&style=for-the-badge&logo=kubernetes&logoColor=white
[MySQL.icon]: https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white

[ElasticSearch.icon]: https://img.shields.io/badge/Elastic_Search-005571?style=for-the-badge&logo=elasticsearch&logoColor=white
[FluentD.icon]: https://img.shields.io/badge/Fluentd-599CD0?style=for-the-badge&logo=fluentd&logoColor=white&labelColor=599CD0
[Kibana.icon]: https://img.shields.io/badge/Kibana-005571?style=for-the-badge&logo=Kibana&logoColor=white
[Prometheus.icon]: https://img.shields.io/badge/Prometheus-000000?style=for-the-badge&logo=prometheus&labelColor=000000
[Grafana.icon]: https://img.shields.io/badge/Grafana-F2F4F9?style=for-the-badge&logo=grafana&logoColor=orange&labelColor=F2F4F9

[Jenkins.icon]: https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white

[JUnit.icon]: https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white

[Jira.icon]: https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=Jira&logoColor=white
[Slack.icon]: https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white
[Notion.icon]: https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white
