# Used Trading API 

## ✏개요

중고거래 어플리케이션의 기능들을 보고 해당 기능들을 구현해 보고 싶었습니다. 그래서 당근마켓, 번개장터 등의 어플리케이션을 보고 필요한 기능과, 필요한 데이터를 나름대로 파악해 보고 구현을 시작해 보았습니다.

## 🛠사용 기술

- Java11
- Spring Boot 2.5
- Gradle
- Spring Data JPA
- H2
- AWS S3
- AWS EC2
- docker-compose
- jenkins

## ⛓프로젝트의 구조

![used_trading_api_구조도](https://user-images.githubusercontent.com/69035612/158099277-ab9792c1-df26-4361-9c8f-13d240e46a68.png)

## ⛓DB ERD

![used_trading_api_erd](https://user-images.githubusercontent.com/69035612/158097692-334303f7-515c-4340-bd8a-4c0cb303d241.png)
 

## 📄프로젝트 이야기

### - 테스트 케이스 작성
여러 번 작은 프로젝트를 진행하면서 단위 테스트를 작성해 본 적도 있고, 통합 테스트를 작성해 본 적도 있었습니다. 그런데 상대적으로 규모가 컸던 이번 프로젝트를 진행하면서
단위 테스트를 작성하는 것이 생각보다 오랜 시간이 소요된 다는 것을 느꼈습니다. 그러나 테스트를 작성하면서 내가 만들었던 코드들에 대한 이해가 늘어나고 검토를 하는 효과도 있다는 것을 느끼게 되었습니다.

### - AWS S3에 파일 업로드하기
유저와 상품에 대한 이미지를 업로드하기 위해서 AWS S3을 이용해보았습니다. 작업을 진행하던 중 AWS Keys에 대해서 문제를 겪게 되었습니다. 로컬 환경에서 진행을 할 때에는 AWS의 access_key, secret_key를 aws.yml에 입력해 놓고 .gitignore를 이용하여 제외시키면 안전하게 사용할 수 있었지만
docker-compose를 이용해서 배포하려면 결국 github에 코드가 올라가야만 하는 문제가 발생했습니다. 이런 보안이 필요한 key를 배포 해본 적이 없던 저는 당황했지만 천천히 해결 방법을 찾기 시작했고
AWS의 IAM role을 이용해서 S3에 대한 권한을 준 뒤 EC2에 연결을 해주는 방법으로 잘 해결을 해낼 수 있었습니다.

### - AWS EC2 프리티어 메모리 부족
jenkins와 github을 연결한 뒤 빌드를 진행해보았습니다. 그런데 jeknins에서의 빌드가 끝나지 않는 문제가 발생했습니다. 무슨 문젠가 하여 EC2 서버를 살펴보니 서버의 CPU 사용율이 90퍼센트를 넘어 가고 있었습니다. 즉 과부화가 걸린 것으로 보였습니다.

<img src="https://user-images.githubusercontent.com/69035612/158112962-88279a92-6d1e-4042-a741-93a55894bab1.png" width="400px" height="400px" />

그래서 프리티어로 사용하면은 jenkins는 사용하지 못하는건가? 프로젝트가 크기가 커서 그런가? 하는 각을 하고 방법을 찾아보던 와중에 swap 메모리라는 방법을 찾게 되었습니다. 컴퓨터에서 제공하는 물리적인 메모리 공간이
가득 찼을 때 HDD나 SDD의 일정부분을 할당받아 메모리 공간으로 사용할 수 있도록 도와주는 방법이었고 이를 적용하여 프로젝트를 무사히 빌드 시킬 수 있었습니다.


<!-- 
- 테스트 케이스 작성하기
- 이미지 AWS S3에 업로드하기
- AWS EC2, Docker, Jenkins 적용해보기
  - 구조에 대한 이해하기 
  - AWS EC2 프리티어를 사용하는 중 메모리 부족 문제 발생
-->