package lottery.domains.content.payment.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class XmlUtils
{
  public static String parseRequst(HttpServletRequest request)
  {
    String body = "";
    try
    {
      ServletInputStream inputStream = request.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
      for (;;)
      {
        String info = br.readLine();
        if (info == null) {
          break;
        }
        if ((body == null) || ("".equals(body))) {
          body = info;
        } else {
          body = body + info;
        }
      }
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return body;
  }
  
  public static String parseXML(SortedMap<String, String> parameters)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("<xml>");
    Set es = parameters.entrySet();
    Iterator it = es.iterator();
    while (it.hasNext())
    {
      Map.Entry entry = (Map.Entry)it.next();
      String k = (String)entry.getKey();
      String v = (String)entry.getValue();
      if ((v != null) && (!"".equals(v)) && (!"appkey".equals(k))) {
        sb.append("<" + k + ">" + (String)parameters.get(k) + "</" + k + ">\n");
      }
    }
    sb.append("</xml>");
    return sb.toString();
  }
  
  public static SortedMap getParameterMap(HttpServletRequest request)
  {
    Map properties = request.getParameterMap();
    
    SortedMap returnMap = new TreeMap();
    Iterator entries = properties.entrySet().iterator();
    
    String name = "";
    String value = "";
    while (entries.hasNext())
    {
      Map.Entry entry = (Map.Entry)entries.next();
      name = (String)entry.getKey();
      Object valueObj = entry.getValue();
      if (valueObj == null)
      {
        value = "";
      }
      else if ((valueObj instanceof String[]))
      {
        String[] values = (String[])valueObj;
        for (int i = 0; i < values.length; i++) {
          value = values[i] + ",";
        }
        value = value.substring(0, value.length() - 1);
      }
      else
      {
        value = valueObj.toString();
      }
      returnMap.put(name, value.trim());
    }
    return returnMap;
  }
  
  public static Map<String, String> toMap(byte[] xmlBytes, String charset)
    throws Exception
  {
    SAXReader reader = new SAXReader(false);
    InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));
    source.setEncoding(charset);
    Document doc = reader.read(source);
    Map<String, String> params = toMap(doc.getRootElement());
    return params;
  }
  
  public static Map<String, String> getXmlElmentValue(String xml)
  {
    Map<String, String> map = new HashMap();
    try
    {
      Document doc = DocumentHelper.parseText(xml);
      Element el = doc.getRootElement();
      return recGetXmlElementValue(el, map);
    }
    catch (DocumentException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  private static Map<String, String> recGetXmlElementValue(Element ele, Map<String, String> map)
  {
    List<Element> eleList = ele.elements();
    if (eleList.size() == 0)
    {
      map.put(ele.getName(), ele.getTextTrim());
      return map;
    }
    for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();)
    {
      Element innerEle = (Element)iter.next();
      recGetXmlElementValue(innerEle, map);
    }
    return map;
  }
  
  public static Map<String, String> toMap(Element element)
  {
    Map<String, String> rest = new HashMap();
    List<Element> els = element.elements();
    for (Element el : els) {
      rest.put(el.getName().toLowerCase(), el.getTextTrim());
    }
    return rest;
  }
  
  public static String toXml(Map<String, String> params)
  {
    StringBuilder buf = new StringBuilder();
    List<String> keys = new ArrayList(params.keySet());
    Collections.sort(keys);
    buf.append("<xml>");
    for (String key : keys)
    {
      buf.append("<").append(key).append(">");
      buf.append("<![CDATA[").append((String)params.get(key)).append("]]>");
      buf.append("</").append(key).append(">\n");
    }
    buf.append("</xml>");
    return buf.toString();
  }
}
