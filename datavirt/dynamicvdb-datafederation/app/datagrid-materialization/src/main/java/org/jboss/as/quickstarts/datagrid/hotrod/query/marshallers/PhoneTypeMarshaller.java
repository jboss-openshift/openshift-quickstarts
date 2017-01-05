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

import org.infinispan.protostream.EnumMarshaller;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.PhoneType;

/**
 * @author Adrian Nistor
 */
public class PhoneTypeMarshaller implements EnumMarshaller<PhoneType> {

   @Override
   public Class<PhoneType> getJavaClass() {
      return PhoneType.class;
   }

   @Override
   public String getTypeName() {
      return "quickstart.Person.PhoneType";
   }

   @Override
   public PhoneType decode(int enumValue) {
      switch (enumValue) {
         case 0:
            return PhoneType.MOBILE;
         case 1:
            return PhoneType.HOME;
         case 2:
            return PhoneType.WORK;
      }
      return null;
   }

   @Override
   public int encode(PhoneType phoneType) {
      switch (phoneType) {
         case MOBILE:
            return 0;
         case HOME:
            return 1;
         case WORK:
            return 2;
      }

      throw new IllegalArgumentException("Unexpected PhoneType value : " + phoneType);
   }
}
