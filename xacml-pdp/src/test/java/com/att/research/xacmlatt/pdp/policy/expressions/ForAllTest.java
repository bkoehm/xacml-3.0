package com.att.research.xacmlatt.pdp.policy.expressions;

import com.att.research.xacmlatt.pdp.eval.EvaluationException;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.ExpressionResultBoolean;
import com.att.research.xacmlatt.pdp.policy.Policy;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ForAll quantified expression. See section 5.2 of the
 * <a href="https://docs.oasis-open.org/xacml/xacml-3.0-related-entities/v1.0/cs02/xacml-3.0-related-entities-v1.0-cs02.html">
 * XACML v3.0 Related and Nested Entities Profile Version 1.0</a> specification.
 *
 * @author ygrignon
 */
public class ForAllTest extends QuantifiedExpressionTest {
    @Override
    protected QuantifiedExpression newInstance(Policy policy) {
        return new ForAll(policy);
    }

    /**
     * Note that the ForAll expression evaluates to "true" if the domain is an empty bag.
     * @param result The result of the quantified expression evaluation.
     */
    @Override
    protected void assertEmptyDomainResult(ExpressionResult result) {
        assertTrue(result.isOk());
        assertEquals(Boolean.TRUE, result.getValue().getValue());
    }

    /**
     * The iterant expression of a ForAll expression SHALL be an expression that evaluates to a value of the
     * http://www.w3.org/2001/XMLSchema#boolean data-type.
     * @throws EvaluationException
     */
    @Test
    public void testIterantShallEvaluateToBoolean() throws EvaluationException {
        Policy policy = new Policy();

        // Create a quantified expression with a bag iterant
        QuantifiedExpression quantifiedExpression = newInstance(policy);
        quantifiedExpression.setVariableId("test");
        quantifiedExpression.setDomainExpression(EX_BAG_TRUE);
        quantifiedExpression.setIterantExpression(EX_BAG_TRUE);

        // Evaluate the quantified expression and make sure it fails
        ExpressionResult result = evaluate(quantifiedExpression);
        assertFalse(result.isOk());
        assertEquals(ExpressionResultBoolean.STATUS_PE_RETURNED_BAG, result.getStatus());

        // Update the iterant expression so it produces a string
        quantifiedExpression.setIterantExpression(EX_STRING);

        // Evaluate the quantified expression and make sure it still fails
        result = evaluate(quantifiedExpression);
        assertFalse(result.isOk());
        assertEquals(ExpressionResultBoolean.STATUS_PE_RETURNED_NON_BOOLEAN, result.getStatus());
    }

    /**
     * The ForAll expression evaluates to “false” if the iterant expression evaluates to “false” for any value from the domain.
     * @throws EvaluationException
     */
    @Test
    public void testFalseIfAnyFalse() throws EvaluationException {
        Policy policy = new Policy();

        // Create a quantified expression where the iterant evaluates to true for the first domain value and false for
        // the second domain value
        QuantifiedExpression quantifiedExpression = newInstance(policy);
        quantifiedExpression.setVariableId("quantifiedVariable");
        quantifiedExpression.setDomainExpression(EX_BAG_TRUE_FALSE);
        quantifiedExpression.setIterantExpression(new VariableReference(policy, "quantifiedVariable"));

        // Evaluate the quantified expression and make sure it returns false
        ExpressionResult result = evaluate(quantifiedExpression);
        assertTrue(result.getStatus().getStatusMessage(), result.isOk());
        assertEquals(Boolean.FALSE, result.getValue().getValue());
    }

    /**
     * The ForAll expression evaluates to “true” if the iterant expression evaluates to “true” for all value from the domain.
     * @throws EvaluationException
     */
    @Test
    public void testTrueIfAllTrue() throws EvaluationException {
        Policy policy = new Policy();

        // Create a quantified expression where the iterant evaluates to true for all domain values
        QuantifiedExpression quantifiedExpression = newInstance(policy);
        quantifiedExpression.setVariableId("quantifiedVariable");
        quantifiedExpression.setDomainExpression(EX_BAG_TRUE_TRUE);
        quantifiedExpression.setIterantExpression(new VariableReference(policy, "quantifiedVariable"));

        // Evaluate the quantified expression and make sure it returns true
        ExpressionResult result = evaluate(quantifiedExpression);
        assertTrue(result.getStatus().getStatusMessage(), result.isOk());
        assertEquals(Boolean.TRUE, result.getValue().getValue());
    }
}
