plugins {
    id("java")
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    // 요청 객체 검증
    implementation ("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("com.h2database:h2") // 인메모리 데이터베이스
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    //테스트 코드용 롬복
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // 1 스프링 시큐리티를 사용하기 위한 스타터 추가
    implementation("org.springframework.boot:spring-boot-starter-security")
// 2 타임리프에서 스프링 시큐리티를 사용하기 위한 의존성 추가
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
// 3 스프링 시큐리티를 테스트하기 위한 의존성 추가
    testImplementation("org.springframework.security:spring-security-test")

    implementation("io.jsonwebtoken:jjwt:0.9.1") // 자바 JWT 라이브러리
    implementation("javax.xml.bind:jaxb-api:2.3.1") // XML 문서와 Java 객체 간 매핑 자동화

    // 0Auth2를 사용하기 위한 스타터 추가
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
}

tasks.test {
    useJUnitPlatform()
}