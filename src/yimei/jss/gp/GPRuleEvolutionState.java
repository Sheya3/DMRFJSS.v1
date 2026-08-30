package yimei.jss.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.simple.SimpleEvolutionState;
import ec.util.Checkpoint;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparatorL;
import yimei.jss.gp.terminal.AttributeGPNode;
import yimei.jss.gp.terminal.JobShopAttribute;
import yimei.jss.niching.PhenoCharacterisation;
import yimei.jss.rule.AbstractRuleHelper;
import yimei.jss.ruleoptimisation.RuleOptimizationProblem;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static yimei.jss.gp.GPRun.out_dir;

/**
 * The evolution state of evolving dispatching rules with GP.
 *
 * @author yimei
 *
 */

public class GPRuleEvolutionState extends SimpleEvolutionState {

	public static PhenoCharacterisation[] phenoCharacterisation;

	/**
	 * Read the file to specify the terminals.
	 */
	public final static String P_TERMINALS_FROM = "terminals-from";
	public final static String P_INCLUDE_ERC = "include-erc";

	protected String[] terminalsFrom;
	protected boolean[] includeErc;
	protected long jobSeed;
	protected GPNode[][] terminals;
	//fzhang 2019.5.20 set weights to terminals in each subpop
	protected double[][] weights;

	public void setWeights(double[][] weights){
		this.weights = weights;
	}

	List<Double> genTimes = new ArrayList<>();

	public GPNode[][] getTerminals() {
		return terminals;
	}

    public GPNode[] getTerminals(int subPopNum) {
        return terminals[subPopNum];
    }

    public long getJobSeed() {
		return jobSeed;
	}

	public void setTerminals(GPNode[][] terminals) {
		this.terminals = terminals;
	}

    public void setTerminals(GPNode[] terminals, int subPopNum) {
        this.terminals[subPopNum] = terminals;
    }

    //fzhang 2019.5.20 set weights to each subpopulation
/*    public void setWeights(double[] weights, int subPopNum) {
        this.weights[subPopNum] = weights;
    }*/

    //fzhang 16.7.2018 in order to use selected features to initialize population
    public void setTreeTerminals(GPNode[] terminals, int numTrees) {
        this.terminals[numTrees] = terminals;
    }

	//From LUYAO
	public ArrayList<double[]> seqFeatureOccurrences = new ArrayList<>();
	public ArrayList<double[]> rouFeatureOccurrences = new ArrayList<>();

	ArrayList<double[]> seqFeatureOccurrencesIntree = new ArrayList<>();
	ArrayList<double[]> rouFeatureOccurrencesIntree = new ArrayList<>();
	public ArrayList<double[]> ordFeatureOccurrences = new ArrayList<double[]>();

