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
package org.jboss.as.quickstarts.datagrid.hotrod.query.marshallers;

import org.infinispan.protostream.MessageMarshaller;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.Person;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.PhoneNumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adrian Nistor
 */
public class PersonMarshaller implements MessageMarshaller<Person> {

   @Override
   public String getTypeName() {
      return "quickstart.Person";
   }

   @Override
   public Class<Person> getJavaClass() {
      return Person.class;
   }

   @Override
   public Person readFrom(ProtoStreamReader reader) throws IOException {
      String name = reader.readString("name");
      int id = reader.readInt("id");
      String email = reader.readString("email");
      List<PhoneNumber> phones = reader.readCollection("phone", new ArrayList<PhoneNumber>(), PhoneNumber.class);

      Person person = new Person();
      person.setName(name);
      person.setId(id);
      person.setEmail(email);
      person.setPhones(phones);
      return person;
   }

   @Override
   public void writeTo(ProtoStreamWriter writer, Person person) throws IOException {
      writer.writeString("name", person.getName());
      writer.writeInt("id", person.getId());
      writer.writeString("email", person.getEmail());
      writer.writeCollection("phone", person.getPhones(), PhoneNumber.class);
   }
}
