package com.afitnerd.distributedjmeter.rule;

import com.afitnerd.distributedjmeter.annotation.UsesGet;
import com.afitnerd.distributedjmeter.annotation.UsesPost;
import com.afitnerd.distributedjmeter.annotation.UsesTag;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class BeforeSwitches implements TestRule {
    private boolean usesPost;
    private boolean withResponse;
    private boolean usesGet;
    private boolean usesTag;
    private String tag;
    private String url;

    @Override
    public Statement apply(Statement base, Description description) {
        usesPost = (description.getAnnotation(UsesPost.class) != null);
        if (usesPost) {
            url = description.getAnnotation(UsesPost.class).value();
            withResponse = description.getAnnotation(UsesPost.class).withResponse();
        }
        usesGet = (description.getAnnotation(UsesGet.class) != null);
        usesTag = (description.getAnnotation(UsesTag.class) != null);
        if (usesTag) {
            tag = description.getAnnotation(UsesTag.class).value();
        }

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };
    }

    public boolean usesGet() {
        return usesGet;
    }

    public boolean usesPost() {
        return usesPost;
    }

    public boolean usesTag() {
        return usesTag;
    }

    public String tag() {
        return tag;
    }

    public String url() {
        return url;
    }

    public boolean withResponse() {
        return withResponse;
    }
}