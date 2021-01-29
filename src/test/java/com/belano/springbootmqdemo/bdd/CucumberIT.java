package com.belano.springbootmqdemo.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features",
    plugin = {"pretty:target/all-test-pretty-report.txt",
        "html:target/all-test-html-report",
        "json:target/jsonReports/all-test-json-report.json"
    }
)
public class CucumberIT {

}
