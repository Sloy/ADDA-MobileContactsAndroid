package model;

import java.util.Set;

public interface Diary{
	public boolean addContact(String name, String field,  String value);
	public boolean deleteContact(String name);
  	public String getContact(String name);
  	public String getName(String number);
  	public Set<String> contacts();
}