package org.example.basicMarket.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.helper.NestedConvertHelper;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    private String name;
    private List<CategoryDto> children;

    public static List<CategoryDto> toDtoList(List<Category> categories){

        // 람다식은 지금 진행되지 않는다. categories의 타입은 List<Catogory>로 일반적인 형태지만,
        // 나머지 4개의 매개변수는 람다식 형태이기 떄문에 타입이 Function 형태이다.
        // 즉 람다식 자체가 매개변수에 인자로 들어가는 것이다.
        // NestedConvertHelper 객체의 Function 타입의 4개의 멤버변수들에 각 4개의 람다식이 저장된다.
        // newInstance(생성자생성)의 역할은 딱 여기까지다. 이 람다식이 활용되는 순간은
        //NestedConvertHelper 내부에서 다른 매서드(apply())에 의해 진행될 것이다.
        NestedConvertHelper helper = NestedConvertHelper.newInstance(
                categories,
                c->new CategoryDto(c.getId(),c.getName(),new ArrayList<>()),
                c->c.getParent(),
                c->c.getId(),
                d->d.getChildren()
        );

        return helper.convert();
    }
}
