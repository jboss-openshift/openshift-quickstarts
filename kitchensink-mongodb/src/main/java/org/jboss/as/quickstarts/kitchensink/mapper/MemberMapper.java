package org.jboss.as.quickstarts.kitchensink.mapper;

import org.bson.Document;
import org.jboss.as.quickstarts.kitchensink.model.Member;

public class MemberMapper {

    public static final String ATTR_NAME = "name";
    public static final String ATTR_ID = "_id";
    public static final String ATTR_EMAIL = "email";
    public static final String ATTR_PHONE_NUMBER = "phoneNumber";

    public Document toDocument(Member member) {
        return new Document()
                    .append(ATTR_ID, member.getId())
                    .append(ATTR_NAME, member.getName())
                    .append(ATTR_EMAIL, member.getEmail())
                    .append(ATTR_PHONE_NUMBER, member.getPhoneNumber());
    }

    public Member toMember(Document document) {
        Member member = new Member();
        member.setId(document.getLong(ATTR_ID));
        member.setName(document.getString(ATTR_NAME));
        member.setEmail(document.getString(ATTR_EMAIL));
        member.setPhoneNumber(document.getString(ATTR_PHONE_NUMBER));
        return member;
    }
}
