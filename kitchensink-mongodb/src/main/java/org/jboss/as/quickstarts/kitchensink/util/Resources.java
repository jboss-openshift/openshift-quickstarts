/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.jboss.as.quickstarts.kitchensink.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jboss.as.quickstarts.kitchensink.model.Member;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 * <p>
 * <p>
 * Example injection on a managed bean field:
 * </p>
 * <p>
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
public class Resources {

    public static final String DB_SERVICE_PREFIX_MAPPING = "DB_SERVICE_PREFIX_MAPPING";
    @Inject
    private DbServicePrefixMappingParser dbServicePrefixMappingParser;

    @Produces
    private MongoClient produceMongoClient() {
        List<DbServicePrefixMappingParser.DbServicePrefixMapping> mappings = dbServicePrefixMappingParser.parseDbServicePrefixMappingEnvVar(System.getenv(DB_SERVICE_PREFIX_MAPPING));
        for (DbServicePrefixMappingParser.DbServicePrefixMapping mapping : mappings) {
            if ("MONGODB".equals(mapping.getDatabaseType().toUpperCase())) {
                String hostname = System.getenv(mapping.getServiceName() + "_SERVICE_HOST");
                String port = System.getenv(mapping.getServiceName() + "_SERVICE_PORT");
                String database = System.getenv(mapping.getEnvPrefix() + "_DATABASE");
                String username = System.getenv(mapping.getEnvPrefix() + "_USERNAME");
                String password = System.getenv(mapping.getEnvPrefix() + "_PASSWORD");
                MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
                return new MongoClient(new ServerAddress(hostname, Integer.parseInt(port)), Collections.singletonList(credential));
            }
        }
        throw new IllegalStateException("No MongoDB mapping in " + DB_SERVICE_PREFIX_MAPPING);
    }

    @Produces
    private MongoDatabase produceMongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(System.getenv("DB_DATABASE"));
    }

    @Produces
    private MongoCollection<Document> produceMongoCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection(Member.class.getSimpleName());
    }

    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Produces
    @RequestScoped
    public FacesContext produceFacesContext() {
        return FacesContext.getCurrentInstance();
    }

}
