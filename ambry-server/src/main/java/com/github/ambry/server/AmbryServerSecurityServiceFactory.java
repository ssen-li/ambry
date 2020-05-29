/*
 * Copyright 2020 LinkedIn Corp. All rights reserved.
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

package com.github.ambry.server;

import com.github.ambry.account.AccountService;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.commons.ServerMetrics;
import com.github.ambry.config.FrontendConfig;
import com.github.ambry.config.ServerConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.frontend.IdSigningService;
import com.github.ambry.frontend.SecurityService;
import com.github.ambry.frontend.UrlSigningService;


public class AmbryServerSecurityServiceFactory implements ServerSecurityServiceFactory {

  private final ServerConfig serverConfig;
  private final ServerMetrics serverMetrics;

  public AmbryServerSecurityServiceFactory(VerifiableProperties verifiableProperties, ServerMetrics serverMetrics) {
    this.serverConfig = new ServerConfig(verifiableProperties);
    this.serverMetrics = serverMetrics;
  }

  @Override
  public ServerSecurityService getServerSecurityService() throws InstantiationException {
    return new AmbryServerSecurityService(serverConfig, serverMetrics);
  }
}