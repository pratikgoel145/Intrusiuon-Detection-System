import java.util.ArrayList;

public class Rule {
	ArrayList<RuleElement> LHS;
	RuleElement RHS;
	char ruleType;
	float leverage;
	
	public Rule(){
		LHS = null;
		RHS = null;
		ruleType = '\0';
		leverage = Float.MIN_VALUE;
	}
	
	public Rule(ArrayList<RuleElement> LHS, RuleElement RHS){
		this.LHS = new ArrayList<RuleElement>();
		for(int i = 0; i < LHS.size(); i++){
			RuleElement r = new RuleElement(LHS.get(i).getType(), LHS.get(i).getAttribute());
			this.LHS.add(r);
		}
		this.RHS = new RuleElement(RHS.getType(), RHS.getAttribute());
		ruleType = '\0';
		leverage = Float.MIN_VALUE;
	}
	
	public void setLHS(ArrayList<RuleElement> LHS){
		this.LHS = new ArrayList<RuleElement>();
		for(int i = 0; i < LHS.size(); i++){
			RuleElement r = new RuleElement(LHS.get(i).getType(), LHS.get(i).getAttribute());
			this.LHS.add(r);
		}
	}
	
	public void setRHS(RuleElement RHS){
		this.RHS = new RuleElement(RHS.getType(), RHS.getAttribute());
	}
	
	public void setRuleType(char ruleType){
		this.ruleType = ruleType;
	}
	
	public void setLeverage(float leverage){
		this.leverage = leverage;
	}
	
	public ArrayList<RuleElement> getLHS(){
		return LHS;		
	}
	
	public RuleElement getRHS(){
		return RHS;
	}
	
	public char getRuleType(){
		return ruleType;
	}
	
	public float getLeverage(){
		return leverage;
	}
}
