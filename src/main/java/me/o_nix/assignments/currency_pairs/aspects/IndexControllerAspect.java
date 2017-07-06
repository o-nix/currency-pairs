package me.o_nix.assignments.currency_pairs.aspects;

import lombok.extern.slf4j.Slf4j;
import me.o_nix.assignments.currency_pairs.dto.PairDetailsResponse;
import me.o_nix.assignments.currency_pairs.filters.LoggingFilter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
public class IndexControllerAspect {
  @Autowired
  private ApplicationContext context;

  @Pointcut("execution(* me.o_nix.assignments.currency_pairs.controllers.IndexController.getRatesForDates(..))")
  public void indexControllerSpecificMethods() {}

  @AfterReturning(value = "indexControllerSpecificMethods()", returning = "entity")
  public void processAfterReturn(JoinPoint joinPoint, PairDetailsResponse entity) throws Throwable {
    log.debug("Catching IndexController response...");

    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

    if (requestAttributes instanceof ServletRequestAttributes) {
      HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

      request.setAttribute(LoggingFilter.RESULT_KEY, entity);
    }
  }
}
