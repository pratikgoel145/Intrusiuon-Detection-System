
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class getcrud {
	
	static ArrayList<ArrayList<RuleElement>> dataset = new ArrayList<ArrayList<RuleElement>>();
	static ArrayList<RuleElement> row;
	
	protected static int run(String fileName){
    	dataset = new ArrayList<ArrayList<RuleElement>>();
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqlfilename = fileName;
        int ret = sqlparser.parse();
        
        if (ret != 0){
            //System.out.println(sqlparser.getErrormessage());
            System.out.println("Invalid query syntax!");
            return ret;
        }
        
        for (int i = 0; i < sqlparser.sqlstatements.size(); i++){       		
        	row = new ArrayList<RuleElement>();
            analyzeStmt(sqlparser.sqlstatements.get(i));
            
            // Optimizing the sequence formed
            int j, k;
            ArrayList<RuleElement> modRow = new ArrayList<RuleElement>();
            for(j = 0; j < row.size(); j++){
            	for(k = 0; k < modRow.size(); k++)
            		if(row.get(j).getAttribute().equals(modRow.get(k).getAttribute()) && row.get(j).getType() == modRow.get(k).getType())
            			break;
            	if(k == modRow.size())
            		modRow.add(row.get(j));
            }
            row = modRow;
            
            // Adding the sequence
            dataset.add(row);
        }
        return ret;
    }
	
	
	public static boolean isSelect(String query){
		File file = new File("testing.sql");
		
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			bw.write(query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqlfilename = "testing.sql";
        sqlparser.parse();
        switch(sqlparser.sqlstatements.get(0).sqlstatementtype){
        	case sstselect: return true;
        	default: return false;
        }
	}
	
	public static ArrayList<RuleElement> queryConvert(String query) throws IOException{
		File file = new File("testing.sql");
		
		if (!file.exists()) 					// If file doesn't exists, then create it
				file.createNewFile();
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(query);
		bw.close();

		if(run("testing.sql") == 0)
			return dataset.get(0);
		else
			return null;
	}
	
    public static ArrayList<ArrayList<RuleElement>> getDataset(String fileName){
    	run(fileName);
    	return dataset;
     }
    
    public static void main(String args[]){
    	run("testing.sql");
    	for(int i = 0; i < dataset.size(); i++){
    		IDS.displayElement(dataset.get(i));
    		System.out.println();
    	}
    }

    protected static void analyzeStmt(TCustomSqlStatement stmt){
    	
    	//For complex Queries
    	for (int i=0;i<stmt.getStatements().size();i++)
            analyzeStmt(stmt.getStatements().get(i));
    	
        switch(stmt.sqlstatementtype){
            case sstselect:
                analyzeSelectStmt((TSelectSqlStatement)stmt);
                break;
            case sstupdate:
                analyzeUpdateStmt((TUpdateSqlStatement)stmt);
                break;
            case sstinsert:
                analyzeInsertStmt((TInsertSqlStatement)stmt);
                break;
            case sstdelete:
                analyzeDeleteStmt((TDeleteSqlStatement) stmt);
                break;
                /*
            case sstcreatetable:
                analyzeCreateTableStmt((TCreateTableSqlStatement)stmt);
                break;
            case sstaltertable:
                analyzeAlterTableStmt((TAlterTableStatement) stmt);
                break;
            case sstcreateview:
                analyzeCreateViewStmt((TCreateViewSqlStatement)stmt);
                break;*/
            default:
                //System.out.println(stmt.sqlstatementtype.toString());
                return;
        }        
    }
/*
    protected static void printConstraint(TConstraint constraint, Boolean outline){

        if (constraint.getConstraintName() != null){
            System.out.println("\t\tconstraint name:"+constraint.getConstraintName().toString());
        }

        switch(constraint.getConstraint_type()){
            case notnull:
                System.out.println("\t\tnot null");
                break;
            case primary_key:
                System.out.println("\t\tprimary key");
                if (outline){
                    String lcstr = "";
                    if (constraint.getColumnList() != null){
                        for(int k=0;k<constraint.getColumnList().size();k++){
                            if (k !=0 ){lcstr = lcstr+",";}
                            lcstr = lcstr+constraint.getColumnList().getObjectName(k).toString();
                        }
                        System.out.println("\t\tprimary key columns:"+lcstr);
                    }
                }
                break;
            case unique:
                System.out.println("\t\tunique key");
                if(outline){
                    String lcstr="";
                    if (constraint.getColumnList() != null){
                        for(int k=0;k<constraint.getColumnList().size();k++){
                            if (k !=0 ){lcstr = lcstr+",";}
                            lcstr = lcstr+constraint.getColumnList().getObjectName(k).toString();
                        }
                    }
                    System.out.println("\t\tcolumns:"+lcstr);
                }
                break;
            case check:
                System.out.println("\t\tcheck:"+constraint.getCheckCondition().toString());
                break;
            case foreign_key:
            case reference:
                System.out.println("\t\tforeign key");
                if(outline){
                    String lcstr="";
                    if (constraint.getColumnList() != null){
                        for(int k=0;k<constraint.getColumnList().size();k++){
                            if (k !=0 ){lcstr = lcstr+",";}
                            lcstr = lcstr+constraint.getColumnList().getObjectName(k).toString();
                        }
                    }
                    System.out.println("\t\tcolumns:"+lcstr);
                }
                System.out.println("\t\treferenced table:"+constraint.getReferencedObject().toString());
                if (constraint.getReferencedColumnList() != null){
                    String lcstr="";
                    for(int k=0;k<constraint.getReferencedColumnList().size();k++){
                        if (k !=0 ){lcstr = lcstr+",";}
                        lcstr = lcstr+constraint.getReferencedColumnList().getObjectName(k).toString();
                    }
                    System.out.println("\t\treferenced columns:"+lcstr);
                }
                break;
            default:
                break;
        }
    }

    protected static void printObjectNameList(TObjectNameList objList){
        for(int i=0;i<objList.size();i++){
            System.out.println(objList.getObjectName(i).toString());
        }

    }
    protected static void printColumnDefinitionList(TColumnDefinitionList cdl){
        for(int i=0;i<cdl.size();i++){
            System.out.println(cdl.getColumn(i).getColumnName());
        }
    }
    protected static void printConstraintList(TConstraintList cnl){
        for(int i=0;i<cnl.size();i++){
            printConstraint(cnl.getConstraint(i),true);
        }
    }

    protected static void printAlterTableOption(TAlterTableOption ato){
        System.out.println(ato.getOptionType());
        switch (ato.getOptionType()){
            case AddColumn:
                printColumnDefinitionList(ato.getColumnDefinitionList());
                break;
            case ModifyColumn:
                printColumnDefinitionList(ato.getColumnDefinitionList());
                break;
            case AlterColumn:
                System.out.println(ato.getColumnName().toString());
                break;
            case DropColumn:
                System.out.println(ato.getColumnName().toString());
                break;
            case SetUnUsedColumn:  //oracle
                printObjectNameList(ato.getColumnNameList());
                break;
            case DropUnUsedColumn:
                break;
            case DropColumnsContinue:
                break;
            case RenameColumn:
                System.out.println("rename "+ato.getColumnName().toString()+" to "+ato.getNewColumnName().toString());
                break;
            case ChangeColumn:   //MySQL
                System.out.println(ato.getColumnName().toString());
                printColumnDefinitionList(ato.getColumnDefinitionList());
                break;
            case RenameTable:   //MySQL
                System.out.println(ato.getColumnName().toString());
                break;
            case AddConstraint:
                printConstraintList(ato.getConstraintList());
                break;
            case AddConstraintIndex:    //MySQL
                if (ato.getColumnName() != null){
                    System.out.println(ato.getColumnName().toString());
                }
                printObjectNameList(ato.getColumnNameList());
                break;
            case AddConstraintPK:
            case AddConstraintUnique:
            case AddConstraintFK:
                if (ato.getConstraintName() != null){
                    System.out.println(ato.getConstraintName().toString());
                }
                printObjectNameList(ato.getColumnNameList());
                break;
            case ModifyConstraint:
                System.out.println(ato.getConstraintName().toString());
                break;
            case RenameConstraint:
                System.out.println("rename "+ato.getConstraintName().toString()+" to "+ato.getNewConstraintName().toString());
                break;
            case DropConstraint:
                System.out.println(ato.getConstraintName().toString());
                break;
            case DropConstraintPK:
                break;
            case DropConstraintFK:
                System.out.println(ato.getConstraintName().toString());
                break;
            case DropConstraintUnique:
                if (ato.getConstraintName() != null){ //db2
                    System.out.println(ato.getConstraintName());
                }

                if (ato.getColumnNameList() != null){//oracle
                    printObjectNameList(ato.getColumnNameList());
                }
                break;
            case DropConstraintCheck: //db2
                System.out.println(ato.getConstraintName());
                break;
            case DropConstraintPartitioningKey:
                break;
            case DropConstraintRestrict:
                break;
            case DropConstraintIndex:
                System.out.println(ato.getConstraintName());
                break;
            case DropConstraintKey:
                System.out.println(ato.getConstraintName());
                break;
            case AlterConstraintFK:
                System.out.println(ato.getConstraintName());
                break;
            case AlterConstraintCheck:
                System.out.println(ato.getConstraintName());
                break;
            case CheckConstraint:
                break;
            case OraclePhysicalAttrs:
            case toOracleLogClause:
            case OracleTableP:
            case MssqlEnableTrigger:
            case MySQLTableOptons:
            case Db2PartitioningKeyDef:
            case Db2RestrictOnDrop:
            case Db2Misc:
            case Unknown:
                break;
        }

    }

    protected static void analyzeCreateViewStmt(TCreateViewSqlStatement pStmt){
        TCreateViewSqlStatement createView = pStmt;
        System.out.println("View name:"+createView.getViewName().toString());
        TViewAliasClause aliasClause = createView.getViewAliasClause();
        for(int i=0;i<aliasClause.getViewAliasItemList().size();i++){
            System.out.println("View alias:"+aliasClause.getViewAliasItemList().getViewAliasItem(i).toString());
        }

        System.out.println("View subquery: \n"+ createView.getSubquery().toString() );
    }
*/
    
    
    protected static ArrayList<String> getColumns(TExpression t){
    	if(t == null)
    		return null;
    	ArrayList<String> ans = new ArrayList<String>();
    	String string = t.toString();
    	List<String> arr = Arrays.asList(string.split(" "));
    	for(int i = 0; i < IDS.attributes.size(); i++)
    		if(arr.contains(IDS.attributes.get(i)))
    			ans.add(IDS.attributes.get(i));
    	return ans;
    }
    
    protected static void analyzeSelectStmt(TSelectSqlStatement pStmt){
        /*System.out.println("\nSelect:");
        if (pStmt.isCombinedQuery()){
            String setstr="";
            switch (pStmt.getSetOperator()){
                case 1: setstr = "union";break;
                case 2: setstr = "union all";break;
                case 3: setstr = "intersect";break;
                case 4: setstr = "intersect all";break;
                case 5: setstr = "minus";break;
                case 6: setstr = "minus all";break;
                case 7: setstr = "except";break;
                case 8: setstr = "except all";break;
            }
            
            System.out.printf("set type: %s\n",setstr);
            System.out.println("left select:");
            analyzeSelectStmt(pStmt.getLeftStmt());
            System.out.println("right select:");
            analyzeSelectStmt(pStmt.getRightStmt());
            
            if (pStmt.getOrderbyClause() != null){
                System.out.printf("order by clause %s\n",pStmt.getOrderbyClause().toString());
            }
        }else{*/
    	
    	
    		//where
            if (pStmt.getWhereClause() != null){
            	ArrayList<String> columns = getColumns(pStmt.getWhereClause().getCondition());
            	for(int i = 0; i < columns.size(); i++)
            		row.add(new RuleElement('R', columns.get(i)));
            }
            	
            // group by
            if (pStmt.getGroupByClause() != null){
                for(int i = 0; i < pStmt.getGroupByClause().getItems().size(); i++)
                	row.add(new RuleElement('R', pStmt.getGroupByClause().getItems().getGroupByItem(i).toString()));
            
                //having
                if (pStmt.getGroupByClause().getHavingClause() != null)
                	row.add(new RuleElement('R', pStmt.getGroupByClause().getHavingClause().getLeftOperand().toString()));
            }
            
            // order by
            if (pStmt.getOrderbyClause() != null)
            	for(int i = 0; i < pStmt.getOrderbyClause().getItems().size(); i++)
            		row.add(new RuleElement('R', pStmt.getOrderbyClause().getItems().getOrderByItem(i).getStartToken().toString()));           
            
            //select list
            for(int i = 0; i < pStmt.getResultColumnList().size(); i++){
                TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
                if(resultColumn.getExpr().toString().charAt(0) == '*')
              	  for(int j = 0; j < IDS.attributes.size(); j++)
              		  row.add(new RuleElement('R', IDS.attributes.get(j)));
                else 
                	row.add(new RuleElement('R', resultColumn.getExpr().toString()));
                //System.out.printf("Column: %s, Alias: %s\n",resultColumn.getExpr().toString(), (resultColumn.getAliasClause() == null)?"":resultColumn.getAliasClause().toString());
            }
            
            /*
            //from clause, check this document for detailed information
            //http://www.sqlparser.com/sql-parser-query-join-table.php
            for(int i=0;i<pStmt.joins.size();i++){
                TJoin join = pStmt.joins.getJoin(i);
                switch (join.getKind()){
                    case TBaseType.join_source_fake:
                        System.out.printf("table: %s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
                        break;
                    case TBaseType.join_source_table:
                        System.out.printf("table: %s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
                        for(int j=0;j<join.getJoinItems().size();j++){
                            TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                            System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }
                        break;
                    case TBaseType.join_source_join:
                        TJoin source_join = join.getJoin();
                        System.out.printf("table: %s, alias: %s\n",source_join.getTable().toString(),(source_join.getTable().getAliasClause() !=null)?source_join.getTable().getAliasClause().toString():"");

                        for(int j=0;j<source_join.getJoinItems().size();j++){
                            TJoinItem joinItem = source_join.getJoinItems().getJoinItem(j);
                            System.out.printf("source_join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }

                        for(int j=0;j<join.getJoinItems().size();j++){
                            TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                            System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                            System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                            if (joinItem.getOnCondition() != null){
                                System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                            }else  if (joinItem.getUsingColumns() != null){
                                System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                            }
                        }

                        break;
                    default:
                        System.out.println("unknown type in join!");
                        break;
                }
            }
			
            //where clause
            if (pStmt.getWhereClause() != null){
                System.out.printf("where clause: \n%s\n", pStmt.getWhereClause().toString());
            }

            // group by
            if (pStmt.getGroupByClause() != null){
                System.out.printf("group by: \n%s\n",pStmt.getGroupByClause().toString());
            }

            // order by
            if (pStmt.getOrderbyClause() != null){
              System.out.printf("order by: \n%s\n",pStmt.getOrderbyClause().toString());
            }
            
            // for update
            if (pStmt.getForUpdateClause() != null){
                System.out.printf("for update: \n%s\n",pStmt.getForUpdateClause().toString());
            }

            // top clause
            if (pStmt.getTopClause() != null){
                System.out.printf("top clause: \n%s\n",pStmt.getTopClause().toString());
            }

            // limit clause
            if (pStmt.getLimitClause() != null){
                System.out.printf("limit clause: \n%s\n",pStmt.getLimitClause().toString());
            }
            
        }*/
    }

    protected static void analyzeInsertStmt(TInsertSqlStatement pStmt){
        /*if (pStmt.getTargetTable() != null){
            System.out.println("Table name:"+pStmt.getTargetTable().toString());
        }

        System.out.println("insert value type:"+pStmt.getValueType());				
		*/
        if(pStmt.getColumnList() != null){
            //System.out.println("columns:");
            for(int i = 0; i < pStmt.getColumnList().size(); i++){
            	//System.out.println("\t"+pStmt.getColumnList().getObjectName(i).toString());
            	row.add(new RuleElement('W', pStmt.getColumnList().getObjectName(i).toString()));
            }
        }
        
        if(pStmt.getColumnList() == null && pStmt.getValues() != null)
        	for(int j = 0; j < IDS.attributes.size(); j++)
        		  row.add(new RuleElement('W', IDS.attributes.get(j)));
        /*
        if (pStmt.getValues() != null){
            System.out.println("values:");
            for(int i=0;i<pStmt.getValues().size();i++){
                TMultiTarget mt = pStmt.getValues().getMultiTarget(i);
                for(int j=0;j<mt.getColumnList().size();j++){
                    System.out.println("\t"+mt.getColumnList().getResultColumn(j).toString());
                }
            }
        }
		*/
        if (pStmt.getSubQuery() != null){
            analyzeSelectStmt(pStmt.getSubQuery());
        }
    }
    protected static void analyzeUpdateStmt(TUpdateSqlStatement pStmt){
    	//where
        if (pStmt.getWhereClause() != null){
        	ArrayList<String> columns = getColumns(pStmt.getWhereClause().getCondition());
        	for(int i = 0; i < columns.size(); i++)
        		row.add(new RuleElement('R', columns.get(i)));
        }
    	
    	
        //System.out.println("Table Name:"+pStmt.getTargetTable().toString());
        //System.out.println("set clause:");
        for(int i=0;i<pStmt.getResultColumnList().size();i++){
            TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
            TExpression expression = resultColumn.getExpr().getLeftOperand();
            row.add(new RuleElement('W', expression.toString()));
            //System.out.println("\tcolumn:"+expression.getLeftOperand().toString()+"\tvalue:"+expression.getRightOperand().toString());
        }
        /*
        if(pStmt.getWhereClause() != null){
            System.out.println("where clause:\n");
            	System.out.print(pStmt.getWhereClause().getCondition().toString());
            
        }*/
    }
    
    protected static void analyzeDeleteStmt(TDeleteSqlStatement pStmt){
    	//where
        if (pStmt.getWhereClause() != null){
        	ArrayList<String> columns = getColumns(pStmt.getWhereClause().getCondition());
        	for(int i = 0; i < columns.size(); i++)
        		row.add(new RuleElement('R', columns.get(i)));
        }
        for(int j = 0; j < IDS.attributes.size(); j++)
  		  row.add(new RuleElement('W', IDS.attributes.get(j)));
        
    }
    /*
     protected static void analyzeAlterTableStmt(TAlterTableStatement pStmt){
         System.out.println("Table Name:"+pStmt.getTableName().toString());
         System.out.println("Alter table options:");
         for(int i=0;i<pStmt.getAlterTableOptionList().size();i++){
             printAlterTableOption(pStmt.getAlterTableOptionList().getAlterTableOption(i));
         }
     }

    protected static void analyzeCreateTableStmt(TCreateTableSqlStatement pStmt){
        System.out.println("Table Name:"+pStmt.getTargetTable().toString());
        System.out.println("Columns:");
        TColumnDefinition column;
        for(int i=0;i<pStmt.getColumnList().size();i++){
            column = pStmt.getColumnList().getColumn(i);
            System.out.println("\tname:"+column.getColumnName().toString());
            System.out.println("\tdatetype:"+column.getDatatype().toString());
            if (column.getDefaultExpression() != null){
                System.out.println("\tdefault:"+column.getDefaultExpression().toString());
            }
            if (column.isNull()){
                System.out.println("\tnull: yes");
            }
            if (column.getConstraints() != null){
                System.out.println("\tinline constraints:");
                for(int j=0;j<column.getConstraints().size();j++){
                    printConstraint(column.getConstraints().getConstraint(j),false);
                }
            }
            System.out.println("");
        }

        if(pStmt.getTableConstraints().size() > 0){
            System.out.println("\toutline constraints:");
            for(int i=0;i<pStmt.getTableConstraints().size();i++){
              printConstraint(pStmt.getTableConstraints().getConstraint(i), true);
              System.out.println("");
            }
        }
    }
    */

}