    /**
	 * Initialize the terminal set with all the job shop attributes.
	 */
	public void initTerminalSet() {
        int numSubPops = parameters.getInt(new Parameter("pop.subpops"),null); //numSubPops = 2
        //terminals is a double array [][], type: GPNode
        this.terminals = new GPNode[numSubPops][];

        for (int subPopNum = 0; subPopNum < numSubPops; subPopNum++) {
         //for (int subPopNum = 0; subPopNum < numSubPops; ++subPopNum) {
        	//terminals From should have two elements, terminalsFrom[0] and terminalsFrom[1]
        	//terminals-from.0 = relative            terminalsFrom[0] = relative
        	//terminals-from.1 = systemstate         terminalsFrom[1] = systemstate   Yes, it is right.
            String terminalFrom = terminalsFrom[subPopNum];
            boolean includeErc = this.includeErc[subPopNum];
            if (terminalFrom.equals("basic")) {
                initBasicTerminalSet(subPopNum);
            }
            else if (terminalFrom.equals("relative")) {
            	//that is to say, put the terminals we defined into terminals[]
                initRelativeTerminalSet(subPopNum);
            }
            //modified by fzhang 27.5.2018   create some new features mainly about systemstate
            else if(terminalFrom.equals("systemstate")) {
            	initSystemstateTerminalSet(subPopNum);
            }
          //modified by fzhang 6.6.2018   create some new features from relativeWithoutWeight
            else if(terminalFrom.equals("relativeWithoutWeight")) {
            	initRelativeWithoutWeightTerminalSet(subPopNum);
            }
            //fzhang 19.7.2018
            else if(terminalFrom.equals("current")) {
            	initRelativeCurrentAttributesTerminalSet(subPopNum);
            }
            else if(terminalFrom.equals("future")) {
            	initRelativeFutureAttributesTerminalSet(subPopNum);
            }
            else if(terminalFrom.equals("history")) {
            	initRelativeHistoryAttributesTerminalSet(subPopNum);
            }
			//Liu Feige 2025.10 features for MTTSP
			else if(terminalFrom.equals("relativeForTugboat")) {
				initRelativeCurrentAttributesTerminalSetTugboat(subPopNum);
			}
			else if(terminalFrom.equals("relativeForTugboatSet")){
				initRelativeCurrentAttributesTerminalSetTugboatSet(subPopNum);
			}
			else if(terminalFrom.equals("relativeForSingleTugboat")){
				initRelativeCurrentAttributesTerminalSingleTugboat(subPopNum);
			}
            else {
            	String terminalFile = terminalFrom;
                //String terminalFile = "terminals/" + terminalFrom;  //set directly in parameter file fzhang 21.6.2018
                initTerminalSetFromCsv(new File(terminalFile), subPopNum);
            }
            if (includeErc) {
                //terminals.add(new DoubleERC());
                //TODO: Implement this
                System.out.println("INCLUDE ERC NOT IMPLEMENTED");
            }
        }
	}

