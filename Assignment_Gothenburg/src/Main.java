import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.TimeoutException;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

public class Main {

    public static void main(String[] args) throws org.sat4j.specs.TimeoutException, TimeoutException {
        
    	ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600); // 1 hour timeout
        
        DimacsReader reader = new DimacsReader(solver);
        
        // CNF filename is given on the command line 
        try {
            IProblem problem = reader.parseInstance(args[0]);
            
            if (problem.isSatisfiable()) {
                System.out.println("Satisfiable !");
                System.out.println(reader.decode(problem.model()));
            } else {
                System.out.println("Unsatisfiable !");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
        } catch (ParseFormatException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (ContradictionException e) {
            System.out.println("Unsatisfiable (trivial)!");
        }
    }
}