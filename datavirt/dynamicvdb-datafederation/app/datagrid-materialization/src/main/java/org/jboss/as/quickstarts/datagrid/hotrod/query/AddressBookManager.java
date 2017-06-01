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
package org.jboss.as.quickstarts.datagrid.hotrod.query;

import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.Memo;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.Person;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.PhoneNumber;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.PhoneType;
import org.jboss.as.quickstarts.datagrid.hotrod.query.marshallers.PersonMarshaller;
import org.jboss.as.quickstarts.datagrid.hotrod.query.marshallers.PhoneNumberMarshaller;
import org.jboss.as.quickstarts.datagrid.hotrod.query.marshallers.PhoneTypeMarshaller;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

/**
 * @author Adrian Nistor
 */
public class AddressBookManager {

   private static final String SERVER_HOST = "jdg.host";
   private static final String HOTROD_PORT = "jdg.hotrod.port";
   private static final String CACHE_NAME = "jdg.cache";
   private static final String CACHE_MAT_NAME = "jdg.mat.cache";
   private static final String CACHE_ALIAS_NAME = "jdg.alias.cache";

   private static final String PROPERTIES_FILE = "jdg.properties";

   private static final String PROTOBUF_DEFINITION_RESOURCE = "/quickstart/addressbook.proto";

   private static final String APP_MENU = "\nAvailable actions:\n" +
         "0. Display available actions\n" +
         "1. Add person\n" +
         "2. Remove person\n" +
         "3. Add phone to person\n" +
         "4. Remove phone from person\n" +
         "5. Query persons by name\n" +
         "6. Query persons by phone\n" +
         "7. Query memo by author\n" +
         "8. Display all cache entries\n" +
         "9. Quit\n";

   private RemoteCacheManager cacheManager;

   /**
    * A cache that hold both Person and Memo objects.
    */
   private RemoteCache<Integer, Object> addressbookCache;
   private RemoteCache<Integer, Object> addressbookMatCache;
   private RemoteCache<Object, Object> aliasCache;

   public AddressBookManager() throws Exception {
      final String host = jdgProperty(SERVER_HOST);
      final int hotrodPort = Integer.parseInt(jdgProperty(HOTROD_PORT));
      final String cacheName = jdgProperty(CACHE_NAME);  // The name of the address book  cache, as defined in your server config.
     final String cacheMatName = jdgProperty(CACHE_MAT_NAME);  // The name of the address book  cache, as defined in your server config.
     final String cacheAliasName = jdgProperty(CACHE_ALIAS_NAME);  // The name of the address book  cache, as defined in your server config.

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer()
            .host(host)
            .port(hotrodPort)
            .marshaller(new ProtoStreamMarshaller());  // The Protobuf based marshaller is required for query capabilities
      cacheManager = new RemoteCacheManager(builder.build());

      addressbookCache = cacheManager.getCache(cacheName);
      if (addressbookCache == null) {
         throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
      }
      addressbookMatCache = cacheManager.getCache(cacheMatName);
      if (addressbookMatCache == null) {
         throw new RuntimeException("Cache '" + cacheMatName + "' not found. Please make sure the server is properly configured");
      }
      aliasCache = cacheManager.getCache(cacheAliasName);
      if (aliasCache == null) {
         throw new RuntimeException("Cache '" + cacheAliasName + "' not found. Please make sure the server is properly configured");
      }
      

      registerSchemasAndMarshallers();
   }

