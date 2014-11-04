/*
 * Copyright 2012 Corentin Azelart.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.azelart.artnetstack.utils;

public final class ByteUtilsArt {
	
	public static int byte2toIn( byte[] b, int offset ) {
		return (b[offset+1]&0xff)<<8 | (b[offset] & 0xff);
		//return b[3]<<24 | b[2]<<16 | b[1]<<8 | b[0];
	}
	
	public static int byte4toIn( byte[] b, int offset ) {
		return (b[offset+3]&0xff)<<8 | (b[offset+2]&0xff)<<8 | (b[offset+1]&0xff)<<8 | (b[offset] & 0xff);
	}
	
	
	public static boolean bitIsSet( int i, int offset ) {
		return (i & (1 << offset)) != 0;
	}
	
	public static byte[] in8toByte( int i ) {
		return new byte[] {(byte)((i >> 0) & 0xff)};
	}
	
	public static byte[] in16toByte( int data ) {
		return new byte[] {(byte)((data >> 0) & 0xff),(byte)((data >> 8) & 0xff)};
	}
	
	/**
	 * New method to convert integer to 2*8 bit.
	 * @param data is number.
	 * @return the byte array.
	 */
	public static byte[] in16toBit( int data ) {
		return new byte[] {(byte)(data >>> 8),(byte)data};
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

}
