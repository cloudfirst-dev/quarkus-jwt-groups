package com.idsysapps.auth.dbroles.runtime.auth;

import javax.enterprise.inject.spi.CDI;

import com.idsysapps.auth.dbroles.runtime.PrincipalProducer;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class MpJwtPrincipalHandler implements HttpHandler {

  private final HttpHandler next;

  public MpJwtPrincipalHandler(HttpHandler next) {
    this.next = next;
  }

  /**
   * If there is a JWTAccount installed in the exchange security context, create
   * 
   * @param exchange - the request/response exchange
   * @throws Exception on failure
   */
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    Account account = exchange.getSecurityContext().getAuthenticatedAccount();
    if (account instanceof JWTAccount) {
      JWTAccount jwtAccount = (JWTAccount) account;
      PrincipalProducer myInstance = CDI.current().select(PrincipalProducer.class).get();
      myInstance.setAccount(jwtAccount);
    }
    next.handleRequest(exchange);
  }
}