   /**
    * Register the Protobuf schemas and marshallers with the client and then register the schemas with the server too.
    */
   private void registerSchemasAndMarshallers() throws IOException {
      // Register entity marshallers on the client side ProtoStreamMarshaller instance associated with the remote cache manager.
      SerializationContext ctx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
      ctx.registerProtoFiles(FileDescriptorSource.fromResources(PROTOBUF_DEFINITION_RESOURCE));
      ctx.registerMarshaller(new PersonMarshaller());
      ctx.registerMarshaller(new PhoneNumberMarshaller());
      ctx.registerMarshaller(new PhoneTypeMarshaller());

      // generate the 'memo.proto' schema file based on the annotations on Memo class and register it with the SerializationContext of the client
 //      ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
//       String memoSchemaFile = protoSchemaBuilder
//             .fileName("memo.proto")
//             .packageName("quickstart")
//             .addClass(Memo.class)
//             .build(ctx);
// 
//       // register the schemas with the server too
//       RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
//       metadataCache.put(PROTOBUF_DEFINITION_RESOURCE, readResource(PROTOBUF_DEFINITION_RESOURCE));
//       metadataCache.put("memo.proto", memoSchemaFile);
//       String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
//       if (errors != null) {
//          throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
//       }
   }

   private void queryPersonByName() {
      String namePattern = readConsole("Enter person name pattern: ");

      QueryFactory qf = Search.getQueryFactory(addressbookCache);
      Query query = qf.from(Person.class)
            .having("name").like(namePattern).toBuilder()
            .build();

      List<Person> results = query.list();
      System.out.println("Found " + results.size() + " matches:");
      for (Person p : results) {
         System.out.println(">> " + p);
      }
   }

   private void queryPersonByPhone() {
      String phoneNumber = readConsole("Enter phone number: ");

      QueryFactory qf = Search.getQueryFactory(addressbookCache);
      Query query = qf.from(Person.class)
            .having("phone.number").eq(phoneNumber).toBuilder()
            .build();

      List<Person> results = query.list();
      System.out.println("Found " + results.size() + " matches:");
      for (Person p : results) {
         System.out.println(">> " + p);
      }
   }

   private void addPerson() {
      int id = Integer.parseInt(readConsole("Enter person id (int): "));
      String name = readConsole("Enter person name (string): ");
      String email = readConsole("Enter person email (string): ");
      Person person = new Person();
      person.setId(id);
      person.setName(name);
      person.setEmail(email);

      // put the Person in cache
      addressbookCache.put(person.getId(), person);
   }

   private void removePerson() {
      int id = Integer.parseInt(readConsole("Enter person id to remove (int): "));

      // remove from cache
      Person prevValue = (Person) addressbookCache.withFlags(Flag.FORCE_RETURN_VALUE).remove(id);
      System.out.println("Removed: " + prevValue);
   }

   private void addPhone() {
      System.out.println("Adding a phone number to a person");
      int id = Integer.parseInt(readConsole("Enter person id (int): "));
      Person person = (Person) addressbookCache.get(id);
      if (person == null) {
         System.out.println("Person not found");
         return;
      }
      System.out.println("> " + person);

      String number = readConsole("Enter phone number (string): ");
      PhoneType type = PhoneType.valueOf(readConsole("Enter phone type " + EnumSet.allOf(PhoneType.class) + ": ").toUpperCase());
      List<PhoneNumber> phones = person.getPhones();
      if (phones == null) {
         phones = new ArrayList<PhoneNumber>();
      }
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.setNumber(number);
      phoneNumber.setType(type);
      phones.add(phoneNumber);
      person.setPhones(phones);

      // update the Person in cache
      addressbookCache.put(person.getId(), person);
   }

   private void removePhone() {
      System.out.println("Removing a phone number from a person");
      int id = Integer.parseInt(readConsole("Enter person id (int): "));
      Person person = (Person) addressbookCache.get(id);
      if (person == null) {
         System.out.println("Person not found");
         return;
      }
      System.out.println("> " + person);

      if (person.getPhones() != null && !person.getPhones().isEmpty()) {
         int idx = Integer.parseInt(readConsole("Enter phone index [0.." + (person.getPhones().size() - 1) + "]: "));
         if (idx < 0 || idx >= person.getPhones().size()) {
            System.out.println("Index out of range");
            return;
         }
         person.getPhones().remove(idx);

         // update the Person in cache
         addressbookCache.put(person.getId(), person);
      } else {
         System.out.println("The person does not have any phones");
      }
   }

