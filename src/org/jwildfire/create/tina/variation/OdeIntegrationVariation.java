package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * A generic ODE solver variation.
 * Supports custom user-defined equations via Janino compilation.
 */
public class OdeIntegrationVariation extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // Parameters
    private double dt = 0.01;
    private double a = 10.0;
    private double b = 28.0;
    private double c = 8.0 / 3.0;
    private double d = 0.0;
    private double e = 0.0;
    private double f = 0.0;
    
    // 0=Lorenz, 1=Rossler, 2=Aizawa, 3=Custom
    private int type = 0; 

    private static final String[] PARAM_NAMES = { "dt", "a", "b", "c", "d", "e", "f", "type" };
    private static final String[] RES_NAMES = { "code" };

    private String code = "";
    private transient OdeIntegrationRunner runner;
    private transient boolean runnerFailed = false;

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        if (type == 3) { // Custom
            if (runner == null && !runnerFailed && code != null && !code.isEmpty()) {
                try {
                    runner = OdeIntegrationRunner.compile(code);
                } catch (Exception ex) {
                    runnerFailed = true;
                    System.err.println("Failed to compile ODE script: " + ex.getMessage());
                }
            }
            
            if (runner != null) {
                runner.setParams(dt, a, b, c, d, e, f);
                runner.transform(pContext, pXForm, pAffineTP, pVarTP, pAmount);
                return;
            }
        }

        // Built-in solvers
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
            // Using standard Aizawa params if not provided: a=0.95, b=0.7, c=0.6, d=3.5, e=0.25, f=0.1
            // Mapping: a->a, b->b, c->c, d->d, e->e, f->f
            dx = (z - b) * x - d * y;
            dy = d * x + (z - b) * y;
            dz = c + a * z - (z * z * z) / 3.0 - (x * x + y * y) * (1.0 + e * z) + f * z * x * x * x;
        }

        // Euler integration
        pVarTP.x += dx * dt * pAmount;
        pVarTP.y += dy * dt * pAmount;
        pVarTP.z += dz * dt * pAmount;
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
        return new Object[] { dt, a, b, c, d, e, f, type };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (pName.equalsIgnoreCase("dt")) dt = pValue;
        else if (pName.equalsIgnoreCase("a")) a = pValue;
        else if (pName.equalsIgnoreCase("b")) b = pValue;
        else if (pName.equalsIgnoreCase("c")) c = pValue;
        else if (pName.equalsIgnoreCase("d")) d = pValue;
        else if (pName.equalsIgnoreCase("e")) e = pValue;
        else if (pName.equalsIgnoreCase("f")) f = pValue;
        else if (pName.equalsIgnoreCase("type")) type = (int) pValue;
    }

    @Override
    public String[] getRessourceNames() {
        return RES_NAMES;
    }

    @Override
    public byte[][] getRessourceValues() {
        return new byte[][] { (code != null ? code.getBytes() : new byte[0]) };
    }

    @Override
    public void setRessource(String pName, byte[] pValue) {
        if (RES_NAMES[0].equalsIgnoreCase(pName)) {
            code = new String(pValue);
            runner = null; // Force recompile
            runnerFailed = false;
        }
    }

    @Override
    public RessourceType getRessourceType(String pName) {
        return RessourceType.JAVA_CODE;
    }
}
