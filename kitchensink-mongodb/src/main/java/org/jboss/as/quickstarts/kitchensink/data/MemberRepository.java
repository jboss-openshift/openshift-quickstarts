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
package org.jboss.as.quickstarts.kitchensink.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jboss.as.quickstarts.kitchensink.mapper.MemberMapper;
import org.jboss.as.quickstarts.kitchensink.model.Member;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MemberRepository {

    @Inject
    private MongoCollection<Document> mongoCollection;

    @Inject
    private MemberMapper memberMapper;

    public Member findById(String id) {
        Document document = mongoCollection.find(Filters.eq(MemberMapper.ATTR_ID, new ObjectId(id))).first();
        return convert(document);
    }

    public Member findByEmail(String email) {
        FindIterable<Document> results = mongoCollection.find(Filters.eq(MemberMapper.ATTR_EMAIL, email));
        return results.iterator().hasNext() ? convert(results.iterator().next()) : null;
    }

    public List<Member> findAllOrderedByName() {
        FindIterable<Document> results = mongoCollection.find().sort(new Document(MemberMapper.ATTR_NAME, 1));
        List<Member> list = new ArrayList<Member>();
        for (Document document : results) {
            list.add(convert(document));
        }
        return list;
    }

    private Member convert(Document document) {
        return memberMapper.toMember(document);
    }
}
