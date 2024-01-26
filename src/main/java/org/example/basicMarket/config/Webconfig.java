package org.example.basicMarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

// Spring MVC에서 정적 리소스에 대한 핸들링을 구성하는 클래스인 Webconfig를 정의
@EnableWebMvc
@Configuration
public class Webconfig implements WebMvcConfigurer {

    @Value("${upload.image.location}")
    private String location;

    // 정적 리소스에 대한 핸들러를 추가하는 메서드
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // "localhost:8080/image/aa.jpg"를 입력하면 로컬파일시스템에 지정한 디렉토리로 이동해 aa.jpg를 찾아서 보여주는 거구나
        registry.addResourceHandler("/image/**") // url에 /image/ 접두 경로가 설정되어있으면,
                // "file:" 로컬 파일 시스템에 직접 접근하는 프로토콜로, 인터넷에 연결되어 있지 않아도 동작
                .addResourceLocations("file:" + location) // 파일 시스템의 location 경로에서 파일에 접근합니다
                // 캐시 헤더를 설정합니다. 여기서는 1시간 동안 캐시를 유지하도록 설정하고, 이 캐시를 공유할 수 있도록 cachePublic()을 사용합니다. 이는 클라이언트가 이미지를 다운로드 받고 1시간 동안 캐시된 이미지를 사용하게 됩니다. 1시간이 지나면 클라이언트는 새로운 이미지를 서버에서 받아올 것입니다.
                .setCacheControl(CacheControl.maxAge(Duration.ofHours(1L)).cachePublic()); // 업로드된 각각의 이미지는 고유한 이름을 가지고 있으며 수정되지 않을 것이기 때문에, 캐시를 설정해주었습니다. 자원에 접근할 때마다 새롭게 자원을 내려받지 않고, 캐시된 자원을 이용할 것입니다. 1시간이 지나면 캐시는 만료되고, 다시 요청하게 될 것입니다.
    }
}
