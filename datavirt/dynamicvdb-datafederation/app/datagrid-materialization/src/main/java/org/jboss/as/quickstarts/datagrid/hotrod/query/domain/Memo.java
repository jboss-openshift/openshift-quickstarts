package org.jboss.as.quickstarts.datagrid.hotrod.query.domain;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoEnumValue;
import org.infinispan.protostream.annotations.ProtoField;

/**
 * @author Adrian Nistor
 */
@ProtoDoc("@Indexed")
public class Memo {

   private int id;

   private String text;

   private Person author;

   private boolean isRead;

   private Priority priority;

   public enum Priority {

      @ProtoEnumValue(number = 1)
      LOW,

      @ProtoEnumValue(number = 2)
      HIGH,

      @ProtoEnumValue(number = 3)
      WHATEVER
   }

   @ProtoField(number = 1, required = true)
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   @ProtoDoc("@IndexedField")
   @ProtoField(number = 2)
   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   @ProtoDoc("@IndexedField")
   @ProtoField(number = 3)
   public Person getAuthor() {
      return author;
   }

   public void setAuthor(Person author) {
      this.author = author;
   }

   @ProtoField(number = 4, defaultValue = "false")
   public boolean isRead() {
      return isRead;
   }

   public void setRead(boolean isRead) {
      this.isRead = isRead;
   }

   @ProtoField(number = 5)
   public Priority getPriority() {
      return priority;
   }

   public void setPriority(Priority priority) {
      this.priority = priority;
   }

   @Override
   public String toString() {
      return "Memo{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", author=" + author +
            ", isRead=" + isRead +
            ", priority=" + priority +
            '}';
   }
}
