public class Discount {

       public double getDiscount(double amount) {
              if (amount > 1000)
                     return (amount * 0.05);
              else
                     return 0;
       }
}

