package test;

import java.util.HashSet;

public class DuplicatesArrayUsingHashSet {
	
	public static void main(String []args)
	{
	
	String [] strArray ={"1","2","3","4","5","1","5","3"};
	
	HashSet<String> set=new HashSet<String>();
	
	for(String arrayElement :strArray)
		
		if(!set.add(arrayElement))
		{System.out.println("Duplicates array values" +arrayElement);

}
}
}
