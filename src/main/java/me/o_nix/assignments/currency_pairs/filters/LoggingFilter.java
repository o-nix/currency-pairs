package me.o_nix.assignments.currency_pairs.filters;

import lombok.extern.slf4j.Slf4j;
import me.o_nix.assignments.currency_pairs.controllers.IndexController;
import me.o_nix.assignments.currency_pairs.dto.PairDetailsResponse;
import me.o_nix.assignments.currency_pairs.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Slf4j
public class LoggingFilter implements Filter {
  public static final String RESULT_KEY = "x-result";
  private static final String PAIR_URL_PATTERN = "^[A-Z]{3}/[A-Z]{3}";

  @Autowired
  LoggingService loggingService;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    // Log only main pair details requests

    String url = ((HttpServletRequest) request).getRequestURL().toString();
    int pairRequestMatchPos = url.lastIndexOf(IndexController.API_PAIR_URL);

    boolean prefixMatches = pairRequestMatchPos > -1;
    boolean urlMatches = false;
    long logId = 0;

    if (prefixMatches) {
      url = url.substring(pairRequestMatchPos + IndexController.API_PAIR_URL.length());
      if (url.charAt(0) == '/') {
        url = url.substring(1);
      }

      urlMatches = url.matches(PAIR_URL_PATTERN);

      if (urlMatches) {
        String ip = request.getRemoteAddr();
        logId = loggingService.logInteraction(ip, url);

        log.info("IP {} is requesting this URL for the {} time", ip, loggingService.countRequestsFromIp(ip));
      }
    }

    // Wait for response to be set successfully
    filterChain.doFilter(request, response);

    if (prefixMatches && urlMatches) {
      PairDetailsResponse result = (PairDetailsResponse) request.getAttribute(RESULT_KEY);

      if (result != null) {
        loggingService.updateInteractionDetails(logId, result);
      }
    }
  }

  @Override
  public void destroy() {
  }
}
