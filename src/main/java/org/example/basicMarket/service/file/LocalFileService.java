package org.example.basicMarket.service.file;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.exception.FIleUploadFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class LocalFileService implements FileService{

    @Value("${upload.image.location}")
    private String location; // 1

    @PostConstruct //스프링부트가 빈을 생성한후에 POstConstruct가 적용된 매서드를 실행한다.
    void postConstruct() { // 2
        File dir = new File(location); // "/window/aaa/" 형태면 폴더가 생성되고 , hi.html 형태면 file이 생성되나 보다. "/window/aaa/hi.html" 형태면 aaa 디렉토리에 hi.html 파일을 만드는것같다. 다만 aaa디렉토리는 존재해야 한다.(디렉토리까지 생성하지는 않는것 같다)
        if (!dir.exists()) {
            dir.mkdir(); // 업로드 파일 생성
        }
    }

    @Override
    public void upload(MultipartFile file, String filename) { // 3
        try {
            file.transferTo(new File(location + filename)); // MultipartFile 객체를 location 디렉토리에 filname(~~~.jpg형태)을 가진 파일로 저장한다.
        }catch(IOException e) {
            throw new FIleUploadFailureException(e);
        }
    }

    @Override
    public void delete(String filename) {
        new File(location+filename).delete();
    }

}
