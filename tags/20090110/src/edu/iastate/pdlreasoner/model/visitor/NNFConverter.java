package edu.iastate.pdlreasoner.model.visitor;

import edu.iastate.pdlreasoner.model.AllValues;
import edu.iastate.pdlreasoner.model.And;
import edu.iastate.pdlreasoner.model.Atom;
import edu.iastate.pdlreasoner.model.Bottom;
import edu.iastate.pdlreasoner.model.Concept;
import edu.iastate.pdlreasoner.model.DLPackage;
import edu.iastate.pdlreasoner.model.ModelFactory;
import edu.iastate.pdlreasoner.model.Negation;
import edu.iastate.pdlreasoner.model.Or;
import edu.iastate.pdlreasoner.model.Role;
import edu.iastate.pdlreasoner.model.SetOp;
import edu.iastate.pdlreasoner.model.SomeValues;
import edu.iastate.pdlreasoner.model.Top;

public class NNFConverter {
	
	private final DLPackage m_NNFContext;
	
	public NNFConverter(DLPackage context) {
		m_NNFContext = context;
	}

	public Concept convert(Concept c) {
		return new NNFConverterImpl().convert(c);
	}
	
	private class NNFConverterImpl implements ConceptVisitor {

		private Concept m_Result;
		
		public Concept convert(Concept c) {
			c.accept(this);
			return m_Result;
		}
		
		@Override
		public void visit(Bottom bottom) {
			m_Result = bottom;
		}

		@Override
		public void visit(Top top) {
			m_Result = top;
		}

		@Override
		public void visit(Atom atom) {
			m_Result = atom;
		}

		@Override
		public void visit(Negation negation) {
			DLPackage negationContext = negation.getContext();
			Concept negatedConcept = negation.getNegatedConcept();
			m_Result = new NegationPropagator(negationContext).convert(negatedConcept);
		}

		private Concept[] visitSetOp(SetOp set) {
			Concept[] operands = set.getOperands();
			Concept[] nnfOperands = new Concept[operands.length];
			for (int i = 0; i < operands.length; i++) {
				operands[i].accept(this);
				nnfOperands[i] = m_Result;
			}
			return nnfOperands;
		}
		
		@Override
		public void visit(And and) {
			Concept[] nnfOperands = visitSetOp(and);
			m_Result = ModelFactory.makeAnd(nnfOperands);
		}

		@Override
		public void visit(Or or) {
			Concept[] nnfOperands = visitSetOp(or);
			m_Result = ModelFactory.makeOr(nnfOperands);
		}
		
		@Override
		public void visit(SomeValues someValues) {
			Role role = someValues.getRole();
			someValues.getFiller().accept(this);
			m_Result = ModelFactory.makeSomeValues(role, m_Result);
		}

		@Override
		public void visit(AllValues allValues) {
			Role role = allValues.getRole();
			allValues.getFiller().accept(this);
			m_Result = ModelFactory.makeAllValues(role, m_Result);
		}
		
	}
	
	private class NegationPropagator implements ConceptVisitor {

		private final DLPackage m_NegationContext;
		private final Top m_NegationContextTop;
		private Concept m_Result;
		
		public NegationPropagator(DLPackage negationContext) {
			m_NegationContext = negationContext;
			m_NegationContextTop = ModelFactory.makeTop(m_NegationContext);
		}
		
		public Concept convert(Concept c) {
			c.accept(this);
			return m_Result;
		}
		
		private void makeAndWithNegationContextTop() {
			if (!m_NegationContext.equals(m_NNFContext)) {
				m_Result = ModelFactory.makeAnd(m_NegationContextTop, m_Result);				
			}
		}

		@Override
		public void visit(Bottom bottom) {
			m_Result = m_NegationContextTop;
		}

		private void visitAtomOrTop(Concept c) {
			m_Result = ModelFactory.makeNegation(m_NNFContext, c);
			makeAndWithNegationContextTop();
		}
		
		@Override
		public void visit(Top top) {
			DLPackage topContext = top.getContext();
			if (topContext.equals(m_NegationContext)) {
				m_Result = Bottom.INSTANCE;
			} else {
				visitAtomOrTop(top);				
			}
		}

		@Override
		public void visit(Atom atom) {
			visitAtomOrTop(atom);
		}

		@Override
		public void visit(Negation negation) {
			DLPackage innerNegationContext = negation.getContext();
			Concept negatedConcept = negation.getNegatedConcept();
			
			m_Result = new NNFConverterImpl().convert(negatedConcept);
			
			if (!innerNegationContext.equals(m_NegationContext)) {
				Negation negatedTop = ModelFactory.makeNegation(m_NegationContext, ModelFactory.makeTop(innerNegationContext));
				m_Result = ModelFactory.makeOr(m_Result, negatedTop);
			}
			
			makeAndWithNegationContextTop();
		}

		private Concept[] visitSetOp(SetOp set) {
			Concept[] operands = set.getOperands();
			Concept[] negatedOperands = new Concept[operands.length];
			for (int i = 0; i < operands.length; i++) {
				operands[i].accept(this);
				negatedOperands[i] = m_Result;
			}
			return negatedOperands;
		}
		
		@Override
		public void visit(And and) {
			Concept[] negatedOperands = visitSetOp(and);
			m_Result = ModelFactory.makeOr(negatedOperands);
		}

		@Override
		public void visit(Or or) {
			Concept[] negatedOperands = visitSetOp(or);
			m_Result = ModelFactory.makeAnd(negatedOperands);
		}

		@Override
		public void visit(SomeValues someValues) {
			Role role = someValues.getRole();
			Concept filler = someValues.getFiller();
			Concept negatedFiller = new NegationPropagator(m_NNFContext).convert(filler);
			m_Result = ModelFactory.makeAllValues(role, negatedFiller);
			makeAndWithNegationContextTop();
		}

		@Override
		public void visit(AllValues allValues) {
			Role role = allValues.getRole();
			Concept filler = allValues.getFiller();
			Concept negatedFiller = new NegationPropagator(m_NNFContext).convert(filler);
			m_Result = ModelFactory.makeSomeValues(role, negatedFiller);
			makeAndWithNegationContextTop();
		}
		
	}
	
}
