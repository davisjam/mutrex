package regex.mutrex.parallel.mutant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import regex.distinguishing.DSgenPolicy;
import regex.distinguishing.DistStringCreator;
import regex.mutrex.ds.DSSet;
import regex.mutrex.ds.DSSetGenerator;
import regex.mutrex.ds.DistinguishingAutomaton;
import regex.mutrex.ds.DistinguishingAutomaton.RegexWAutomata;
import regex.mutrex.parallel.DistinguishingAutomatonTh;
import regex.operators.RegexMutator;
import regex.operators.RegexMutator.MutatedRegExp;

public class MutantParallelCollectDSSetGenerator extends DSSetGenerator {
	private static Logger logger = Logger.getLogger(MutantParallelCollectDSSetGenerator.class.getName());
	int numRunningMutants = 0;

	public synchronized Mutant getMutant(Iterator<MutatedRegExp> mutants) {
		while (mutants.hasNext()) {
			if (numRunningMutants >= Runtime.getRuntime().availableProcessors()) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			} else {
				numRunningMutants++;
				return mutants.next();
			}
		}
		return null;
	}

	@Override
	public void addStringsToDSSet(DSSet dsS, RegExp regex, Iterator<MutatedRegExp> mutants) {
		
		RegexWAutomata regexWithAutomata = new RegexWAutomata(regex);
		DasManager dasManager = new DasManager();
		Mutant mutant = null;
loopMut:while ((mutant = getMutant(mutants)) != null) {
			
		}
	}
}

class MutTh extends Thread {
	Mutant mutant;
	DasManager dasManager;

	public MutTh(Mutant mutant, DasManager dasManager) {
		this.mutant = mutant;
		this.dasManager = dasManager;
	}
	
	@Override
	public void run() {
		List<Boolean> trueFalse = Arrays.asList(true, false);
		boolean isCovered = false;
		while(!isCovered) {
			DistinguishingAutomatonClass da = dasManager.getDA(mutant);
			if(da == null) {
				Collections.shuffle(trueFalse);
				for (boolean b : trueFalse) {
					DistinguishingAutomaton newDa = new DistinguishingAutomaton(mutant.mutant, b);
					if (newDa.add(mutant.mutant)) {
						DistinguishingAutomatonClass newDaC = new DistinguishingAutomatonClass(newDa);
						dasManager.addDA(newDaC);
						break;
					}
				}
				isCovered = true;
				mutant.isEquivalent = true;
			}
			else {
				
			}
		}
	}
	
}

class DasManager {
	Set<DistinguishingAutomatonClass> daS = new HashSet<DistinguishingAutomatonClass>();
	List<Boolean> trueFalse = Arrays.asList(true, false);

	public synchronized void addDA(DistinguishingAutomatonClass da) {
		daS.add(da);
	}
	
	public synchronized DistinguishingAutomatonClass getDA(Mutant mutant) {
		Set<DistinguishingAutomatonClass> checkedByMut = mutant.visited;
		for (DistinguishingAutomatonClass da : daS) {
			if (!checkedByMut.contains(da)) {
				if (!da.locked) {
					da.locked = true;
					return da;
				}
			}
		}
		return null;
	}
}

class DistinguishingAutomatonClass {
	DistinguishingAutomaton da;
	boolean locked = false;

	public DistinguishingAutomatonClass(DistinguishingAutomaton da) {
		this.da = da;
	}
}

class Mutant {
	RegexWAutomata mutant;
	Set<DistinguishingAutomatonClass> visited;
	boolean isEquivalent;

	public Mutant(MutatedRegExp mutatedRegExp) {
		this.mutant = new RegexWAutomata(mutatedRegExp.mutatedRexExp);
		visited = new HashSet<DistinguishingAutomatonClass>();
	}

	public void setVisitedDA(DistinguishingAutomatonClass da) {
		visited.add(da);
	}

	public boolean hasVisitedDA(DistinguishingAutomatonClass dat) {
		return visited.contains(dat);
	}

	public RegExp getRegex() {
		return mutant.getMutant();
	}
}