
import java.io.*;

import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Scanner;
import java.security.spec.RSAPrivateKeySpec;

import java.math.BigInteger;

import javax.crypto.Cipher;

public class KeyGen {
	public static void main(String[] args) throws Exception {

		// Generate a pair of keys
		SecureRandom random = new SecureRandom();
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(1024, random); // 1024: key size in bits
		KeyPair pair1 = generator.generateKeyPair();
		Key xPubKey = pair1.getPublic();
		Key xPrivKey = pair1.getPrivate();
		KeyPair pair2 = generator.generateKeyPair();
		Key yPubKey = pair2.getPublic();
		Key yPrivKey = pair2.getPrivate();

		/*
		 * next, store the keys to files, read them back from files, and then,
		 * encrypt & decrypt using the keys from files.
		 */

		// get the parameters of the keys: modulus and exponet
		KeyFactory factory = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pubKSpec = factory.getKeySpec(xPubKey, RSAPublicKeySpec.class);
		RSAPrivateKeySpec privKSpec = factory.getKeySpec(xPrivKey, RSAPrivateKeySpec.class);

		// save the parameters of the keys to the files
		saveToFile("XPublic.key", pubKSpec.getModulus(), pubKSpec.getPublicExponent());
		saveToFile("XPrivate.key", privKSpec.getModulus(), privKSpec.getPrivateExponent());

		pubKSpec = factory.getKeySpec(yPubKey, RSAPublicKeySpec.class);
		privKSpec = factory.getKeySpec(yPrivKey, RSAPrivateKeySpec.class);

		saveToFile("YPublic.key", pubKSpec.getModulus(), pubKSpec.getPublicExponent());
		saveToFile("YPrivate.key", privKSpec.getModulus(), privKSpec.getPrivateExponent());
		// read 16-char input from the keyboard for 128-bit AES Symmetric key
		Scanner reader = new Scanner(System.in); // Reading from System.in
		String input = "";
		do {
			System.out.println("Enter a 16-char AES symmetric key: ");
			input = reader.nextLine();
			if (input.length() != 16)
				System.out.println("Length of input must be 16 characters.");
		} while (input.length() != 16);
		try {
			PrintWriter out = new PrintWriter("symmetric.key");
			out.print(input);
			out.close();
		} catch (Exception e) {
			System.out.println("Error writing to file 'symmetric.key': " + e.toString());
		} 
		System.out.println("Finished generating keys.");
	}

	// save the prameters of the public and private keys to file
	public static void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws IOException {

		System.out.println(
				"Write to " + fileName + ": modulus = " + mod.toString() + ", exponent = " + exp.toString() + "\n");

		ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));

		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			throw new IOException("Unexpected error", e);
		} finally {
			oout.close();
		}
	}

}
