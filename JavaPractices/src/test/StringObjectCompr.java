package test;

public class StringObjectCompr {
	

	 public static void main(String args[]){  
	
	String obj1 = new String("xyz");

	String obj2 = new String("xyz");
	String s1 = "xyz";
	String s2 = "xyz";
	
	if(s1==s2)
		
		System.out.println("s1==s2 is TRUE");
	
	else
		
		System.out.println("s1==s2 is false");
		
	 //it checks the value in memory location

	if(obj1 == obj2)
	   System.out.println("obj1==obj2 is TRUE");
	else
	  System.out.println("obj1==obj2 is FALSE");
	
	//it checks the value of the string not in the memory
	
	if(obj1.equals(obj2))
		   System.out.println("obj1==obj2 using equals commands is TRUE");
		else
		  System.out.println("obj1==obj2 using equals commands is FALSE");
	 }
	
}



