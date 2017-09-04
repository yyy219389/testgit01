package com.xyz.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
	*@author XieYiZhuo
	*@version 创建时间:2017年9月3日下午2:50:54
	*/
public class NavigationTag extends TagSupport{
    static final long serialVersionUID = 0x20ec7a11236675b1L;
    private String bean;
    private String url;
    private int number;

    public NavigationTag()
    {
        bean = "page";
        url = null;
        number = 5;
    }

    public int doStartTag()
    {
        JspWriter writer = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        Page page = (Page)request.getAttribute(bean);
        if (page == null)
            return 0;
        try
        {
            url = resolveUrl(url, pageContext);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        try
        {
            int pageCount = page.getTotal() / page.getSize();
            if (page.getTotal() % page.getSize() > 0)
                pageCount++;
            writer.print("<nav><ul class=\"pagination\">");
            if (page.getPage() > 1)
            {
                String preUrl = append(url, "page", page.getPage() - 1);
                preUrl = append(preUrl, "rows", page.getSize());
                writer.print((new StringBuilder("<li><a href=\"")).append(preUrl).append("\">上一页</a></li>").toString());
            } else
            {
                writer.print("<li class=\"disabled\"><a href=\"#\">上一页</a></li>");
            }
            int indexPage = page.getPage() - 2 <= 0 ? 1 : page.getPage() - 2;
            for (int i = 1; i <= number && indexPage <= pageCount; i++)
            {
                if (indexPage == page.getPage())
                {
                    writer.print((new StringBuilder("<li class=\"active\"><a href=\"#\">")).append(indexPage).append("<span class=\"sr-only\">(current)</span></a></li>").toString());
                } else
                {
                    String pageUrl = append(url, "page", indexPage);
                    pageUrl = append(pageUrl, "rows", page.getSize());
                    writer.print((new StringBuilder("<li><a href=\"")).append(pageUrl).append("\">").append(indexPage).append("</a></li>").toString());
                }
                indexPage++;
            }

            if (page.getPage() < pageCount)
            {
                String nextUrl = append(url, "page", page.getPage() + 1);
                nextUrl = append(nextUrl, "rows", page.getSize());
                writer.print((new StringBuilder("<li><a href=\"")).append(nextUrl).append("\">下一页</a></li>").toString());
            } else
            {
                writer.print("<li class=\"disabled\"><a href=\"#\">下一页</a></li>");
            }
            writer.print("</nav>");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    private String append(String url, String key, int value)
    {
        return append(url, key, String.valueOf(value));
    }

    private String append(String url, String key, String value)
    {
        if (url == null || url.trim().length() == 0)
            return "";
        if (url.indexOf("?") == -1)
            url = (new StringBuilder(String.valueOf(url))).append("?").append(key).append("=").append(value).toString();
        else
        if (url.endsWith("?"))
            url = (new StringBuilder(String.valueOf(url))).append(key).append("=").append(value).toString();
        else
            url = (new StringBuilder(String.valueOf(url))).append("&amp;").append(key).append("=").append(value).toString();
        return url;
    }

    private String resolveUrl(String url, PageContext pageContext)
        throws Exception
    {
        if (url != null)
            url = new String(url.getBytes("iso8859-1"), "utf-8");
        Map params = pageContext.getRequest().getParameterMap();
        for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();)
        {
            Object key = iterator.next();
            if (!"page".equals(key) && !"rows".equals(key))
            {
                Object value = params.get(key);
                if (value != null)
                    if (value.getClass().isArray())
                        url = append(url, key.toString(), new String(((String[])value)[0].getBytes("iso8859-1"), "utf-8"));
                    else
                    if (value instanceof String)
                        url = append(url, key.toString(), new String(value.toString().getBytes("iso8859-1"), "utf-8"));
            }
        }

        return url;
    }

    public String getBean()
    {
        return bean;
    }

    public void setBean(String bean)
    {
        this.bean = bean;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }
}
