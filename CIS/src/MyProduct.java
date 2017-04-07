import java.text.NumberFormat;

public class MyProduct extends Product {

	public MyProduct(String code, String description, double price) {
		super(code,description,price);
	}
	public String getPrice(NumberFormat nf) {
		return nf.format(this.getPrice());
	}
}
