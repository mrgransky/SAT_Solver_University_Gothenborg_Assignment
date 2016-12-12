# SAT_Solver_Chalmers|Gothenborg_Univ_Assignment

Imagine a software product line with more than 1000 features. Such features are typically described in a feature model, which declares features and their dependencies. To reason about the allowed combinations of features, feature models are often converted into a propositional formula (where a feature is represented by a Boolean variable), which is then, after converting the formula into the conjunctive normal form, fed into a SAT solver. A simple query is to ask the solver whether the formula is satisfiable. If it is, then the formula has at least one satisfying assignment (a mapping from variables to {true,false}), and one can infer that the product line has at least one valid combination of features.

I have attached a DIMACS file -- a standard file format typically used as input to SAT solvers. It contains the formula (converted from a feature model of an embedded system) in conjunctive normal form. Your tasks are to (i) check whether the formula is satisfiable and (ii) to find all dead features. A dead feature is a feature (Boolean variable) that can never be enabled in any valid combination of features. In other words, the Boolean variable is never true in all satisfying assignments.

Note that in the dimacs, the Boolean variables are represented as integers. There is a mapping in the beginning of the file (as comments -- lines that start with "c") that assigns feature names to the Boolean variables. Please output the names of the dead features, not the integer names of the respective features.
