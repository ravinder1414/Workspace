package test;

class OverlodingMethod_ReturnTypeDifferent
{
   public double myMethod(int num1, int num2)
   {
      //System.out.println("First myMethod of class Demo");
      return num1+num2;
      //System.out.println(num1 + " "+num2);
   }
   public int myMethod(int var1, int var2,int var3)
   {
      //System.out.println("Second myMethod of class Demo");
      return var1+var2-var3;
   }



   public static void main(String args[])
   {
	   OverlodingMethod_ReturnTypeDifferent obj2= new OverlodingMethod_ReturnTypeDifferent();
      double result=obj2.myMethod(5,5);
      
      System.out.println(obj2.myMethod(10,10,5));
      
      System.out.println(result);
      obj2.myMethod(20,12,5); 
   }
}
   




