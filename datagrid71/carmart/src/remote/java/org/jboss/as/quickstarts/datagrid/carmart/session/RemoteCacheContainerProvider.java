/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.datagrid.carmart.session;

import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jboss.as.quickstarts.datagrid.carmart.session.CacheContainerProvider;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

/**
 * 
 * {@link CacheContainerProvider}'s implementation creating a HotRod client. 
 * JBoss Data Grid server needs to be running and configured properly 
 * so that HotRod client can remotely connect to it - this is called client-server mode.
 * 
 * @author Martin Gencur
 * 
 */
@ApplicationScoped
public class RemoteCacheContainerProvider extends CacheContainerProvider {

    private Logger log = Logger.getLogger(this.getClass().getName());

    private BasicCacheContainer manager;

    public BasicCacheContainer getCacheContainer() {
        if (manager == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addServer()
                 .host(jdgProperty(DATAGRID_HOST))
                 .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));

            builder.security() 
    		.authentication()
    		.enable()
    		.serverName("jdg-server")
    		.saslMechanism("DIGEST-MD5")
    		.callbackHandler(new TestCallbackHandler(jdgProperty("datagrid.username"), "ApplicationRealm", jdgProperty("datagrid.password").toCharArray()));

            manager = new RemoteCacheManager(builder.build());
            log.info("=== Using RemoteCacheManager (Hot Rod) ===");
        }
        return manager;
    }

    public static class TestCallbackHandler implements CallbackHandler {
      final private String username;
      final private char[] password;
      final private String realm;
   
      public TestCallbackHandler(String username, String realm, char[] password) {
         this.username = username;
         this.password = password;
         this.realm = realm;
      }
   
      @Override
      public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
         for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
               NameCallback nameCallback = (NameCallback) callback;
               nameCallback.setName(username);
            } else if (callback instanceof PasswordCallback) {
               PasswordCallback passwordCallback = (PasswordCallback) callback;
               passwordCallback.setPassword(password);
            } else if (callback instanceof AuthorizeCallback) {
               AuthorizeCallback authorizeCallback = (AuthorizeCallback) callback;
               authorizeCallback.setAuthorized(authorizeCallback.getAuthenticationID().equals(
                     authorizeCallback.getAuthorizationID()));
            } else if (callback instanceof RealmCallback) {
               RealmCallback realmCallback = (RealmCallback) callback;
               realmCallback.setText(realm);
            } else {
               throw new UnsupportedCallbackException(callback);
            }
         }
      }
   }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}
