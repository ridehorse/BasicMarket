package org.example.basicMarket.helper;


import org.example.basicMarket.exception.CannotConvertNestedStructureException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NestedConvertHelper<K,E,D> {

    private List<E> entities; // 플랫한 구조의 엔티티 목록
    private Function<E, D> toDto; // 엔티티를 DTO로 변환해주는 Function
    private Function<E, E> getParent; // 엔티티의 부모 엔티티를 반환해주는 Function
    private Function<E, K> getKey; // 엔티티의 Key(id)를 반환해주는 function
    private Function<D, List<D>> getChildren; // DTO의 children 리스트를 반환해주는 Function

    public static <K, E, D> NestedConvertHelper newInstance(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        return new NestedConvertHelper<K, E, D>(entities, toDto, getParent, getKey, getChildren);
    }

    private NestedConvertHelper(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        this.entities = entities;
        this.toDto = toDto;
        this.getParent = getParent;
        this.getKey = getKey;
        this.getChildren = getChildren;
    }

    public List<D> convert() {
        try {
            return convertInternal();
        } catch (NullPointerException e) {
            throw new CannotConvertNestedStructureException(e.getMessage());
        }
    }

    private List<D> convertInternal() {
        Map<K, D> map = new HashMap<>();
        List<D> roots = new ArrayList<>();

        // e: Catogory Entity
        for (E e : entities) { // List<Category>에서 List에 담긴 순서대로 Category 객체 반환
            D dto = toDto(e); // toDto(e) = toDto.apply(e); Category 객체의 id, name값을 CategoryDto 객체의 id name필드에 저장하고, children필드에는 빈 리스트 저장
            map.put(getKey(e), dto); // map에 Category객체의 id값을 키에 저장, 위  코드에서 만든 CategoryDto 객체를 벨류에 저장
            if (hasParent(e)) { // Category객체의 parent 필드가 존재하는지 확인 -> 존재한다면 if으로 들어감
                E parent = getParent(e); //Category객체에서 parent 필드의 Category객체를 반환
                K parentKey = getKey(parent); // 위 코드에서 반환된 부모 Category객체의 id값 반환
                D parentDto = map.get(parentKey); // 부모 Category객체의 id 값으로 if문 들어오기전에 만들었던 map에서 key 값에 대응 하는 벨류값을 반환(CategoryDto객체)
                                                  // 부모를 가지고 있는 자식객체이므로 그 부모 객체는 먼저 조회되서 map에 key와 value로 저장되있어야 한다. 없다면 오류 발생
                getChildren(parentDto).add(dto);  // 위에서 찾은 부모 CategoryDto 객체에서 children 필드에 담긴 값을 반환한다. -> List<CategoryDto>형태로 반환(지금은 빈 List일 것이다) 여기에 add로 자식Category 객체 저장
                                                  // 이렇게 CategoryDto에 자식객체들를 저장함으로써 계층구조를 완성했다.
            } else {
                roots.add(dto); // parent 필드가 없다면 최상위 객체이므로 roots List에 따로 저장
            }
        }
        return roots; // roots만 반환해도 되는 이유는 roots에 저장된 dto는 map에도 저장되어있기 떄문에(roots와 map에는 같은 dto객체가 저장) map에서 dto를 꺼내 수정하면 결국 roots에 있는 부모dto가 수정되는것과 마찬가지이다.(children필드가 채워지는것)
        // 최상의 dto만 저장하면 나머지 자식들은 children필드에 의해서 저장되어져 있기 떄문에 roots내에 따로 저장할 필요는 없다.
    }
//    1 -> 3 -> 5
//    1 : map, roots
//    3 : map, 1의 dto에 children 필드
//    5 : map, 3의 dto에 children 필드

    private boolean hasParent(E e) {
        return getParent(e) != null;
    }

    //c->c.getParent()
    private E getParent(E e) {
        return getParent.apply(e);
    }

    //CategoryDto <id,name,children>
    //c->new CategoryDto(c.getId(),c.getName(),new ArrayList<>()) : 빈 리스트이므로 자식이 없는 상태의 d를 만든다.
    private D toDto(E e) {
        return toDto.apply(e);
    }

    //c->c.getId()
    private K getKey(E e) {
        return getKey.apply(e);
    }

    //d->d.getChildren()
    private List<D> getChildren(D d) {
        return getChildren.apply(d);
    }
}
