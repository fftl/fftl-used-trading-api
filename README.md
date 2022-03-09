# Used Trading API 

## 개요

중고거래 어플리케이션의 기능들을 보고 해당 기능들을 구현해 보고 싶었습니다. 그래서 당근마켓, 번개장터 등의 어플리케이션을 보고 필요한 기능과, 필요한 데이터를 나름대로 파악해 보고 구현을 시작해 보았습니다.

## 사용 기술

Java, Spring Boot, Spring Data JPA, MySQL, AWS(IAM, S3)

## DB 설계

테이블, 즉 Entity는 User, Product, Image, Review, Category 이렇게 다섯가지가 존재합니다.

```java
User

Long id;
String username;
String password;

List<Category> categories;
List<Product> myProducts;
List<Product> wishProducts;
List<Review> reviews;
Image image;

boolean removed;

```

```java
Product

Long id;

String title;
String price;
String description;

//@Embedeed String state, String city, String town
Address address; 

Status status;
Category category;

Integer like;

User user;
List<Review> review;
List<Image> images;
```

```java
Image

Long id;
String url;
    
//Enum
ImageType imageType;

Product product;
User user;
```

```java
Review

Long id;
String content;

Product product;
User user;
```

```java
Category

Long id;
String cateogryName;

```
jenkins 테스트를 다시 한번 진행합니다.