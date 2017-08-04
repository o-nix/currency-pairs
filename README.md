## Meritservus Test Assignment [![Build Status](https://travis-ci.org/o-nix/currency-pairs.svg?branch=master)](https://travis-ci.org/o-nix/currency-pairs)

![Site Screenshot](https://user-images.githubusercontent.com/647149/28976214-b6d6db30-7945-11e7-95ea-b2f9f5c4d194.png)

### Description

Write a client-server currency exchange service based on requirements.

Client part is a view with controls for choosing currency pair and date range.
Server side represents a service which should be able to return JSON messages with exchange currency
 coefficient of pairs (like: USD-EUR).

Message in JSON format should contains currency pair (USDEUR, USDRUB, etc), date range (period of dates),
and array of currency coefficients.

For currency coefficients random values can be used.

The messages exchange flow should be implemented via AJAX requests to the server.

The service should be able to collect and persist statistic either.

The statistic should hold next information: Timestamp of request, IP of request,
 count of requests from same IP, currency pair and currency coefficient.

### Requirements

* Java 8
* JSP or HTML
* JQuery
* Bootstrap
* Tomcat 7
* Spring 4.1
* MySQL
* JPA

#### Additional components

* Spring Boot 1.2.8 with Spring Core 4.1.9
* Lombok
* opencsv
* Docker + Docker Compose

Exchange rates data are downloaded from European Central Bank's
 [Euro foreign exchange reference rates][1].  

#### Good parts

* Real data
* Integration and Unit tests
* Layers and Patterns
* Low-level JPA queries, fetching optimization, transactions nesting, EAV pattern
* AspectJ-assisted logging
* Spring's internals (e.g. `PropertyEditors`, bean overrides, etc.)
* Overall use of `java.nio.file`
* Java 8 time conversions, everything is in UTC
* `BigDecimals` and `DECIMAl` for the financial arithmetic
* Object-oriented Javascript code, BEM-like CSS, Flexbox, animations

#### Missing parts

##### Backend
  * Downloaded data has some gaps, so initially I had an idea of
     automatic refreshes from https://openexchangerates.org but due to their limits and the lack of my free time
     it has not been implemented and there is no re-import functionality yet
  * Seems this old version of Spring Data does not support [projections][1]
  * Logging happens in the same thread, make stacking / in a separate thread
  * No Flyway migrations yet

##### UI
  * No local fallback from CDN
  * No preprocessing for static files (JS/CSS), no additional cache busting via additional URL params,
    only `Last-Modified`
  * No Babel configured, no CSS/polyfills for older browsers
  
[1]: http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections
[2]: http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html
