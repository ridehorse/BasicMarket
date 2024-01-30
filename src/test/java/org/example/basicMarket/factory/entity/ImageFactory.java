package org.example.basicMarket.factory.entity;

import org.example.basicMarket.entity.post.Image;
import org.springframework.test.util.ReflectionTestUtils;

public class ImageFactory {

    public static Image createImage() {
        return new Image("origin_filename.jpg");
    }

    public static Image createImageWithOriginName(String originName) {
        return new Image(originName);
    }

    public static Image createImageWithIdAndOriginName(Long id,String originName){
        Image image = new Image(originName);
        //ReflectionTestUtils는 Spring 프레임워크에서 제공하는 유틸리티 클래스로, 테스트 코드에서 리플렉션을 사용하여 프라이빗 필드나 메소드에 접근할 수 있게 도와줍니다.
        //위의 코드에서 ReflectionTestUtils.setField(image, "id", id)는 image 객체의 id라는 이름의 프라이빗 필드에 id 값을 할당하는 역할을 합니다.
        // 이것은 일반적으로 테스트 코드에서 특정 상황을 시뮬레이션하거나 특정 상태를 설정하기 위해 사용됩니다.
        //만약에 Image 클래스에 id 필드가 private으로 선언되어 있고, 테스트 코드에서 이 필드에 값을 설정해야 하는 상황이라면, 리플렉션을 사용하여 필드에 접근하고 값을 설정할 수 있습니다.
        // 하지만 이는 일반적으로 권장되는 방식은 아니며, 테스트 용도로만 사용되어야 합니다. 일반적으로는 적절한 메소드나 생성자를 통해 필드를 설정하도록 설계하는 것이 좋습니다.
        ReflectionTestUtils.setField(image, "id", id);
        return image;
    }
}
