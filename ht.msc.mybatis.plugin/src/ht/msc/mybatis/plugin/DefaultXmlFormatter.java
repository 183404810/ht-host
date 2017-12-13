package ht.msc.mybatis.plugin;

public class DefaultXmlFormatter implements XmlFormatter
{
	protected Context context;
	
	public String getFormattedContent(Document document)
	{
	  return document.getFormattedContent();
	}
	
	public void setContext(Context context) {
	  this.context = context;
	}
}