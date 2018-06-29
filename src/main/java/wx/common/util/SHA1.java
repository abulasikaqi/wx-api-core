/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package wx.common.util;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * SHA1 class
 *
 * 计算公众平台的消息签名接口.
 */
public class SHA1 {

	/**
	 * 用SHA1算法生成安全签名
	 * @param token 票据
	 * @param timestamp 时间戳
	 * @param nonce 随机字符串
	 * @param encrypt 密文
	 * @return 安全签名
	 * @throws AesException 
	 */
	public static String getSHA1(String token, String timestamp, String nonce, String encrypt)  throws AesException{
		String[] array = new String[]{token, timestamp, nonce, encrypt};
		StringBuffer sb = new StringBuffer();
		// 字符串排序
		Arrays.sort(array);
		for (int i = 0; i < 4; i++) {
			sb.append(array[i]);
		}
		String str = sb.toString();
		return SHA1(str);
	}

	public static String SHA1(String str) throws AesException {
		try {
			// SHA1签名生成
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			byte[] digest = md.digest();

			StringBuffer hstr = new StringBuffer();
			String shaHex = "";
			for (byte aDigest : digest) {
				shaHex = Integer.toHexString(aDigest & 0xFF);
				if (shaHex.length() < 2) {
					hstr.append(0);
				}
				hstr.append(shaHex);
			}
			return hstr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}
}
