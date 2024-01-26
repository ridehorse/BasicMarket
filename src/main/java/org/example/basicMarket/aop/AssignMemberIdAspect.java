package org.example.basicMarket.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.basicMarket.config.security.gaurd.AuthHelper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Aspect // 1
@Component // 2
@RequiredArgsConstructor
@Slf4j
public class AssignMemberIdAspect {

    private final AuthHelper authHelper;

    @Before("@annotation(org.example.basicMarket.aop.AssignMemberId)") // 3
    public void assignMemberId(JoinPoint joinPoint) { // 4
        Arrays.stream(joinPoint.getArgs()) // target method의 매개변수에 담긴 인자를 가져온다.create(@Valid @ModelAttribute PostCreateRequest req) 매서드에 적용되어있으므로, 매개변수가 1개이다. 그러므로 길이가1인 배열이 되어 반환된다. 요소값은 req이다.
                .forEach(arg -> getMethod(arg.getClass(), "setMemberId") // forEach : 각 요소를 순회, getClass() : 모든 object에 있는 매서드로, 이 매서드에 의해반환된 객체는 쿨래스의 메타데이터(필드,상위클래스,매서드)정보를 얻을 수 있다.
                        .ifPresent(setMemberId -> invokeMethod(arg, setMemberId, authHelper.extractMemberId()))); // ifPresent :getMethod(arg.getClass(), "setMemberId")의 결과값은 Optional<T>이다. Optional의 내부가 null이 아닐때 내부 코드를 실행한다. 인자로 T가 전달된다.
    }

    private Optional<Method> getMethod(Class<?> clazz, String methodName) { // 6
        try {
            return Optional.of(clazz.getMethod(methodName, Long.class)); // getMethod() 이름이 mothodName변수와 같고, 매개변수의 타입이 Long인 매서드를 찾아 반환 Optional<T>
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private void invokeMethod(Object obj, Method method, Object... args) { // 7
        try {
            method.invoke(obj, args); // invoke() : method 호출될 매서드, obj : 매서드를 가지고 있는 class(객체), 매서드에 전달된 인자들, SetMemberId(Long memberId)를 호출해 인자를 전달하고 수행한다.
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
