import java.text.NumberFormat;

public class Main {
	public static void main(String[] args) {
		MyProduct[] products = ProductDB.getAllProducts();
		
		System.out.println("LIST OF ALL PRODUCTS");
		
		for (int i = 0; i < products.length; i++) {
			MyProduct product = products[i];
			System.out.println("Code: " + product.getCode());
			System.out.println("Description: " + product.getDescription());
			System.out.println("Price: " + product.getPrice(NumberFormat.getCurrencyInstance()));
			System.out.println();
		}
	}
}
