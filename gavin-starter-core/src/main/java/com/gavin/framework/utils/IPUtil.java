package com.gavin.framework.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

/**
 * IP地址工具类
 * 方法来自于互联网
 *
 * @author Uncarbon
 */
public class IPUtil {
  private static final String[] HEADERS_TO_TRY = {
          "X-Forwarded-For",
          "Proxy-Client-IP",
          "WL-Proxy-Client-IP",
          "HTTP_X_FORWARDED_FOR",
          "HTTP_X_FORWARDED",
          "HTTP_X_CLUSTER_CLIENT_IP",
          "HTTP_CLIENT_IP",
          "HTTP_FORWARDED_FOR",
          "HTTP_FORWARDED",
          "HTTP_VIA",
          "REMOTE_ADDR",
          "X-Real-IP"};

  /**
   * 获取ip地址
   */
  public static String getIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    for (String header : HEADERS_TO_TRY) {
      String tempIp = request.getHeader(header);
      if (tempIp != null && tempIp.length() != 0 && !"unknown".equalsIgnoreCase(tempIp)) {
        break;
      }
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // 本机访问
    if ("localhost".equalsIgnoreCase(ip) || "127.0.0.1".equalsIgnoreCase(ip) || "0:0:0:0:0:0:0:1".equalsIgnoreCase(ip)) {
      // 根据网卡取本机配置的IP
      InetAddress inet;
      try {
        inet = InetAddress.getLocalHost();
        ip = inet.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }

    // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
    if (null != ip && ip.length() > 15) {
      if (ip.indexOf(",") > 15) {
        ip = ip.substring(0, ip.indexOf(","));
      }
    }

    return ip;
  }

  /**
   * 获取mac地址
   */
  public static String getMacAddress() throws Exception {
    // 取mac地址
    byte[] macAddressBytes = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
    // 下面代码是把mac地址拼装成String
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < macAddressBytes.length; i++) {
      if (i != 0) {
        sb.append("-");
      }
      // mac[i] & 0xFF 是为了把byte转化为正整数
      String s = Integer.toHexString(macAddressBytes[i] & 0xFF);
      sb.append(s.length() == 1 ? 0 + s : s);
    }
    return sb.toString().trim().toUpperCase();
  }

}
