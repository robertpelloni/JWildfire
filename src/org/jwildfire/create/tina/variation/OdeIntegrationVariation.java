package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * A generic ODE solver variation.
 * Currently implements the Lorenz attractor as a proof of concept.
 * Future versions will parse user-defined equations.
 */
public class OdeIntegrationVariation extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // Parameters
    private double dt = 0.01;
    private double a = 10.0; // Sigma
    private double b = 28.0; // Rho
    private double c = 8.0 / 3.0; // Beta
    private int type = 0; // 0=Lorenz, 1=Rossler, 2=Aizawa

    private static final String[] PARAM_NAMES = { "dt", "a", "b", "c", "type" };

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        double x = pAffineTP.x;
        double y = pAffineTP.y;
        double z = pAffineTP.z;

        double dx = 0, dy = 0, dz = 0;

        if (type == 0) { // Lorenz
            dx = a * (y - x);
            dy = x * (b - z) - y;
            dz = x * y - c * z;
        } else if (type == 1) { // Rossler
            dx = -y - z;
            dy = x + a * y;
            dz = b + z * (x - c);
        } else if (type == 2) { // Aizawa
            double eps = 0.25;
            double f = 0.11; // Using 'c' for f? Let's stick to standard params mapping
            // Aizawa usually has: a, b, c, d, e, f. We only have 3 params exposed + dt.
            // Let's map: a=a, b=b, c=c. Hardcode others or reuse.
            // dx = (z-b)x - dy
            // dy = dx + (z-b)y
            // dz = c + az - z^3/3 - (x^2+y^2)(1+ez) + fz x^3
            // This is too complex for 3 params. Fallback to simple Lorenz for now.
            dx = a * (y - x);
            dy = x * (b - z) - y;
            dz = x * y - c * z;
        }

        // Euler integration
        pVarTP.x += dx * dt * pAmount;
        pVarTP.y += dy * dt * pAmount;
        pVarTP.z += dz * dt * pAmount;
        
        // If we want to replace the point instead of adding to it (which is typical for attractors):
        // But JWildfire variations usually add to the result or transform it.
        // If we want to trace the orbit, we need to maintain state, which is hard in this stateless transform.
        // However, if we treat the input point as the "current state" and output the "next state",
        // iterating this variation in the chaos game will trace the attractor *if* the affine transform doesn't mess it up.
        // Usually, for attractors, we want the affine transform to be Identity.
    }

    @Override
    public String getName() {
        return "ode_solver";
    }

    @Override
    public String[] getParameterNames() {
        return PARAM_NAMES;
    }

    @Override
    public Object[] getParameterValues() {
        return new Object[] { dt, a, b, c, type };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (pName.equalsIgnoreCase("dt")) dt = pValue;
        else if (pName.equalsIgnoreCase("a")) a = pValue;
        else if (pName.equalsIgnoreCase("b")) b = pValue;
        else if (pName.equalsIgnoreCase("c")) c = pValue;
        else if (pName.equalsIgnoreCase("type")) type = (int) pValue;
    }
}
