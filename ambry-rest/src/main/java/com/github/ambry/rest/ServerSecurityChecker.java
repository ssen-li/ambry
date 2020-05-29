/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.rest;

import com.github.ambry.router.Callback;
import com.github.ambry.server.ServerSecurityService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.ssl.SslHandler;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler that performs any security validation on the new HTTP2 connection.
 */
@ChannelHandler.Sharable
public class ServerSecurityChecker extends ChannelInboundHandlerAdapter {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ServerSecurityService serverSecurityService;

  public ServerSecurityChecker(ServerSecurityService serverSecurityService) {
    this.serverSecurityService = serverSecurityService;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("Channel Active " + ctx.channel().remoteAddress());
    validateSslConnection(ctx);
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("Channel Inactive " + ctx.channel().remoteAddress());
    super.channelInactive(ctx);
  }

  /**
   * Listen to the handshake future if this is an SSL connection. Log and update metrics if the handshake failed.
   * @param ctx the {@link ChannelHandlerContext}.
   */
  private void validateSslConnection(ChannelHandlerContext ctx) throws Exception {
    SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
    if (sslHandler != null) {
      sslHandler.handshakeFuture().addListener(future -> {
        if (!future.isSuccess()) {
          logger.debug("SSL handshake failed for channel: {}", ctx.channel(), future.cause());
        } else {
          logger.debug("SSL handshake succedded for channel: {}", ctx.channel(), future.cause());
          serverSecurityService.validateConnection(ctx, (r, e) -> {
            if (e != null) {
              //logger.info("security validation failed for channel: {}", ctx.channel(), e);
              ctx.channel().close();
              //throw new Http2Exception(Http2Error.INADEQUATE_SECURITY, "authorization failed", e, Http2Exception.ShutdownHint.GRACEFUL_SHUTDOWN);
            } else {
              logger.info("security validation succeeded for channel: {}", ctx.channel());
            }
          });
        }
      });
    }
  }
}
