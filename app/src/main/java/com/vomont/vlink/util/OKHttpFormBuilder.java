package com.vomont.vlink.util;

import android.text.TextUtils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.nio.charset.Charset;

public class OKHttpFormBuilder
{
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");
    
    private final StringBuilder content = new StringBuilder();
    
    private String encoding = "utf-8";
    
    public OKHttpFormBuilder()
    {
    }
    
    public OKHttpFormBuilder(String encoding)
    {
        if (!TextUtils.isEmpty(encoding))
        {
            this.encoding = encoding;
        }
    }
    
    /** Add new key-value pair. */
    public OKHttpFormBuilder add(String name, String value)
    {
        if (content.length() > 0)
        {
            content.append('&');
        }
        content.append(name).append('=').append(value);
        return this;
    }
    
    public RequestBody build()
    {
        if (content.length() == 0)
        {
            throw new IllegalStateException("Form encoded body must have at least one part.");
        }
        // Convert to bytes so RequestBody.create() doesn't add a charset to the content-type.
        byte[] contentBytes = content.toString().getBytes(Charset.forName(encoding));
        return RequestBody.create(CONTENT_TYPE, contentBytes);
    }
}
