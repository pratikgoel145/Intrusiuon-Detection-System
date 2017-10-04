public class RuleElement{
	char type;
	String attribute;
	
	public RuleElement(){
		type = '\0';
		attribute = null;
	}
	
	public RuleElement(char type, String attribute){
		this.type = type;
		this.attribute = attribute;
	}
	
	public void setType(char type){
		this.type = type;
	}
	
	public void setAttribute(String attribute){
		this.attribute = attribute;
	}
	
	public char getType(){
		return type;		
	}
	
	public String getAttribute(){
		return attribute;
	}
}
