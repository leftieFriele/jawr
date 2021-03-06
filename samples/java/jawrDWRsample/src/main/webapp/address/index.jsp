<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Dynamic Address Entry Demo</title>
  <meta http-equiv="Content-Type" content="text/html; charset=us-ascii" />
	<jwr:script src="/bundles/global.js"/>
	<jwr:script src="/address/index.js"/>
  <link rel="stylesheet" type="text/css" href="../tabs/tabs.css" />
  <link rel="stylesheet" type="text/css" href="../generic.css" />
</head>
<body onload="dwr.util.useLoadingMessage();Tabs.init('tabList', 'tabContents');">
<div id="page-title">[
  <a href="http://getahead.org/dwr/">DWR Website</a> |
  <a href="..">Web Application Index</a>
]</div>

<h1>Dynamic Address Entry</h1>

<p>This is a practical example of making form fill easier for users, and how
simple this is with DWR.</p>

<ul id="tabList">
  <li><a href="#" tabId="demoDiv">Demo</a></li>
  <li><a href="#" tabId="explainDiv">How it works</a></li>
  <li><a href="#" tabId="sourceDiv">Source</a></li>
</ul>

<div id="tabContents">

  <div id="demoDiv">

    <p>A fully functional version of this example needs full access to a
    zipcode/postcode database. This jar file does not so this demo is restricted
    to the following UK postcodes:</p>

    <ul>
      <li>LE16 7TR</li>
      <li>NR14 7SL</li>
      <li>B92 7TT</li>
      <li>E17 8YT</li>
      <li>SN4 8QS</li>
      <li>NN5 7HT</li>
    </ul>

    <table class="plain">
      <tr>
        <td>Zipcode/Postcode:</td>
        <td><input id="postcode" type="text" onchange="fillAddress()"/></td>
      </tr>
      <tr>
        <td>House name/number:</td>
        <td><input id="house" type="text"/></td>
      </tr>
      <tr>
        <td>Line 2:</td>
        <td><input id="line2" type="text"/></td>
      </tr>
      <tr>
        <td>Line 3:</td>
        <td><input id="line3" type="text"/></td>
      </tr>
      <tr>
        <td>Line 4:</td>
        <td><input id="line4" type="text" size="30"/></td>
      </tr>
    </table>

    <p>&nbsp;</p>
    <p>&nbsp;</p>

  </div>

  <div id="explainDiv">
    <p>When you tab out of the "postcode" field the browser calls the onchange
    event, which calls the <code>fillAddress()</code> function:</p>
    
<pre>
function fillAddress() {
  var postcode = dwr.util.getValue("postcode");
  AddressLookup.fillAddress(postcode, function(address) {
    dwr.util.setValues(address);
  });
}
</pre>

    <p>This code first gets the contents of the postcode field, and then
    performs a call to the server using this postcode.</p>

    <p>When the server replies we fill the values into the form using the
    <a href="http://getahead.org/dwr/browser/util/setvalues"><code>setValues()</code></a>
    function.</p>

    <p>On the server, we could have created a JavaBean to hold the address data
    but for this example we have just used a java.util.Map. We could change to
    using a JavaBean without any JavaScript changes so long as the Map keys have
    the same names as the JavaBean properties.</p>
    
<pre>
public Map fillAddress(String origpostcode)
{
    Map reply = new HashMap();
    String postcode = LocalUtil.replace(origpostcode, " ", "");

    if (postcode.equalsIgnoreCase("LE167TR"))
    {
        reply.put(LINE2, "Church Lane");
        reply.put(LINE3, "Thorpe Langton");
        reply.put(LINE4, "MARKET HARBOROUGH");
    }
    ...
    else
    {
        reply.put(LINE2, "Postcode not found");
        reply.put(LINE3, "");
        reply.put(LINE4, "");
    }

    return reply;
}
</pre>
    
  </div>

  <div id="sourceDiv">

