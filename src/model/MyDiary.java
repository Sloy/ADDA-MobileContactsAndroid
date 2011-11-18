package model;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

public class MyDiary implements Diary {
	//=======================================================================
	// Fields
	//=======================================================================
	//primer String es para el nombre del contacto (key), dentro del segundo Map, el primer String es para el campo (key)
	//y el ultimo String es para el valor del campo
  	private Map<String, Map<String, String>> diary; 
 
	//=======================================================================
	// Constructor/s
	//=======================================================================
  	public MyDiary() {
  		diary = Maps.newHashMap();
  	}

	//=======================================================================
	// Getters/Setters
	//=======================================================================
	public Map<String, Map<String, String>> getAgenda() {
		return diary;
	}

	public void setAgenda(Map<String, Map<String, String>> agenda) {
		this.diary = agenda;
	}
  	
	//=======================================================================
	// Methods of Interfaces
	//=======================================================================
	/**
	 * Adds or updates a contact to the diary
	 * @param name the name of a new or an already registered contact
	 * @param  field the new or an already registered field
	 * @param value the value of the field
	 * @return returns true if the contact has been registered or updated in the diary, 
	 * and false in the other case
	 */
	@Override
	public boolean addContact(String name, String field, String value) {
		Boolean res = false;
		Map<String, String> values = null;
		
		//new contact
		if(!diary.containsKey(name)){	
			values = Maps.newHashMap();
			values.put(field, value);
			
			diary.put(name, values);
			res = true;
		}
		else{ //modify contact
			values = diary.get(name); //map with values of name
			values.put(field, value); //add or modify the field	
			res = true;
		}
		
		return res;
	}

	/**
	 * Deteles a contact
	 * @param name the name of a contact
	 * @return returns true if aux!=null, and false in other case. 
	 * aux is null when the name does not exist in the diary
	 * aux is not null when the name exist in the diary 
	 */
	@Override
	public boolean deleteContact(String name) {
		Map<String, String> aux = diary.remove(name);
		Boolean res = aux != null;
		
		return res; //we return this checking because when aux
	}

	/**
	 * @param name the name of the contact
	 * @return returns a null value if the name of contact does not exist in the diary
	 * or a string value with all fields and values of these fields for the contact name
	 */
	@Override
	public String getContact(String name) {	
		String res = "";
		
		if(diary.containsKey(name)){
			for(String f: diary.get(name).keySet()){
				res += "   · " + f + ": " + diary.get(name).get(f) + "\n";
			}	
		}
		else{
			res = null;
		}		
		
		return res;
	}

	/**
	 * This method is used to obtain the name of a contact that has the number that 
	 * is passed as parameter. We decide to return the first contact that has this number
	 * @param number the number of the contact
	 * @return returns the name of the contact that has the phone number
	 */
	@Override
	public String getName(String number) {
		String res = null;
		
		for(String name: diary.keySet()){ //names of contacts
			if(diary.get(name).containsValue(number)) { //filters that the contact contains the value "number" that at this moment it is possible
				//that this value is not the real number. It is possible that this value is a value of other fields
				for(String field: diary.get(name).keySet()){ //fields of contacts
					//filers that the contained field is ant of that we considered as phone numbers
					if(diary.get(name).get(field).contains(number) &&
							(field.equalsIgnoreCase("mobile") || field.equalsIgnoreCase("landline")
							|| field.equalsIgnoreCase("Business phone") || field.equalsIgnoreCase("Office Landline"))){
						res = name; 
						break;
					}
				}
			
			}			
			
		}
		
		return res;
	}

	/**
	 * @return returns all contacts of the diary
	 */
	@Override
	public Set<String> contacts() {
		return diary.keySet();
	}

	@Override
	public String toString() {
		String res = "\nContacts:\n------------------\n";
		
		for(String c: contacts()){
			res += c + "\n";
			
			for(String f: diary.get(c).keySet()){
				res += "   · " + f + ": " + diary.get(c).get(f) + "\n";
			}			
		}
		
		res+="\n";	
				
		return res;
	}
	
}