package me.o_nix.assignments.currency_pairs;

import me.o_nix.assignments.currency_pairs.controllers.IndexController;
import me.o_nix.assignments.currency_pairs.dto.PairDetailsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CurrencyPairsApplication.class)
@WebIntegrationTest("server.port:0")
@ActiveProfiles({"dev", "integration"})
public class CurrencyPairsApplicationTests {
  @Value("${local.server.port}")
  private int port;

  @Test
	public void happyIntegrationPath() {
    RestTemplate restTemplate = new TestRestTemplate();

    String url = String.format("http://localhost:%d%s/EUR/USD?from=2017-06-01&to=2017-07-01",
        port, IndexController.API_PAIR_URL);
    PairDetailsResponse result = restTemplate.getForObject(
        url, PairDetailsResponse.class);

    Assert.notNull(result, "Empty response");
    Assert.notEmpty(result.getDates(), "Should have dates fetched");
    Assert.notEmpty(result.getValues(), "Should have values fetched");
    Assert.isTrue(result.getValues().size() == result.getDates().size(),
        "Should have same length");
  }
}
