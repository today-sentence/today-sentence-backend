package today.todaysentence.global.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.dto.MemberRequest;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class LoggingAspect {


    @Before("execution(* today.todaysentence.domain.*.service..*(..))")
    public void logWithUserDetails(JoinPoint joinPoint) {

        String methodName = joinPoint.getSignature().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<String> args = Arrays.stream(joinPoint.getArgs())
                .filter(arg ->!(arg instanceof CustomUserDetails) )
                .map(Object::toString)
                .toList();

        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            log.info("Service - [ Member :  anonymousUser ]  [ Method : {}]  [ args : {} ] ", methodName, args);
            return;
        }
        CustomUserDetails member = (CustomUserDetails) authentication.getPrincipal();
        log.info("Service - [ Member : {} ]  [ Method : [{}] ]  [ args : {} ] " , member.getMemberNickname(), methodName, args);


    }

    @AfterReturning(pointcut = "execution(* today.todaysentence.domain.*.service..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {

        String methodName = joinPoint.getSignature().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            log.info("Return - [ Member :  anonymousUser ]  [ Method : {} ]  [ Result : {} ]", methodName, result);
            return;
        }
        CustomUserDetails member = (CustomUserDetails) authentication.getPrincipal();
        log.info("Return - [ Member : {} ]  [ Method : {} ]  [ Result : {} ]", member.getMemberNickname(), methodName, result);
    }


    @AfterThrowing(pointcut = "execution(* today.todaysentence.domain..*(..))" ,throwing ="ex")
    public void logAfterThrow(JoinPoint joinPoint, Throwable ex){
        String methodName = joinPoint.getSignature().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        List<String> args = Arrays.stream(joinPoint.getArgs())
                .filter(arg ->!(arg instanceof CustomUserDetails) )
                .map(Object::toString)
                .toList();

        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            log.error("Error : [ Member :  anonymousUser ]  [ Method : {} ]  [ Exception : {} ]  [ Message : {} ]",
                     methodName, ex.getClass().getName(), ex.getMessage());

        } else {
            CustomUserDetails member = (CustomUserDetails) authentication.getPrincipal();
            log.error("Error : [ Member : {} ]  [ Method : {} ]  [ Exception : {} ]  [ Message : {} ]",
                    member.getMemberNickname(), methodName, ex.getClass().getName(), ex.getMessage());

        }

    }


}