   private void printAllEntries() {
      for (int id : addressbookCache.keySet()) {
         Object entry = addressbookCache.get(id);
         System.out.println(entry);
      }
   }

   private void addMemo() {
      int id = Integer.parseInt(readConsole("Enter memo id (int): "));
      String text = readConsole("Enter memo text (string): ");
      Memo.Priority priority = Memo.Priority.valueOf(readConsole("Enter priority " + EnumSet.allOf(Memo.Priority.class) + ": ").toUpperCase());

      int authorId = Integer.parseInt(readConsole("Enter author id (int): "));
      Person author = (Person) addressbookCache.get(authorId);
      if (author == null) {
         System.out.println("Person not found");
         return;
      }
      System.out.println("> " + author);

      Memo memo = new Memo();
      memo.setId(id);
      memo.setText(text);
      memo.setPriority(priority);
      memo.setAuthor(author);

      // put the Memo in cache
      addressbookCache.put(memo.getId(), memo);
   }

   private void queryMemoByAuthor() {
      String namePattern = readConsole("Enter person name pattern: ");

      QueryFactory qf = Search.getQueryFactory(addressbookCache);
      Query query = qf.from(Memo.class)
            .having("author.name").like(namePattern).toBuilder()
            .build();

      List<Memo> results = query.list();
      System.out.println("Found " + results.size() + " matches:");
      for (Memo p : results) {
         System.out.println(">> " + p);
      }
   }

   private void stop() {
      cacheManager.stop();
   }

   public static void main(String[] args) throws Exception {
      AddressBookManager manager = new AddressBookManager();
      System.out.println(APP_MENU);

      while (true) {
         try {
            String action = readConsole("> ");
            if (action == null) {
               continue;
            }
            action = action.trim();
            if (action.length() == 0) {
               continue;
            }

            if ("0".equals(action)) {
               System.out.println(APP_MENU);
            } else if ("1".equals(action)) {
               manager.addPerson();
            } else if ("2".equals(action)) {
               manager.removePerson();
            } else if ("3".equals(action)) {
               manager.addPhone();
            } else if ("4".equals(action)) {
               manager.removePhone();
            } else if ("5".equals(action)) {
               manager.queryPersonByName();
            } else if ("6".equals(action)) {
               manager.queryPersonByPhone();
            } else if ("7".equals(action)) {
               manager.queryMemoByAuthor();
            } else if ("8".equals(action)) {
               manager.printAllEntries();
            } else if ("9".equals(action)) {
               System.out.println("Bye!");
               break;
            } else {
               System.out.println("\nUnrecognized action!");
               System.out.println(APP_MENU);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      manager.stop();
   }

   private static String readConsole(String prompt) {
      // this method is intended to be as simple as possible rather than
      // being efficient by caching a reference to the console/buffered reader

      Console con = System.console();
      if (con != null) {
         return con.readLine(prompt);
      }

      System.out.print(prompt);
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         return reader.readLine();
      } catch (IOException ex) {
         throw new IOError(ex);
      }
   }

   private String jdgProperty(String name) {
      InputStream res = null;
      try {
         res = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
         Properties props = new Properties();
         props.load(res);
         return props.getProperty(name);
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      } finally {
         if (res != null) {
            try {
               res.close();
            } catch (IOException e) {
               // ignore
            }
         }
      }
   }

   private String readResource(String resourcePath) throws IOException {
      InputStream is = getClass().getResourceAsStream(resourcePath);
      try {
         final Reader reader = new InputStreamReader(is, "UTF-8");
         StringWriter writer = new StringWriter();
         char[] buf = new char[1024];
         int len;
         while ((len = reader.read(buf)) != -1) {
            writer.write(buf, 0, len);
         }
         return writer.toString();
      } finally {
         is.close();
      }
   }
}
