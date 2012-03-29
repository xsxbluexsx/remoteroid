package org.secmem.remoteroid.util;

import java.util.Comparator;

public class NomalComparator  implements Comparator<String> {

	public int compare(String l, String r) {
		// TODO Auto-generated method stub
		if (isHangul(l)) {
			if (isHangul(r)) {
				return generalCompare(l, r);
			}
			else {
				return -1;
			}
		}
		else if (isAlphabat(l)) {
			if (isHangul(r)) {
				return 1;
			}
			else if (isAlphabat(r)) {
				return generalCompare(l, r);
			}
			else {
				return -1;
			}
		}
		else if (isNumber(l)) {
			if (isHangul(r)) {
				return 1;
			}
			else if (isAlphabat(r)) {
				return 1;
			}
			else if (isNumber(r)) {
				return generalCompare(l, r);
			}
			else {
				return -1;
			}
		}
		else {
			if (isEtc(r)) {
				return generalCompare(l, r);
			}
			else {
				return 1;
			}
		}
	}
	
	public static int generalCompare(String a, String b) {
		char l = Character.toUpperCase(a.charAt(0));
		char r = Character.toUpperCase(b.charAt(0));
		if (l > r) {
			return 1;
		}
		else if (r > l) {
			return -1;
		}
		else {
			return 0;
		}
	}
	
	public static boolean isHangul(String p) {
		if (p.charAt(0) >= 44032 && p.charAt(0) < 55203 ) {
			return true;
		}
		else 
			return false;
	}
	
	public static boolean isAlphabat(String p) {
		if (p.charAt(0) >= 'a' && p.charAt(0) <= 'z') {
			return true;
		}
		else if (p.charAt(0) >= 'A' && p.charAt(0) <= 'Z') {
			return true;
		}
		else 
			return false;
	}
	
	public static boolean isEtc(String p) {
		if (!isHangul(p) && !isAlphabat(p) && !isNumber(p)) {
			return true;
		}
		else 
			return false;
	}

	public static boolean isNumber(String p) {
		// TODO Auto-generated method stub
		if (p.charAt(0) >= '0' && p.charAt(0) <= '9') {
			return true;
		}
		else
			return false;
	}
}
