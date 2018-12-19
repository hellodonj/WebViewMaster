package com.osastudio.common.library;

import java.util.LinkedList;

public class ObjectStack<T> {

   protected LinkedList<T> stack;

   public ObjectStack() {
      this.stack = new LinkedList<T>();
   }

   public void push(T object) {
      this.stack.addFirst(object);
   }

   public T pop() {
      if (!this.stack.isEmpty()) {
         return this.stack.removeFirst();
      }
      return null;
   }

   public T pop(T object) {
      if (!this.stack.isEmpty()) {
         if (this.stack.getFirst() == object) {
            return this.stack.removeFirst();
         }
      }
      return null;
   }

   public void add(T object) {
      push(object);
   }

   public void remove(T object) {
      if (this.stack.contains(object)) {
         this.stack.remove(object);
      }
   }

   public T getTop() {
      if (!this.stack.isEmpty()) {
         return this.stack.getFirst();
      }
      return null;
   }

   public T getBottom() {
      if (!this.stack.isEmpty()) {
         return this.stack.getLast();
      }
      return null;
   }

   public int getCount() {
      return this.stack.size();
   }

   public boolean isEmpty() {
      return this.stack.size() <= 0;
   }

   public void clear() {
      this.stack.clear();
   }

}
