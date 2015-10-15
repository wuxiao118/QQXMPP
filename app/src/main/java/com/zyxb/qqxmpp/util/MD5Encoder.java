package com.zyxb.qqxmpp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MD5Encoder {

	public static String encode(String pwd) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(pwd.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String s = Integer.toHexString(0xff & bytes[i]);

				if (s.length() == 1) {
					sb.append("0" + s);
				} else {
					sb.append(s);
				}
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("buhuifasheng");
		}
	}

	public static String encode(String pwd, String rand) {
		return rand + encode(encode(pwd) + rand);
	}

	public static String encodeRandom(String pwd) {

		return encode(pwd, randomMD5(8));
	}

	public static String random(int num) {
		Character[] cs = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
				'6', '7', '8', '9' };
		Boolean[] bs = new Boolean[cs.length];
		for (int i = 0; i < cs.length; i++) {
			bs[i] = false;
		}

		// 随机生成
		StringBuilder sb = new StringBuilder();
		Random rd = new Random();
		int j = 0;
		while (j < num) {
			int pos = rd.nextInt(cs.length);
			if (!bs[pos]) {
				bs[pos] = true;
				sb.append(cs[pos]);
				j++;
			}
		}

		return sb.toString();
	}

	public static String random2(int num) {
		Character[] cs = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9' };

		// 随机生成
		StringBuilder sb = new StringBuilder();
		Random rd = new Random();
		int j = 0;
		while (j < num) {
			int pos = rd.nextInt(cs.length);

			sb.append(cs[pos]);
			j++;

		}

		return sb.toString();
	}

	public static String randomMD5(int num){
		if(num > 32){
			throw new IllegalArgumentException("参数错误,不能大于32");
		}

		String s = random(8);

		return encode(s).substring(0,num);
	}

	public static void main(String[] args) {
		String s = randomMD5(8);
		System.out.println(s);
		System.out.println(encode("1234", s));

		System.out.println("========>" + encode("1234"));
	}
}
