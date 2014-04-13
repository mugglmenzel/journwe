package com.journwe.booking.expedia.auth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignatureGenerator {

	public static String generateMD5Signature(final String apiKey, final String sharedSecret) {
		String digest = null;
		String unixtimestamp = ""+(System.currentTimeMillis() / 1000L);
		System.out.println("Unix timestamp used for signature: "+unixtimestamp);
		String text = apiKey + sharedSecret + (unixtimestamp);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(text.getBytes("UTF-8"));
	           
            //converting byte array to Hexadecimal String
           StringBuilder sb = new StringBuilder(2*hash.length);
           for(byte b : hash){
               sb.append(String.format("%02x", b&0xff));
           }
          
           digest = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return digest;	
	}
	
	public static void main(String... args) {
		System.out.println(generateMD5Signature("abc", "secret"));
	}
}
