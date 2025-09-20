package com.learn.micro.resourceprocessor.e2e;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/resource_flow_e2e.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.learn.micro.resourceprocessor.e2e")
public class ResourceFlowE2ETestRunner {}
