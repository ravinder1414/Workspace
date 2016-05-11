package test;

public class ReverseString {

	

	    public static void main(String args[]) {
	        String original, reverse = "";
	        original = "java program to reverse a string";
	        int length = original.length();
	        for (int i = length - 1; i >= 0; i--) {
	            reverse = reverse + original.charAt(i);
	        }
	        System.out.println("-----------Method 1-------------");
	        System.out.println("Original String:: " + original);
	        System.out.println("Reveresed String:: " + reverse);
	        //reverseString();
	    }

	    public static void reverseString() {
	        StringBuffer original = new StringBuffer("program to reverse string in java");
	        System.out.println("-----------Method 2-------------");
	        System.out.println("Original String:: " + original);
	        System.out.println("Reveresed String:: " + original.reverse());
	    }


	}


