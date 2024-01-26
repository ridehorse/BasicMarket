package org.example.basicMarket.service.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalFileServiceTest {

    LocalFileService localFileService = new LocalFileService();
    String testLocation = new File("src/test/resources/static").getAbsolutePath() + "/"; // 1



    @BeforeEach
    void beforeEach() throws IOException { // 2
        // ReflectionTestUtils는 스프링 테스트에서 리플렉션을 사용하여 테스트 대상 객체의 프로퍼티나 필드 값을 설정하는 유틸리티 클래스입니다.
        // 테스트에서는 주로 프라이빗 필드나 메서드에 접근하여 값을 설정하거나 검사해야 할 때 사용됩니다
        // ReflectionTestUtils.setField() 메서드를 사용하여 localFileService 객체의 location 필드 값을 설정하고 있습니다.
        // 이를 통해 private 필드인 location에 값을 주입할 수 있습니다.
        // 해당 코드에서는 testLocation이라는 테스트 전용 경로를 localFileService의 location에 주입하여 파일 업로드 테스트를 수행하고 있습니다
        ReflectionTestUtils.setField(localFileService, "location", testLocation);
        FileUtils.cleanDirectory(new File(testLocation));
    }

    @Test
    void uploadTest() { // 3
        // given
        //MockMultipartFile은 스프링 테스트에서 사용되는 클래스로, MultipartFile의 구현체 중 하나입니다.
        // 주로 파일 업로드 관련 테스트에서 사용되며, 실제 파일이나 데이터를 사용하지 않고 가상의 파일 데이터를 생성하여 테스트할 수 있도록 도와줍니다.
        // 파일 이름, 원본 파일 이름, 컨텐츠 타입, 바이트 배열로 생성
        MultipartFile file = new MockMultipartFile("myFile", "myFile.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
        String filename = "testFile.txt";

        // when
        localFileService.upload(file, filename);

        // then
        assertThat(isExists(testLocation + filename)).isTrue();
    }

    @Test
    void deleteTest() {
        // given
        MultipartFile file = new MockMultipartFile("myFile", "myFile.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
        String filename = "testFile.txt";
        localFileService.upload(file, filename);
        boolean before = isExists(testLocation + filename);

        // when
        localFileService.delete(filename);

        // then
        boolean after = isExists(testLocation + filename);
        assertThat(before).isTrue();
        assertThat(after).isFalse();
    }

    boolean isExists(String filePath) {
        return new File(filePath).exists();
    }
}
