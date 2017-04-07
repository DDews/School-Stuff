
public class ProductDB {
	private static String[][] productArray = {
			{"java", "Murcah's Java Programming", "57.50"},
			{"jsp", "Murach's Java Servlets and JSP", "57.50"},
			{"mysql", "Murach's MySQL", "54.50"}
	};
	
	public static MyProduct[] getAllProducts() {
		MyProduct[] products = new MyProduct[productArray.length];
		for (int i = 0; i < productArray.length; i++) {
			products[i] = new MyProduct(productArray[i][0], productArray[i][1], Double.parseDouble(productArray[i][2]) );
		}
		return products;
	}
}