<h2>HTML source:</h2>
<pre>
&lt;table&gt;
  &lt;tr&gt;
    &lt;td&gt;Zipcode/Postcode:&lt;/td&gt;
    &lt;td&gt;&lt;input id="postcode" type="text" onchange="fillAddress()"/&gt;&lt;/td&gt;
  &lt;/tr&gt;
  &lt;tr&gt;
    &lt;td&gt;House name/number:&lt;/td&gt;
    &lt;td&gt;&lt;input id="house" type="text"/&gt;&lt;/td&gt;
  &lt;/tr&gt;
  &lt;tr&gt;
    &lt;td&gt;Line 2:&lt;/td&gt;
    &lt;td&gt;&lt;input id="line2" type="text"/&gt;&lt;/td&gt;
  &lt;/tr&gt;
  &lt;tr&gt;
    &lt;td&gt;Line 3:&lt;/td&gt;
    &lt;td&gt;&lt;input id="line3" type="text"/&gt;&lt;/td&gt;
  &lt;/tr&gt;
  &lt;tr&gt;
    &lt;td&gt;Line 4:&lt;/td&gt;
    &lt;td&gt;&lt;input id="line4" type="text"/&gt;&lt;/td&gt;
  &lt;/tr&gt;
&lt;/table&gt;
</pre>

<h2>Javascript source:</h2>
<pre>
function fillAddress() {
  var postcode = dwr.util.getValue("postcode");
  AddressLookup.fillAddress(postcode, function(address) {
    dwr.util.setValues(address);
  });
}
</pre>

<h2>Java source:</h2>
<pre>
package org.getahead.dwrdemo.address;

import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.util.LocalUtil;

public class AddressLookup
{
    private static final String LINE4 = "line4";

    private static final String LINE3 = "line3";

    private static final String LINE2 = "line2";

    public Map fillAddress(String origpostcode)
    {
        Map reply = new HashMap();
        String postcode = LocalUtil.replace(origpostcode, " ", "");

        if (postcode.equalsIgnoreCase("LE167TR"))
        {
            reply.put(LINE2, "Church Lane");
            reply.put(LINE3, "Thorpe Langton");
            reply.put(LINE4, "MARKET HARBOROUGH");
        }
        else if (postcode.equalsIgnoreCase("NR147SL"))
        {
            reply.put(LINE2, "Rectory Lane");
            reply.put(LINE3, "Poringland");
            reply.put(LINE4, "NORWICH");
        }
        else if (postcode.equalsIgnoreCase("B927TT"))
        {
            reply.put(LINE2, "Olton Mere");
            reply.put(LINE3, "Warwick Road");
            reply.put(LINE4, "SOLIHULL");
        }
        else if (postcode.equalsIgnoreCase("E178YT"))
        {
            reply.put(LINE2, "");
            reply.put(LINE3, "PO Box 43108 ");
            reply.put(LINE4, "LONDON");
        }
        else if (postcode.equalsIgnoreCase("SN48QS"))
        {
            reply.put(LINE2, "Binknoll");
            reply.put(LINE3, "Wootton Bassett");
            reply.put(LINE4, "SWINDON");
        }
        else if (postcode.equalsIgnoreCase("NN57HT"))
        {
            reply.put(LINE2, "Heathville");
            reply.put(LINE3, "");
            reply.put(LINE4, "NORTHAMPTON");
        }
        else
        {
            reply.put(LINE2, "Postcode not found");
            reply.put(LINE3, "");
            reply.put(LINE4, "");
        }

        return reply;
    }
}
</pre>

<h2>dwr.xml</h2>
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;!DOCTYPE dwr PUBLIC
    "-//GetAhead Limited//DTD Direct Web Remoting 2.0//EN"
    "http://getahead.org/dwr/dwr20.dtd"&gt;

&lt;dwr&gt;
  &lt;allow&gt;
    &lt;create creator="new" javascript="AddressLookup"&gt;
      &lt;param name="class" value="org.getahead.dwrdemo.address.AddressLookup"/&gt;
    &lt;/create&gt;
  &lt;/allow&gt;
&lt;/dwr&gt;
</pre>

  </div>

</div>

</body>
</html>
