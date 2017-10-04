import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class IDS {
	static int K = 20;
	static int maxLHSsize = 4;
	static float minCoverage = (float)0.1;
	static float minLeverage = (float)0.1;	
	static float rFactor = (float)0.8;
	
	static ArrayList<ArrayList<RuleElement>> dataset = new ArrayList<ArrayList<RuleElement>>();			//Set of Transactions for officer
	static ArrayList<Rule> CurrentSolution = new ArrayList<Rule>();
	
	static ArrayList<String> attributes = new ArrayList<String>();										//Set of Attributes
	
	public static float coverage(RuleElement P){
		int num = 0, i, j;
		for(i = 0; i < dataset.size(); i++){
			ArrayList<RuleElement> temp = dataset.get(i);
			for(j = 0; j < temp.size(); j++){
				if(temp.get(j).getType() == P.getType() && temp.get(j).getAttribute().equals(P.getAttribute())){
					num++;
					break;
				}
			}
		}
		return (float) num / dataset.size();
	}

	public static float coverage(ArrayList<RuleElement> T){
		int num = 0, i, j, k;
		for(i = 0; i < dataset.size(); i++){
			ArrayList<RuleElement> temp = dataset.get(i);
			k = 0;
			for(j = 0; j < temp.size(); j++){
				if(temp.get(j).getType() == T.get(k).getType() && temp.get(j).getAttribute().equals(T.get(k).getAttribute()))
					k++;
				if(k == T.size()){
					num++;
					break;
				}
			}
		}
		return (float) num / dataset.size();
	}

	public static float support(Rule rule){		
		if(rule.getLHS().isEmpty() && rule.getRHS() == null)
			return Float.MIN_VALUE;
		int num = 0, i, j, k;
		for(i = 0; i < dataset.size(); i++){
			ArrayList<RuleElement> temp = dataset.get(i);
			k = 0;
			for(j = 0; j < temp.size(); j++){
				if(temp.get(j).getAttribute().equals(rule.getLHS().get(k).getAttribute()) && temp.get(j).getType() == rule.getLHS().get(k).getType())
					k++;
				if(k == rule.getLHS().size()){
					num++;
					break;
				}
			}
			if(j == temp.size())
				for(j = 0; j < temp.size(); j++)
					if(temp.get(j).getAttribute().equals(rule.getRHS().getAttribute()) && temp.get(j).getType() == rule.getRHS().getType()){
						num++;
						break;
					}
		}
		return (float) num / dataset.size();
	}
	
	public static float confidence(Rule rule){		
		if(rule.getLHS().isEmpty() && rule.getRHS() == null)
			return Float.MIN_VALUE;
		int num = 0, i, j, k;
		for(i = 0; i < dataset.size(); i++){
			ArrayList<RuleElement> temp = dataset.get(i);
			k = 0;
			for(j = 0; j < temp.size(); j++){
				if(temp.get(j).getAttribute().equals(rule.getLHS().get(k).getAttribute()) && temp.get(j).getType() == rule.getLHS().get(k).getType())
					k++;
				if(k == rule.getLHS().size())
					break;
			}
			for(j++; j < temp.size(); j++){
				if(temp.get(j).getAttribute().equals(rule.getRHS().getAttribute()) && temp.get(j).getType() == rule.getRHS().getType()){
					num++;
					break;
				}
			}
		}
		return (float) num / dataset.size();
	}

	public static float leverage(Rule rule){
		if(coverage(rule.getRHS()) != (float)0.0){				//pruning rule 3
			//rule.setLeverage(support(rule) - (coverage(rule.getLHS()) * coverage(rule.getRHS())));
			rule.setLeverage(confidence(rule) / support(rule));
			return rule.getLeverage();
		}
		return (float)0.0;
	}

	//extracting read rules
	public static void readRule(){
//		ArrayList<RuleElement> currLHS = new ArrayList<RuleElement>();
		ArrayList<RuleElement> availLHS = new ArrayList<RuleElement>();
		ArrayList<RuleElement> availRHS = new ArrayList<RuleElement>();

		for(int i = 0; i < attributes.size(); i++){
			RuleElement read = new RuleElement('R', attributes.get(i));
			availLHS.add(read);

			RuleElement write = new RuleElement('W', attributes.get(i));
			availRHS.add(write);
		}
		ArrayList<ArrayList<RuleElement>> sol = func(availLHS, availRHS, 'R');
	/*	for(int i=0;i<sol.size();i++){
			displayElement(sol.get(i));
			System.out.println();
		}*/
//		algo(currLHS, availLHS, availRHS, 'R');
	}

	//extracting write rules
	public static void writeRule(){
//		ArrayList<RuleElement> currLHS = new ArrayList<RuleElement>();
		ArrayList<RuleElement> availLHS = new ArrayList<RuleElement>();
		ArrayList<RuleElement> availRHS = new ArrayList<RuleElement>();

		for(int i = 0; i < attributes.size(); i++){
			RuleElement read = new RuleElement('W', attributes.get(i));
			availLHS.add(read);

			RuleElement write = new RuleElement('W', attributes.get(i));
			availRHS.add(write);
		}
		func(availLHS, availRHS, 'W');
//		algo(currLHS, availLHS, availRHS, 'W');
	}

	public static Rule FindMinValueRule(ArrayList<Rule> currSoln){
		if(currSoln == null){
			return null;
		}
		float lev = Float.MAX_VALUE;
		Rule minRule = new Rule();
		for(int i = 0; i < currSoln.size(); i++){
			if(currSoln.get(i).getLeverage() < lev){
				lev = currSoln.get(i).getLeverage();
				minRule = currSoln.get(i);
			}
		}
		return minRule;
	}

	public static ArrayList<RuleElement> removeRuleElement(RuleElement P, ArrayList<RuleElement> arr){
		ArrayList<RuleElement> ans = new ArrayList<RuleElement>();
		int i = 0;
		while(i < arr.size() && (arr.get(i).getType() != P.getType() || !arr.get(i).getAttribute().equals(P.getAttribute())))
			ans.add(arr.get(i++));
		i++;
		while(i < arr.size())
			ans.add(arr.get(i++));
		return ans;
	}
	
	public static int ruleEqual(Rule a, Rule b){
		if(a.getRHS().getType() != b.getRHS().getType() || !a.getRHS().getAttribute().equals(b.getRHS().getAttribute()))
			return 0;
		if(a.getLHS().size() != b.getLHS().size())
			return 0;
		for(int i = 0; i < a.getLHS().size(); i++)
			if(a.getLHS().get(i).getType() != b.getLHS().get(i).getType() || !a.getLHS().get(i).getAttribute().equals(b.getLHS().get(i).getAttribute()))
				return 0;
		return 1;
	}
	
	public static ArrayList<Rule> removeRule(Rule P, ArrayList<Rule> arr){
		ArrayList<Rule> ans = new ArrayList<Rule>();
		int i = 0;
		while(i < arr.size() && ruleEqual(P, arr.get(i)) == 0)
			ans.add(arr.get(i++));
		i++;
		while(i < arr.size())
			ans.add(arr.get(i++));
		return ans;
	}
	
//	static ArrayList<RuleElement> SoFar = null;
	
	public static ArrayList<ArrayList<RuleElement>> func(ArrayList<RuleElement> AvailableLHS, ArrayList<RuleElement> AvailableRHS, char RuleType){
		if(AvailableLHS.size() == 0){
			ArrayList<ArrayList<RuleElement>> temp = new ArrayList<ArrayList<RuleElement>>();
			ArrayList<RuleElement> element = new ArrayList<RuleElement>();
			temp.add(element);
			return  temp;
		}
		ArrayList<RuleElement> newAvailableLHS = new ArrayList<RuleElement>();   // check 
		for(int i=1; i<AvailableLHS.size() ;i++){
			newAvailableLHS.add(AvailableLHS.get(i));
		}
		
//		System.out.println("\n\nAvailableLHS\n");
//		displayElement(newAvailableLHS);
//		System.out.println();
		
		ArrayList<ArrayList<RuleElement>> secLast = new ArrayList<ArrayList<RuleElement>>();
		ArrayList<ArrayList<RuleElement>> last = func(newAvailableLHS,AvailableRHS,RuleType);
		for(int i=0 ; i < last.size() ;i++){
			secLast.add(last.get(i));
		}
		for(int i=0 ; i < last.size() ;i++){
			ArrayList<RuleElement> element = new ArrayList<RuleElement>();
			element.add(AvailableLHS.get(0));
			for(int j=0; j<last.get(i).size() ;j++){
				element.add(last.get(i).get(j));
			}
			secLast.add(element);
		}
		for(int i=secLast.size()/2; i<secLast.size() ;i++){
			for(int j=0; j<AvailableRHS.size() ;j++){
				if(coverage(secLast.get(i)) > minCoverage && secLast.get(i).size() <= maxLHSsize){
					if(coverage(AvailableRHS.get(j)) > minCoverage && secLast.get(i) != null){
						Rule tempRule = new Rule(secLast.get(i), AvailableRHS.get(j));
						tempRule.setRuleType(RuleType);
						if(CurrentSolution.size() < K && leverage(tempRule) > minLeverage){		//K rules
							CurrentSolution.add(tempRule);					//add new rule with rule type
						}
						else if(CurrentSolution.size() > K && leverage(tempRule) > minLeverage){
							Rule MinValueRule = FindMinValueRule(CurrentSolution);
							if(leverage(tempRule) > MinValueRule.getLeverage()){		// add constraints
								CurrentSolution = removeRule(MinValueRule,CurrentSolution);
								CurrentSolution.add(tempRule);					//add new rule with rule type
							}
						}
					}
				}
			}
		}
//		for(int i=0;i<secLast.size();i++){
//			if(secLast.get(i).size() <= maxLHSsize){
//			displayElement(secLast.get(i));
//			System.out.println();
//			}
//		}
		return secLast;
	}
	
//	public static void algo(ArrayList<RuleElement> CurrentLHS, ArrayList<RuleElement> AvailableLHS, 
//			ArrayList<RuleElement> AvailableRHS, char RuleType){
//		ArrayList<RuleElement> NewLHS = null,  NewAvailableRHS = null, NewAvailableLHS = null;
//		for(int i = 0; i < AvailableLHS.size(); i++){
//			RuleElement P = AvailableLHS.get(i);
////			SoFar.add(P);
//			if(coverage(P) > minCoverage){						//pruning rule 1
//				CurrentLHS.add(P);
//				NewLHS = CurrentLHS;
////				NewAvailableLHS = SoFar; 
//				AvailableRHS = removeRuleElement(P,AvailableRHS);						
//				NewAvailableRHS = AvailableRHS;
//				float cov = coverage(NewLHS);
//				if(NewLHS.size() == maxLHSsize);		//saving 1
//				else{
//					for(int j = 0; j < NewAvailableRHS.size(); j++){
//						RuleElement Q = NewAvailableRHS.get(j); 
//						if(coverage(Q) < minCoverage)
//							NewAvailableRHS.remove(Q);
//						else{
//								Rule tempRule = new Rule(NewLHS, Q);
//								tempRule.setRuleType(RuleType);
//								if(CurrentSolution.size() < K && leverage(tempRule) > minLeverage)		//K rules
//									CurrentSolution.add(tempRule);					//add new rule with rule type
//								else if(CurrentSolution.size() > K && leverage(tempRule) > minLeverage){
//									Rule MinValueRule = FindMinValueRule(CurrentSolution);
//									if(leverage(tempRule) > MinValueRule.getLeverage()){		// add constraints
//										CurrentSolution = removeRule(MinValueRule,CurrentSolution);
//										CurrentSolution.add(tempRule);					//add new rule with rule type
//									}
//								}
//						}
//					}
//				}
//				if(NewAvailableLHS.size() > 0 && NewAvailableRHS.size() > 0)
//					algo(NewLHS, NewAvailableLHS, NewAvailableRHS, RuleType);
//			}
//		}
//	}
	// if rFactor > minValue => valid query    
		public static float Rfactor(ArrayList<RuleElement> test){
			float rFactor = 0;// [0,1]
			float temp;
			for(int i=0; i < dataset.size(); i++){
				temp = LCS(test, dataset.get(i));
				if(temp > rFactor){
					rFactor = temp;
				}
				if(rFactor == 1){
					break;// perfect match found
				}
			}
			return rFactor;
		}
		
		public static boolean ruleMatch(ArrayList<RuleElement> T){
			if(T == null){
				return false;
			}
			int i, j, k;
			for(i = 0; i < CurrentSolution.size(); i++){
				ArrayList<RuleElement> temp = CurrentSolution.get(i).getLHS();
				k = 0;
				for(j = 0; j < temp.size(); j++){
					if(temp.get(j).getType() == T.get(k).getType() && temp.get(j).getAttribute().equals(T.get(k).getAttribute())){
						k++;
					}	
					if(k == (T.size()-1)){
						if(CurrentSolution.get(i).getRHS().getType() == T.get(k).getType() && CurrentSolution.get(i).getRHS().getAttribute().equals(T.get(k).getAttribute())){
							return true;
						}
					}
				}
			}
			return false;
		}

	public static float LCS(ArrayList<RuleElement> pattern, ArrayList<RuleElement> sequence) {
		int n = pattern.size();
		int m = sequence.size();
		int i, j, maxMatch = 0;
		for(i = 0,j = 0; i < m; ++i) {
			if( pattern.get(j).getType() == sequence.get(i).getType() && pattern.get(j).getAttribute().equals(sequence.get(i).getAttribute())){
				j++;
				if(j == n){
					return 1;
				}
			}
			else{
				if(sequence.get(i).getType() == 'W'){
					if(maxMatch < j){
						maxMatch = j;
					}
					j = 0;
				}
			}
		}
		return (float)maxMatch/pattern.size();
//		int i, j;
//
//		int NEITHER     = 0;
//		int UP          = 1;
//		int LEFT        = 2;
//		int UP_AND_LEFT = 3;
//
//		for(i = 0; i <= n; ++i) {
//			S[i][0] = 0;
//			R[i][0] = UP;
//		}
//		for(j = 0; j <= m; ++j) {
//			S[0][j] = 0;
//			R[0][j] = LEFT;
//		}
//
//		for(i = 1; i <= n; ++i) {
//			for(j = 1; j <= m; ++j) { 
//
//				if( pattern.get(i - 1).getType() == sequence.get(j - 1).getType() && pattern.get(i - 1).getAttribute() == sequence.get(j - 1).getAttribute()){
//					S[i][j] = S[i-1][j-1] + 1;
//					R[i][j] = UP_AND_LEFT;
//				}
//
//				else {
//					S[i][j] = S[i-1][j-1] + 0;
//					R[i][j] = NEITHER;
//				}
//
//				if( S[i-1][j] >= S[i][j] ) {	
//					S[i][j] = S[i-1][j];
//					R[i][j] = UP;
//				}
//
//				if( S[i][j-1] >= S[i][j] ) {
//					S[i][j] = S[i][j-1];
//					R[i][j] = LEFT;
//				}
//			}
//		}
		// The length of the longest substring is S[n][m]
//		return (float)S[n][m]/pattern.size();
	}
	
	public static void main(String args[]) throws IOException{
		
//      Roles :
//		0 Manager
//		1 Officer
//		2 Clerk
		
	//password is same as user name (case sensitive)
		
		//Learning Phase Query Parsing
		attributes.add("C_id");
		attributes.add("C_name");
		attributes.add("C_address");
		attributes.add("C_city");
		attributes.add("C_state");
		attributes.add("C_pincode");
		attributes.add("C_phone");
		attributes.add("C_email");
		attributes.add("C_dob");
		attributes.add("C_gender");
		attributes.add("acc_no");
		attributes.add("balance");
		
		Scanner s = new Scanner(System.in);

		dataset = Inputs_Results.getDataset("learningDataset.sql");	
		readRule();
		writeRule();		
		
		//FOR DEBUGGING ONLY
		
		System.out.println("Dataset: Officer");
		for(int i = 0; i < dataset.size(); i++){
			displayElement(dataset.get(i));			
			System.out.println();
		}
		System.out.print("\nRules: Officer");
		displayRule(CurrentSolution);
		System.out.println("\n");
		
		/*
		String user, pass;
		int chance = 3;
		int role = -1;
		System.out.println("\t\tBANK DATABASE SYSTEM");
		while(chance-- > 0){    						//Three tries
			System.out.print("Enter Username: ");
			user = s.nextLine().toLowerCase();
			System.out.print("Enter Password: ");
			pass = s.nextLine();
			if(!user.equals(pass))
				role = -1;
			else if(user.equals("manager"))
				role = 0;
			else if(user.equals("officer"))
				role = 1;
			else if(user.equals("clerk"))
				role = 2;
			
			if(role >= 0){
				System.out.println("Access Granted !!\n");
				break;
			}
			else
				System.out.println("Invalid Usename/Password !!\nAttempts left: " + (chance));
		}
		if(role == -1){
			System.out.println("\n\nLogin attempts finished.... System exiting!");
			System.exit(1);
		}
		String ans = "y";
		ArrayList<RuleElement> test = new ArrayList<RuleElement>();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do{
			System.out.print("\nEnter Query: ");
			String query = br.readLine();
			
			try {
				test = getcrud.queryConvert(query);
				if(test == null)
					continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(role == 0)
				OracleCon.execute(query);				// Allowed
			else if(role == 1){
				if(getcrud.isSelect(query))
					OracleCon.execute(query);				// Allowed
				else if(ruleMatch(test))
					OracleCon.execute(query);				// Allowed
				else if(Rfactor(test) >= rFactor)
					OracleCon.execute(query);  				// Allowed
				else{
					System.out.println("You do not have enough priviledges!\nAlarm Raised !!");
					System.exit(1);								// Halt  the program if alarm is raised
				}
			}
			else{
				if(getcrud.isSelect(query))
					OracleCon.execute(query);				// Allowed
				else{
					System.out.println("You do not have enough priviledges!\nAlarm Raised !!");
					System.exit(1);								// Halt  the program if alarm is raised
				}
			}
			System.out.print("Continue(Y/N)? : ");
			ans = s.next().toLowerCase();
		}while(ans.equals("y"));
		s.close();
		*/
	}
		
	public static void displayRule(ArrayList<Rule> temp){
		System.out.println();
		for(int i=0;i<temp.size();i++){
			System.out.print(temp.get(i).getRuleType()+" : ");
			displayElement(temp.get(i).getLHS());
			System.out.println(" -> "+temp.get(i).getRHS().getType()+"(" + temp.get(i).getRHS().getAttribute() +") Leverage = "+temp.get(i).getLeverage());
		}
		System.out.println();
	}
	
	public static void displayElement(ArrayList<RuleElement> temp){
		for(int i=0;i<temp.size();i++){
			System.out.print(temp.get(i).getType()+"(" + temp.get(i).getAttribute() + ")");
			if(i < temp.size() - 1)
				System.out.print(',');
		}
	}
}
