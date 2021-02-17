package com.github.FrestoBar;

import com.github.FrestoBar.exceptions.GoogleActionInitException;
import com.github.FrestoBar.exceptions.NoSuchIntentException;
import com.github.FrestoBar.intent.AbstractIntent;
import com.github.FrestoBar.intent.Intent;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import org.apache.logging.log4j.core.util.IOUtils;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

public class GoogleActionFramework {

    public static final String JSON_QUERY_KEY_FOR_FINDING_INTENT = "$.queryResult.intent.displayName";
    private static Reflections scanner;

    public static final String COULD_NOT_INIT_CURRENT_INTENT = "Could not init current Intent %s";

    public static String start(InputStream inputStream, Class currentClass) throws IOException {
        String request = IOUtils.toString(new InputStreamReader(inputStream));
        return start(request, currentClass);
    }


    public static String start(String request, Class currentClass) {
        Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider()).build();

        String intent = JsonPath.using(conf).parse(request).read(JSON_QUERY_KEY_FOR_FINDING_INTENT).toString();
        intent = intent.substring(1, intent.length() - 1);

        scanner = new Reflections(currentClass.getPackage().getName());

        Constructor intentConstructor = initConstructor(intent);

        AbstractIntent googleActionCurrentIntent = initGoogleActionCurrentIntent(request, intentConstructor);

        return googleActionCurrentIntent.execute();
    }

    private static AbstractIntent initGoogleActionCurrentIntent(String intent, Constructor intentConstructor) {
        AbstractIntent googleActionCurrentIntent;
        try {
            googleActionCurrentIntent = (AbstractIntent) (intentConstructor.newInstance(intent));
            checkIfCurrentIntentIsNull(intent, googleActionCurrentIntent);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new GoogleActionInitException(e);
        }

        return googleActionCurrentIntent;
    }

    private static void checkIfCurrentIntentIsNull(String intent, AbstractIntent googleActionCurrentIntent) {
        if(googleActionCurrentIntent == null){
            throw new GoogleActionInitException(String.format(COULD_NOT_INIT_CURRENT_INTENT, intent) );
        }
    }

    private static Constructor initConstructor(String intent) {
        Constructor intentConstructor;
        try {
            Constructor classResponsiveForIntent = findClassResponsiveForIntent(intent);
            checkIfNoClassResponsiveForIntentNotFound(intent, classResponsiveForIntent);
            intentConstructor = classResponsiveForIntent;

        } catch (NoSuchMethodException e) {
            throw new NoSuchIntentException(intent);
        }
        return intentConstructor;
    }

    private static void checkIfNoClassResponsiveForIntentNotFound(String intent, Constructor intentConstructor) {
        if(intentConstructor == null){
            throw new NoSuchIntentException(intent);
        }
    }

    private static Constructor findClassResponsiveForIntent(String request) throws NoSuchMethodException {
        Set<Class<? extends AbstractIntent>> allClasses = scanner.getSubTypesOf(AbstractIntent.class);
        for (Class<?> allClass : allClasses) {
            Set<Constructor> injectables = Collections.singleton(allClass.getDeclaredConstructor(String.class));

            for (Constructor m : injectables) {
                if (m.isAnnotationPresent(Intent.class)) {
                    Intent cmd = (Intent) m.getAnnotation(Intent.class);
                    if (cmd.value().equals(request)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }
}
