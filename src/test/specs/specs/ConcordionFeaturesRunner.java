package specs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.ResultSummary;
import org.concordion.internal.FixtureRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

public class ConcordionFeaturesRunner extends FeaturesRunner {

    private Description fixtureDescription;
    private FakeFrameworkMethod fakeMethod;
    private ResultSummary result;



    public ConcordionFeaturesRunner(Class<?> classToRun) throws InitializationError {
        super(classToRun);
        String testDescription = ("[Concordion Specification for '" + classToRun.getSimpleName()).replaceAll("Test$", "']"); // Based on suggestion by Danny Guerrier
        fixtureDescription = Description.createTestDescription(classToRun, testDescription);
        try {
            fakeMethod = new FakeFrameworkMethod(getClass().getMethod("fakeMethod", (Class<?>[]) null));
        } catch (Exception e) {
            throw new InitializationError("Failed to initialize ConcordionRunner");
        }
    }

    public void fakeMethod() {
    }

    static class FakeFrameworkMethod extends FrameworkMethod {

        public FakeFrameworkMethod(Method method) {
            super(method);
        }

        public String getName() {
            return "[Concordion Specification]";
        }

        public Annotation[] getAnnotations() {
            return new Annotation[0];
        }

        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return null;
        }

        public int hashCode() {
            return 1;
        }
    }


    @Override
    protected List<FrameworkMethod> getChildren() {
        List<FrameworkMethod> children = new ArrayList<FrameworkMethod>();
        children.addAll(super.getChildren());
        children.add(fakeMethod);
        return children;
    }

    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        if (method == fakeMethod) {
            return specExecStatement(test);
        }
        return super.methodInvoker(method, test);
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (method == fakeMethod) {
            return fixtureDescription;
        }
        return super.describeChild(method);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
        if (result != null && result.getIgnoredCount() > 0) {
            notifier.fireTestIgnored(fixtureDescription);
        }
    }

    protected Statement specExecStatement(final Object fixture) {
        return new Statement() {
            public void evaluate() throws Throwable {
                beforeMethodRun(fakeMethod, fixture);
                try {

                    result = new FixtureRunner().run(fixture);
                } finally {
                    afterMethodRun(fakeMethod, fixture);
                }
            }
        };
    }

    @Override
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);
    }

}
