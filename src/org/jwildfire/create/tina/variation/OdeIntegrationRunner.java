package org.jwildfire.create.tina.variation;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.StringReader;

public class OdeIntegrationRunner {
    public static OdeIntegrationRunner compile(String pScript) throws Exception {
        return (OdeIntegrationRunner) ClassBodyEvaluator.createFastClassBodyEvaluator(
            new Scanner(null, new StringReader(pScript)), 
            OdeIntegrationRunner.class, 
            (ClassLoader) null
        );
    }

    // Parameters exposed to the script
    protected double dt = 0.01;
    protected double a = 10.0;
    protected double b = 28.0;
    protected double c = 8.0 / 3.0;
    protected double d = 0.0;
    protected double e = 0.0;
    protected double f = 0.0;

    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        // Default implementation (Lorenz)
        double x = pAffineTP.x;
        double y = pAffineTP.y;
        double z = pAffineTP.z;

        double dx = a * (y - x);
        double dy = x * (b - z) - y;
        double dz = x * y - c * z;

        pVarTP.x += dx * dt * pAmount;
        pVarTP.y += dy * dt * pAmount;
        pVarTP.z += dz * dt * pAmount;
    }

    public void setParams(double dt, double a, double b, double c, double d, double e, double f) {
        this.dt = dt;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }
}