	public void initBasicTerminalSet(int subPopNum) {
		LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.basicAttributes()) {
            GPNode attribute = new AttributeGPNode(a);
            terminals.add(attribute);
		}
		this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);

	}

	public void initRelativeTerminalSet(int subPopNum) {
        LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.relativeAttributes()) {
		    GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute); //read all the terminals we defined to keep them into terminals[]
		}
        this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
    }

	//modified by fzhang  27.5.2018  create some new features
	public void initSystemstateTerminalSet(int subPopNum) {
        LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.systemstateAttributes()) {
		    GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute);  //read terminals into terminals[]  rigth
		}
        this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);  //terminals[subPopNum] value is right
    }

	//modified by fzhang  6.6.2018  do not use weight as terminal in non-weighted-objective
		public void initRelativeWithoutWeightTerminalSet(int subPopNum) {
	        LinkedList<GPNode> terminals = new LinkedList<GPNode>();
			for (JobShopAttribute a : JobShopAttribute.relativeWithoutWeightAttributes()) {
			    GPNode attribute = new AttributeGPNode(a);
				terminals.add(attribute);
			}
	        this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
	    }

	// modified by fzhang 19.7.2018  relativeCurrentAttributes
	public void initRelativeCurrentAttributesTerminalSet(int subPopNum) {
		LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.relativeCurrentAttributes()) {
			GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute);
		}
		this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
	}

	//LIUFEIGE
	public void initRelativeCurrentAttributesTerminalSetTugboat(int subPopNum) {
		LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.relativeAttributesForTugboat()) {
			GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute);
		}
		this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
	}

	public void initRelativeCurrentAttributesTerminalSetTugboatSet(int subPopNum){
		LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.relativeAttributesForTugboatSet()) {
			GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute);
		}
		this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
	}

	public void initRelativeCurrentAttributesTerminalSingleTugboat(int subPopNum){
		LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.relativeAttributesForSingleTB()) {
			GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute);
		}
		this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
	}


	//relativeFutureAttributes
	public void initRelativeFutureAttributesTerminalSet(int subPopNum) {
		LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.relativeFutureAttributes()) {
			GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute);
		}
		this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
	}

	//relativeHistoryAttributes
	public void initRelativeHistoryAttributesTerminalSet(int subPopNum) {
		LinkedList<GPNode> terminals = new LinkedList<GPNode>();
		for (JobShopAttribute a : JobShopAttribute.relativeHistoryAttributes()) {
			GPNode attribute = new AttributeGPNode(a);
			terminals.add(attribute);
		}
		this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
	}

	public void initTerminalSetFromCsv(File csvFile, int subPopNum) {
        LinkedList<GPNode> terminals = new LinkedList<GPNode>();

		BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	JobShopAttribute a = JobShopAttribute.get(line);
                GPNode attribute = new AttributeGPNode(a);
                terminals.add(attribute);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.terminals[subPopNum] = terminals.toArray(new GPNode[0]);
    }

	/**
	 * Return the index of an attribute in the terminal set.
	 * @param attribute the attribute.
	 * @return the index of the attribute in the terminal set.
	 */
	public int indexOfAttribute(JobShopAttribute attribute, int subPopNum) {
	    GPNode[] terminals = this.terminals[subPopNum];
		for (int i = 0; i < terminals.length; i++) {
			JobShopAttribute terminalAttribute = ((AttributeGPNode)terminals[i]).getJobShopAttribute();
			if (terminalAttribute == attribute) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Randomly pick a terminal from the terminal set.
	 * @return the selected terminal, which is a GPNode.
	 */
	//fzhang  random pick terminals will come to here
	public GPNode pickTerminalRandom(int subPopNum) {
    	int index = random[0].nextInt(terminals[subPopNum].length);
    	return terminals[subPopNum][index];
    }

    //fzhang 2019.5.27 another pickTerminalRandom with different parameters
	public GPNode pickTerminalRandom(EvolutionState state, int subPopNum) {
		int index = state.random[0].nextInt(terminals[subPopNum].length);
		return terminals[subPopNum][index];
	}

	// the best individual in subpopulation
	public Individual bestIndi(int subpop) {
		int best = 0;
		for(int x = 1; x < population.subpops[subpop].individuals.length; x++)
			if (population.subpops[subpop].individuals[x].fitness.betterThan(
			        population.subpops[subpop].individuals[best].fitness))
				best = x;

		return population.subpops[subpop].individuals[best];
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		Parameter p;
		//fzhang 2018.11.8 I need to do this to be able to load seed values in the AbtractRule class.
		AbstractRuleHelper.state = this;
		String filePath = this.parameters.getString(new Parameter("filePath"), null);

		// Get the job seed.
		p = new Parameter("seed").push(""+0);
		jobSeed = parameters.getLongWithDefault(p, null, 0);

//		phenoCharacterisation = new PhenoCharacterisation[2];
//		//this is the baseline PhenoCharacterisation with baseline rule "SPT" "WIQ", it will be set again by the best rule, so it is useful here
//		if (filePath == null) {
//			//dynamic simulation
//			phenoCharacterisation[0] =
//					SequencingPhenoCharacterisation.defaultPhenoCharacterisation();
////                        SequencingPhenoCharacterisation.currentGenPhenoCharacterisation(this,(RuleOptimizationProblem) evaluator.p_problem);
//			phenoCharacterisation[1] =
//					RoutingPhenoCharacterisation.defaultPhenoCharacterisation();
////                    RoutingPhenoCharacterisation.currentGenPhenoCharacterisation(this,(RuleOptimizationProblem) evaluator.p_problem);
//		} else {
//			//static simulation
//			phenoCharacterisation[0] =
//					SequencingPhenoCharacterisation.defaultPhenoCharacterisation(filePath);
//			phenoCharacterisation[1] =
//					RoutingPhenoCharacterisation.defaultPhenoCharacterisation(filePath);
//		}

		setupTerminals();

		super.setup(this, base);
	}

	@Override
	public void run(int condition)
    {
		double totalTime = 0;

		if (condition == C_STARTED_FRESH) {
			startFresh();
        }
		else {
			startFromCheckpoint();
        }

		int result = R_NOTDONE; //2, means not finished, continue to do
		while ( result == R_NOTDONE )
        {
			//fzhang 21.7.2018  after startFresh(), we will in this loop
			//here, to reFresh breeder only
			//startFreshResetOperatorProb();

			long start = System.currentTimeMillis();//yimei.util.Timer.getCpuTime();

			result = evolve();//System.out.println(result);

			long finish =System.currentTimeMillis();// yimei.util.Timer.getCpuTime();
			double duration = (finish - start) / 1000;//000000;
			genTimes.add(duration);
			totalTime += duration;

			output.message("Generation " + (generation-1) + " elapsed " + duration + " seconds.");// time used for each generation
        }

		output.message("The whole program elapsed " + totalTime + " seconds."); // time used for total program

		File timeFile = new File(out_dir +"/job." + jobSeed + ".time.csv"); //jobSeed = 0
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(timeFile));
			writer.write("Gen,Time");
			writer.newLine();
			for (int gen = 0; gen < genTimes.size(); gen++) {
				writer.write(gen + "," + genTimes.get(gen));
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		finish(result);
    }


    @Override
	public int evolve() {
	    if (generation > 0)
	        output.message("Generation " + generation);

		//From LUYAO
		//After evaluate all individuals, we record feature information about 5 elites
        if(population.subpops.length > 1){
            //means CCGP
            double[][] terminalOccurance = new double[population.subpops.length][];
            int numElites = this.parameters.getInt(new Parameter("breed.elite.0"), null, 5);


            for (int sub = 0; sub < population.subpops.length; sub++) {

                terminalOccurance[sub] = new double[terminals[sub].length];

                //First, find the elites in each population
                int[] orderedPop = new int[population.subpops[sub].individuals.length];
                for (int x = 0; x < population.subpops[sub].individuals.length; x++)
                    orderedPop[x] = x;
                // sort the best so far where "<" means "not as fit as"
                QuickSort.qsort(orderedPop, new EliteComparator(population.subpops[sub].individuals));
                // load the top N individuals

                Individual[] oldinds = population.subpops[sub].individuals; //has values
                for (int x = population.subpops[sub].individuals.length - numElites; x < population.subpops[sub].individuals.length; x++) {//start from 510, because numElites(state,sub)
                    Individual elite = (Individual) (oldinds[orderedPop[x]].clone());

                    String rule = elite.genotypeToString();
                    for (int f = 0; f < terminals[sub].length; f++) {
                        terminalOccurance[sub][f] += countSubstring(rule, terminals[sub][f].toString());
                    }
                }
            }

            seqFeatureOccurrences.add(terminalOccurance[0]);
            rouFeatureOccurrences.add(terminalOccurance[1]);

            if(population.subpops.length == 3){
                ordFeatureOccurrences.add(terminalOccurance[2]);
            }

        }
		else{
            //means MTGP
            int numTrees = parameters.getInt(new Parameter("pop.subpop.0.species.ind.numtrees"), null, 3);


            int numElites = this.parameters.getInt(new Parameter("breed.elite.0"), null, 5);
			double[][][] terminalOccurance = new double[numTrees][numElites][]; //r/s ;1-10; terms
			double[][] terminalOccurance0 = new double[numTrees][]; //r/s ; terms
			double[][] terminalfre = new double[numTrees][]; //
            for (int t = 0; t < numTrees; t++) {
				//LIU FEIGE
				if(t>=terminals.length){
					terminalOccurance[t] = new double[numElites][terminals[terminals.length-1].length];
					terminalfre[t] = new double[terminals[terminals.length-1].length];
					terminalOccurance0[t]= new double[terminals[terminals.length-1].length];

				}else
                terminalOccurance[t] = new double[numElites][terminals[t].length];
				terminalfre[t] = new double[terminals[terminals.length-1].length];
				terminalOccurance0[t]= new double[terminals[terminals.length-1].length];

			}

            for (int sub = 0; sub < population.subpops.length; sub++) {

                //First, find the elites in each population
                int[] orderedPop = new int[population.subpops[sub].individuals.length];
                for (int x = 0; x < population.subpops[sub].individuals.length; x++)
                    orderedPop[x] = x;
                // sort the best so far where "<" means "not as fit as"

//                QuickSort.qsort(orderedPop,new EliteComparator(population.subpops[sub].individuals));
                // load the top N individuals
                Individual[] oldinds = population.subpops[sub].individuals; //has values
				for (int i = 0; i < numElites; i++) {
					for (int j = 0; j < population.subpops.length - i - 1; j++) {

						Individual ind1 = oldinds[orderedPop[j]];
						Individual ind2 = oldinds[orderedPop[j + 1]];

						// 如果 ind1 比 ind2 更好，则交换
						if (ind1.fitness.betterThan(ind2.fitness)) {
							int tmp = orderedPop[j];
							orderedPop[j] = orderedPop[j + 1];
							orderedPop[j + 1] = tmp;
						}
					}
				}
				int x1=0;
                for (int x = population.subpops[sub].individuals.length - numElites; x < population.subpops[sub].individuals.length; x++) {//start from 510, because numElites(state,sub)
                    Individual elite = (Individual) (oldinds[orderedPop[x]].clone());
                    for (int t = 0; t < ((GPIndividual) elite).trees.length; t++) {  //rou or seq
                        String rule = findTreeString(elite.genotypeToString(), t);
						int tt=t;
						if (tt>=terminals.length) tt=t-1;
                        for (int f = 0; f < terminals[tt].length; f++) { //terminal id
                            terminalOccurance[t][x1][f] += countSubstring(rule, terminals[tt][f].toString());
							terminalOccurance0[t][f] += countSubstring(rule, terminals[tt][f].toString());
                        }
                    }
					x1++;
                }
            }
			for (int i = 0; i < terminalOccurance.length; i++) {
				for (int j = 0; j < terminalOccurance[i].length; j++) {
					double totalOfIndj = 0;
					for (int k = 0; k < terminalOccurance[i][j].length; k++) {
						totalOfIndj+=terminalOccurance[i][j][k];
					}
					for (int k = 0; k < terminalOccurance[i][j].length; k++) {
						terminalOccurance[i][j][k]=terminalOccurance[i][j][k]/totalOfIndj;
					}
				}
				for (int j = 0; j < terminalOccurance[i].length; j++) {
					for (int k = 0; k < terminalOccurance[0][0].length; k++) {
						terminalfre[i][k]+=terminalOccurance[i][j][k];
					}
				}
			}
            seqFeatureOccurrences.add(terminalfre[0]);
            if(numTrees == 2){
            rouFeatureOccurrences.add(terminalfre[1]);
            }
            if (numTrees == 3) {
                ordFeatureOccurrences.add(terminalfre[2]);
            }
        }

		// SHOULD WE QUIT?
		if (generation == numGenerations - 1) {
//			if (generation == 3 - 1) {

//			writeDiversityToFile(entropyDiversity);
//            writeSuccessfulCrossoverRatioToFile(improvedCrossoverRatio);
            writeTerimalOccuranceToFile(seqFeatureOccurrences, 0, "seq");
            writeTerimalOccuranceToFile(rouFeatureOccurrences, 1, "rou");
//            if(ordFeatureOccurrences.size() >= 1)
//                writeTerimalOccuranceToFile(ordFeatureOccurrences, 2, "ord");
			generation++; // in this way, the last generation value will be printed properly.  fzhang 28.3.2018
			return R_FAILURE;
		}

	    //System.out.println("generation "+generation);
	    // EVALUATION
	    statistics.preEvaluationStatistics(this); //

	    evaluator.evaluatePopulation(this);  //// here, after this we evaluate the population
  	    statistics.postEvaluationStatistics(this);

	    // SHOULD WE QUIT?
	    if (evaluator.runComplete(this) && quitOnRunComplete)
	        {
	        output.message("Found Ideal Individual");
	        return R_SUCCESS;
	        }
	    // SHOULD WE QUIT?
	    if (generation == numGenerations-1)
	        {
	    	generation++; // in this way, the last generation value will be printed properly.  fzhang 28.3.2018
	        return R_FAILURE;
	        }

	    // PRE-BREEDING EXCHANGING
	    statistics.prePreBreedingExchangeStatistics(this);
	    population = exchanger.preBreedingExchangePopulation(this);  /** Simply returns state.population. */
	    statistics.postPreBreedingExchangeStatistics(this);

	    String exchangerWantsToShutdown = exchanger.runComplete(this);  /** Always returns null */
	    if (exchangerWantsToShutdown!=null)
	        {
	        output.message(exchangerWantsToShutdown);
	        /*
	         * Don't really know what to return here.  The only place I could
	         * find where runComplete ever returns non-null is
	         * IslandExchange.  However, that can return non-null whether or
	         * not the ideal individual was found (for example, if there was
	         * a communication error with the server).
	         *
	         * Since the original version of this code didn't care, and the
	         * result was initialized to R_SUCCESS before the while loop, I'm
	         * just going to return R_SUCCESS here.
	         */

	        return R_SUCCESS;
	        }

	    // BREEDING
	    statistics.preBreedingStatistics(this);

	    population = breeder.breedPopulation(this); //!!!!!!   return newpop;  if it is NSGA-II, the population here is 2N

	    // POST-BREEDING EXCHANGING
	    statistics.postBreedingStatistics(this);   //position 1  here, a new pop has been generated.

	    // POST-BREEDING EXCHANGING
	    statistics.prePostBreedingExchangeStatistics(this);
	    population = exchanger.postBreedingExchangePopulation(this);   /** Simply returns state.population. */
	    statistics.postPostBreedingExchangeStatistics(this);  //position 2

	    // Generate new instances if needed
		RuleOptimizationProblem problem = (RuleOptimizationProblem)evaluator.p_problem;
	    if (problem.getEvaluationModel().isRotatable()) {
			problem.rotateEvaluationModel();
		}

	    // INCREMENT GENERATION AND CHECKPOINT
	    generation++;
	    if (checkpoint && generation%checkpointModulo == 0)
	        {
	        output.message("Checkpointing");
	        statistics.preCheckpointStatistics(this);
	        Checkpoint.setCheckpoint(this);
	        statistics.postCheckpointStatistics(this);
	        }

	    return R_NOTDONE;
	}

	//FEIGE LIU
	static class EliteComparator implements SortComparatorL {
		Individual[] inds;

		public EliteComparator(Individual[] inds) {
			super();
			this.inds = inds;
		}

		public boolean lt(long a, long b) {
			return inds[(int) b].fitness.betterThan(inds[(int) a].fitness);
		}

		public boolean gt(long a, long b) {
			return inds[(int) a].fitness.betterThan(inds[(int) b].fitness);
		}
	}

	// From LUYAO
	public String findTreeString(String tree, int f) {
		String start = "T" + String.valueOf(f) + ":";
		int startIndex = tree.indexOf(start);

		int endIndex;

		if (f == 2) {
			endIndex = tree.length();
		} else {
			String end = "T" + String.valueOf(f + 1);
			endIndex = tree.indexOf(end);
			if(endIndex == -1)
				endIndex = tree.length();
		}

		if (startIndex != -1 && endIndex != -1) {
			return tree.substring(startIndex + start.length(), endIndex);
		} else {
			return "";
		}
	}

	//From LUYAO
	//calculate the occurrance of one terminal
	public static int countSubstring(String rule, String terminal) {
		int count = 0;
		int index = 0;

		while ((index = rule.indexOf(terminal, index)) != -1) {
			count++;
			index += terminal.length();
		}

		if (terminal.equals("W")) {
			return count
					- countSubstring(rule, "WIQ")
					- countSubstring(rule, "WKR")
					- countSubstring(rule, "MWT")
					- countSubstring(rule, "OWT")
					- countSubstring(rule, "AWIS")
					- countSubstring(rule, "MWIS")
					- countSubstring(rule, "BWKR")
					- countSubstring(rule, "AMWTF")
					- countSubstring(rule, "AWIQF")
					- countSubstring(rule, "AWIQ");


		} else if (terminal.equals("PT")) {
			return count
					- countSubstring(rule, "NPT")
					- countSubstring(rule, "TPT")
					- countSubstring(rule, "BPT")
					- countSubstring(rule, "SPTE")
					- countSubstring(rule, "SPTS")
					- countSubstring(rule, "APTF");
		} else if (terminal.equals("R")) {
			return count
					- countSubstring(rule, "WKR")
					- countSubstring(rule, "NOR")
					- countSubstring(rule, "RDD")
					- countSubstring(rule, "BNOR")
					- countSubstring(rule, "BWKR");
		} else if (terminal.equals("WKR")) {
			return count
					- countSubstring(rule, "BWKR")
			- countSubstring(rule, "BWKR");  // 防止 BWKR 统计两次 WKR
		} else if (terminal.equals("NOR")) {
			return count
					- countSubstring(rule, "BNOR");  // 防止 BNOR 统计两次 NOR
		} else if (terminal.equals("MWT")) {
			return count
					- countSubstring(rule, "MAXMWT")
					- countSubstring(rule, "MINMWT")
					- countSubstring(rule, "AMWT");  // 防止 BWKR 统计两次 WKR
		} else if (terminal.equals("WIQ")) {
			return count
					- countSubstring(rule, "MAXWIQ")
					- countSubstring(rule, "AWIQ")
					- countSubstring(rule, "MINWIQ");  // 防止 BNOR 统计两次 NOR
		} else if (terminal.equals("NIQ")) {
			return count
					- countSubstring(rule, "MAXNIQ")
					- countSubstring(rule, "MINNIQ")
					- countSubstring(rule, "ANIQ");  // 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("MBT")) {
			return count
					- countSubstring(rule, "MAXMBT")
					- countSubstring(rule, "MINMBT")
					- countSubstring(rule, "AMBT");  // 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("MS")) {
			return count
					- countSubstring(rule, "MSS");
		}else if (terminal.equals("BPT")) {
			return count
					- countSubstring(rule, "BPTS");// 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("SSPT")) {
			return count
					- countSubstring(rule, "SSPTS");// 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("SEPT")) {
			return count
					- countSubstring(rule, "SEPTS");// 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("ES")) {
			return count
					- countSubstring(rule, "TES");  // 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("EB")) {
			return count
					- countSubstring(rule, "TEB");  // 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("DSTS")) {
			return count
					- countSubstring(rule, "ADSTS");  // 防止 BNOR 统计两次 NOR
		}else if (terminal.equals("DSTE")) {
			return count
					- countSubstring(rule, "ADSTE");  // 防止 BNOR 统计两次 NOR
		}else {
			return count;
		}
	}

	public static int countSubstringOfTugboat(String rule, String terminal) {
		int count = 0;
		int index = 0;

		while ((index = rule.indexOf(terminal, index)) != -1) {
			count++;
			index += terminal.length();
		}

		if (terminal.equals("W")) {
			return count
					- countSubstring(rule, "WIQ")
					- countSubstring(rule, "WKR")
					- countSubstring(rule, "MWT")
					- countSubstring(rule, "OWT")
					- countSubstring(rule, "AWIS")
					- countSubstring(rule, "MWIS")
					- countSubstring(rule, "BWKR")
					- countSubstring(rule, "AMWTF")
					- countSubstring(rule, "AWIQF");
		} else if (terminal.equals("PT")) { //
			return count
					- countSubstring(rule, "NPT")
					- countSubstring(rule, "TPT")
					- countSubstring(rule, "APTF");
		} else if (terminal.equals("R")) {
			return count
					- countSubstring(rule, "WKR")
					- countSubstring(rule, "NOR")
					- countSubstring(rule, "RDD")
					- countSubstring(rule, "BNOR")
					- countSubstring(rule, "BWKR");
		} else if (terminal.equals("WKR")) { //
			return count
					- countSubstring(rule, "BWKR");  // 防止 BWKR 统计两次 WKR
		} else if (terminal.equals("NOR")) {
			return count
					- countSubstring(rule, "BNOR");  // 防止 BNOR 统计两次 NOR
		} else if (terminal.equals("MWT")) { //
			return count
					- countSubstring(rule, "AMWTF");  // 防止 BWKR 统计两次 WKR
		} else if (terminal.equals("WIQ")) { //
			return count
					- countSubstring(rule, "AWIQF");  // 防止 BNOR 统计两次 NOR
		} else if (terminal.equals("NIQ")) { //
			return count
					- countSubstring(rule, "ANIQF");  // 防止 BNOR 统计两次 NOR
		}
		else {
			return count;
		}
	}

	//LIUFEIGE
	public void writeTerimalOccuranceToFile(ArrayList<double[]> featureOccurance, int t, String s) {
		//fzhang 2019.5.21 save the number of cleared individuals
//		File weightFile = new File( "/job." + jobSeed + "." + s + "FeatureOccurance.csv"); // jobSeed = 0
		File weightFile = new File(out_dir + "/job." + jobSeed + "." + s + "FeatureOccurance.csv"); // jobSeed = 0
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(weightFile));

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < terminals[0].length; i++) {
				sb.append(terminals[0][i].toStringForHumans());
				if (i < terminals[0].length - 1) {
					sb.append(","); // 添加逗号分隔符
				}
			}

			writer.write("Gen" + "," + sb.toString());
			writer.newLine();
			for (int i = 0; i < featureOccurance.size(); i++) { //every two into one generation
				//writer.newLine();
				String[] stringArray = Arrays.stream(featureOccurance.get(i))
						.mapToObj(String::valueOf)
						.toArray(String[]::new);

				String content = String.join(",", stringArray);

				writer.write(i + "," + content + "\n");
			}

			featureOccurance.clear();
//			writer.write(numGenerations -1 + ", " + 0 + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupTerminals() {
        Parameter p;

        //Need to know how many populations we're expecting here, as will need
        //one terminal set per population
        int numSubPops = parameters.getInt(new Parameter("pop.subpops"),null);

        if (numSubPops == 1) {
            p = new Parameter(P_TERMINALS_FROM);

//            terminalsFrom = new String[]{parameters.getStringWithDefault(p,
//                    null, "relative")};
			//Liu Feige 2025.10 another terminalsFrom
//			terminalsFrom = new String[]{parameters.getStringWithDefault(p,
//					null, "relativeForTugboat")}; //for 1
			terminalsFrom = new String[]{parameters.getStringWithDefault(p,
					null, "relativeForTugboatSet")}; //for 2

            p = new Parameter(P_INCLUDE_ERC);
            //includeErc seems like does not have influence.
            includeErc = new boolean[]{parameters.getBoolean(p, null, false)};
            initTerminalSet();
        } else if (numSubPops == 2) {
            terminalsFrom = new String[numSubPops];
            includeErc = new boolean[numSubPops];
            int subPopNum = 0;

            p = new Parameter(P_TERMINALS_FROM + "." + subPopNum);
            String subPop1TerminalSet = parameters.getStringWithDefault(p,
                    null, null);
            if (subPop1TerminalSet == null) {
                //might have provided other value by mistake, we should check for this
                p = new Parameter(P_TERMINALS_FROM);
                subPop1TerminalSet = parameters.getStringWithDefault(p,
                        null, "relative");
                output.warning("No terminal set for subpopulation 1 specified - using "+subPop1TerminalSet+".");

            }
            terminalsFrom[subPopNum] = subPop1TerminalSet;

            subPopNum++;
            p = new Parameter(P_TERMINALS_FROM + "." + subPopNum);
            String subPop2TerminalSet = parameters.getStringWithDefault(p,
                    null, null);
            if (subPop2TerminalSet == null) {
                //use whatever we settled on for first population
                subPop2TerminalSet = subPop1TerminalSet;
                output.warning("No terminal set for subpopulation 2 specified - using terminal set for subpopulation 1.");
            }
            terminalsFrom[subPopNum] = subPop2TerminalSet;
            //TODO: Add support for erc - will be false by default

            initTerminalSet(); //right
        }
    }
}
