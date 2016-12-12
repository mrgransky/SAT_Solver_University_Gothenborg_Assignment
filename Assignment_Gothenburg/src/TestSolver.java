import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;
import org.sat4j.tools.SolutionCounter;

public class TestSolver {

	// Global variables
	public static ISolver solver;
	public static int MAXVAR;
	public static int NBCLAUSES;
	//public static String filename = "uf50-0999.cnf"; // simple file 
	public static String filename = "ecos_features.dimacs"; // real assignment


	public static void main(String[] args) throws TimeoutException, FileNotFoundException, ContradictionException{

		//ISolver solver = SolverFactory.newDefault(); // give only one
		solver = new ModelIterator(SolverFactory.newSAT()); // gives all sol.
		SolutionCounter counter = new SolutionCounter(SolverFactory.newDefault());
		int nbSol = (int) counter.countSolutions();

		solver.setTimeout(3600); // 1 hour timeout

		importFile();
		solver.newVar(MAXVAR);
		solver.setExpectedNumberOfClauses(NBCLAUSES);
		DimacsReader reader = new DimacsReader(SolverFactory.newSAT());
		LinkedList<Integer> dfList = new LinkedList<Integer>();
		String tempString;

		try{

			// try with simple clauses ... 
			//int [] clause= {3,-4}; // get the clause from somewhere
			//solver.addClause(new VecInt(clause)); 

			//int [] clause2 = {1,-2,3};
			//solver.addClause(new VecInt(clause2)); 

			//int [] clause3 = {-1,4};
			//solver.addClause(new VecInt(clause3));

			//int [] clause4 = {4};
			//solver.addClause(new VecInt(clause4));


			IProblem problem = solver;
			while (problem.isSatisfiable()) {

				tempString = reader.decode(problem.model());
				System.out.println("Solution Number = " + nbSol);
				System.out.println("Sol " + nbSol + " = " + tempString);
				LinkedList<Integer>  negVal = extractNegVal(tempString);

				nbSol = nbSol + 1;
				if(nbSol == 2){
					for (int i=0; i<negVal.size(); i++) {	
						dfList.add(negVal.get(i));
					}
				}else {
					//System.out.println("Number of Dead feature = " + dfList.size());

					for (int i = 0; i < dfList.size(); i++){

						int currDF =  dfList.get(i);
						//System.out.println("Current Dead Feature = " + currDF);

						if (negVal.contains(currDF)){
							// true => common in both lists => proceed, move on
							//System.out.println("Do nothing, currDF  = " + currDF);
						}else { // not common in both lists => remove
							dfList.remove((Integer)currDF);
							//System.out.println("Delete, removed = " + currDF);
							i--;
						}
					}



				}

				if(true){
				}else {
					System.out.println("Unsatisfiable !");
				}


			}
			System.out.println("***** Operation ENDED! *****");
			System.out.println("Problem is SAT " + " , Sol_NB = " + (nbSol-1));

			if (dfList.size() == 0){
				System.out.println("No Dead Feature found in this problem!");
			}else {
				for (int j = 0; j < dfList.size(); ++j){
					int deadFeature = dfList.get(j);
					//System.out.println("Dead feature is : " + deadFeature);
					deadFeatName(deadFeature);
				}
			}
		}catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static LinkedList<Integer> extractNegVal(String temp){

		StringBuilder sb = new StringBuilder(5);
		int index = 0;
		char tempChar;
		int tmpVar;
		LinkedList<Integer> list = new LinkedList<Integer>();

		boolean readEnd = false;
		while (!readEnd)
		{
			tempChar = temp.charAt(index);
			switch (tempChar) {
			case '-': 
				sb.append('-');
				break;
			case ' ':
				tmpVar= Integer.parseInt(sb.toString()); 
				sb.setLength(0);
				// save num if < 0
				if(tmpVar < 0)
				{
					list.offer(tmpVar);
				}
				break;
				// full solution
			case '0':
				readEnd = true;
				break;
			default : 
				sb.append(tempChar);
				break;
			}

			index = index +1;
		}
		//	while (list.size()>0)
		//{

		//System.out.println(list.poll());
		//	}

		return list;
	}

	private static void importFile() throws FileNotFoundException, ContradictionException{
		Scanner sc = new Scanner (new File(filename));
		int idx; 
		int tmpVar_in_lineP = 0;
		LinkedList<Integer> tempClauseList = new LinkedList<Integer>();
		int tmpVar;

		boolean addClause = false;
		while (sc.hasNextLine()){ 
			String at_line = sc.nextLine();
			//System.out.println(" line = " + at_line);
			idx = 0;
			// look for character with the index
			char tempChar = at_line.charAt(idx);

			StringBuilder sb_file = new StringBuilder();

			// take a look @ 1st letter of each line 

			// 'c' => comment, do nothing...
			if(tempChar=='c'){
				// nothing...
				addClause = false;  
				// 'p' => store number of variables and clauses
			}else if (tempChar == 'p'){

				for (int j = 0; j < at_line.length(); j++){
					tempChar = at_line.charAt(j);
					// in case of numbers => put them 2gether 
					if(tempChar == '0' || tempChar == '1' 
							|| tempChar == '2' || tempChar == '3' 
							|| tempChar == '4' || tempChar == '5' 
							|| tempChar == '6' || tempChar == '7' 
							|| tempChar == '8' || tempChar == '9'){
						// => build string
						sb_file.append(String.valueOf(tempChar));

					} else if (tempChar == ' ' && sb_file.length() != 0) {
						tmpVar_in_lineP = Integer.parseInt(sb_file.toString());
						MAXVAR = tmpVar_in_lineP;

						// after saving the varNB, string should be emptied:
					} else if (tempChar == ' '){
						sb_file.setLength(0);

						// 
						//} else if (tempChar == ' ' && tmpVar_in_lineP != 0) {
					} else if (tempChar == ' ' && sb_file.length() == 0) {

						tmpVar_in_lineP= Integer.parseInt(sb_file.toString());
						NBCLAUSES = tmpVar_in_lineP;

					}	else {
						//System.out.println("sth went wrong!!!");
					}
				}
				addClause = false;  
				// number => example) 1 -2 4 0	
			}else{
				addClause = true;  
				sb_file.setLength(0);
				for (int j = 0; j < at_line.length(); j++){
					tempChar = at_line.charAt(j);

					if (tempChar == '-'||tempChar == '0' 
							|| tempChar == '1' || tempChar == '2' 
							|| tempChar == '3' || tempChar == '4' 
							|| tempChar == '5' || tempChar == '6' 
							|| tempChar == '7' || tempChar == '8' 
							|| tempChar == '9'){
						// put them 2gether ...
						sb_file.append(String.valueOf(tempChar));
						//System.out.println("string builder = " + sb_file + " , length = " + sb_file.length());

					} else if (tempChar == ' '){
						// either line is ended or next number 

						if (tempChar == '0') {
							// end of line 
							sb_file.setLength(0); // clear the string builder

						} else {
							tmpVar = Integer.parseInt(sb_file.toString());
							//System.out.println("string builder = " + sb_file + ", integer = " + tmpVar + " , length = " + sb_file.length());
							tempClauseList.add(tmpVar);
							sb_file.setLength(0); // clear the string builder
						}
					}
				}

			}
			int size = tempClauseList.size();
			//System.out.println("sizee: " + size);


			int [] tmpClause = new int [size]; // initialize array with list size

			for (int i=0; i < size; i++) {

				//System.out.println("temp clause B4 = " + tmpClause[i]);

			}
			for (int k = 0; k < size; k++){
				int tempListNum = tempClauseList.poll();
				//System.out.println("list poll = "+ tempListNum);
				//System.out.println("k value = " + k);
				tmpClause[k] = tempListNum;
				//System.out.println("Clause : " + tmpClause[k]);
				//tempClauseList.remove(0);
			}
			//System.out.println("Clause : end" );

			for (int kk=0; kk<size; kk++){
				//System.out.println("final clause = " + tmpClause[kk] );
			}
			if (addClause){
				solver.addClause(new VecInt(tmpClause));
			}
		}
	}

	private static void deadFeatName(int inpDF) throws FileNotFoundException{

		if (inpDF < 0){inpDF = -inpDF;}

		System.out.println("Dead Feature index = " + inpDF );


		Scanner scan = new Scanner (new File(filename));
		int indexDeadFeat = 0;
		int length_line;
		int varNB_DF = 0;
		StringBuilder DF = null;
		
		while(scan.hasNextLine()){
			String lineDF = scan.nextLine();
			//length_line = lineDF.length();


			char tmpChar = lineDF.charAt(indexDeadFeat);
			StringBuilder sb_DF = new StringBuilder();

			//System.out.println(" line = " + lineDF + " , length = " + lineDF.length() + "character [0]" + tmpChar);

			while (tmpChar == 'c'){ // the first character is 'c', we don't care about the rest
				//System.out.println(" 2nd while loop");

				for (int a = 0; a < lineDF.length(); a++){
					tmpChar = lineDF.charAt(a);
					//System.out.println("------------------");

					if((tmpChar == '0' || tmpChar == '1' 
							|| tmpChar == '2' || tmpChar == '3' 
							|| tmpChar == '4' || tmpChar == '5' 
							|| tmpChar == '6' || tmpChar == '7' 
							|| tmpChar == '8' || tmpChar == '9')
							&& sb_DF.length() == 0){
						// => build string
						//System.out.println("number MODE , " + " length = " + sb_DF.length());
						
						sb_DF.append(String.valueOf(tmpChar));
						//System.out.println("string builder = " + sb_DF + " , length = " + sb_DF.length());
						

					} else if (tmpChar == ' ' && sb_DF.length() != 0) {
						varNB_DF = Integer.parseInt(sb_DF.toString());
						//System.out.println("var = " + varNB_DF);
						sb_DF.setLength(0);//clear the string
						
					} else if (tmpChar == 'A' || tmpChar == 'B'
							||tmpChar == 'C' || tmpChar == 'D'
							||tmpChar == 'E' || tmpChar == 'F'
							||tmpChar == 'G' || tmpChar == 'H'
							||tmpChar == 'I' || tmpChar == 'J'
							||tmpChar == 'K' || tmpChar == 'L'
							||tmpChar == 'M' || tmpChar == 'N'
							||tmpChar == 'O' || tmpChar == 'P'
							||tmpChar == 'Q' || tmpChar == 'R'
							||tmpChar == 'S' || tmpChar == 'T'
							||tmpChar == 'U' || tmpChar == 'V'
							||tmpChar == 'X' || tmpChar == 'Y'
							||tmpChar == 'Z' || tmpChar == '_'
							||tmpChar == '0' || tmpChar == '1' 
							||tmpChar == '2' || tmpChar == '3' 
							||tmpChar == '4' || tmpChar == '5'
							||tmpChar == '6' || tmpChar == '7' 
							||tmpChar == '8' || tmpChar == '9') {

						//System.out.println("mixture");
						
						sb_DF.append(String.valueOf(tmpChar));
						DF = sb_DF;
						//System.out.println("string  = " + DF + " , length = " + sb_DF.length());
						//sb_DF.setLength(0);//clear the string

					} else if (tmpChar == ' ' && varNB_DF == inpDF){
						System.out.println("dead feature name =  " + DF );

					} else {
						//System.out.println("ELSE MODE ---!");
					}

				}
			}




		}


	}



}

