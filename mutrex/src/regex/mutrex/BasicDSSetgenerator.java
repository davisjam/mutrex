package regex.mutrex;

import java.util.Collections;
import java.util.Iterator;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import regex.distinguishing.DSgenPolicy;
import regex.distinguishing.DistStringCreator;
import regex.distinguishing.DistinguishingString;
import regex.mutrex.ds.DSSet;
import regex.mutrex.ds.DSSetGenerator;
import regex.operators.RegexMutator.MutatedRegExp;

/**
 * generates a ds for each mutation and then it collects the ds and keeps track
 * if one kills many
 * 
 * @author garganti
 *
 */
public class BasicDSSetgenerator extends DSSetGenerator {
	public static DSSetGenerator generator = new BasicDSSetgenerator();

	protected BasicDSSetgenerator() {
	}

	@Override
	public void addStringsToDSSet(DSSet result, RegExp regex, Iterator<MutatedRegExp> mutants) {
		Automaton regexAut = regex.toAutomaton();
		while (mutants.hasNext()) {
			MutatedRegExp mutant = mutants.next();
			// generate a distinguishing string
			// DistinguishingString ds = DistStringCreator.getDS(regex,
			// mutant.mutatedRexExp, DSgenPolicy.RANDOM);
			DistinguishingString ds = DistStringCreator.getDS(regexAut, mutant.mutatedRexExp.toAutomaton(),
					DSgenPolicy.RANDOM);
			if (ds != null) {
				result.add(ds, Collections.singletonList(mutant));
			}
		}
		return;
	}
}