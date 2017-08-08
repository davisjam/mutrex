package regex.operators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import regex.operators.RegexMutator.MutatedRegExp;
import regex.utils.IteratorUtils;

public class RegexMutationTest {

	/**
	 * accept all the strings
	 * 
	 * @param re
	 * @param ss
	 */
	protected void accept(RegExp re, String... ss) {
		Automaton automaton = re.toAutomaton();
		for (String s : ss) {
			assertTrue(s + " not accepted by " + re.toString(), automaton.run(s));
		}
	}

	protected void acceptNot(RegExp re, String... ss) {
		Automaton automaton = re.toAutomaton();
		for (String s : ss) {
			assertFalse(s + " is accepted by " + re.toString(), automaton.run(s));
		}
	}

	/**
	 * at least an automaton in m must be equal to regex
	 * 
	 * @param m
	 * @param regex
	 */
	protected void assertOneEqualTo(List<MutatedRegExp> m, String regex) {
		assertTrue("no regex in " + m + " is equal to " + regex, OneEqualTo(m, regex));
	}
	
	protected void assertOneEqualTo(Iterator<MutatedRegExp> m, String regex) {
		List<MutatedRegExp> iteratorToList = IteratorUtils.iteratorToList(m);
		assertTrue("no regex in " + iteratorToList + " is equal to " + regex, OneEqualTo(m, regex));
	}

	protected boolean OneEqualTo(Iterator<MutatedRegExp> res, String regex) {
		return OneEqualTo(IteratorUtils.iteratorToList(res),regex);
	}

	
	protected boolean OneEqualTo(List<MutatedRegExp> m, String regex) {
		Automaton a = new RegExp(regex).toAutomaton();
		for (MutatedRegExp j : m) {
			if (j.mutatedRexExp.toAutomaton().equals(a))
				return true;
		}
		return false;
	}

	protected void assertOneRegexEqualTo(List<RegExp> m, String regex) {
		Automaton a = new RegExp(regex).toAutomaton();
		for (RegExp j : m) {
			if (j.toAutomaton().equals(a))
				return;
		}
		fail("no regex in " + m + " is equal to " + regex);
	}
}
