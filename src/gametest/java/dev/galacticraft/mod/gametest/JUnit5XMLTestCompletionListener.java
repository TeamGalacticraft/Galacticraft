/*
 * Copyright (c) 2019-2021 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.gametest;

import com.google.common.base.Stopwatch;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestCompletionListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Modified from {@link net.minecraft.test.XmlReportingTestCompletionListener} to include proper testsuite element attributes.
 */
public class JUnit5XMLTestCompletionListener implements TestCompletionListener {
    private final Document document;
    private final Element testSuiteElement;
    private final Stopwatch stopwatch;
    private final File file;

    private int tests = 0;
    private int failed = 0;
    private int skipped = 0;

    public JUnit5XMLTestCompletionListener(File file) throws ParserConfigurationException {
        this.file = file;
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.testSuiteElement = this.document.createElement("testsuite");
        this.testSuiteElement.setAttribute("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        this.stopwatch = Stopwatch.createStarted();
    }

    @Override
    public void onTestFailed(GameTestState test) {
        Element failedStateElement;
        if(test.isRequired()) {
            failedStateElement = this.document.createElement("failure");
            this.failed++;
        } else {
            failedStateElement = this.document.createElement("skipped");
            this.skipped++;
        }

        failedStateElement.setAttribute("message", test.getThrowable().getMessage());
        this.addTestCase(test, test.getStructurePath()).appendChild(failedStateElement);
    }

    @Override
    public void onTestPassed(GameTestState test) {
        this.addTestCase(test, test.getStructurePath());
        this.tests++;
    }

    @Override
    public void onStopped() {
        this.stopwatch.stop();
        this.testSuiteElement.setAttribute("time", String.valueOf(this.stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d));
        this.testSuiteElement.setAttribute("tests", String.valueOf(this.tests));
        this.testSuiteElement.setAttribute("failures", String.valueOf(this.failed));
        this.testSuiteElement.setAttribute("skipped", String.valueOf(this.skipped));
        this.document.appendChild(this.testSuiteElement);

        try {
            this.saveReport(this.file);
        } catch (TransformerException e) {
            throw new Error("Couldn't save test report", e);
        }
    }

    private Element addTestCase(GameTestState state, String name) {
        Element testCase = this.document.createElement("testcase");
        testCase.setAttribute("name", name);
        testCase.setAttribute("classname", state.getStructureName());
        testCase.setAttribute("time", String.valueOf(state.getElapsedMilliseconds() / 1000d));
        this.testSuiteElement.appendChild(testCase);
        return testCase;
    }

    private void saveReport(File file) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource source = new DOMSource(this.document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}
