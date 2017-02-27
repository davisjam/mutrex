package regex.mutrex;

import java.util.Collections;
import java.util.Iterator;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import regex.distinguishing.DSgenPolicy;
import regex.distinguishing.DistStringCreator;
import regex.distinguishing.DistinguishingString;
import regex.operators.RegexMutator.MutatedRegExp;

/**
 * generates a ds for each mutation and then it collects the ds and keeps track
 * if one kills many
 * 
 * @author garganti
 *
 */
public class MonitoringDSSetgenerator extends DSSetGenerator {
	public static DSSetGenerator generator = new MonitoringDSSetgenerator();

	protected MonitoringDSSetgenerator() {
	}

	@Override
	public void addStringsToDSSet(DSSet result, RegExp regex, Iterator<MutatedRegExp> mutants) {
		Automaton regexAut = regex.toAutomaton();
		// all the regexes that are accepted by regex
mutLoop: while (mutants.hasNext()) {
			RegExp mutant = mutants.next().mutatedRexExp;
			// monitoring
			Automaton mutAutom = mutant.toAutomaton();
			// check if there exists a ds in results that covers this mutant
			for (DistinguishingString ds : result) {
				String dsStr = ds.getDs();
				assert ds.isConfirming() == regexAut.run(dsStr);
				boolean acceptedByMut = mutAutom.run(dsStr);
				if (ds.isConfirming() != acceptedByMut) {
					result.add(ds, Collections.singletonList(mutant));
					// ds also distinguishes r and mutant
					continue mutLoop;
				}
			}
			// generation
			DistinguishingString ds = DistStringCreator.getDS(regexAut, mutAutom, DSgenPolicy.RANDOM);
			if (ds != null) {
				assert ds.isConfirming() == regexAut.run(ds.getDs());
				assert ds.isConfirming() != mutAutom.run(ds.getDs());
				result.add(ds, Collections.singletonList(mutant));
			}
		}
	}

	/*@Override
	public void addStringsToDSSet(DSSet result, RegExp regex, Iterator<MutatedRegExp> mutants) {
		Automaton regexAut = regex.toAutomaton();
		// all the regexes that are accepted by regex
		Set<String> DSsAcceptedByR = new HashSet<String>();
mutLoop: while (mutants.hasNext()) {
			RegExp mutant = mutants.next().mutatedRexExp;
			// monitoring
			Automaton mutAutom = mutant.toAutomaton();
			// check if there exists a ds in results that covers this mutant
			for (DistinguishingString ds : result) {
				String dsStr = ds.getDs();
				boolean acceptedByR = DSsAcceptedByR.contains(dsStr);
				assert acceptedByR == regexAut.run(dsStr);
				assert acceptedByR == ds.isConfirming(): acceptedByR;
				//boolean acceptedByMut = BasicOperations.run(mutAutom, dsStr);
				boolean acceptedByMut = mutAutom.run(dsStr);
				if (acceptedByR != acceptedByMut) {
					result.add(ds, Collections.singletonList(mutant));
					// ds also distinguishes r and mutant
					continue mutLoop;
				}
			}
			// generation
			DistinguishingString ds = DistStringCreator.getDS(regexAut, mutAutom, DSgenPolicy.RANDOM);
			if (ds != null) {
				boolean confirming = ds.isConfirming();
				assert confirming == regexAut.run(ds.getDs());
				assert confirming != mutAutom.run(ds.getDs());
				if (confirming) {
					DSsAcceptedByR.add(ds.getDs());
				}
				result.add(ds, Collections.singletonList(mutant));
			}
		}
	}*/
}
